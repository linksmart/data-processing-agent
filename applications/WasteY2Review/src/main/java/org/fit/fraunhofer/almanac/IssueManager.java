package org.fit.fraunhofer.almanac;

import com.google.gson.Gson;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Thing;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttReceivedMessage;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by Werner-Kyt�l� on 08.05.2015.
 */
public class IssueManager implements Observer{

    /***************** CONSTANTS */
    public static final int MIN_ISSUECOUNT = 7; // number of issues created until a route generation is triggered

    public static final String DF_WASTEBINFULL_TOPIC = "/+/+/+/cep/5296850124791908742793477709595434718264213518621513551279291212674474587702";
    // Data Fusion query id: all waste bins with fill level greater than 80%
    public static final String CITIZENAPP_TOPIC = "almanac/citizenapp";  // issues coming from a CitizenApp

    public static final String SMARTWASTE_TOPIC = "almanac/smartwaste/+"; //all smartwaste one-level sub-topics
    public static final String SMARTWASTE_DUPLICATE_TOPIC = "almanac/smartwaste/duplicate";  // duplicate notification coming from the SmartWaste application

    public static final String ISSUE = "issue/";
    public static final String UPDATE = "/update";
    public static final String DUPLICATE = "/duplicate";
    public static final String SUBSCRIBE = "/subscribe";



    private HashMap<String, Issue> issueMap;
//    private ArrayList<Issue> issueList;
    private HashMap<String, Route> routeMap;
    private HashMap<String, Vehicle> vehicleMap;
    private ArrayList<Thing> thingList;
    private WasteMqttClient pubClient;
    private WasteMqttClient subClient;

    private ExecutorService executor;


    public IssueManager(){
        issueMap = new HashMap<String, Issue>();
//        issueList = new ArrayList<Issue>();
        routeMap = new HashMap<String, Route>();
        vehicleMap = new HashMap<String, Vehicle>();

        executor = Executors.newCachedThreadPool();

        mqttClientsSetup();
    }
    public IssueManager(int count){
        // creates a map with a number count of issues
        issueMap = new HashMap<String, Issue>(count);
//        issueList = new ArrayList<Issue>(count);
        routeMap = new HashMap<String, Route>();
        vehicleMap = new HashMap<String, Vehicle>();

        executor = Executors.newCachedThreadPool();

        mqttClientsSetup();

        Issue issue;;

        for (int i = 0 ; i < count ; i++) {
            addIssue();
        }
    }
    public IssueManager(ArrayList<Thing> thingListMetadata){

        issueMap = new HashMap<String, Issue>();
        routeMap = new HashMap<String, Route>();
        vehicleMap = new HashMap<String, Vehicle>();

        executor = Executors.newCachedThreadPool();

        mqttClientsSetup();


        // creates a map/list of issues with the same number of elements as in the metadata file
//        issueMap = new HashMap<String, Issue>();
/*        issueList = new ArrayList<Issue>();
        routeMap = new HashMap<String, Route>();
        vehicleMap = new HashMap<String, Vehicle>();
        thingList = new ArrayList<Thing>();
        thingList.addAll(thingListMetadata);

        executor = Executors.newCachedThreadPool();

        mqttClientsSetup();

        Issue issue;

//        for (Thing thing : thingList) {
        for (int i = 0 ; i <= MIN_ISSUECOUNT ; i++){

            org.geojson.LngLatAlt point =
                    ((org.geojson.Point) ((it.ismb.pertlab.ogc.sensorthings.api.datamodel.Location) thingList.get(i).getLocations().toArray()[0]).getGeometry()).getCoordinates();

            System.out.println("Thing's Id: " + thingList.get(i).getId());
            System.out.println("Thing's Metadata: " + thingList.get(i).getMetadata());
            System.out.println("Thing's Longitude: " + point.getLongitude());
            System.out.println("Thing's Latitude: " + point.getLatitude());
            System.out.println();

            addIssue(thingList.get(i).getId(), point.getLatitude(), point.getLongitude());
        }
        generateRoute("/almanac/route/initial");
*/
    }

    private void mqttClientsSetup() {
        subClient = WasteMqttClient.getInstanceSub();

        subClient.subscribe(DF_WASTEBINFULL_TOPIC); // Data Fusion query
        subClient.subscribe(CITIZENAPP_TOPIC);      // get notified by a CitizenApp about issues
        subClient.subscribe(SMARTWASTE_TOPIC);      // get notified by the SmartWaste application

        subClient.addObserver(this);

        pubClient = WasteMqttClient.getInstancePub();
    }

