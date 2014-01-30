package eu.ebbits.pwal.impl.driver.wsn;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;

import eu.ebbits.pwal.impl.driver.PWALEventsDelegateImpl;

/**
 * <code>WSNDriver</code> events delegate.
 * <p> 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author    ISMB
 * @version    %I%, %G%
 * @since    M36demo 1.0
 */
public class WSNEventsDelegate extends PWALEventsDelegateImpl {

    /**
     * Constructor of the delegate
     * 
     *    @param driver - <code>WSNDriverImpl</code> that uses the delegate
     */
    public WSNEventsDelegate(WSNDriverImpl driver) {
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
        properties.put("packet",  "byte[]");
            
        Event wsnEvent = new Event("pwal/wsndriver/packet", properties);
        this.registerEvent("pwal/wsndriver/packet",wsnEvent);        
    }

}
