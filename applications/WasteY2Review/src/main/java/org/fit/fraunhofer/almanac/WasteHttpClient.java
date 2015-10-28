package org.fit.fraunhofer.almanac;

/**
 * Created by Werner-Kytölä on 03.07.2015.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.reflect.TypeToken;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Datastream;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Location;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.ObservedProperty;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Thing;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.geojson.GeoJsonObject;
import org.geojson.Geometry;
import org.geojson.LngLatAlt;
import org.geojson.Point;

public class WasteHttpClient{

    private static WasteHttpClient instance = null;

    /***************** CONSTANTS */
    private static final String RESOURCECAT_GET_URL = "http://linksmart.cnet.se:44441/ogc/Things?$filter=";
    private static final String RESOURCECAT_BINLOCATION = "http://linksmart.cnet.se:44441/ogc/Things?$filter=type%20eq%20WasteBin%20and%20location%20geo.distance%20";
    private static final String RESOURCECAT_LOCATION = "http://linksmart.cnet.se:44441/ogc/Things?$filter=location%20geo.distance%20";

    private static final String RESOURCECAT_THINGID = "http://linksmart.cnet.se:44441/ogc/Things?$filter=thingid%20eq%20";
    private static final String RESOURCECAT_FILLLEVEL_OPEN = "$xpath=//IoT:fillLevel%5B%2E%3E";  // "$xpath=//IoT:fillLevel[.>";
    private static final String RESOURCECAT_FILLLEVEL_CLOSE = "%5D";                         // "]";

    private static final int SUCCESSFUL_LOWERLIMIT = 200;
    private static final int SUCCESSFUL_UPPERLIMIT = 300;


    public static synchronized WasteHttpClient getInstance() {
        if (instance == null) {
            instance = new WasteHttpClient();
        }

        return instance;
    }

    public ArrayList<Thing> getBinsInVicinity(int distance, double referenceLatitude, double referenceLongitude){
        try {
            String wasteBinResponse = sendGET(RESOURCECAT_BINLOCATION + distance + "%20" + referenceLatitude + "%20" + referenceLongitude);

            ObjectMapper mapper = new ObjectMapper();
            Things bins = mapper.readValue(wasteBinResponse, Things.class);

            ArrayList<Thing> binsInVicinity = new ArrayList<>(bins.getThing());

            return binsInVicinity;
        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public HashMap<String,org.geojson.LngLatAlt> getLocationOfBinsInVicinity(int distance, double referenceLatitude, double referenceLongitude){
        try {
            String wasteBinResponse = sendGET(RESOURCECAT_BINLOCATION + distance + "%20" + referenceLatitude + "%20" + referenceLongitude);

            ObjectMapper mapper = new ObjectMapper();
            Things bins = mapper.readValue(wasteBinResponse, Things.class);

            HashMap<String,org.geojson.LngLatAlt> binsLocationMap = new HashMap<>();
            for (Thing bin : bins.getThing()) {
                binsLocationMap.put(bin.getId(), ((org.geojson.Point) ((it.ismb.pertlab.ogc.sensorthings.api.datamodel.Location) bin.getLocations().toArray()[0]).getGeometry()).getCoordinates());
            }

            return binsLocationMap;
        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Thing> getBinsInVicinityWithFillLevelAboveThreshold(int distance, double referenceLatitude, double referenceLongitude, int threshold){
        try {
            String url = RESOURCECAT_BINLOCATION + distance + "%20" + referenceLatitude + "%20" +
                    referenceLongitude + RESOURCECAT_FILLLEVEL_OPEN + threshold + RESOURCECAT_FILLLEVEL_CLOSE;

            String wasteBinResponse = sendGET(url);

            ObjectMapper mapper = new ObjectMapper();
            Things bins = mapper.readValue(wasteBinResponse, Things.class);

            ArrayList<Thing> binsInVicinityAboveFillThreshold = new ArrayList<>(bins.getThing());

            return binsInVicinityAboveFillThreshold;
        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public HashMap<String,org.geojson.LngLatAlt> getLocationsOfBinsInVicinityWithFillLevelAboveThreshold(int distance, double referenceLatitude, double referenceLongitude, float threshold){
        try {
            String wasteBinResponse = sendGET(RESOURCECAT_BINLOCATION + distance + "%20" + referenceLatitude + "%20" +
                    referenceLongitude + "%20" + RESOURCECAT_FILLLEVEL_OPEN + threshold + RESOURCECAT_FILLLEVEL_CLOSE);

            ObjectMapper mapper = new ObjectMapper();
            Things bins = mapper.readValue(wasteBinResponse, Things.class);

            HashMap<String,org.geojson.LngLatAlt> binsLocationMap = new HashMap<>();
            for (Thing bin : bins.getThing()) {
                binsLocationMap.put(bin.getId(), ((org.geojson.Point) ((it.ismb.pertlab.ogc.sensorthings.api.datamodel.Location) bin.getLocations().toArray()[0]).getGeometry()).getCoordinates());
            }

            return binsLocationMap;
        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public Thing getBin(String id){
        try{
            String wasteBinResponse = sendGET(RESOURCECAT_THINGID + id);

            ObjectMapper mapper = new ObjectMapper();
            Things bins = mapper.readValue(wasteBinResponse, Things.class);

            Thing bin = bins.ThingsArray().get(0);
            return bin;
        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public org.geojson.LngLatAlt getBinGeolocation(String id){
        try{
            String wasteBinResponse = sendGET(RESOURCECAT_THINGID + id);

            ObjectMapper mapper = new ObjectMapper();
            Things bins = mapper.readValue(wasteBinResponse, Things.class);

            org.geojson.LngLatAlt binLocation = ((org.geojson.Point) ((it.ismb.pertlab.ogc.sensorthings.api.datamodel.Location) bins.ThingsArray().get(0).getLocations().toArray()[0]).getGeometry()).getCoordinates();

/*            ObjectMapper mapper = new ObjectMapper();
            Things bins = mapper.readValue(wasteBinResponse, Things.class);
            {"Thing":[{"id":"","Description":"","Metadata":"","Locations":[{"Time":"2015-06-10T08:13:27.148Z","Geometry":{"type":"Point",
            "coordinates":[7.6761046,45.07856176]}}],"Datastreams":[{"id":"",
            "ObservedProperty":{"id":"","URI":"","UnitOfMeasurement":"C"}},{"id":"0ef639cdf3735fb9063a29284a051cbf4f340323c249cfd80e7e88eb0325d7a9","ObservedProperty":{"id":"e641920aff981b7f2712a09babcc3e604bc18e39e0d5a2b6cdc1caaee7644282","UnitOfMeasurement":"unknown"}},{"id":"e174eb15aa911717d6489827acf76adf89e8d2aec6bd16b42ed43b5363991cfe","ObservedProperty":{"id":"117ca9cceea0a2ec351cc451c22c61efda1ec0d447d1d0b74e52baf9341f3988",
            "URI":"http://almanac-project.eu/ontologies/smartcity.owl#FillLevelState","UnitOfMeasurement":"%"}}]}]}
*/


/*            Things thingExample = new Things();
            Thing thing1 = new Thing();
            thing1.setId("ea725b113eef37f8289962d4e2e39bb24657b4c5916b42701028e89ee5953fd7");
            thing1.setDescription("The WasteBin connected to the WasteBinSimulator network.");
            thing1.setMetadata("http://almanac-project.eu/ontologies/smartcity.owl#WasteBin");
            Location location = new Location();
            location.setTime(new Date());
            GeoJsonObject geometry = new Point(7.6761046,45.07856176);

            location.setGeometry(geometry);

            HashSet<Location> ob = new HashSet<Location>();
            ob.add(location);
            thing1.setLocations(ob);
            Datastream datastream = new Datastream();
            datastream.setId("65fa3b218346ad5788214305639b2bd10082ef2a4539dbcaade63eb00a5177f7");
            ObservedProperty observedProperty = new ObservedProperty();
            observedProperty.setId("01eaf22a265210e4ed2a6ffbbe20fa5a20533032ac2c2a1dee23e1bf85c741ed");
            observedProperty.setUrn("http://almanac-project.eu/ontologies/smartcity.owl#TemperatureState");
            observedProperty.setUnitOfMeasurement("C");
            datastream.setObservedProperty(observedProperty);
            thing1.addDatastream(datastream);
            thingExample.ThingsArray().add(thing1);

            //Gson gsonObj = (new GsonBuilder().serializeSpecialFloatingPointValues().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'")).create();
            ObjectMapper mapper = new ObjectMapper();

            String json = mapper.writeValueAsString(thingExample);

            Things bins = mapper.readValue("{\"Thing\":[{\"id\":\"ea725b113eef37f8289962d4e2e39bb24657b4c5916b42701028e89ee5953fd7\",\"Description\":\"The WasteBin connected to the WasteBinSimulator network.\",\"Metadata\":\"http://almanac-project.eu/ontologies/smartcity.owl#WasteBin\",\"Locations\":[{\"Time\":\"2015-06-10T08:13:27.148Z\",\"Geometry\":{\"type\":\"Point\",\"coordinates\":[7.6761046,45.07856176]}}],\"Datastreams\":[{\"id\":\"01eaf22a265210e4ed2a6ffbbe20fa5a20533032ac2c2a1dee23e1bf85c741ed\",\"ObservedProperty\":{\"id\":\"65fa3b218346ad5788214305639b2bd10082ef2a4539dbcaade63eb00a5177f7\",\"URI\":\"http://almanac-project.eu/ontologies/smartcity.owl#TemperatureState\",\"UnitOfMeasurement\":\"C\"}},{\"id\":\"0ef639cdf3735fb9063a29284a051cbf4f340323c249cfd80e7e88eb0325d7a9\",\"ObservedProperty\":{\"id\":\"e641920aff981b7f2712a09babcc3e604bc18e39e0d5a2b6cdc1caaee7644282\",\"UnitOfMeasurement\":\"unknown\"}},{\"id\":\"e174eb15aa911717d6489827acf76adf89e8d2aec6bd16b42ed43b5363991cfe\",\"ObservedProperty\":{\"id\":\"117ca9cceea0a2ec351cc451c22c61efda1ec0d447d1d0b74e52baf9341f3988\",\"URI\":\"http://almanac-project.eu/ontologies/smartcity.owl#FillLevelState\",\"UnitOfMeasurement\":\"%\"}}]}]}", Things.class);
*/

            return binLocation;
        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String sendGET(String getUrl) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {

            //URLEncoder.encode(getUrl, "utf-8");

            HttpGet httpGetRequest = new HttpGet(getUrl);

            httpGetRequest.addHeader("content-type", "application/json");
            httpGetRequest.addHeader("accept", "application/json");

            System.out.println("Executing request " + httpGetRequest.getRequestLine());

            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                @Override
                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {

                    int status = response.getStatusLine().getStatusCode();
                    if (status >= SUCCESSFUL_LOWERLIMIT && status < SUCCESSFUL_UPPERLIMIT) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };

            String responseBody = httpClient.execute(httpGetRequest, responseHandler);
            System.out.println("----------------------------------------");
            System.out.println(responseBody);

            return responseBody;
        } finally {
            httpClient.close();
        }
    }
}