    private ObjectMapper mapper = new ObjectMapper();

    // This callback is invoked whenever a message on a subscribed topic has arrived
    // through WasteMqttClient.messageArrived(String topic, MqttMessage message)
    @Override
    public void update(Observable observable, Object arg) {
        try{
/*            MqttReceivedMessage data = (MqttReceivedMessage)arg;
            it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation obs =
                    mapper.readValue(data.getPayload(), it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation.class);*/
            WasteMqttClient.MqttMessageWithTopic data = (WasteMqttClient.MqttMessageWithTopic)arg;

            switch(data.Topic()){
                case DF_WASTEBINFULL_TOPIC:
                    handleDFWastebinFull(data.Payload());
                    break;
                case CITIZENAPP_TOPIC:
                    handleCitizenApp(data.Payload());
                    break;
                case SMARTWASTE_DUPLICATE_TOPIC:
                    handleSmartWasteDuplicate(data.Payload());
                    break;
                default:
                    break;
            }
        }catch(Exception e) {
                e.printStackTrace();
        }
    }

    private org.geojson.LngLatAlt findThingLocation(String binId){
        for (Thing thing : thingList) {
            if (thing.getId().compareTo(binId) == 0) {
//                org.geojson.LngLatAlt point =((org.geojson.Point) ((it.ismb.pertlab.ogc.sensorthings.api.datamodel.Location) thingList.get(0).getLocations().toArray()[0]).getGeometry()).getCoordinates();
                org.geojson.LngLatAlt point =((org.geojson.Point) ((it.ismb.pertlab.ogc.sensorthings.api.datamodel.Location) thing.getLocations().toArray()[0]).getGeometry()).getCoordinates();

                return point;
            }
        }
        return null;
    }

