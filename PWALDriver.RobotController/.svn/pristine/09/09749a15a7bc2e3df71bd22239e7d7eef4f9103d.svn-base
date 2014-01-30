package eu.ebbits.pwal.impl.driver.robotcontroller;

import org.osgi.service.component.ComponentContext;

import eu.ebbits.pwal.api.driver.PWALEventsDelegate;
import eu.ebbits.pwal.api.driver.PWALServicesDelegate;
import eu.ebbits.pwal.api.driver.PWALVariablesDelegate;
import eu.ebbits.pwal.api.driver.robotcontroller.RobotControllerDriver;
import eu.ebbits.pwal.impl.driver.PWALDriverImpl;
import eu.ebbits.pwal.impl.driver.robotcontroller.client.RobotControllerClient;

/**
 * Implementation of the PWAL Driver for the robot controller used in M24 demo, updated for the PWAL 2.0.
 *
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author    ISMB
 * @version    %I%, %G%
 * @since    M36demo 1.0
 * 
 */
public class RobotControllerDriverImpl extends PWALDriverImpl implements RobotControllerDriver {

    /** number of axis of the robot 
     *
     */
    public static final int N_OF_AXIS = 6;
    
    /** Client to control the robot **/
    private RobotControllerClient client;

    @Override
    public void init(ComponentContext context) {
        this.client = new RobotControllerClient();
    }

    @Override
    public void run() {
        this.client.openConnection();
    }

    @Override
    public void stop() {
            client.closeConnection();
        super.stop();
    }

    /**
     * @return the host
     */
    public String getHost() {
        return this.client.getHost();
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.client.setHost(host);
    }

    /**
     * @return the port
     */
    public int getPort() {
        return this.client.getPort();
    }

    /**
     * @param port the port to set
     */
    public void setPort(Integer port) {
        this.client.setPort(port);
    }
    
    
    /**
     * Retrieves the client to use to connect to the robot
     * 
     * @return    the client as <code>RobotControllerClient</code>
     */
    protected RobotControllerClient getClient() {
        return client;
    }

    /**
     * Sets the client to use to connect to the robot
     * 
     * @param client - the client to set as <code>RobotControllerClient</code>
     */
    protected void setClient(RobotControllerClient client) {
        this.client = client;
    }

    @Override
    protected PWALVariablesDelegate initVariablesDelegate() {
        return (PWALVariablesDelegate) new RobotControllerVariablesDelegate(this);
    }

    @Override
    protected PWALServicesDelegate initServicesDelegate() {
        return (PWALServicesDelegate) new RobotControllerServicesDelegate(this);
    }

    @Override
    protected PWALEventsDelegate initEventsDelegate() {
        return (PWALEventsDelegate) new RobotControllerEventsDelegate(this);
    }
}
