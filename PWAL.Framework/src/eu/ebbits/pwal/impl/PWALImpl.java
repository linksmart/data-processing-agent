package eu.ebbits.pwal.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import eu.ebbits.pwal.api.PWAL;
import eu.ebbits.pwal.api.PWALDriverPort;
import eu.ebbits.pwal.api.driver.PWALDriver;
import eu.ebbits.pwal.api.driver.PWALEventsDelegate;
import eu.ebbits.pwal.api.driver.PWALServicesDelegate;
import eu.ebbits.pwal.api.driver.PWALVariablesDelegate;

/**
 * <code>PWAL</code> service implementation.
 * <p> 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author    ISMB
 * @version    %I%, %G%
 * @since    PWAL 0.1.0
 */
public class PWALImpl implements PWAL, PWALDriverPort {

    /** A <code>org.apache.log4j.Logger</code> instance */
    private Logger log = Logger.getLogger(PWAL.class.getName());

    private Map<String, PWALDriver> pwalDrivers;

    private Map<String, Collection<String>> pwalDriverNames;

    /**
     *  OSGI activation method for PWAL service
     *  
     *  @param    context            A <code>org.osgi.service.component.ComponentContext</code> object,
     *                          with the current OSGI context
     */
    protected void activate(ComponentContext context) {
        log.debug("Starting " + context.getBundleContext().getBundle().getSymbolicName() + "...");
        
        pwalDrivers = new HashMap<String, PWALDriver>();
        pwalDriverNames = new HashMap<String, Collection<String>>();
        
        log.debug("Started " + context.getBundleContext().getBundle().getSymbolicName());
    }

    /**
     *  OSGI de-activation method for PWAL service
     *  
     *  @param    context            A <code>org.osgi.service.component.ComponentContext</code> object,
     *                          with the current OSGI context
     */
    protected void deactivate(ComponentContext context) {
        log.debug("Stopping " + context.getBundleContext().getBundle().getSymbolicName() + "...");
        
        log.debug("Stopped " + context.getBundleContext().getBundle().getSymbolicName());
    }

    
    @Override
    public void configureDriver(String driverID, Map<String, Object> params) {
        if ((pwalDrivers != null) && (!pwalDrivers.isEmpty()) && (pwalDrivers.get(driverID) != null)) {
            this.pwalDrivers.get(driverID).configure(params);
        }
    }

    @Override
    public void configureDriver(String driverID, String methodName, List<Object> params) {
        if ((pwalDrivers != null) && (!pwalDrivers.isEmpty()) && (pwalDrivers.get(driverID) != null)) {
            this.pwalDrivers.get(driverID).configure(methodName,params);
        }
    }    
    
    @Override
    public void configureDriverParam(String driverID, String methodName, Object value) {
        if ((pwalDrivers != null) && (!pwalDrivers.isEmpty()) && (pwalDrivers.get(driverID) != null)) {
            this.pwalDrivers.get(driverID).configureParam(methodName, value);
        }
    }

    @Override
    public PWALEventsDelegate getEventsDelegate(String driverID) {
        if ((pwalDrivers != null) && (!pwalDrivers.isEmpty()) && (pwalDrivers.get(driverID) != null)) {
            return pwalDrivers.get(driverID).getEventsDelegate();
        } else {
            return null;
        }
    }

    @Override
    public PWALServicesDelegate getServicesDelegate(String driverID) {
        if ((pwalDrivers != null) && (!pwalDrivers.isEmpty()) && (pwalDrivers.get(driverID) != null)) {
            return pwalDrivers.get(driverID).getServicesDelegate();
        } else {
            return null;
        }
    }

    @Override
    public PWALVariablesDelegate getVariablesDelegate(String driverID) {
        if ((pwalDrivers != null) && (!pwalDrivers.isEmpty()) && (pwalDrivers.get(driverID) != null)) {
            return pwalDrivers.get(driverID).getVariablesDelegate();
        } else {
            return null;
        }
    }

    @Override
    public Collection<String> getRegisteredDrivers() {
        if ((pwalDrivers != null) && (!pwalDrivers.isEmpty())) {
            return pwalDrivers.keySet();
        } else {
            return null;
        }
    }

    @Override
    public Collection<String> getRegisteredDriversByName(String driverName) {
        if ((pwalDrivers != null) && (!pwalDrivers.isEmpty())) {
            return pwalDriverNames.get(driverName);
        } else {
            return null;
        }
    }

    @Override
    public boolean isDriverRegistered(String driverID) {
        if ((pwalDrivers != null) && (!pwalDrivers.isEmpty())) {
            return pwalDrivers.containsKey(driverID);
        } else {
            return false;
        }
    }

    @Override
    public boolean isDriverRegistered(PWALDriver driver) {
        if ((pwalDrivers != null) && (!pwalDrivers.isEmpty())) {
            return pwalDrivers.containsValue(driver);
        } else {
            return false;
        }
    }

    @Override
    public void registerDriver(PWALDriver driver) {
        if (!this.pwalDrivers.containsValue(driver)) {
            this.pwalDrivers.put(driver.getDriverID(), driver);
            
            Collection<String> driverIds;
            if (!this.pwalDriverNames.containsKey(driver.getDriverName())) {
                driverIds = new ArrayList<String>();
                this.pwalDriverNames.put(driver.getDriverName(), driverIds);
            } else {
                driverIds = this.pwalDriverNames.get(driver.getDriverName());
            }
            driverIds.add(driver.getDriverID());
        }
    }

    @Override
    public void unregisterDriver(PWALDriver driver) {
        if ((pwalDrivers != null) && pwalDrivers.containsValue(driver)) {
            this.pwalDrivers.remove(driver);
        }
        if ((pwalDriverNames != null) && pwalDriverNames.containsKey(driver.getDriverName())) {
            this.pwalDriverNames.get(driver.getDriverName()).remove(driver.getDriverID());
        }
    }
    
    @Override
    public synchronized ServiceRegistration subscribeTopic(String topic, String filter, EventHandler handler) {
        String[] topics = new String[] {
                topic
        };
        Dictionary props = new Hashtable();
        props.put(EventConstants.EVENT_TOPIC, topics);
        if(filter!=null && !filter.isEmpty()) {
            props.put(EventConstants.EVENT_FILTER, filter);
        }
        BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
        return context.registerService(EventHandler.class.getName(), handler , props);
    }

    @Override
    public synchronized void unsubscribeTopic(ServiceRegistration reg) {
        reg.unregister();
    }

    @Override
    public synchronized void unsubscribeTopics(List<ServiceRegistration> regs) {
        for(ServiceRegistration reg : regs) {
            reg.unregister();
        }
    }
}
