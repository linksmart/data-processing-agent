package eu.ebbits.pwal.api.driver;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.ComponentContext;

import eu.ebbits.pwal.api.driver.PWALDelegateSubscriber;
import eu.ebbits.pwal.api.exceptions.PWALSubscriptionException;

/**
 * Generic interface for controlling the <code>PWALDriver</code>'s delegates.
 * Specific delegates implements this one.
 * <p> 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author        ISMB
 * @version        %I%, %G%
 * @see         PWALEventsDelegate
 * @see         PWALServicesDelegate
 * @see         PWALVariablesDelegate
 * @since        PWAL 0.1.0
 */
public interface PWALDelegate extends Runnable {

    /** 
     * Configures the delegate.
     *
     * @param paramters - a map that contains the parameters (and the values)
     *                     to be configured in the driver
     *
     * @since        PWAL 0.2.0
     */
    void configure(Map<String, Object> parameters);

    /** 
     * Configures the delegate.
     *
     * @param methodName - name of the method to be used to configure the driver
     * @parm values - values to set in the configuration parameters of the driver
     *
     * @since        PWAL 0.2.0
     */
    void configure(String methodName, List<Object>values);

    /** 
     * Configures a delegate's param.
     *
     * @param methodName - name of the method to be used to configure a driver's param
     * @param value - value to configure for the parameter
     *
     *
     * @since        PWAL 0.2.0
     */
    void configureParam(String methodName, Object value);

    
    /** 
     * Retrieves the delegate's associated driver.
     *
     * @return        the <code>PWALDriver</code> associated
     *  
     * @see            PWALDelegateSubscriber
     * @since        PWAL 0.2.0
     */
    PWALDriver getDriver();

    /** 
     * Sets the delegate's associated driver.
     *
     * @param s -    the <code>PWALDriver</code> to associate
     * 
     * @see            PWALDriver
     * @since        PWAL 0.2.0
    */
    void setDriver(PWALDriver driver);

    /** 
     * Initializes the delegate.
     * 
     * @param context - the driver <code>ComponentContext</code>
     * 
     * @since        PWAL 0.1.0
     */
    void init(ComponentContext context);

    /** 
     * Checks whether the delegate is started.
     * 
     * @return        <code>true</code> if the delegate is started,
     *                 <code>false</code> otherwise.
     *
     * @since        PWAL 0.1.0
     */
    boolean isStarted();

    /** 
     * Starts the delegate.
     *
     * @since        PWAL 0.1.0
     */
    void start();

    /** 
     * Stops the delegate.
     *
     * @since        PWAL 0.1.0
     */
    void stop();

    /** 
     * Adds a new subscriber to the control events.
     *
     * @param s -    the <code>PWALDelegateSubscriber</code> to subscribe
     *  
     * @throw PWALSubscriptionException - if something goes wrong adding the subscriber 
     *  
     * @see            PWALDelegateSubscriber
     * @since        PWAL 0.1.0
     */
    void subscribe(PWALDelegateSubscriber s) throws PWALSubscriptionException;

    /** 
     * Removes a subscriber from the control events.
     *
     * @param s        the <code>PWALDelegateSubscriber</code> to unsubscribe
     *  
     * @throw PWALSubscriptionException - if something goes wrong removing the subscriber  
     *  
     * @see            PWALDelegateSubscriber
     * @since        PWAL 0.1.0
     */
    void unsubscribe(PWALDelegateSubscriber s) throws PWALSubscriptionException;

    /** 
     * Checks whether a subscriber is subscribed to the control events.
     *
     * @param s -    the <code>PWALDelegateSubscriber</code> to check
     *  
     * @return        <code>true</code> if the subscriber is subscribed,
     *                 <code>false</code> otherwise.
     *
     * @see            PWALDelegateSubscriber
     * @since        PWAL 0.1.0
     */
    boolean isSubscribed(PWALDelegateSubscriber s);

}
