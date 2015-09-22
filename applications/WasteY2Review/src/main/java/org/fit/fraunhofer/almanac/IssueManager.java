package org.fit.fraunhofer.almanac;

import com.google.gson.Gson;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.linksmart.data.DataManagementException;
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
    public static final String DF_WASTEBINFULL_CEP_TOPIC = "cep/86918461073161120583227673852984066215228465530507767448056485667068884010202";
    // Data Fusion query id: toy bin has surpassed 70%
    //    public static final String DF_TOYBINFULL_TOPIC = "/+/+/+/cep/5296850124791908742793477709595434718264213518621513551279291212674474587702";
    // Data Fusion query id: waste bins whose fill level has now gotten less than 70%
    public static final String DF_WASTEBINEMPTY_TOPIC = "/+/+/+/cep/109674137994894161254362755448038878130087099950442517320455058187787869606540";

    public static final String TICKETING_EVENTS = "almanac/+/ticket";


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

        createTicket(fullBinsInVicinityList);

        fullBins = new ArrayList<Thing>(fullBinsInVicinityList);

    }

    private void createTicket(ArrayList<Thing> fullBinsInVicinityList){
        // this is the initial data read from the Resource Catalogue: full bins in the vicinity (within 100m radius) of a specific location

        for (Thing thing : fullBinsInVicinityList) {
            eu.linksmart.smartcity.issue.Location location = new eu.linksmart.smartcity.issue.Location(
                    ((org.geojson.Point)((it.ismb.pertlab.ogc.sensorthings.api.datamodel.Location)(thing.getLocations().toArray()[0])).getGeometry()).getCoordinates().getLatitude(),
                    ((org.geojson.Point)((it.ismb.pertlab.ogc.sensorthings.api.datamodel.Location)(thing.getLocations().toArray()[0])).getGeometry()).getCoordinates().getLongitude());

            eu.linksmart.smartcity.issue.Issue issue = new Issue("BeWaste",
                                                                 "Ticket created from BeWaste: initial endpoints in vicinity.",
                                                                 "Waste issue.");
            issue.setResource(thing.getId());
            issue.setLocation(location);

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

    private void createTicket(String label, String binId, double latitude, double longitude){
        eu.linksmart.smartcity.issue.Location location = new eu.linksmart.smartcity.issue.Location(latitude, longitude);

        eu.linksmart.smartcity.issue.Issue issue = new Issue("BeWaste", label, "Waste issue.");

        issue.setResource(binId);
        issue.setLocation(location);

        try {
            String ticketId = otrsTicketManager.create(issue);

            binTicketMap.put(issue.getResource(), ticketId);

        } catch (DataManagementException e) {
            System.err.println(e.getMessage());
        }
    }


    private void issueMembersInit(){

        binIssueMap = new HashMap<String, String>();
        binTicketMap = new HashMap<String, String>();
//        routeMap = new HashMap<String, Route>();
//        vehicleMap = new HashMap<String, Vehicle>();

        otrsTicketManager = new TicketAdapterClient("http://almanac.fit.fraunhofer.de:8888/OTRS/Ticket");
    }

    private void mqttClientsSetup() {
        subClient = WasteMqttClient.getInstanceSub();

//        subClient.subscribe(SIMULATE_DF_TOPIC);        // FIXME: simulation
//        subClient.subscribe(SIMULATE_TICKET_TOPIC);    // FIXME: simulation

        subClient.subscribe(DF_WASTEBINFULL_TOPIC);
//        subClient.subscribe(DF_WASTEBINEMPTY_TOPIC); // Not relevant for MeetIoT
        subClient.subscribe(TICKETING_EVENTS);

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

            System.out.println("| Topic at callback update:" + data.Topic());

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

                    System.out.println("SIMULATE_DF_TOPIC: fullBins size: " + fullBins.size() + "!");
                    generateRoute("/almanac/route");

                    break;

/*                case DF_WASTEBINFULL_TOPIC:  // when we change DF_WASTEBINFULL_TOPIC to be a full match: VL related (mqtt over WebSockets)
                    handleDFWastebinFull(data.Payload());
                    break;*/
                case DF_WASTEBINEMPTY_TOPIC:
                    handleDFWastebinEmpty(data.Payload());
                    break;
                default:
                    // TICKETING_EVENTS
                    if(data.Topic().matches("almanac/.+/ticket")){  // "almanac/" followed by anything(client id) followed by "/ticket"
                        handleTicketingEvent(data.Payload());
                    }else {
                        // DF_WASTEBINFULL_TOPIC
                        if(data.Topic().matches("/.+/.+/.+/" + DF_WASTEBINFULL_CEP_TOPIC)){
                            handleDFWastebinFull(data.Payload());
                        }
                    }

                    break;
            }
        }catch(Exception e) {
                e.printStackTrace();
        }
    }

    // The incoming message is Data Fusion related: a waste bin fill level has
    // surpassed the threshold and is full. A new ticket is to be created out
    // of this observation.
    private void handleDFWastebinFull(MqttMessage message){
        try{
            if (message.getPayload().length != 0) {
                it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation obs =
                        mapper.readValue(message.getPayload(), it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation.class);

                // get the waste bin id: event result value
                String binId = obs.getResultValue().toString();

                System.out.println("A Data Fusion message has arrived. The waste bin " + binId + " is full!");

                // Now the resource catalogue can be used to find the specific waste bin holding the id and then get
                // its location (and bin type?), before a new issue can be created.

                // This is the toy bin, the only one which matters for MeetIoT demo
                if( (fullBins.size() == 7) &&
                    binId.compareTo("9ad30f72cae63d40d3f81ab6d6a501889455e1c07d6ba88390a443023ce1c2b5") == 0) {
//            Thing bin = wasteHttpClient.getBin(binId);
//            org.geojson.LngLatAlt location = wasteHttpClient.getBinGeolocation(binId);
                    // For MeetIoT: hardcoding the DF related Thing:
                    Thing bin = new Thing();
//                bin.setId("6a2a241332749463701d4d9607c02bc903c8bea1");
                    bin.setId(binId);
                    bin.setDescription("Waste resource created by a Data Fusion event.");
                    bin.setMetadata("http://almanac-project.eu/ontologies/smartcity.owl#WasteBin");
                    Location location = new Location();
                    location.setTime(new Date());
                    GeoJsonObject geometry = new org.geojson.Point(7.665539, 45.053309);

                    location.setGeometry(geometry);

                    HashSet<Location> ob = new HashSet<Location>();
                    ob.add(location);
                    bin.setLocations(ob);

                    fullBins.add(bin);

                    System.out.println("DF event: fullBins size: " + fullBins.size() + "!");
                    generateRoute("/almanac/route");

                    fullBins.remove(7);
                    fullBins.remove(6);

//            System.out.println("***Full Bin: " + binId + " Latitude: " + location.getLatitude() + " Longitude: " + location.getLongitude());
                    System.out.println("***Full Bin: " + binId +
                            " Latitude: " + ((org.geojson.Point) (location.getGeometry())).getCoordinates().getLatitude() +
                            " Longitude: " + ((org.geojson.Point) (location.getGeometry())).getCoordinates().getLongitude());

//            createTicket("Ticket created from BeWaste: Data Fusion waste bin full.",
//                         binId, location.getLatitude(), location.getLongitude());
                    createTicket("Ticket created from BeWaste: Data Fusion waste bin full.",
                            binId, ((org.geojson.Point) (location.getGeometry())).getCoordinates().getLatitude(), ((org.geojson.Point) (location.getGeometry())).getCoordinates().getLongitude());
                }
            }
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
            case "status":
                handleStatusUpdated(event);
                break;
            case "priority":
                break;
            case "time2completion":
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
                GeoJsonObject geometry = new org.geojson.Point(7.668163, 45.048770);

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
