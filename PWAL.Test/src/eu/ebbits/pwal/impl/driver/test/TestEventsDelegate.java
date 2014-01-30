package eu.ebbits.pwal.impl.driver.test;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.service.component.ComponentContext;

import org.osgi.service.event.Event;

import eu.ebbits.pwal.api.annotations.PWALServiceAnnotation;
import eu.ebbits.pwal.api.model.PWALControlEvent;
import eu.ebbits.pwal.impl.driver.PWALEventsDelegateImpl;

/**
 * 
 * This is the events delegate of the DummyDriver. It uses EventAdmin to expose the events.
 * 
 * This package is imagined as part of the new ebbits DDK: it will thus undergo significant modifications in the ebbits iterative process. 
 * it will be thus documented during the final release after all needed adapters are available.
 * 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author     ISMB
 * @version    %I%, %G%
 * @see        eu.ebbits.pwal.impl.driver.PWALDriverImpl
 * @since      PWAL 0.2.0
 */

public class TestEventsDelegate extends PWALEventsDelegateImpl {
    private final String topic = "pwal/coolingcircuit/";
    
    public TestEventsDelegate(TestDriver dummyDriver) {
        super(dummyDriver);
    }

    /**
     * Method used to generate debug events
     */
    public void my_gen_debug() {
        this.signalDebugEvent(new PWALControlEvent(PWALControlEvent.ControlEventType.DEFAULT_DEBUG));
        
    }
    
    /**
     * Method used to generate info events
     */
    public void my_gen_info() {
        this.signalInfoEvent(new PWALControlEvent(PWALControlEvent.ControlEventType.DEFAULT_INFO));
        
    }

    /**
     * Method used to generate warning events
     */
    public void my_gen_warning() {
        this.signalWarningEvent(new PWALControlEvent(PWALControlEvent.ControlEventType.DEFAULT_WARNING));
        
    }

    /**
     * Method to generate error events
     */
    public void my_gen_error() {
        this.signalErrorEvent(new PWALControlEvent(PWALControlEvent.ControlEventType.DEFAULT_ERROR));
        
    }
    
    /**
     * Method to generate critical events
     */
    public void my_gen_critical() {
        this.signalCriticalEvent(new PWALControlEvent(PWALControlEvent.ControlEventType.DEFAULT_CRITICAL));
        
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updatePWALEventsCollection() {
        Dictionary<String, String> properties = new Hashtable<String, String>();
        properties.put("report", String.class.getName());
        Event reportEvent = new Event("pwal/llrpreader/readreport", properties);
        this.registerEvent("pwal/llrpreader/readreport",reportEvent);

        properties = new Hashtable<String, String>();
        properties.put("temperature", Float.class.getName());
        properties.put("timiestamp", Integer.class.getName());
        String temperatureTopic = topic+"temperature";
        Event temperatureEvent = new Event(temperatureTopic, properties);
        this.registerEvent(temperatureTopic, temperatureEvent);

        properties = new Hashtable<String, String>();
        properties.put("waterFlow", Float.class.getName());
        String waterFlowTopic = topic+"waterflow";
        Event waterFlowEvent = new Event(waterFlowTopic, properties);
        this.registerEvent(waterFlowTopic, waterFlowEvent);
    }


    @Override
    public void init(ComponentContext context) {
        // TODO Auto-generated method stub
    }
}
