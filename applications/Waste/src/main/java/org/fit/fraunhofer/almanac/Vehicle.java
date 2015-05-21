package org.fit.fraunhofer.almanac;

import java.util.UUID;

/**
 * Created by Werner-Kytölä on 08.05.2015.
 */
public class Vehicle {
    private String id;
    private VehicleType type;
    private int capacity;
    private VehicleState state;
    private int fillLevel;  // current amount of waste (in kg) in the truck

    public enum VehicleType {
        NONE, ORGANIC, PLASTIC, GLASMETAL, PAPER, CLOTHING, WASTE
    }
    public enum VehicleState {
        AVAILABLE, BUSY, INSERVICE
    }

    public Vehicle(){
        uniqueID uId = new uniqueID();
        id = uId.generateUUID();
        if(!id.isEmpty()) {
            type = VehicleType.NONE;
            state = VehicleState.AVAILABLE;
            fillLevel = 0;
        }
    }

    public Vehicle(VehicleType Type){
        uniqueID uId = new uniqueID();
        id = uId.generateUUID();
        if(!id.isEmpty()) {
            type = Type;
            state = VehicleState.AVAILABLE;
            fillLevel = 0;
        }
    }
    public Vehicle(VehicleType Type, int Capacity){
        uniqueID uId = new uniqueID();
        id = uId.generateUUID();
        if(!id.isEmpty()) {
            type = Type;
            capacity = Capacity;
            state = VehicleState.AVAILABLE;
            fillLevel = 0;
        }
    }

    // Vehicle getters
    public String id() {
        return id;
    }
    public VehicleType type() {
        return type;
    }
    public double capacity() {
        return capacity;
    }
    public VehicleState state() {
        return state;
    }
    public int fillLevel() {
        return fillLevel;
    }

    // update methods
    public void update(int fillLevel){
        this.fillLevel += fillLevel;
    }

    public void update(VehicleState state){
        this.state = state;
    }
}
