package eu.ebbits.pwal.api;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.EventHandler;

import eu.ebbits.pwal.api.driver.PWALEventsDelegate;
import eu.ebbits.pwal.api.driver.PWALServicesDelegate;
import eu.ebbits.pwal.api.driver.PWALVariablesDelegate;

/**
 * <code>PWAL</code> service interface.
 * <p> 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author    ISMB
 * @version    %I%, %G%
 * @since    PWAL 0.1.0
 */
public interface PWAL { 

    /**
     * Configures the driver.
     * 
     * @param driverID - the ID of PWALDriver to configure.
     * @param params - a <code>Map</code> of parameters to configure.
     *
     * 
     * @since        PWAL 0.2.0
     */
    void configureDriver(String driverID, Map<String, Object> params);

    /**
     * Configures the driver.
     * 
     * @param driverID - the ID of PWALDriver to configure.
     * @param methodName - the method name to be used to configure the parameters.
     * @param params - values to configure.
     *  
     * 
     * @since        PWAL 0.2.0
     */
    void configureDriver(String driverID, String methodName, List<Object> params);

    
    /**
     * Configures a driver's param.
     * 
     * @param driverID - the ID of PWALDriver to configure.
     * @param methodName - the method name to be used to configure the parameters.
     * @param params - value to configure.
     *  
     * 
     * @since        PWAL 0.2.0
     */
    void configureDriverParam(String driverID, String methodName, Object param);

    
    
    /**
     * Subscribes to a topic.
     * 
     * @param topic -  the topic to be subscribed
     * @param filter - content filter for the events (can be <code>null</code>)
     * @param handler - handler for the events
     * 
     * @return <code>ServiceRegistration</code> - registration done for the events
     * 
     * @since        PWAL 0.2.0
     */
    ServiceRegistration subscribeTopic(String topic, String filter, EventHandler handler);
    
    /** 
    * Removes the subscription to a topic.
    *
    * @param reg -    the <code>ServiceRegistration</code> to unsubscribe
    *  
    * @throw IllegalStateException - if something goes wrong unsubscribing to a topic 
    *  
    * @since        PWAL 0.2.0
    */
    void unsubscribeTopic(ServiceRegistration reg) throws IllegalStateException ;

    /** 
    * Removes the subscription to a set of topics.
    *
    * @param regs        the <code>ServiceRegistration</code>s to unsubscribe
    * 
    * @throw IllegalStateException - if something goes wrong unsubscribing to a topic
    * 
    * @since        PWAL 0.2.0
    */
    void unsubscribeTopics(List<ServiceRegistration> regs) throws IllegalStateException ;
    
    // TODO hanlde and expose ALL PWALEvents, PWALServices and PWALVariables through this interface, 
    // avoiding the need to specify the driver and get the delegate interface for each type of PWALObjects

    /**
     * Retrieves the Events delegate interface.
     * 
     * @param driverID - the ID of PWALDriver to retrieve from.
     * 
     * @return        the PWALEvents delegate interface.
     * 
     * @since        PWAL 0.1.0
     */
    PWALEventsDelegate getEventsDelegate(String driverID);

    /**
     * Retrieves the Services delegate interface.
     * 
     * @param driverID - the ID of PWALDriver to retrieve from.
     * 
     * @return        the PWALServices delegate interface.
     * 
     * @since        PWAL 0.1.0
     */
    PWALServicesDelegate getServicesDelegate(String driverID);

    /**
     * Retrieves the Variables delegate interface.
     * 
     * @param driverID - the ID of PWALDriver to retrieve from.
     * 
     * @return        the PWALVariables delegate interface.
     * 
     * @since        PWAL 0.1.0
     */
    PWALVariablesDelegate getVariablesDelegate(String driverID);

    /**
     * Retrieves the IDs of all <code>PWALDriver</code>s registered.
     * 
     * @return        a <code>Collection</code> of <code>String</code>s with the IDs 
     *                 of the current <code>PWALDriver</code>s registered.
     * 
     * @since        PWAL 0.2.0
     */
    Collection<String> getRegisteredDrivers();

    /**
     * Retrieves the IDs of registered <code>PWALDriver</code>s that match the given type.
     * 
     * @param driverName - a <code>String</code> with the PWALDriver name to check.
     * 
     * @return        a <code>Collection</code> of <code>String</code>s with the IDs 
     *                 of the registered <code>PWALDriver</code>s of the given name.
     * 
     * @since        PWAL 0.2.0
     */
    Collection<String> getRegisteredDriversByName(String driverName);

    /**
     * Checks whether a Driver is registered or not.
     * 
     * @param driverID - the id of PWALDriver to check.
     * 
     * @return        <code>True</code> if the Driver is loaded,
     *                 <code>False</code> otherwise.
     * 
     * @since        PWAL 0.2.0
     */
    boolean isDriverRegistered(String driverID);

}