    // The incoming message is Data Fusion related: a waste bin fill level has
    // surpassed the threshold and is full. A new issue is to be created out
    // of this observation.
    private void handleDFWastebinFull(MqttMessage message){
        try{
            it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation obs =
                    mapper.readValue(message.getPayload(), it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation.class);

            // get the waste bin id: event result value
            String binId = obs.getResultValue().toString();

            // Now the resource catalogue can be used to find the specific waste bin holding the id and then get
            // its location and bin type, before a new issue can be created.
            // Resource Catalogue API use is missing here!!!!

            System.out.println("A Data Fusion message has arrived. The waste bin " + binId + " is full!");

            org.geojson.LngLatAlt thingLocation = findThingLocation(binId);
            if(thingLocation!= null){   // this is the toy bin, the only one relevant to the demo
                executor.execute(new Runnable() {
                    public void run() {
                        generateRoute("/almanac/route");
                    }
                });

/*                Thread routeGenerator = new Thread(new Runnable() {
                    public void run() {
                        generateRoute("/almanac/route");
                    }
                });
                routeGenerator.start();*/
            }

        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    // The incoming message is CitizenApp related: A new issue is to be created.
    // payload has issueId, geolocation, picture, name, and comment of a citizen's issue
    private void handleCitizenApp(MqttMessage message){

        // An issue generated by the CitizenApp comes in Json format
        Gson gsonObj = new Gson();

        String json = "";
        try {
            json = new String((byte[]) message.getPayload(), "UTF-8");
            PicIssue picIssue = gsonObj.fromJson(json, PicIssue.class);
            picIssue.print();

            // now it's about creating an issue... A city employee will still check for duplicates. If the employee finds this issue
            // to be a duplicate of an existing one, this issue will be marked as duplicate within the SmartWaste application and
            // its issueId will be sent to BeWaste under the topic almanac/smartwaste/duplicate.
            addPicIssue(picIssue);

        }catch(UnsupportedEncodingException e) {
        }
    }

    // The incoming message is SmartWaste related: it tells about an issue being duplicated
    private void handleSmartWasteDuplicate(MqttMessage message){

        Gson gsonObj = new Gson();

        String json = "";
        try {
            json = new String((byte[]) message.getPayload(), "UTF-8");
            DuplicateIssue dupIssue = gsonObj.fromJson(json, DuplicateIssue.class);

            // now it's about deleting the duplicated issue...
            removeIssue(dupIssue.issueId());

            // and publishing the duplication so that the CitizenApp can be notified
            String topic = ISSUE + dupIssue.issueId() + DUPLICATE;

            json = gsonObj.toJson(dupIssue);
            pubClient.publish(topic, json);

        }catch(UnsupportedEncodingException e) {
        }
    }

    private void addIssue(){
        Issue issue = new Issue();
        issueMap.put(issue.id(), issue);
//        issueList.add(issue);
    }

    private void addIssue(String binId, double latitude, double longitude){
        Issue issue = new Issue(binId, latitude, longitude);
        issueMap.put(issue.id(), issue);
//        issueList.add(issue);
    }

    private void addIssue(String binId, double latitude, double longitude, String binType){
        Issue issue = new Issue(binId, latitude, longitude, binType);
        issueMap.put(issue.id(), issue);
//        issueList.add(issue);
    }

    private void addPicIssue(PicIssue picIssue){
        Issue issue = new Issue(picIssue);
        issueMap.put(issue.id(), issue);
//        issueList.add(issue);

        // test - update this newly created issue
//        (issueList.get(issueList.size() - 1)).update(Issue.Priority.MAJOR);
        issueMap.get(issue.id()).update(Issue.Priority.MAJOR);

        // end test
    }

    private void removeIssue(String issueId){
        issueMap.remove(issueId);
    }

    protected void generateRoute(String routeType){
    /*
//        Route route = addRoute();

//        route.generateRoute(issueMap);  // this is simplified: In the first go, the route will be generated out
                                         // of all issues. It is assumed that all issues will belong to the same route.

//        if(!issueMap.isEmpty()){
          if(!issueList.isEmpty()){
            ArrayList<RouteEndpoint> routeEndpointList = new ArrayList<RouteEndpoint>();

//            for (Map.Entry<String, Issue> entry : issueMap.entrySet()) {
              for (Issue entry : issueList){
                RouteEndpoint aux = new RouteEndpoint();
//                Issue values = entry.getValue();

//                aux.setId(values.id());
                  aux.setId(entry.id());
//                  aux.setGeoLocation(values.latitude(), values.longitude());
                  aux.setGeoLocation(entry.latitude(), entry.longitude());

                routeEndpointList.add(aux);
            }
            if(routeType.equals("/almanac/route/initial") && routeEndpointList.size() > MIN_ISSUECOUNT) {
                routeEndpointList.remove(MIN_ISSUECOUNT);
            }
            // route endpoints will be formatted to Json: [{"id":"<id>","geoLocation":{"latitude":0.0,"longitude":0.0}}]
            Gson gsonObj = new Gson();

            String issueGson = gsonObj.toJson(routeEndpointList);
//            System.out.println(gsonObj.toJson(routeEndpointsList));

            // now it's about publishing the route (more specifically, the geolocation of the issues contained in the route),
            // so that the Driver-App can listen to it, use Google DirectionsService to calculate the route and render it.
            pubClient.publish(routeType, issueGson);
            System.out.println("Route endpoints published under topic " + routeType);
        }
*/
    }

    public Route addRoute(){
        Route route = new Route();
        routeMap.put(route.id(), route);

        return route;
    }

    public Route findRoute(String id){
        if(!routeMap.isEmpty() ) {
            return routeMap.get(id);
        }
        return null;
    }

/*    public void print(){
        // Iterate over all issues, using the keySet method.
        for(String key: issueMap.keySet()) {
            System.out.println(key + " -  UUID " + issueMap.get(key).id());
            System.out.println(key + " -  Creation date " + (issueMap.get(key)).creationDate());
            System.out.println(key + " -  Creator " + (issueMap.get(key)).creator());
            System.out.println(key + " -  Assignee " + (issueMap.get(key)).assignee());
            System.out.println(key + " -  State " + (issueMap.get(key)).state());
            System.out.println(key + " -  Estimated Time " + (issueMap.get(key)).estimatedTime());
            System.out.println(key + " -  Priority " + (issueMap.get(key)).priority());
//            System.out.println(key + " -  Geolocation " + (issueMap.get(key)).geoLocation());
            System.out.println(key + " -  Geolocation: " + "lat: " + (issueMap.get(key)).latitude() + " long: " + (issueMap.get(key)).longitude() + "\n");
        }
        System.out.println();
    }
*/

/*    static public WasteMqttClient getObserver(){
        return issuePubSub;
    }*/
}
