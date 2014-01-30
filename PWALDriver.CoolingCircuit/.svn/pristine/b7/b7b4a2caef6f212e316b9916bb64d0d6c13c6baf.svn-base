package eu.ebbits.pwal.api.driver.device.coolingcircuit.impl;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.ebbits.pwal.api.driver.device.coolingcircuit.CoolingCircuit;
import eu.ebbits.pwal.api.driver.device.coolingcircuit.CoolingCircuitListener;


/**
 * Encapsulates communication with an Arduino sensor platform. Provides an
 * interface to request the latest sensor values, set the LED status, and
 * register as an event listener to receive updates about sensor values.
 * 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 * 
 * @author simon
 * @version    %I%, %G%
 * @since      PWAL 0.1.0
 */
@SuppressWarnings("unused")
public class CoolingCircuitImpl implements CoolingCircuit {
    private ArduinoSerial serial;

    private Map<Integer, Float> flowValues = new HashMap<Integer, Float>();
    private Map<Integer, Float> tempValues = new HashMap<Integer, Float>();

    private List<CoolingCircuitListener> listeners = new LinkedList<CoolingCircuitListener>();

    private static Logger log = Logger.getLogger(CoolingCircuitImpl.class.getName());

    /**
     * Creates a new representation of the pre-configured Arduino platform,
     * connected through a serial port
     * 
     * @param portName
     *          the port name, as a String: on Windows, use the COM port name,
     *          e.g., "COM3"; on Mac OS X, use the device file name, e.g.,
     *          "/dev/tty.usbserial-A600etcM"
     */
    public CoolingCircuitImpl(String portName) {
        this.serial = new ArduinoSerial(portName);
        this.serial.addListener(new ListenerToUse());
    }


    @Override
    public void setPump(int id, byte speed) {
        this.serial.writeToSerial("p" + id, speed);
    }

    @Override
    public float getWaterFlow(int id) {
        if (!flowValues.containsKey(id)) {
            throw new IndexOutOfBoundsException("No value for id " + id);
        }
        return flowValues.get(id);
    }

    @Override
    public float getTemperature(int id) {
        if (!tempValues.containsKey(id)) {
            throw new IndexOutOfBoundsException("No value for id " + id);
        }
        return tempValues.get(id);
    }
    
    @Override
    public void addListener(CoolingCircuitListener listener) {
        listeners.add(listener);
    }
    
    
    private class ListenerToUse implements ArduinoListener {
        @Override
        public void updateBrightness(int percentage) {
        }

        @Override
        public void updateTemperature(int id, float degreesCelsius) {
            tempValues.put(id, degreesCelsius);
            for (CoolingCircuitListener listener : listeners) {
                listener.updateTemperature(id, degreesCelsius);
            }
        }

        @Override
        public void updateMotion(boolean motion) {
        }

        @Override
        public void updateWaterFlow(int id, float lpm) {
            flowValues.put(id, lpm);
            for (CoolingCircuitListener listener : listeners) {
                listener.updateWaterFlow(id, lpm);
            }
        }
    }
}
