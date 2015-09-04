package org.fit.fraunhofer.almanac;

import com.google.gson.Gson;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.linksmart.data.DataManagementException;
import eu.linksmart.smartcity.issue.Ticket;
import eu.linksmart.smartcity.issue.TicketEvent;
import eu.linksmart.smartcity.issue.service.TicketManager;

import eu.linksmart.smartcity.issue.client.TicketAdapterClient;
import eu.linksmart.smartcity.issue.Issue;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Location;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Thing;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttReceivedMessage;
import org.geojson.GeoJsonObject;

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

     // To simulate a DF event with fill level above 70%
    public static final String SIMULATE_DF_TOPIC = "almanac/DF";
    public static final String SIMULATE_TICKET_TOPIC= "almanac/ticket";
    // Data Fusion query id: waste bins whose fill level has now surpassed 70%
    public static final String DF_WASTEBINFULL_TOPIC = "/+/+/+/cep/86918461073161120583227673852984066215228465530507767448056485667068884010202";
    // Data Fusion query id: toy bin has surpassed 80%
    //    public static final String DF_TOYBINFULL_TOPIC = "/+/+/+/cep/5296850124791908742793477709595434718264213518621513551279291212674474587702";
    // Data Fusion query id: waste bins whose fill level has now gotten less than 70%
    public static final String DF_WASTEBINEMPTY_TOPIC = "/+/+/+/cep/109674137994894161254362755448038878130087099950442517320455058187787869606540";

    public static final String TICKETING_EVENTS = "almanac/+/ticket";
//    public static final String CITIZENAPP_TOPIC = "almanac/citizenapp";  // issues coming from a CitizenApp

//    public static final String SMARTWASTE_TOPIC = "almanac/smartwaste/+";                     //all smartwaste one-level sub-topics
//    public static final String SMARTWASTE_CREATE_TOPIC = "almanac/smartwaste/create";
//    public static final String SMARTWASTE_UPDATE_TOPIC = "almanac/smartwaste/update";
//    public static final String SMARTWASTE_DUPLICATE_TOPIC = "almanac/smartwaste/duplicate";  // duplicate notification coming from OTRS
//    public static final String SMARTWASTE_ACCEPT_TOPIC = "almanac/smartwaste/accept";        // accept notification coming from OTRS

//    public static final String ISSUE = "issue/";
//    public static final String UPDATE = "/update";
//    public static final String DUPLICATE = "/duplicate";
//    public static final String SUBSCRIBE = "/subscribe";



//    private HashMap<String, Issue> issueMap;
//    private ArrayList<Issue> issueList;
    private ArrayList<Thing> fullBins;
    private HashMap<String, String> binIssueMap;   // waste bin-issue relation
    private HashMap<String, String> binTicketMap;  // waste bin-ticket relation
//    private HashMap<String, Route> routeMap;
//    private HashMap<String, Vehicle> vehicleMap;
    private ArrayList<Thing> thingList;
    private WasteMqttClient pubClient;
    private WasteMqttClient subClient;
    private WasteHttpClient wasteHttpClient;

    private TicketManager otrsTicketManager;

    private ExecutorService executor;


    public IssueManager(){
        issueMembersInit();

        executor = Executors.newCachedThreadPool();

        mqttClientsSetup();
        httpClientsSetup();
    }

    public IssueManager(ArrayList<Thing> fullBinsInVicinityList){

        issueMembersInit();

        executor = Executors.newCachedThreadPool();

        mqttClientsSetup();
        httpClientsSetup();

//         createTicket(fullBinsInVicinityList);  OTRS integration

        fullBins = new ArrayList<Thing>(fullBinsInVicinityList);

//        generateRoute("/almanac/route/initial");

        // this is the initial data read from the Resource Catalogue: full bins in the vicinity (within 100m radius) of a specific location
//        for (Thing thing : fullBinsInVicinityList) {
//            addIssue(Issue.Priority.UNCLASSIFIED,
//                     thing.getId(),
//                     ((org.geojson.Point) ((it.ismb.pertlab.ogc.sensorthings.api.datamodel.Location) thing.getLocations().toArray()[0]).getGeometry()).getCoordinates().getLatitude(),
//                     ((org.geojson.Point) ((it.ismb.pertlab.ogc.sensorthings.api.datamodel.Location) thing.getLocations().toArray()[0]).getGeometry()).getCoordinates().getLongitude());
//        }
    }

