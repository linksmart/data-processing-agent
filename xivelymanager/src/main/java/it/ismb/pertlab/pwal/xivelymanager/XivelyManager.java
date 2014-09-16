package it.ismb.pertlab.pwal.xivelymanager;

import it.ismb.pertlab.pwal.api.devices.events.DeviceListener;
import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.interfaces.DevicesManager;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceNetworkType;
import it.ismb.pertlab.pwal.xivelymanager.device.HumidityDevice;
import it.ismb.pertlab.pwal.xivelymanager.device.LightDevice;
import it.ismb.pertlab.pwal.xivelymanager.device.PressureDevice;
import it.ismb.pertlab.pwal.xivelymanager.device.ThermometerDevice;
import it.ismb.pertlab.pwal.xivelymanager.utils.Utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xively.client.XivelyService;
import com.xively.client.http.exception.HttpException;
import com.xively.client.http.util.exception.ParseToObjectException;
import com.xively.client.model.Datastream;
import com.xively.client.model.Feed;

/**
 * Manager for devices xively
 * 
 */
public class XivelyManager extends DevicesManager {
	// Number of feeds to search
	private static final int N_OF_FEEDS_TO_SEARCH_PER_TIME = 25;

	// Variable used to store the timestamp of the most recently created feed
	// found in a search (associated with the tag searched), it's used to avoid
	// to re-handle the same
	// feeds already found, in the next searches
	private static Map<String, String> LAST_MOST_RECENT_FEEDS = new HashMap<String, String>();

	// List of devices supported
	private static final String[] TAGS = { "temperature", "humidity", "light",
			"pressure" };

	static {
		for (String tag : TAGS) {
			LAST_MOST_RECENT_FEEDS.put(tag, null);
		}
	}

	@Override
	public void run() {
		while (!t.isInterrupted()) {
			try {
				searchFeeds();
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				log.debug("Interruption received", e);
				t.interrupt();
			}
		}
	}

	/**
	 * Method used to search the feeds
	 */
	private void searchFeeds() {
		for (String tag : TAGS) {
			final String lastMostRecentFeed = LAST_MOST_RECENT_FEEDS.get(tag);
			// This variable is used to store the value of the most recent
			// feed found in this search
			String newMostRecentFeed = null;
			boolean stopSearch = false;
			// FIXME now to fetch N feeds, N requests are done, fetching a feed
			// per time
			// this is done because if there is an error in the JSON received
			// the library discards the entire JSON, so if all the N feeds are
			// obtained with only one request, if one has an error, all are
			// discarded.
			// This approach has some issues:
			// one minute is required to fetch 100 feeds (this time can be
			// shortened
			// using a per page greater than one, but in that case, one error
			// in one feed, discards <per_page> number of feeds).
			// the operation is not atomic, if one new feed is created during
			// the
			// fetching of the feeds, the results are not correct
			for (int i = 1; i < N_OF_FEEDS_TO_SEARCH_PER_TIME && !stopSearch; i++) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("per_page", 1);
				params.put("page", i);
				params.put("tag", tag);
				params.put("order", "created_at");
				Collection<Feed> coll = null;
				try {
					coll = XivelyService.instance().feed().get(params);
				} catch (final HttpException | ParseToObjectException ex) {
					log.warn("Feed ignored, because not correctly parsed ", ex);
					continue;
				}
				List<Feed> feedsToHandle = new ArrayList<Feed>();
				try {
					if (lastMostRecentFeed == null) {
						// The timestamp of the more recently created feed is
						// stored
						// so in the next search, the feeds already loaded can
						// be
						// ignored
						feedsToHandle = new ArrayList<Feed>(coll);
					} else {
						// The feeds already found in previous search, are
						// ignored now
						for (Feed feed : coll) {
							if (Utils.compareCreatedAt(lastMostRecentFeed,
									feed.getCreatedAt()) < 0) {
								feedsToHandle.add(feed);
							} else {
								stopSearch = true;
								break;
							}
						}
					}
					// The first feed has to be stored has the new most recent
					// feed
					// If there are not new most recent feed, if no one more
					// recent
					// that the last has been found, the last most recent feed
					// is again the most recently created
					if (newMostRecentFeed == null) {
						newMostRecentFeed = (!feedsToHandle.isEmpty()) ? feedsToHandle
								.get(0).getCreatedAt() : lastMostRecentFeed;
					}
				} catch (ParseException e) {
					log.error("Invalid date format for a <created at> date");
					return;
				}
				// The new feeds are handled
				for (Feed feed : feedsToHandle) {
					Map<String, Object> mapOfStreams = new HashMap<String, Object>();
					if (feed.getDatastreams() != null) {
						for (Datastream stream : feed.getDatastreams()) {
							handleDatastream(mapOfStreams, stream);
						}
					}
					// If in the feeds there was some useful datastream
					// a new PWAL Device mapping that datastream is created
					if (!mapOfStreams.isEmpty()) {
						createDevicesFromStreams(feed, mapOfStreams);
					}
				}
			}
			// The timestamp of the most recently created feed of this search
			// is stored for the next searches
			LAST_MOST_RECENT_FEEDS.put(tag, newMostRecentFeed);
		}
	}

	/**
	 * Collects the streams, the devices are not created directly here because
	 * it could be necessary to manage the case of PWAL devices, which contains
	 * more than one streams (like the accelerometers)
	 * 
	 * @param mapOfStreams
	 *            Map that contains all the managed streams of the feed
	 * 
	 * @param stream
	 *            Stream to handle
	 * 
	 */
	private void handleDatastream(Map<String, Object> mapOfStreams,
			Datastream stream) {
		if (stream.getTags() != null) {
			for (String tag : stream.getTags()) {
				tag = tag.toLowerCase();
				if (tag.matches(".*temperature.*|.*humidity.*|.*light.*|.*pressure.*")) {
					log.debug("New managed datastream found: " + stream.getId());
					mapOfStreams.put(tag + stream.getId(), stream);
				}
			}
		}
	}

	/**
	 * From the streams collected, it creates the devices
	 * 
	 * @param mapOfStreams
	 *            Map that contains all the managed streams of the feed
	 * 
	 */
	private void createDevicesFromStreams(Feed feed,
			Map<String, Object> mapOfStreams) {
		Integer feedID = feed.getId();
		for (String key : mapOfStreams.keySet()) {
			Device device = null;
			String streamID = ((Datastream) mapOfStreams.get(key)).getId();
			if (key.contains("temperature")) {
				device = new ThermometerDevice(feedID, streamID);
			} else if (key.contains("humidity")) {
				device = new HumidityDevice(feedID, streamID);
			} else if (key.contains("light")) {
				device = new LightDevice(feedID, streamID);
			} else if (key.contains("pressure")) {
				device = new PressureDevice(feedID, streamID);
			}
			if (device != null) {
				if (feed.getLocation() != null) {
					device.setLocation(Utils.convertLocation(feed.getLocation()));
				}
				log.info("Device created for Feed: " + feedID
						+ ", datastream: " + streamID);
				if (!this.devicesDiscovered.containsKey(feedID + streamID)) {
					List<Device> ld = new ArrayList<>();
					this.devicesDiscovered.put(feedID + streamID, ld);
				}
				devicesDiscovered.get(feedID + streamID).add(device);
				for (DeviceListener l : deviceListener) {
					l.notifyDeviceAdded(device);
				}
			}
		}
	}

	@Override
	public String getNetworkType() {
		return DeviceNetworkType.XIVELY;
	}

}