package eu.ebbits.pwal.impl.driver.wsn.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.apache.log4j.Logger;


/**
 * Class used to send commands to the WSN
 *
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author    ISMB
 * @version    %I%, %G%
 * @since    M36demo 1.0
 * 
 */
public class CommandSender {
    private static final int TX_PORT_WSN = 7731;
    private static final short TYPE_CONTROL_PAYLOAD = 192; //0xc0
    
    private Logger log = Logger.getLogger(this.getClass().getName());
    
    /**
     * Sends a control command to a WSN node
     * 
     * @param nodeAddress - address of the node
     * @param type The payload type identification
     * @param seqNo The packet's sequence number
     * @param command The identifier of the control command sent to the sensor node
     * @param data1 Field specifying a parameter, as required by the command
     * @param data2 Field specifying a parameter, as required by the command
     * @param data3 Field specifying a parameter, as required by the command
     * 
     * @return    a <code>boolean</code>, true if the command as be sent, false if something goes wrong
     */
    public boolean sendControlCommand(InetAddress nodeAddress, short seqNo, short command, short data1, short data2, short data3) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            log.error(e.getStackTrace());
        }
        byte [] payload = FormatDecoder.fillUpPayload(TYPE_CONTROL_PAYLOAD, seqNo, command, data1, data2, data3);
        DatagramPacket packet = new DatagramPacket(payload, payload.length, nodeAddress, TX_PORT_WSN);

        try {
            socket.send(packet);
        } catch (IOException e1) {
            log.error(e1.getStackTrace());
        }

        socket.close();
        log.info("Sent ControlCommand to "+nodeAddress.getHostName()+" on port "+TX_PORT_WSN);
        return true;
    }
}
