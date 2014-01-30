package eu.ebbits.pwal.impl.driver.wsn;

import org.osgi.service.component.ComponentContext;

import eu.ebbits.pwal.api.driver.PWALEventsDelegate;
import eu.ebbits.pwal.api.driver.PWALServicesDelegate;
import eu.ebbits.pwal.api.driver.PWALVariablesDelegate;
import eu.ebbits.pwal.api.driver.wsn.WSNDriver;
import eu.ebbits.pwal.impl.driver.PWALDriverImpl;
import eu.ebbits.pwal.impl.driver.wsn.client.UDPReceive;


/**
 * Implementation of the PWAL Driver for the WSN
 *
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author    ISMB
 * @version    %I%, %G%
 * @since    M36demo 1.0
 * 
 */
public class WSNDriverImpl extends PWALDriverImpl implements WSNDriver {
    //===================numeric constants=================
    public static final int N_2 = 2;
    public static final int N_4 = 4;
    public static final int N_8 = 8;
    public static final int N_16 = 16;
    public static final int N_24 = 24;
    
    public static final int PAYLOAD_SIZE = 12;
    
    private UDPReceive client;

    @Override
    public void init(ComponentContext context) {
        this.client = new UDPReceive(this);
    }

    @Override
    public void run() {
        this.client.start();
    }

    @Override
    public void stop() {
        this.client.stopReceive();
    }

    /**
     * @return the UDP port used
     */
    public int getRXPort() {
        return this.client.getRXPort();
    }

    /**
     * @param RX port used
     */
    public void setRXPort(Integer rxPort) {
        this.client.setRXPort(rxPort);
    }

    @Override
    protected PWALEventsDelegate initEventsDelegate() {
        return (PWALEventsDelegate) new WSNEventsDelegate(this);
    }

    @Override
    protected PWALServicesDelegate initServicesDelegate() {
        return (PWALServicesDelegate) new WSNServicesDelegate(this);
    }

    @Override
    protected PWALVariablesDelegate initVariablesDelegate() {
        return (PWALVariablesDelegate) new WSNVariablesDelegate(this);
    }
}