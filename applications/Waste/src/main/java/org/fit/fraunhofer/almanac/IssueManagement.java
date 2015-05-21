package org.fit.fraunhofer.almanac;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.util.*;


/**
 * Created by Werner-Kyt�l� on 08.05.2015.
 */
public class IssueManagement implements Observer{

    /***************** CONSTANTS */
    public static final int MIN_ISSUECOUNT = 10; // number of issues created until a route generation is triggered


    private HashMap<String, Issue> issueMap;
    private HashMap<String, Route> routeMap;
    private HashMap<String, Vehicle> vehicleMap;
    private static WasteMqttClient issuePubSub;


    public enum State {
        OPEN, SCHEDULED, DONE, CLOSED
    }

    public enum Priority {
        MINOR, MAJOR, CRITICAL
    }

    public enum IssueType {
        NONE, ORGANIC, PLASTIC, GLASMETAL, PAPER, CLOTHING, WASTE
    }


    public IssueManagement(WasteMqttClient wasteMqttClient){
        issueMap = new HashMap();
        routeMap = new HashMap();
        vehicleMap = new HashMap();

        wasteMqttClient.addObserver(this);
        issuePubSub = wasteMqttClient;
    }
    public IssueManagement(WasteMqttClient wasteMqttClient, int count){
        // creates a map with a number (count) of issues
        issueMap = new HashMap(count);
        routeMap = new HashMap();
        vehicleMap = new HashMap();

        wasteMqttClient.addObserver(this);
        issuePubSub = wasteMqttClient;

        Issue issue;;

        for (int i = 0 ; i < count ; i++) {
            addIssue();
        }
    }

    // This callback is invoked whenever a Data Fusion message comes in saying that a
    // waste bin fill level has surpassed the threshold and is full. A new issue is
    // to be created out of this event.
    @Override
    public void update(Observable observable, Object arg) {

        Gson gson = new Gson();
        JsonParser parser = new JsonParser();

        JsonArray array = parser.parse(arg.toString()).getAsJsonArray();

        String binId = gson.fromJson(array.get(0), String.class);
        double latitude = gson.fromJson(array.get(1), double.class);
        double longitude = gson.fromJson(array.get(1), double.class);
        String binType = gson.fromJson(array.get(2), String.class);
        System.out.printf("Using Gson.fromJson() to get: %s, %.3f, %.3f, %s", binId, latitude, longitude, binType);

        addIssue(binId, latitude, longitude, binType);  // need to give the location here to add issue
    }

    private void addIssue(){
        Issue issue = new Issue();
        issueMap.put(issue.id(), issue);

        // Route generation starts automatically after at least 10 issues have been created
//        if(issueMap.size() >= MIN_ISSUECOUNT){
        if(issueMap.size() >= 2){
                generateRoute();
        }
    }

    private void addIssue(String binId, double latitude, double longitude, String binType){
        Issue issue = new Issue(binId, latitude, longitude, binType);
        issueMap.put(issue.id(), issue);

        // Route generation/re-generation starts automatically after at least 10 issues have been created
//        if(issueMap.size() >= MIN_ISSUECOUNT){
        if(issueMap.size() >= 2){
            generateRoute();
        }
    }

    private void generateRoute(){
        Route route = addRoute();

    //    route.generateRoute(issueMap);  // this is simplified: In the first go, the route will be generated out
                                        // of all issues. It is assumed that all issues will belong to the same route.


        if(!issueMap.isEmpty()) {
            ArrayList<RouteEndpoints> routeEndpointsList = new ArrayList();
            RouteEndpoints aux = new RouteEndpoints();
            for (Map.Entry<String, Issue> entry : issueMap.entrySet()) {
                Issue values = entry.getValue();

                aux.id(values.id());
                aux.geoLocation(values.latitude(), values.longitude());

                routeEndpointsList.add(aux);
            }

            // route endpoints will be formatted to Json: [{"id":"<id>","geoLocation":{"latitude":0.0,"longitude":0.0}},{
            Gson gsonObj = new Gson();

            String issueGson = gsonObj.toJson(routeEndpointsList);
//            System.out.println(gsonObj.toJson(routeEndpointsList));

            //    issueMap = g.fromJson(str,HashMap.class);

            // now it's about publishing the route (more specifically, the geolocation of the issues contained in the route),
            // so that the Driver-App can listen to it, use Google DirectionsService to calculate the route and render it.

            issuePubSub.publish("route", issueGson);
        }
    }

    public void deleteIssue(String ident){
        if(issueMap.containsKey(ident)) {
            issueMap.remove(ident);
        }
    }

 /*   public void updateIssue(String ident, Vehicle assignee, State state, Date etc, Priority priority){
        if(!issueMap.isEmpty() ) {
            if(ident != null) {
                issueMap.get(ident).update(assignee);

                if (state != null) {
                    issueMap.get(ident).update(state);
                }
                if (etc != null) {
                    issueMap.get(ident).update(etc);
                }
                if (priority != null) {
                    issueMap.get(ident).update(priority);
                }
            }
        }
    }*/

    public void updateIssue(String ident, Vehicle assignee){
        if(!issueMap.isEmpty() ) {
            if(ident != null) {
                issueMap.get(ident).update(assignee);
            }
        }
    }

    public void updateIssue(String ident, State state){
        if(!issueMap.isEmpty() ) {
            if(ident != null) {
                issueMap.get(ident).update(state);
            }
        }
    }

    public void updateIssue(String ident, Date etc){
        if(!issueMap.isEmpty() ) {
            if(ident != null) {
                issueMap.get(ident).update(etc);
            }
        }
    }

    public void updateIssue(String ident, Priority priority){
        if(!issueMap.isEmpty() ) {
            if(ident != null) {
                issueMap.get(ident).update(priority);
            }
        }
    }

    public Issue findIssue(String id){
        if(!issueMap.isEmpty() ) {
            return issueMap.get(id);
        }
        return null;
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

    public void print(){
        // Iterate over all issues, using the keySet method.
        for(String key: issueMap.keySet()) {
            System.out.println(key + " -  UUID " + issueMap.get(key).id());
            System.out.println(key + " -  Creation date " + (issueMap.get(key)).creationDate());
            System.out.println(key + " -  Creator " + (issueMap.get(key)).creator());
            System.out.println(key + " -  Assignee " + (issueMap.get(key)).assignee());
            System.out.println(key + " -  State " + (issueMap.get(key)).state());
            System.out.println(key + " -  Estimated Time " + (issueMap.get(key)).estimatedTime());
            System.out.println(key + " -  Priority " + (issueMap.get(key)).priority());
            System.out.println(key + " -  Geolocation " + (issueMap.get(key)).geoLocation());
        }
        System.out.println();
    }

    static public WasteMqttClient getObserver(){
        return issuePubSub;
    }

/*    public class RouteEndpoints {
        private String id;
        private Location geoLocation;

        RouteEndpoints(){
            geoLocation = new Location();
        }
    }
*/
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
            uniqueID uId = new uniqueID();
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
            uniqueID uId = new uniqueID();
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

        public Issue(String binId, double latitude, double longitude,  String binType) {
            uniqueID uId = new uniqueID();
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
