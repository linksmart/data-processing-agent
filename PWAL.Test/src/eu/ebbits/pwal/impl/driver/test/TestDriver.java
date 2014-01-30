package eu.ebbits.pwal.impl.driver.test;

import org.osgi.service.component.ComponentContext;

import eu.ebbits.pwal.api.model.PWALControlEvent;
import eu.ebbits.pwal.impl.driver.PWALDriverImpl;
import eu.ebbits.pwal.impl.driver.PWALEventsDelegateImpl;
import eu.ebbits.pwal.impl.driver.PWALServicesDelegateImpl;
import eu.ebbits.pwal.impl.driver.PWALVariablesDelegateImpl;

/**
 * 
 * This is a dummy driver which can be used to showcase the features of PWAL drivers.
 * It contains a reflective Service Delegate, a reflective events delegate and a mutable 
 * VariablesDelegate, so it should represent the most complex case of PWALDrivers.
 * 
 * This package is imagined as part of the new ebbits DDK: it will thus undergo significant 
 * modifications in the ebbits iterative process. 
 * It will be thus documented during the final release after all needed adapters are available.
 * 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author        ISMB
 * @version        %I%, %G%
 * @see            eu.ebbits.pwal.impl.driver.PWALDriverImpl
 * @since        PWAL 0.1.0
 */
public class TestDriver extends PWALDriverImpl {

    public TestDriver() {
        this.setDriverName("DummyDriver");
        this.setDriverVersion("0.0");
    }
    
    @Override
    protected PWALVariablesDelegateImpl initVariablesDelegate() {
        return new TestVariablesDelegate(this);
    }

    @Override
    protected PWALServicesDelegateImpl initServicesDelegate() {
        return new TestServicesDelegate(this);
    }

    @Override
    protected PWALEventsDelegateImpl initEventsDelegate() {
        return new TestEventsDelegate(this);
    }

    @Override
    public void init(ComponentContext context) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }

    /**
     * Method to generate debug events
     */
    public void my_gen_debug() {
        this.signalDebugEvent(new PWALControlEvent(PWALControlEvent.ControlEventType.DEFAULT_DEBUG));    
    }
    
    /**
     * Method to generate info events
     */
    public void my_gen_info() {
        this.signalInfoEvent(new PWALControlEvent(PWALControlEvent.ControlEventType.DEFAULT_INFO));
        
    }

    /**
     * Method to generate warning events
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
}
