package org.fit.fraunhofer.almanac;

import com.google.gson.Gson;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Thing;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttReceivedMessage;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by Werner-Kyt�l� on 08.05.2015.
 */
public class IssueManagement implements Observer{

    /***************** CONSTANTS */
    public static final int MIN_ISSUECOUNT = 7; // number of issues created until a route generation is triggered


//    private HashMap<String, Issue> issueMap;
    private ArrayList<Issue> issueList;
    private HashMap<String, Route> routeMap;
    private HashMap<String, Vehicle> vehicleMap;
    private ArrayList<Thing> thingList;
    private static WasteMqttClient issuePubSub;

    private ExecutorService executor;

    public enum State {
        OPEN, SCHEDULED, DONE, CLOSED
    }

    public enum Priority {
        MINOR, MAJOR, CRITICAL
    }

    public enum IssueType {
        NONE, ORGANIC, PLASTIC, GLASMETAL, PAPER, CLOTHING, WASTE
    }


    public IssueManagement(WasteMqttClient  wasteMqttClient){
//        issueMap = new HashMap<String, Issue>();
        issueList = new ArrayList<Issue>();
        routeMap = new HashMap<String, Route>();
        vehicleMap = new HashMap<String, Vehicle>();

        wasteMqttClient.addObserver(this);
        issuePubSub = wasteMqttClient;
    }
    public IssueManagement(WasteMqttClient wasteMqttClient, int count){
        // creates a map with a number count of issues
//        issueMap = new HashMap<String, Issue>(count);
        issueList = new ArrayList<Issue>(count);
        routeMap = new HashMap<String, Route>();
        vehicleMap = new HashMap<String, Vehicle>();

        wasteMqttClient.addObserver(this);
        issuePubSub = wasteMqttClient;

        Issue issue;;

        for (int i = 0 ; i < count ; i++) {
            addIssue();
        }
    }
    public IssueManagement(WasteMqttClient wasteMqttClient, ArrayList<Thing> thingListMetadata){
        // creates a map/list of issues with the same number of elements as in the metadata file
//        issueMap = new HashMap<String, Issue>();
        issueList = new ArrayList<Issue>();
        routeMap = new HashMap<String, Route>();
        vehicleMap = new HashMap<String, Vehicle>();
        thingList = new ArrayList<Thing>();
        thingList.addAll(thingListMetadata);

        executor = Executors.newCachedThreadPool();

        wasteMqttClient.addObserver(this);
        issuePubSub = wasteMqttClient;

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
    }

    private ObjectMapper mapper = new ObjectMapper();

