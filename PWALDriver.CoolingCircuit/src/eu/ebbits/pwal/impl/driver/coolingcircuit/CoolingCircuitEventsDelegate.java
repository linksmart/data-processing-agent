package eu.ebbits.pwal.impl.driver.coolingcircuit;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;

import eu.ebbits.pwal.impl.driver.PWALEventsDelegateImpl;

/**
 * Events delegate. CoolingCircuitDriver does not currently support PWALEvents.
 * 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author     FIT
 * @version    %I%, %G%
 * @see        eu.ebbits.pwal.api.driver.PWALDriver
 */

public class CoolingCircuitEventsDelegate extends PWALEventsDelegateImpl {
    private static final String TOPIC = "pwal/coolingcircuit/";

    /**
     * Constructor of the <code>CoolingCircuitEventsDelegate</code>
     * 
     * @param driver - driver that uses the Events Delegate
     * 
     */
    public CoolingCircuitEventsDelegate(CoolingCircuitDriverImpl driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void init(ComponentContext context) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updatePWALEventsCollection() {
        Dictionary<String, String> properties = new Hashtable<String, String>();
        properties.put("temperature", "float");
        String temperatureTopic = TOPIC+"temperature";
        Event temperatureEvent = new Event(temperatureTopic, properties);
        this.registerEvent(temperatureTopic, temperatureEvent);
        
        properties = new Hashtable<String, String>();
        properties.put("waterFlow", "float");
        String waterFlowTopic = TOPIC+"waterflow";
        Event waterFlowEvent = new Event(waterFlowTopic, properties);
        this.registerEvent(waterFlowTopic, waterFlowEvent);
    }
}
