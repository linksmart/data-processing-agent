package eu.ebbits.pwal.impl.driver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import eu.ebbits.pwal.api.driver.PWALDelegate;
import eu.ebbits.pwal.api.driver.PWALDelegateSubscriber;
import eu.ebbits.pwal.api.driver.PWALDriver;
import eu.ebbits.pwal.api.exceptions.PWALSubscriptionException;
import eu.ebbits.pwal.api.model.PWALControlEvent;

/**
 * This class is imagined as part of the new ebbits DDK: it will thus undergo significant modifications in the ebbits iterative process. 
 * it will be thus documented during the final release after all needed adapters are available.
 * 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author        ISMB
 * @version        %I%, %G%
 * @see            eu.ebbits.pwal.impl.driver.PWALDriverImpl
 * @since        PWAL 0.1.0
 */
public abstract class PWALDelegateImpl implements PWALDelegate {

    private PWALDriver driver;
    private Collection<PWALDelegateSubscriber> subscribers;
    private Thread thread;

    /** A <code>org.apache.log4j.Logger</code> instance */
    private Logger log = Logger.getLogger(PWALDelegateImpl.class.getName());
    
    /**
     * Constructor of the PWALDelegate.
     */
    public PWALDelegateImpl() {
        this.subscribers = new HashSet<PWALDelegateSubscriber>();
        this.thread = new Thread(this);
    }

    /**
     * Constructor of the PWALDelegate.
     * 
     * @param driver - driver that uses the delegate
     */
    public PWALDelegateImpl(PWALDriver driver) {
        this();
        this.driver = driver;
    }

    @Override
    public final void configure(Map<String, Object> parameters) {
        for (Entry<String, Object> param : parameters.entrySet()) {
            try {
                ArrayList<Object> values = new ArrayList<Object>();
                values.add(param.getValue());
                configure(param.getKey(), values);
            } catch (Exception e) {
                log.error("Unable to configure the driver");
                this.signalWarningEvent(new PWALControlEvent("Could not configure the driver", e));                
            }
        }
    }
    
    @Override
    public final void configure(String methodName, List<Object> values) {
        try {
            this.getClass().getMethod(methodName,values.getClass()).invoke(this, values);
        } catch (Exception e) {
            log.error("Unable to configure using method "+methodName+", error: "+ (e.getMessage()==null?"invalid parameters":e.getMessage()));
            this.signalWarningEvent(new PWALControlEvent("Could not " + methodName, e));
        }        
    }
    
    @Override
    public final void configureParam(String methodName, Object argValue) {
        try {
            this.getClass().getMethod(methodName, argValue.getClass()).invoke(this, argValue);
        } catch (Exception e) {
            log.error("Unable to configure using method "+methodName+", error: "+ (e.getMessage()==null?e.getMessage():"invalid parameters"));
            this.signalWarningEvent(new PWALControlEvent("Could not " + methodName, e));
        }
    }
    
    @Override
    public final PWALDriver getDriver() {
        return this.driver;
    }

    @Override
    public final void setDriver(PWALDriver driver) {
        this.driver = driver;
    }

    /**
     * Retrieves the list of the subscribers
     *  
     * @return    a <Collection> of <PWALDelegateSubscriber>
     */
    public final Collection<PWALDelegateSubscriber> getSubscribers() {
        return subscribers;
    }

    /**
     * Retrieves the <code>java.util.Thread</code> of the delegate
     * 
     * @return    the <code>java.util.Thread</code> that controls the delegate
     */
    public final Thread getThread() {
        return thread;
    }
    
    @Override
    public boolean isStarted() {
        return this.thread.isAlive();
    }

    @Override
    public void start() {
        this.thread.start();
    }

    @Override
    public void stop() {
        this.thread.interrupt();
    }

    @Override
    public final synchronized void subscribe(PWALDelegateSubscriber s) throws PWALSubscriptionException {
        try {
            this.subscribers.add(s);
        }catch (Exception e) {
            throw new PWALSubscriptionException(e.getMessage(),e);
        }
    }

    @Override
    public final synchronized void unsubscribe(PWALDelegateSubscriber s) throws PWALSubscriptionException {
        try {
            this.subscribers.remove(s);
        }catch (Exception e) {
            throw new PWALSubscriptionException(e.getMessage(),e);
        }
    }

    @Override
    public final synchronized boolean isSubscribed(PWALDelegateSubscriber s) {
        return this.subscribers.contains(s);
    }

    protected synchronized void signalDebugEvent(PWALControlEvent e) {
        PWALControlEvent ev = new PWALControlEvent(e);
        ev.setType(PWALControlEvent.ControlEventType.DEFAULT_DEBUG);
        for(PWALDelegateSubscriber s : this.subscribers) {
            s.driverDebug(ev);
        }
    }

    protected synchronized void signalWarningEvent(PWALControlEvent e) {
        PWALControlEvent ev = new PWALControlEvent(e);
        ev.setType(PWALControlEvent.ControlEventType.DEFAULT_WARNING);
        for(PWALDelegateSubscriber s : this.subscribers) {
            s.driverWarning(ev);
        }
    }

    protected synchronized void signalErrorEvent(PWALControlEvent e) {
        PWALControlEvent ev = new PWALControlEvent(e);
        ev.setType(PWALControlEvent.ControlEventType.DEFAULT_ERROR);
        for(PWALDelegateSubscriber s : this.subscribers) {
            s.driverError(ev);
        }
    }

    protected synchronized void signalCriticalEvent(PWALControlEvent e) {
        PWALControlEvent ev = new PWALControlEvent(e);
        ev.setType(PWALControlEvent.ControlEventType.DEFAULT_CRITICAL);
        for(PWALDelegateSubscriber s : this.subscribers) {
            s.driverCriticalError(ev);
        }
            
    }

    protected synchronized void signalInfoEvent(PWALControlEvent e) {
        PWALControlEvent ev = new PWALControlEvent(e);
        ev.setType(PWALControlEvent.ControlEventType.DEFAULT_INFO);
        for(PWALDelegateSubscriber s : this.subscribers) {
            s.driverInfo(ev);
        }
    }
}