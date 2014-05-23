package it.ismb.pertlab.pwal.xivelymanager;

import it.ismb.pertlab.pwal.api.devices.events.DeviceListener;
import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.interfaces.DevicesManager;
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
	// found in a search (associated with the tag searched), it's used to avoid to re-handle the same
	// feeds already found, in the next searches
	private static Map<String,String> LAST_MOST_RECENT_FEEDS = new HashMap<String,String>();

	// List of devices supported
	private static final String [] TAGS = {"temperature","humidity","light","pressure"};
	
	static {
		for (String tag : TAGS) {
			LAST_MOST_RECENT_FEEDS.put(tag, null);
		}
	}
	
	@Override
	public void run() {
		while(!t.isInterrupted()) {
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
		for(String tag : TAGS) {
			final String lastMostRecentFeed = LAST_MOST_RECENT_FEEDS.get(tag); 
			// This variable is used to store the value of the most recent
			// feed found in this search
			String newMostRecentFeed = null;
			boolean stopSearch = false;
			// FIXME now to fetch N feeds, N requests are done, fetching a feed per time
			// this is done because if there is an error in the JSON received
			// the library discards the entire JSON, so if all the N feeds are
			// obtained with only one request, if one has an error, all are discarded.
			// This approach has some issues:
			//      one minute is required to fetch 100 feeds (this time can be shortened
			//              using a per page greater than one, but in that case, one error
			//              in one feed, discards <per_page> number of feeds).
			//      the operation is not atomic, if one new feed is created during the
			//      fetching of the feeds, the results are not correct
			for(int i=1; i < N_OF_FEEDS_TO_SEARCH_PER_TIME && !stopSearch ; i++ ) {
				Map <String,Object> params = new HashMap<String,Object>();
				params.put("per_page", 1);
				params.put("page", i);
				params.put("tag", tag);
				params.put("order", "created_at");
				Collection<Feed> coll = null;
				try {
					coll = XivelyService.instance().feed().get(params);
				} catch (final HttpException | ParseToObjectException ex) {
					log.warn("Feed ignored, because not correctly parsed ",ex);
					continue;
				}
				List<Feed> feedsToHandle = new ArrayList<Feed>();
				try {
					if(lastMostRecentFeed == null) {
						// The timestamp of the more recently created feed is stored
						// so in the next search, the feeds already loaded can be
						// ignored
						feedsToHandle = new ArrayList<Feed>(coll);
					} else {
						// The feeds already found in previous search, are ignored now
						for(Feed feed : coll) {
							if(Utils.compareCreatedAt(lastMostRecentFeed, feed.getCreatedAt())<0) {
								feedsToHandle.add(feed);
							} else {
								stopSearch = true;
								break;
							}
						}
					}
					// The first feed has to be stored has the new most recent feed
					// If there are not new most recent feed, if no one more recent
					// that the last has been found, the last most recent feed
					// is again the most recently created
					if(newMostRecentFeed==null) {
						newMostRecentFeed = (!feedsToHandle.isEmpty()) ? feedsToHandle.get(0).getCreatedAt() : lastMostRecentFeed;
					} 
				} catch (ParseException e) {
					log.error("Invalid date format for a <created at> date");
					return;
				}
				// The new feeds are handled
				for(Feed feed : feedsToHandle) {
					Map<String,Object>mapOfStreams = new HashMap<String,Object>();
					if(feed.getDatastreams()!=null) {
						for(Datastream stream : feed.getDatastreams()) {
							handleDatastream(mapOfStreams, stream);
						}
					}
					// If in the feeds there was some useful datastream
					// a new PWAL Device mapping that datastream is created
					if(!mapOfStreams.isEmpty()) {
						createDevicesFromStreams(feed,mapOfStreams);
					}
				}
			}
			// The timestamp of the most recently created feed of this search
			// is stored for the next searches
			LAST_MOST_RECENT_FEEDS.put(tag, newMostRecentFeed);
		}
	}
	
	/**
	 * Collects the streams, the devices are not created directly here
	 * because it could be necessary to manage the case of PWAL devices, which
	 * contains more than one streams (like the accelerometers) 
	 * 
	 * @param mapOfStreams
	 *               Map that contains all the managed streams of the feed
	 *               
	 * @param stream
	 *               Stream to handle
	 * 
	 */
	private void handleDatastream(Map<String,Object>mapOfStreams, Datastream stream) {
		if(stream.getTags()!=null) {
			for(String tag : stream.getTags()) {
				tag = tag.toLowerCase();
				if(tag.matches(".*temperature.*|.*humidity.*|.*light.*|.*pressure.*")) {
					log.debug("New managed datastream found: "+stream.getId());
					mapOfStreams.put(tag+stream.getId(), stream);
				}
			}
		}
	}

	/**
	 * From the streams collected, it creates the devices 
	 * 
	 * @param mapOfStreams
	 *               Map that contains all the managed streams of the feed
	 *               
	 */
	private void createDevicesFromStreams(Feed feed, Map<String,Object>mapOfStreams) {
		Integer feedID = feed.getId();
		for(String key :  mapOfStreams.keySet()) {
			Device device = null;
			String streamID = ((Datastream) mapOfStreams.get(key)).getId();
			if(key.contains("temperature")) {
				device = new ThermometerDevice(feedID, streamID);
			} else if(key.contains("humidity")) {
				device = new HumidityDevice(feedID, streamID);				
			} else if(key.contains("light")) {
				device = new LightDevice(feedID, streamID);
			} else if(key.contains("pressure")) {
				device = new PressureDevice(feedID, streamID);
			}
			if(device!=null) {
				if(feed.getLocation()!=null) {
					device.setLocation(Utils.convertLocation(feed.getLocation()));
				}
				log.info("Device created for Feed: "+feedID+", datastream: "+streamID);
				devicesDiscovered.put(feedID+streamID, device);
				for(DeviceListener l:deviceListener){
					l.notifyDeviceAdded(device);
				}
			}	
		}		
	}

}




