package eu.ebbits.pwal.impl.driver.wsn.client;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import eu.ebbits.pwal.impl.driver.wsn.WSNDriverImpl;

/**
 * Class used to decode the received packets from the proxy
 *
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author    ISMB
 * @version    %I%, %G%
 * @since    M36demo 1.0
 * 
 */
public class ProxyDecoder {

    private static final int N_6 = 6;
    private static final int N_10 = 10;
    
    private static final int NODE_ADDRESS_INDEX = 12;
    
    /* 0xc0 */
    private static final short TYPE_CONTROL_PAYLOAD = 192;
    
    /* 0xc1 */
    private static final short COMMAND_SET_RATE = 193;
    
    /* 0xc2 */
    private static final short COMMAND_STOP = 194;
    
    /* 0xc3 */
    //private static short COMMAND_GET_RATE = 195; 
    
    /* 0xc4 */
    private static final short COMMAND_START = 196;
    
    private CommandSender commandSender;
    
    private Logger log = Logger.getLogger(this.getClass().getName());
    
    /**
     * Decodes a packet arrived from the proxy
     * 
     * @param payloadData - payload of the packet
     */
    public void decode(byte[] payloadData) {
        int type = 0;
        short seqNo = 0;
        short command = 0;
        short data1 = 0;
        short data2 = 0;
        short data3 = 0;
        InetAddress nodeAddress = null;
        byte[] nodeAddressByte = new byte[WSNDriverImpl.N_16];
        
        log.info("Received from proxy");

        /*
        //convert the payload bytes to short
        for (int i=0;i<payload_length;i++) {
            data[i] = (short) payload_data[i];
        }
         */

        //packet type identification
        type = FormatDecoder.decode16U(payloadData, 0, false);
        if(type==TYPE_CONTROL_PAYLOAD){
            log.info("Received TYPE_CONTROL_PAYLOAD");
            /*
            seq_no  = encode16(data[2], data[3]);
            command = encode16(data[4], data[5]);
            data_1  = encode16(data[6], data[7]);
            data_2  = encode16(data[8], data[9]);
            data_3  = encode16(data[10], data[11]);
             */
            seqNo  = (short)FormatDecoder.decode16U(payloadData, WSNDriverImpl.N_2, false);
            command = (short)FormatDecoder.decode16U(payloadData, WSNDriverImpl.N_4, false);
            data1  = (short)FormatDecoder.decode16U(payloadData, N_6, false);
            data2  = (short)FormatDecoder.decode16U(payloadData, WSNDriverImpl.N_8, false);
            data3  = (short)FormatDecoder.decode16U(payloadData, N_10, false);
            //getting none_addr
            int ii = NODE_ADDRESS_INDEX; 
            System.arraycopy(payloadData,0,nodeAddressByte,ii,nodeAddressByte.length);
            try {
                nodeAddress = InetAddress.getByAddress(nodeAddressByte); //got the sensor node IP address
            } catch (UnknownHostException e) {
                log.error(e.getStackTrace());
            }
            if(command==COMMAND_SET_RATE){
                log.info("Received COMMAND_SET_RATE to "+data1+", to node "+nodeAddress.getHostAddress());
                commandSender = new CommandSender();
                commandSender.sendControlCommand(nodeAddress, seqNo, COMMAND_SET_RATE, data1, data2, data3);
            }
            else if(command==COMMAND_START){
                log.info("Received COMMAND_START, to node "+nodeAddress.getHostAddress());
                commandSender = new CommandSender();
                commandSender.sendControlCommand(nodeAddress, seqNo, COMMAND_START, data1, data2, data3);
            }
            else if(command==COMMAND_STOP){
                log.info("Received COMMAND_STOP, to node "+nodeAddress.getHostAddress());
                commandSender = new CommandSender();
                commandSender.sendControlCommand(nodeAddress, seqNo, COMMAND_STOP, data1, data2, data3);
            }
        }
        else {
            log.warn("Unknown packet type");
        }
    }
}
