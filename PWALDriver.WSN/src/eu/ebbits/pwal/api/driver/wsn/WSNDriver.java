/**
 * 
 */
package eu.ebbits.pwal.api.driver.wsn;

import eu.ebbits.pwal.api.driver.PWALDriver;

/**
 * Interface of the PWAL Driver for the WSN
 *
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 *
 * @since    M36demo 1.0
 */
public interface WSNDriver extends PWALDriver {
    
    /**
     * @return the UDP port used
     */
    int getRXPort();

    /**
     * @param RX port used
     */
    void setRXPort(Integer rxPort);
}