/*
public static void main( String[] args )
{
	while(true) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		Datapoint dp1 = new Datapoint();
		dp1.setAt(dateFormat.format(date));
		dp1.setValue(""+(0 + (int)(Math.random() * ((100 - 0) + 1))));

		// assuming your API key has permission to write
		// to the feed:123 and datastream:"test_stream0"
		DatastreamRequester req = XivelyService.instance().datastream(1440608196);
		Datastream stream = req.get("humidity");
		stream.setValue(""+(0 + (int)(Math.random() * ((100 - 0) + 1))));
		req.update(stream);
 		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	Map <String,Object> params = new HashMap<String,Object>();
	params.put("user", "codavide");
//	params.put("lat", 45.51);
//	params.put("lon", 8.0);
//	params.put("dinstance", 10.0);
//	params.put("dinstance_units", "kms");
	Collection<Feed> coll = XivelyService.instance().feed().get(params);
	for(Feed feed : coll) {
		System.out.print("\n\n\n");
		System.out.println("Created at "+feed.getCreatedAt());
		System.out.println("Description "+feed.getDescription());
		System.out.println("Title "+feed.getTitle());
		System.out.println("Updated At "+feed.getUpdatedAt());
		System.out.println("Creator URI "+feed.getCreatorUri());
		System.out.println("Feed URI "+feed.getFeedUri());
		if(feed.getLocation()!=null) {
			System.out.println("Latitude "+feed.getLocation().getLatitiude());
			System.out.println("Longitude "+feed.getLocation().getLongitute());
			System.out.println("Elevation "+feed.getLocation().getElevation());
			System.out.println("Name "+feed.getLocation().getName());
			System.out.println("Disposition "+feed.getLocation().getDisposition());
			System.out.println("Domain "+feed.getLocation().getDomain());
			System.out.println("Exposure "+feed.getLocation().getExposure());
		}
		System.out.println("ID "+feed.getId());
		System.out.println("Status "+feed.getStatus());
		System.out.println("Website "+feed.getWebsite());
		if(feed.getTags()!=null) {
			for (String tag : feed.getTags()) {
				System.out.println("Tag "+tag);
			}
		}
		if(feed.getDatastreams()!=null) {
			for(Datastream stream : feed.getDatastreams()) {
				System.out.println("ID "+stream.getId());
				System.out.println("Max Value "+stream.getMaxValue());
				System.out.println("Min Value "+stream.getMinValue());
				System.out.println("Updated at "+stream.getUpdatedAt());
				System.out.println("Value "+stream.getValue());
				if(stream.getUnit()!=null) {
					System.out.println("Unit Label "+stream.getUnit().getLabel() != null ? stream.getUnit().getLabel() : "");
					System.out.println("Unit Symbol "+stream.getUnit().getSymbol());
				}
				if(stream.getTags()!=null) {
					for (String tag : stream.getTags()) {
						System.out.println("Tag "+tag);
					}
				}
				if(stream.getDatapoints()!=null) {
					for (Datapoint datapoint : stream.getDatapoints()) {
						System.out.println("At "+datapoint.getAt());
						System.out.println("Id "+datapoint.getId());
						System.out.println("Value "+datapoint.getValue());
					}
				}
			}
		}
	}
}
*/