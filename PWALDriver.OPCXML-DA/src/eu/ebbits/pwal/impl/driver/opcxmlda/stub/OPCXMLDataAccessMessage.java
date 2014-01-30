package eu.ebbits.pwal.impl.driver.opcxmlda.stub;

import eu.ebbits.pwal.api.model.PWALControlEvent;

/**
 * Class to be used to send a OPC XML-DA message as <code>PWALControlEvent</code>
 * 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author     ISMB
 * @version    %I%, %G%
 * @see        eu.ebbits.pwal.api.driver.PWALDriver
 * @since      PWAL 0.1.0
 */
public class OPCXMLDataAccessMessage extends PWALControlEvent {

    /**
     * Constructor of the message
     * 
     * @param msg - content of the message as <code>String</code>
     */
    public OPCXMLDataAccessMessage(String msg) {
        super(msg);
    }

}