/*   public IssueManager(ArrayList<Thing> thingListMetadata){

        issueMap = new HashMap<String, Issue>();
        routeMap = new HashMap<String, Route>();
        vehicleMap = new HashMap<String, Vehicle>();

        executor = Executors.newCachedThreadPool();

        mqttClientsSetup();
        httpClientsSetup();


        // creates a map/list of issues with the same number of elements as in the metadata file
//        issueMap = new HashMap<String, Issue>();
        issueList = new ArrayList<Issue>();
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
    }
*/

    private void createTicket(ArrayList<Thing> fullBinsInVicinityList){
        // this is the initial data read from the Resource Catalogue: full bins in the vicinity (within 100m radius) of a specific location
//        eu.linksmart.smartcity.issue.Location location = new eu.linksmart.smartcity.issue.Location(0,0);

        for (Thing thing : fullBinsInVicinityList) {
            eu.linksmart.smartcity.issue.Location location = new eu.linksmart.smartcity.issue.Location(
                    ((org.geojson.Point)((it.ismb.pertlab.ogc.sensorthings.api.datamodel.Location)(thing.getLocations().toArray()[0])).getGeometry()).getCoordinates().getLatitude(),
                    ((org.geojson.Point)((it.ismb.pertlab.ogc.sensorthings.api.datamodel.Location)(thing.getLocations().toArray()[0])).getGeometry()).getCoordinates().getLongitude());

            eu.linksmart.smartcity.issue.Issue issue = new Issue(thing.getId(), location);

            try {
                String ticketId = otrsTicketManager.create(issue);

                binTicketMap.put(thing.getId(), ticketId);

            } catch (DataManagementException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private void createTicket(eu.linksmart.smartcity.issue.Issue issue){

        try {
            String ticketId = otrsTicketManager.create(issue);

            binTicketMap.put(issue.getResource(), ticketId);

        } catch (DataManagementException e) {
            System.err.println(e.getMessage());
        }
    }

    private void createTicket(String binId, double latitude, double longitude){
        eu.linksmart.smartcity.issue.Location location = new eu.linksmart.smartcity.issue.Location(latitude, longitude);
        eu.linksmart.smartcity.issue.Issue issue = new Issue(binId, location);

        try {
            String ticketId = otrsTicketManager.create(issue);

            binTicketMap.put(issue.getResource(), ticketId);

        } catch (DataManagementException e) {
            System.err.println(e.getMessage());
        }
    }


    private void issueMembersInit(){
//        issueMap = new HashMap<String, Issue>();
//        issueList = new ArrayList<Issue>();

        binIssueMap = new HashMap<String, String>();
        binTicketMap = new HashMap<String, String>();
//        routeMap = new HashMap<String, Route>();
//        vehicleMap = new HashMap<String, Vehicle>();

        otrsTicketManager = new TicketAdapterClient("http://almanac.fit.fraunhofer.de:8888/OTRS/Ticket");
    }

    private void mqttClientsSetup() {
        subClient = WasteMqttClient.getInstanceSub();

        subClient.subscribe(SIMULATE_DF_TOPIC);        // FIXME: simulation
        subClient.subscribe(SIMULATE_TICKET_TOPIC);    // FIXME: simulation

        subClient.subscribe(DF_WASTEBINFULL_TOPIC);
        subClient.subscribe(DF_WASTEBINEMPTY_TOPIC);

//        subClient.subscribe(CITIZENAPP_TOPIC);      // get notified by a CitizenApp about issues
//        subClient.subscribe(SMARTWASTE_TOPIC);      // get notified by the SmartWaste application

        subClient.addObserver(this);

        pubClient = WasteMqttClient.getInstancePub();
    }

    private void httpClientsSetup() {
        wasteHttpClient = WasteHttpClient.getInstance();
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
                case SIMULATE_DF_TOPIC:  //FIXME: simulation
                    System.out.println("Received simulated event from DF!");
                    Thing bin = new Thing();
                    bin.setId("6a2a241332749463701d4d9607c02bc903c8bea1");
                    bin.setDescription("Waste resource created by a Data Fusion event.");
                    bin.setMetadata("http://almanac-project.eu/ontologies/smartcity.owl#WasteBin");
                    Location location = new Location();
                    location.setTime(new Date());
                    GeoJsonObject geometry = new org.geojson.Point(7.665539,45.053309);

                    location.setGeometry(geometry);

                    HashSet<Location> ob = new HashSet<Location>();
                    ob.add(location);
                    bin.setLocations(ob);

                    fullBins.add(bin);

 /*                   executor.execute(new Runnable() {
                        public void run() {
                            System.out.println("SIMULATE_DF_TOPIC: fullBins size: " + fullBins.size() + "!");
                            generateRoute("/almanac/route");
                        }
                    });*/

                    System.out.println("SIMULATE_DF_TOPIC: fullBins size: " + fullBins.size() + "!");
                    generateRoute("/almanac/route");

                    fullBins.remove(7);
                    fullBins.remove(6);
                    break;

                case SIMULATE_TICKET_TOPIC:
                    // FIXME: create resource into SCRAL through SCRAL REST-API
                    // FIXME: put it into binTicketMap
                    // FIXME: use List<Ticket> selectTicketsInVicinity(String domain = WASTE, int distance, Location refLocation);
                    // to find out if this issue should be scheduled to the same existing route

//                String binId = getBinFromTicket(event);
//                Thing bin = wasteHttpClient.getBin(binId);
                    // For MeetIoT: hardcoding the CitizenApp Thing:
                    System.out.println("ACCEPTED received!");
                    Thing bin2 = new Thing();
                    bin2.setId("0b60ab7ad145ba4204ae541aa4d428d87f558c1e");
                    bin2.setDescription("Waste resource created by a CitizenApp issue.");
                    bin2.setMetadata("http://almanac-project.eu/ontologies/smartcity.owl#WasteBin");
                    Location location2 = new Location();
                    location2.setTime(new Date());
                    GeoJsonObject geometry2 = new org.geojson.Point(7.668163,45.048770);

                    location2.setGeometry(geometry2);

                    HashSet<Location> ob2 = new HashSet<Location>();
                    ob2.add(location2);
                    bin2.setLocations(ob2);


                    fullBins.add(bin2);

 /*                   executor.execute(new Runnable() {
                        public void run() {
                            System.out.println("SIMULATE_TICKET_TOPIC: fullBins size: " + fullBins.size() + "!");
                            generateRoute("/almanac/route");
                        }
                    });*/

                    System.out.println("SIMULATE_DF_TOPIC: fullBins size: " + fullBins.size() + "!");
                    generateRoute("/almanac/route");

                    break;

                case DF_WASTEBINFULL_TOPIC:
                    handleDFWastebinFull(data.Payload());
                    break;
                case DF_WASTEBINEMPTY_TOPIC:
                    handleDFWastebinEmpty(data.Payload());
                    break;
                case TICKETING_EVENTS:
                    handleTicketingEvent(data.Payload());
                    break;
/*                case CITIZENAPP_TOPIC:
                    handleCitizenApp(data.Payload());
                    break;
                case SMARTWASTE_CREATE_TOPIC:
                    break;
                case SMARTWASTE_UPDATE_TOPIC:
                    handleSmartWasteUpdate(data.Payload());
                    break;
                case SMARTWASTE_DUPLICATE_TOPIC:
                    handleSmartWasteDuplicate(data.Payload());
                    break;
                case SMARTWASTE_ACCEPT_TOPIC:
                    handleSmartWasteAccept(data.Payload());
                    break;
*/
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

    // The incoming message is Data Fusion related: it gives out all full waste bins in the neighborhood,
    // i.e. full bins within 100m radius from a given location. New issues are to be created out
    // of these observations. This will be the initial state of the BEWaste back-end. After reading this
    // in, issues will be created and the back-end will unsubscribe from the query.
 /*   private void handleDFInitAllFullBins(MqttMessage message){
        try{
            it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation obs[] =
                    mapper.readValue(message.getPayload(), it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation[].class);

            String binId;
            for (Observation observation : obs) {
                binId = observation.getResultValue().toString();
                org.geojson.LngLatAlt location = wasteHttpClient.getBinGeolocation(binId);

                // an issue will be created. The bin is full, so priority is critical
                addIssue(Issue.Priority.CRITICAL, binId, location.getLatitude(), location.getLongitude());
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
*/
    // The incoming message is Data Fusion related: a waste bin fill level has
    // surpassed the threshold and is full. A new ticket is to be created out
    // of this observation.
    private void handleDFWastebinFull(MqttMessage message){
        try{
            it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation obs =
                    mapper.readValue(message.getPayload(), it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation.class);

            // get the waste bin id: event result value
            String binId = obs.getResultValue().toString();

            // Now the resource catalogue can be used to find the specific waste bin holding the id and then get
            // its location (and bin type?), before a new issue can be created.

            System.out.println("A Data Fusion message has arrived. The waste bin " + binId + " is full!");

            Thing bin = wasteHttpClient.getBin(binId);
            org.geojson.LngLatAlt location = wasteHttpClient.getBinGeolocation(binId);
            System.out.println("***Full Bin: " + binId + " Latitude: " + location.getLatitude() + " Longitude: " + location.getLongitude());

            createTicket(binId, location.getLatitude(), location.getLongitude());

            fullBins.add(bin);

            executor.execute(new Runnable() {
                public void run() {
                    generateRoute("/almanac/route");
                }
            });
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    // The incoming message is Data Fusion related: a full waste bin has just been
    // emptied. The corresponding Ticket is to be deleted.
    private void handleDFWastebinEmpty(MqttMessage message){
        try{
            it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation obs =
                    mapper.readValue(message.getPayload(), it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation.class);

            // get the waste bin id: event result value
            String binId = obs.getResultValue().toString();

            System.out.println("A Data Fusion message has arrived. The waste bin " + binId + " has just been emptied!");

            org.geojson.LngLatAlt location = wasteHttpClient.getBinGeolocation(binId);
            System.out.println("***Empty Bin: " + binId + " Latitude: " + location.getLatitude() + " Longitude: " + location.getLongitude());

            if(binTicketMap.containsKey(binId)) {
                otrsTicketManager.delete(binTicketMap.get(binId));
            }
//            removeIssue(binIssueMap.get(binId));
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    // The incoming message is related to the Ticketing system: a ticket has been created,
    // deleted or updated. Payload has:
    // {"eventType":"", "ticketId":""} or
    // {"eventType":"", "property":"", "value":"", "ticketId":""} in case the eventType is "update".

    // 1) eventType can be "deleted", "updated", "created".
    // If the eventType's value is "updated", "property" tells what has been updated, "value" tells about the new property value.
    // "property" can be "status", "priority" or "time2completion".

    // 2) A ticket has been accepted by the city employee whenever its state
    // changes from NEW into ACCEPTED => CitizenApp can now confirm to the citizen that the ticket has been created (as opposed
    // to listening to a "created" message which has not been yet accepted by the city employee)

    // 3) A ticket has been rejected by the city employee whenever its state
    // changes from NEW into REJECTED => CitizenApp can now inform the citizen that the ticket has been rejected (as a duplicate?)
    private void handleTicketingEvent(MqttMessage message){

        Gson gsonObj = new Gson();

        String json = "";
        try {
            json = new String((byte[]) message.getPayload(), "UTF-8");
            TicketEvent event = gsonObj.fromJson(json, TicketEvent.class);
//            TicketEvent.print();

            switch(event.getEventType()){
                case CREATED:
                    handleCreatedEvent(event);
                    break;
                case DELETED:
                    handleDeletedEvent(event);
                    break;
                case UPDATED:
                    handleUpdatedEvent(event);
                    break;
            }
        }catch(UnsupportedEncodingException e) {
        }
    }

    private void handleUpdatedEvent(TicketEvent event){
        switch(event.getProperty()){
            case "STATUS":
                handleStatusUpdated(event);
                break;
            case "PRIORITY":
                break;
            case "TIME2COMPLETION":
                break;
        }
    }

    private void handleCreatedEvent(TicketEvent event){

    }

    private void handleDeletedEvent(TicketEvent event){
        for (Map.Entry<String, String> entry  : binTicketMap.entrySet()) {
            if (entry.getValue() == event.getTicketId()) {
                binTicketMap.remove(entry.getKey());
                break;
            }
        }
    }

    private void handleStatusUpdated(TicketEvent event){
        switch(event.getValue()){
            case "ACCEPTED":
                // FIXME: create resource into SCRAL through SCRAL REST-API
                // FIXME: put it into binTicketMap
                // FIXME: use List<Ticket> selectTicketsInVicinity(String domain = WASTE, int distance, Location refLocation);
                // to find out if this issue should be scheduled to the same existing route

//                String binId = getBinFromTicket(event);
//                Thing bin = wasteHttpClient.getBin(binId);
                // For MeetIoT: hardcoding the CitizenApp Thing:
                System.out.println("ACCEPTED received!");
                Thing bin = new Thing();
                bin.setId("0b60ab7ad145ba4204ae541aa4d428d87f558c1e");
                bin.setDescription("Waste resource created by a CitizenApp issue.");
                bin.setMetadata("http://almanac-project.eu/ontologies/smartcity.owl#WasteBin");
                Location location = new Location();
                location.setTime(new Date());
                GeoJsonObject geometry = new org.geojson.Point(45.048770, 7.668163);

                location.setGeometry(geometry);

                HashSet<Location> ob = new HashSet<Location>();
                ob.add(location);
                bin.setLocations(ob);


                fullBins.add(bin);

                executor.execute(new Runnable() {
                    public void run() {
                        generateRoute("/almanac/route");
                    }
                });

                break;
            case "REJECTED":
                binTicketMap.remove(getBinFromTicket(event));
                break;
        }
    }

    String getBinFromTicket(TicketEvent event){
        for (Map.Entry<String, String> entry  : binTicketMap.entrySet()) {
            if (entry.getValue() == event.getTicketId()) {
                return entry.getKey();
            }
        }
        return null;
    }

    // The incoming message is CitizenApp related: A new issue is to be created.
    // payload has issueId, geolocation, picture, name, and comment of a citizen's issue
 /*   private void handleCitizenApp(MqttMessage message){

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

            // A resource creation towards the ALMANAC platform needs to wait... since at this point it is
            // unknown if the issue is going to be accepted after its triage or if it is for example going
            // to be marked as a duplicate. The SmartWaste application needs still to publish an
            // "acceptance" of the issue. Only then an ALMANAC resource can be created.

        }catch(UnsupportedEncodingException e) {
        }
    }
*/
    // The incoming message is SmartWaste related: it tells about issues which have been updated
/*    private void handleSmartWasteUpdate(MqttMessage message){

        Gson gsonObj = new Gson();

        String json = "";
        try {
            json = new String((byte[]) message.getPayload(), "UTF-8");
            Issue issues[] = gsonObj.fromJson(json, Issue[].class);

            for (Issue issue : issues) {
                issue.update(issueMap.get(issue.id()));
            }

        }catch(UnsupportedEncodingException e) {
        }
    }
*/
    // The incoming message is SmartWaste related: it tells about an issue being duplicated
/*    private void handleSmartWasteDuplicate(MqttMessage message){

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
*/
    // The incoming message is SmartWaste related: it tells about an issue which has been accepted
 /*   private void handleSmartWasteAccept(MqttMessage message){

        Gson gsonObj = new Gson();

        String json = "";
        try {
            json = new String((byte[]) message.getPayload(), "UTF-8");
            Issue issue = gsonObj.fromJson(json, Issue.class);

            // An issue created implicitly by a CitizenApp has been accepted. Now it is about updating
            // its properties and creating an ALMANAC resource out of it.

            issue.update(issueMap.get(issue.id()));

            // FIXME: HERE Dario's API call for an ALMANAC resource creation missing!!!!

        }catch(UnsupportedEncodingException e) {
        }
    }
*/
/*    private void addIssue(){
        Issue issue = new Issue();
        issueMap.put(issue.id(), issue);
    }

    private void addIssue(String binId, double latitude, double longitude){
        if(!binIssueMap.containsKey(binId)) {
            Issue issue = new Issue(binId, latitude, longitude);
            issueMap.put(issue.id(), issue);

            binIssueMap.put(binId, issue.id());
        }
    }

    private void addIssue(String binId, double latitude, double longitude, String binType){
        if(!binIssueMap.containsKey(binId)) {
            Issue issue = new Issue(binId, latitude, longitude, binType);
            issueMap.put(issue.id(), issue);

            binIssueMap.put(binId, issue.id());
        }
    }

    private void addIssue(Issue.Priority priority, String binId, double latitude, double longitude){
        if(!binIssueMap.containsKey(binId)) { // create new issue on case it doesn't exist yet
            Issue issue = new Issue(priority, binId, latitude, longitude);
            issueMap.put(issue.id(), issue);

            binIssueMap.put(binId, issue.id());
        }else {  // in case the issue already exists, its priority is updated
            Issue issue = issueMap.get(binIssueMap.get(binId));
            issue.update(priority);
        }
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
        if(binIssueMap.containsValue(issueId)){
            binIssueMap.remove(issueMap.get(issueId).resource());
        }

        issueMap.remove(issueId);
    }
*/

    protected void generateRoute(String routeType){
        if(!fullBins.isEmpty()){

            ArrayList<RouteEndpoint> routeEndpointList = new ArrayList<RouteEndpoint>();

            for (Thing entry : fullBins){
                RouteEndpoint endpoint = new RouteEndpoint();

                endpoint.setId(entry.getId());
                endpoint.setGeoLocation(
                        ((org.geojson.Point) ((it.ismb.pertlab.ogc.sensorthings.api.datamodel.Location) (entry.getLocations().toArray()[0])).getGeometry()).getCoordinates().getLatitude(),
                        ((org.geojson.Point) ((it.ismb.pertlab.ogc.sensorthings.api.datamodel.Location) (entry.getLocations().toArray()[0])).getGeometry()).getCoordinates().getLongitude());

                routeEndpointList.add(endpoint);
            }
//            if(routeType.equals("/almanac/route/initial") && routeEndpointList.size() > MIN_ISSUECOUNT) {
//                routeEndpointList.remove(MIN_ISSUECOUNT);
//            }
            // route endpoints will be formatted to Json: [{"id":"<id>","geoLocation":{"latitude":0.0,"longitude":0.0}}]
            Gson gsonObj = new Gson();

            String issueGson = gsonObj.toJson(routeEndpointList);
//            System.out.println(gsonObj.toJson(routeEndpointsList));

            // now it's about publishing the route (more specifically, the geolocation of the issues contained in the route),
            // so that the DriverApp can listen to it, use Google DirectionsService to calculate the route and render it.
            pubClient.publish(routeType, issueGson);
            System.out.println("Route endpoints published under topic " + routeType);
        }
    }

/*    protected void generateRoute(String routeType){

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

    }
*/
/*    public Route addRoute(){
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
*/

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
