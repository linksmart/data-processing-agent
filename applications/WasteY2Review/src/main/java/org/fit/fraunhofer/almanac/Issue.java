package org.fit.fraunhofer.almanac;

import java.util.Date;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Werner-Kytölä on 26.06.2015.
 */
//public class Issue extends Observable {
public class Issue {
    protected String id;
    protected Date creationDate;
    protected short creator;
    protected String resource; // in case of a waste issue, it points to the waste bin it relates to
    protected IssueType type;
    protected Vehicle assignee;
    protected State state;
    protected Date etc;  // estimated time to completion
    protected Priority priority;
    protected Location geoLocation;

    // an issue coming from the CitizenApp will have name, clientId, and picture:
    private String name;
    private String comment;
    private byte[] pic;


    public enum State {
        OPEN, SCHEDULED, DONE, CLOSED
    }

    public enum Priority {
        MINOR, MAJOR, CRITICAL
    }

    public enum IssueType {
        NONE, ORGANIC, PLASTIC, GLASMETAL, PAPER, CLOTHING, WASTE
    }

    public Issue(){
        UniqueId uId = new UniqueId();
        id = uId.generateUUID();
        if(!id.isEmpty()) {
            creationDate = new Date();
            state = State.OPEN;
            priority = Priority.MINOR;
            type = IssueType.NONE;
            geoLocation = new Location();
        }
    }

    public Issue(PicIssue picIssue){
        id = picIssue.id();
        if(!id.isEmpty()) {
            geoLocation = new Location(picIssue.latitude(), picIssue.longitude());
            pic = picIssue.pic();
            name = picIssue.name();
            comment = picIssue.comment();

            creationDate = new Date();
            state = State.OPEN;
            priority = Priority.MINOR;
            type = IssueType.NONE;
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
//            type = getType(binType);       this can be specified later on, not relevant for IoT demo
            resource = binId;
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
//            type = getType(binType);       this can be specified later on, not relevant for IoT demo
            resource = binId;
        }
    }

    public Issue(String id, String name, String comment, double latitude, double longitude, byte[] pic){
        this.id = id;
        if(!id.isEmpty()) {
            this.name = name;
            this.comment = comment;
            creationDate = new Date();
            state = State.OPEN;
            priority = Priority.MINOR;
            geoLocation = new Location(latitude, longitude);
            this.pic = pic;
        }
    }

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
    protected State state(){ return state; }
    protected Date estimatedTime(){ return etc; }
    protected Priority priority(){ return priority; }
    public Location geoLocation(){ return geoLocation; }
    public double latitude() {return geoLocation().latitude(); }
    public double longitude() {return geoLocation().longitude(); }
    public String name(){ return name; }
    public String comment(){ return comment; }
    public byte[] pic(){ return pic; }


    protected void update(Vehicle assignee){
        this.assignee = assignee;
    }

    protected void update(State state){
        if(this.state != state) {
            State previous = this.state;
            this.state = state;

            publishUpdate("The issue state has been updated from " + previous.toString() + " to " + state.toString());
        }
    }

    protected void update(Date etc){
        if(this.etc != etc){
            Date previous = this.etc;
            this.etc = etc;

            publishUpdate("The issue ETC has been updated from " + previous.toString() + " to " + etc.toString());
        }
    }

    protected void update(Priority priority){
        if(this.priority != priority){
            Priority previous = this.priority;
            this.priority = priority;

            publishUpdate("The issue priority has been updated from " + previous.toString() + " to " + priority.toString());
        }
    }

    private void publishUpdate(String message){
        // will publish to topic "issue/<issueId>/update" to which the client has subscribed to get updates
        String topic = IssueManager.ISSUE + id + IssueManager.UPDATE;
        WasteMqttClient.getInstancePub().publish(topic, message);
    }

    public void print(){
        System.out.println("---------------------");
        System.out.println("id: " + id);
        System.out.println("name: " + name);
        System.out.println("comment: " + comment);
        System.out.println("latitude " + geoLocation.latitude());
        System.out.println("longitude: " + geoLocation.longitude());
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

        public void update(double latitude, double longitude){
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}
