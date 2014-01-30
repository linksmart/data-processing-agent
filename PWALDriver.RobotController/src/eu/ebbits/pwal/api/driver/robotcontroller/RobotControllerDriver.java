package eu.ebbits.pwal.api.driver.robotcontroller;

import eu.ebbits.pwal.api.driver.PWALDriver;


/**
 * Interface of the PWAL Driver for the robot controller used in M24 demo, updated for the PWAL 2.0.
 *
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author    ISMB
 * @version    %I%, %G%
 * @since    M36demo 1.0
 * 
 */
public interface RobotControllerDriver extends PWALDriver {
    
    /**
     * @return the host
     */
    String getHost();

    /**
     * @param host the host to set
     */
    void setHost(String host);

    /**
     * @return the port
     */
    int getPort();

    /**
     * @param port the port to set
     */
    void setPort(Integer port);
}
