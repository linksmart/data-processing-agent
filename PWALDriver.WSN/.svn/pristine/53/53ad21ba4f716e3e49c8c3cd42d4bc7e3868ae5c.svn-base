/** @file UDPReceive.java
 * @author (Modified by) Hussein Khaleel, Mauricio Caceres
 * @brief The class that establishes the UDP socket and receives the packet.
 */

package eu.ebbits.pwal.impl.driver.wsn.client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.apache.log4j.Logger;

import eu.ebbits.pwal.impl.driver.wsn.WSNDriverImpl;

/**
 * Client used to receive data 
 *
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author    ISMB
 * @version    %I%, %G%
 * @since    M36demo 1.0
 * 
 */
public class UDPReceive extends Thread {

    private static final int BUFFER_SIZE = 2048;
    
    private static final int THREAD_WAIT = 2000;
    
    private static int rxPort = 0; // 7730
    private boolean isConfigured = false;
    private boolean working = true;
    private WSNDriverImpl driver;
    
    private Logger log = Logger.getLogger(this.getClass().getName());
    
    /**
     * Constructor of the Thread that receives UDP packets
     * 
     * @param driver - Driver that uses the UDPReceive as <code>WSNDriverImpl</code>
     */
    public UDPReceive(WSNDriverImpl driver) {
        this.driver = driver;
    }


    /** @name run
     * @brief Method to create a UDP socket and enable the reception of UDP packets.
     */
    public void run() {
        if(isConfigured) {
            try {
                int port = rxPort;
    
                // Create a socket to listen on the port.
                DatagramSocket dsocket = new DatagramSocket(port);
    
                // Create a buffer to read datagrams into. If a
                // packet is larger than this buffer, the
                // excess will simply be discarded!
                byte[] buffer = new byte[BUFFER_SIZE];
    
                // Create a packet to receive data into the buffer
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    
                // Create a data decoder
                DataDecoder decoder = new DataDecoder(driver);
    
                // Waiting to receive packets and printing them.
                while (working) {
                    // Wait to receive a datagram
                    dsocket.receive(packet);
        
                    //Decode the packet to extract the data
                    decoder.decode(packet.getAddress(), packet.getPort(), packet.getLength(), packet.getData());
    
                    // Reset the length of the packet before reusing it.
                    packet.setLength(buffer.length);
                }
            } catch (Exception e) {
                log.error(e.getStackTrace());
            }
        } else {
            try {
                sleep(THREAD_WAIT);
            } catch (InterruptedException e) {
                log.warn(e.getStackTrace());
            }
        }
    }
        
        
    /**
     * Method to stop the thread
     */
    public void stopReceive(){
        working = false;
    }


    /**
     * @return the UDP port used
     */    
    public int getRXPort() {
        return rxPort;
    }


    /**
     * @param RX port used
     */
    public void setRXPort(int rxPort) {
        UDPReceive.rxPort = rxPort;
        isConfigured=true;
    }
}
