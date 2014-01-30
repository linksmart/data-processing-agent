package eu.ebbits.pwal.impl.driver.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.osgi.service.component.ComponentContext;

import eu.ebbits.pwal.api.exceptions.PWALWriteNotPossibleException;
import eu.ebbits.pwal.api.model.PWALControlEvent;
import eu.ebbits.pwal.api.model.PWALValue;
import eu.ebbits.pwal.api.model.PWALVariable;
import eu.ebbits.pwal.impl.driver.PWALVariablesDelegateImpl;

/**
 * This is the vzriables delegate of the DummyDriver.
 * 
 * This package is imagined as part of the new ebbits DDK: it will thus undergo significant modifications in the ebbits iterative process. 
 * it will be thus documented during the final release after all needed adapters are available.
 * 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author     ISMB
 * @version    %I%, %G%
 * @see        eu.ebbits.pwal.impl.driver.PWALDriverImpl
 * @since      PWAL 0.1.0
 */
public class TestVariablesDelegate extends PWALVariablesDelegateImpl {

    public TestVariablesDelegate(TestDriver driver) {
        super(driver);
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
     *Method used to generate error events
     */
    public void my_gen_error() {
        this.signalErrorEvent(new PWALControlEvent(PWALControlEvent.ControlEventType.DEFAULT_ERROR));
    }
    
    /**
     * Method used to generate critical events
     */
    public void my_gen_critical() {
        this.signalCriticalEvent(new PWALControlEvent(PWALControlEvent.ControlEventType.DEFAULT_CRITICAL));
        
    }

    @Override
    public PWALValue readNow(PWALVariable variable) {
        System.out.println("Read " + variable.getName() + " = ");
        BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
        try {
            return new PWALValue(buffer.readLine());
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public boolean writeNow(PWALVariable variable, PWALValue value)
            throws PWALWriteNotPossibleException {
        System.out.println("Write " + variable.getName() + " = " + value.toString());
        return true;
    }

    @Override
    public boolean updatePWALVariablesCollection() {
        // TODO Auto-generated method stub
        return true;
    }


    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void init(ComponentContext context) {
        // TODO Auto-generated method stub
        
    }
}
