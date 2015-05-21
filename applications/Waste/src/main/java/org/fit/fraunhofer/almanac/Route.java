package org.fit.fraunhofer.almanac;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Werner-Kyt�l� on 08.05.2015.
 */
public class Route {
    private String id = null;
    private RouteType type;
    private OptimizationCriteria criterion;
    private double capacity;
    private RouteState state;
    private Vehicle assignee;
    private ArrayList<String> collectionPoints; // list of ids of issues which belong to this route, ordered
                                                // by the first to the last location to be visited


    public enum RouteType {
        NONE, ORGANIC, PLASTIC, GLASMETAL, PAPER, CLOTHING, WASTE
    }
    public enum OptimizationCriteria{ // criteria after which a route can be generated
        NONE
    }
    public enum RouteState { // a route is active when it has been already started, otherwise it is inactive
        ACTIVE, INACTIVE
    }

    public Route(){
        uniqueID uId = new uniqueID();
        id = uId.generateUUID();
        if(!id.isEmpty()) {
            type = RouteType.NONE;
            criterion = OptimizationCriteria.NONE;
            state = RouteState.INACTIVE;
            collectionPoints = new ArrayList();
        }
    }

    public Route(RouteType type){
        uniqueID uId = new uniqueID();
        id = uId.generateUUID();
        if(!id.isEmpty()) {
            this.type = type;
            criterion = OptimizationCriteria.NONE;
            state = RouteState.INACTIVE;
            collectionPoints = new ArrayList();
        }
    }

    public Route(OptimizationCriteria criterion){
        uniqueID uId = new uniqueID();
        id = uId.generateUUID();
        if(!id.isEmpty()) {
            type = RouteType.NONE;
            this.criterion = criterion;
            state = RouteState.INACTIVE;
            collectionPoints = new ArrayList();
        }
    }

    public Route(RouteType type, OptimizationCriteria criteria){
        uniqueID uId = new uniqueID();
        id = uId.generateUUID();
        if(!id.isEmpty()) {
            this.type = type;
            this.criterion = criterion;
            state = RouteState.INACTIVE;
            collectionPoints = new ArrayList();
        }
    }

    public void generateRoute(HashMap<String, IssueManagement.Issue> issueMap){
        //  issueMap contains all issues with its geolocations. We'll generate
        // a route out of all the issues by using Google-API giving the issue's
        // geolocation as input... This is to be refined later on!

        // to be implemented!! At the end, this.collectionPoints will contain an ordered array of issue ids

    }

    // Route getters
    public String id(){ return id; }
    public RouteType type(){
        return type;
    }
    public OptimizationCriteria criterion(){
        return criterion;
    }
    public double capacity(){
        return capacity;
    }
    public RouteState state(){
        return state;
    }
    public Vehicle assignee(){
        return assignee;
    }
    public ArrayList<String> collectionPoints() { return collectionPoints; }

    // Route setters
    public void update(RouteType type){
        this.type = type;
    }
    public void update(OptimizationCriteria criterion){
        this.criterion = criterion;
    }
    public void update(double capacity){
        this.capacity = capacity;
    }
    public void update(RouteState state){
        this.state = state;
    }
    public void update(Vehicle assignee){
        this.assignee = assignee;
    }
}
