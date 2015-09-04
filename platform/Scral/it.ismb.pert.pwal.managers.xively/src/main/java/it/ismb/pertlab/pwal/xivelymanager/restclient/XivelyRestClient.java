package it.ismb.pertlab.pwal.xivelymanager.restclient;

import it.ismb.pertlab.pwal.xivelymanager.XivelyManager;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;

import com.xively.client.XivelyService;
import com.xively.client.http.api.DatastreamRequester;
import com.xively.client.http.exception.HttpException;
import com.xively.client.http.util.exception.ParseToObjectException;
import com.xively.client.model.Datastream;

public class XivelyRestClient {
    private static Logger log = null;
    private XivelyManager manager = null;

    public XivelyRestClient(XivelyManager manager, Logger logger)
    {
        this.manager = manager;
        log = logger;
    }

    /**
     * Retrieves the updated datastreams for the devices
     * 
     * @return List of datastreams for the devices
     */
    public Map<String, Datastream> getMeasures() {
    	Map<String,Datastream> result = new HashMap<String,Datastream>();
    	for(String id : manager.getUpdatedDatastreamList()) {
    		log.debug("retrieving info for device with id {} ",id);
    		StringTokenizer tokens = new StringTokenizer(id, XivelyManager.ID_SEPARATOR);
    		Integer feedID = Integer.parseInt(tokens.nextToken());
    		String streamID = tokens.nextToken();
    		DatastreamRequester req = XivelyService.instance().datastream(feedID);
    		try
    		{
    			Datastream stream = req.get(streamID);
    			log.debug("datastream found: {} "+stream.toString());
    			result.put(id,stream);
    		}
    		catch(HttpException | ParseToObjectException e)
    		{
    			log.warn("Exception while getting data from stream: "+streamID);
    		}
    	}
    	return result;
    }
}
