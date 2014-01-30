/** @file UDPSend.java
 * @author Hussein Khaleel
 */

package eu.ebbits.pwal.impl.driver.wsn.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.apache.log4j.Logger;


/**
 * The class that implements the sending of UDP packets.
 *
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author    ISMB
 * @version    %I%, %G%
 * @since    M36demo 1.0
 * 
 */
public class UDPSend {
    
    private Logger log = Logger.getLogger(this.getClass().getName());
    
    private static final int THREAD_WAIT = 100;
    
    /**
     * @name sendControlPacket
     * @brief The method that establishes the UDP transmission socket,
     *        forms the UDP packet payload, and transmits the packet.
     * @param address Destination address.
     * @param txport Destination port.
     * @param type The payload type identification.
     * @param seqNo The packet's sequence number.
     * @param command The identifier of the control command sent to the sensor node.
     * @param data1 Field specifying a parameter, as required by the command.
     * @param data2 Field specifying a parameter, as required by the command.
     * @param data3 Field specifying a parameter, as required by the command.
     * @throws InterruptedException 
     * @throws IOException 
     */
    public boolean sendControlPacket(InetAddress address, int txport, short type, short seqNo,
            short command, short data1, short data2, short data3) throws InterruptedException, IOException {

        int port = txport;
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            log.error(e.getStackTrace());
        }

        byte [] payload = FormatDecoder.fillUpPayload(type, seqNo, command, data1, data2, data3); 
        DatagramPacket packet = new DatagramPacket(payload, payload.length, address, port);
        
        socket.send(packet);
        Thread.sleep(THREAD_WAIT);
        socket.send(packet);
        Thread.sleep(THREAD_WAIT);
        socket.send(packet);
        Thread.sleep(THREAD_WAIT);
        
        socket.close();
        log.info("(2)sent to "+address.getHostName()+" on port "+port);
        return true;
    }
}
