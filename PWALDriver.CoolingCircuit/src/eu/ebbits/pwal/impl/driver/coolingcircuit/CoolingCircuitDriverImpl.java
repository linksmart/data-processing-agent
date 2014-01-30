package eu.ebbits.pwal.impl.driver.coolingcircuit;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import eu.ebbits.pwal.api.driver.coolingcircuit.CoolingCircuitDriver;
import eu.ebbits.pwal.api.driver.device.coolingcircuit.CoolingCircuit;
import eu.ebbits.pwal.api.driver.device.coolingcircuit.CoolingCircuitListener;
import eu.ebbits.pwal.api.driver.device.coolingcircuit.impl.CoolingCircuitImpl;
import eu.ebbits.pwal.impl.driver.PWALDriverImpl;
import eu.ebbits.pwal.impl.driver.PWALEventsDelegateImpl;
import eu.ebbits.pwal.impl.driver.PWALServicesDelegateImpl;
import eu.ebbits.pwal.impl.driver.PWALVariablesDelegateImpl;

/**
 * This is a PWALDriver for the CoolingCircuit/ArduinoSerial prototype. It
 * instantiates the CoolingCircuit using the specified port name and provides
 * access to sensors and actuators through PWALVariables using the
 * {@link PWALVariablesDelegateImpl}.
 * 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 * 
 * @author FIT
 * @version %I%, %G%
 * @see eu.ebbits.pwal.impl.driver.PWALDriverImpl
 */
public class CoolingCircuitDriverImpl extends PWALDriverImpl implements CoolingCircuitDriver {

    @SuppressWarnings("unused")
    private static Logger log = Logger.getLogger(CoolingCircuitDriver.class.getName());

    /**
     * the port name, as a String: on Windows, use the COM port name, e.g.,
     * "COM3"; on Mac OS X, use the device file name, e.g.,
     * "/dev/tty.usbserial-A600etcM"
     */
    private static final String PORTNAME = "/dev/tty.usbserial-A600euMD";

    private static final int MAX_SPEED = 127;
    
    private float notInitialized = Float.NaN;

    /** Connection to the physical device. */
    private CoolingCircuit coolingCircuit;

    /** Current state of the water flow sensor. */
    private float currentFlow = notInitialized;
    
    /** Current state of the temperature sensor. */
    private float currentTemp = notInitialized;
    
    /** Current state of the pump speed actuator. */
    private byte currentSpeed = 0;

    @Override
    public void init(ComponentContext context) {
        this.coolingCircuit = new CoolingCircuitImpl(PORTNAME);
    }

    @Override
    public void run() {
        this.coolingCircuit.addListener(new CoolingCircuitListener() {

            @Override
            public void updateWaterFlow(int id, float lpm) {
                currentFlow = lpm;
            }

            @Override
            public void updateTemperature(int id, float degCelsius) {
                currentTemp = degCelsius;
            }
        });
    }

    @Override
    public float getCurrentWaterFlow() {
        return currentFlow;
    }

    @Override
    public float getCurrentTemperature() {
        return currentTemp;
    }

    @Override
    public byte getCurrentSpeed() {
        return currentSpeed;
    }

    @Override
    public boolean setCurrentSpeed(byte speed) {
        if (speed < 0 || speed > MAX_SPEED) {
            return false;
        }
        this.coolingCircuit.setPump(1, speed);
        this.currentSpeed = speed;
        return true;
    }

    @Override
    protected PWALEventsDelegateImpl initEventsDelegate() {
        return new CoolingCircuitEventsDelegate(this);
    }

    @Override
    protected PWALServicesDelegateImpl initServicesDelegate() {
        return new CoolingCircuitServicesDelegate(this);
    }

    @Override
    protected PWALVariablesDelegateImpl initVariablesDelegate() {
        return new CoolingCircuitVariablesDelegate(this);
    }

    /**
     * Returns the value of notInitialized variable
     * 
     * @return <code>float</code> containing the value
     */
    public float getNotInitialized() {
        return notInitialized;
    }
}