    // This callback is invoked whenever a Data Fusion message comes in saying that a
    // waste bin fill level has surpassed the threshold and is full. A new issue is
    // to be created out of this observation.
    @Override
    public void update(Observable observable, Object arg) {

        try{
            MqttReceivedMessage data = (MqttReceivedMessage)arg;
            it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation obs =
                    mapper.readValue(data.getPayload(), it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation.class);

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

    private void addIssue(){
        Issue issue = new Issue();
//        issueMap.put(issue.id(), issue);
        issueList.add(issue);
    }

    private void addIssue(String binId, double latitude, double longitude){
        Issue issue = new Issue(binId, latitude, longitude);
//        issueMap.put(issue.id(), issue);
        issueList.add(issue);
    }

    private void addIssue(String binId, double latitude, double longitude, String binType){
        Issue issue = new Issue(binId, latitude, longitude, binType);
//        issueMap.put(issue.id(), issue);
        issueList.add(issue);
    }

    protected void generateRoute(String routeType){
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
            // route endpoints will be formatted to Json: [{"id":"<id>","geoLocation":{"latitude":0.0,"longitude":0.0}},{
            Gson gsonObj = new Gson();

            String issueGson = gsonObj.toJson(routeEndpointList);
//            System.out.println(gsonObj.toJson(routeEndpointsList));

            // now it's about publishing the route (more specifically, the geolocation of the issues contained in the route),
            // so that the Driver-App can listen to it, use Google DirectionsService to calculate the route and render it.
            issuePubSub.publish(routeType, issueGson);
            System.out.println("Route endpoints published under topic " + routeType);
        }
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
    static public WasteMqttClient getObserver(){
        return issuePubSub;
    }

    public class Location{
        private double latitude;
        private double longitude;

        public Location() {
            this.latitude = 0.0;
            this.longitude = 0.0;
        }
        public Location(double latitude, double longitude){
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double latitude() {
            return latitude;
        }
        public double longitude() {
            return longitude;
        }
    }

    public class Issue extends Observable {
        private String id;
        private Date creationDate;
        private short creator;
        private String resource; // in case of a waste issue, it points to the waste bin it relates to
        private IssueType type;
        private Vehicle assignee;
        private State state;
        private Date etc;  // estimated time to completion
        private Priority priority;
        private Location geoLocation;
     //   private Picture picFile;


        public Issue(){
            UniqueId uId = new UniqueId();
            id = uId.generateUUID();
            if(!id.isEmpty()) {
                creationDate = new Date();
                state = State.OPEN;
                priority = Priority.MINOR;
                type = IssueType.NONE;
                geoLocation = new Location();

                hookToObserver();
            }
        }


        public Issue(Location location){
            UniqueId uId = new UniqueId();
            id = uId.generateUUID();
            if(!id.isEmpty()) {
                creationDate = new Date();
                state = State.OPEN;
                priority = Priority.MINOR;
                geoLocation = new Location();
                geoLocation = location;
                type = IssueType.NONE;

                hookToObserver();
            }
        }

        public Issue(String binId, double latitude, double longitude) {
            UniqueId uId = new UniqueId();
            id = uId.generateUUID();
            if(!id.isEmpty()) {
                creationDate = new Date();
                state = State.OPEN;
                priority = Priority.MINOR;
                geoLocation = new Location(latitude, longitude);
                //               type = getType(binType);       this can be specified later on, not relevant for IoT demo
                resource = binId;

                hookToObserver();
            }
        }

        public Issue(String binId, double latitude, double longitude,  String binType) {
            UniqueId uId = new UniqueId();
            id = uId.generateUUID();
            if(!id.isEmpty()) {
                creationDate = new Date();
                state = State.OPEN;
                priority = Priority.MINOR;
                geoLocation = new Location(latitude, longitude);
 //               type = getType(binType);       this can be specified later on, not relevant for IoT demo
                resource = binId;

                hookToObserver();
            }
        }

        private void hookToObserver(){
            this.addObserver(IssueManagement.getObserver());
        }

//        public Issue(Location geoLocation, Picture picture){
//        }

        public String id(){
            return id;
        }
        protected Date creationDate(){
            return creationDate;
        }
        protected short creator(){
            return creator;
        }
        protected String resource(){ return resource; }
        protected IssueType type(){ return type; }
        protected Vehicle assignee(){return assignee; }
        protected State state(){
            return state;
        }
        protected Date estimatedTime(){
            return etc;
        }
        protected Priority priority(){
            return priority;
        }
        public Location geoLocation(){
            return geoLocation;
        }
        public double latitude() {return geoLocation().latitude(); }
        public double longitude() {return geoLocation().longitude(); }


        protected void update(Vehicle assignee){
            this.assignee = assignee;
        }

        protected void update(State state){
            if(this.state != state) {
                this.state = state;

                setChanged();
                notifyObservers("The issue state has been updated to " + state.toString());
                // clearChanged(); // called automatically by notifyObservers()
            }
        }

        protected void update(Date etc){
            if(this.etc != etc){
                this.etc = etc;

                setChanged();
                notifyObservers("The issue ETC has been updated to " + etc.toString());
            }
        }

        protected void update(Priority priority){
            if(this.priority != priority){
                this.priority = priority;

                setChanged();
                notifyObservers("The issue priority has been updated to " + priority.toString());
            }
        }
    }
}
