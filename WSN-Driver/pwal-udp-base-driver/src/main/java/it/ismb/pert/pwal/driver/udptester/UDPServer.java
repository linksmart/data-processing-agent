/**
 * This file is a part of the basic OSGi tutorial. This is tutorial will be
 * consist of many breakdowns. This tutorial is growing step
 * by step to cover all the technical directive for building a good
 * OSGi applications.
 */
package it.ismb.pert.pwal.driver.udptester;

import it.ismb.pert.pwal.driver.api.IMessage;
import it.ismb.pert.pwal.driver.api.Response;
import it.ismb.pert.pwal.driver.impl.Queues;
import it.ismb.pert.pwal.driver.impl.TelosBDriver;
import it.ismb.pert.pwal.driver.wsn.customUDP.Definitions;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;


import java.nio.*;
public class UDPServer implements Runnable {
	Queues queues = Queues.getInstance();
	Queue<IMessage> IQueue = queues.getIQueue();
	
	private boolean listenMode = true;
	private int port = 10000;
	private final static Logger  LOGGER = Logger.getLogger( UDPServer.class.getName() );
	byte [] sendData 	= new byte [12];
	byte [] receiveData = new byte [12];
	DatagramSocket udplistener = null;
	Definitions def = new Definitions();
	DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	
	public UDPServer(int port){
		this.port = port;
		new Thread(this).start();
		
	}
	@Override
	public void run() {

		try {
			 udplistener = new DatagramSocket(port);
			 LOGGER.log(Level.INFO, "UDP Listener Statred at Port[ "+ port +" ]");
			 }catch (BindException e) {
				LOGGER.log(Level.SEVERE, "Unable to Start Listening at UDP port ["+port+"]");
				System.err.println("Unable to Start Listening at UDP port ["+port+"]");
				System.exit(0);
			 }catch (SocketException e) {
				 e.printStackTrace();
			 }
		
		while (listenMode == true){
			
			
			//DatagramPacket 
			try {
				udplistener.receive(receivePacket);
				
				//m_data.setDatagramPacket(receivePacket); 
				//byte []  payload = receivePacket.getData();
				IMessage m_data  	= 	new Response( receivePacket.getAddress().getHostAddress() );
				byte [] payload 	= 	Arrays.copyOf( receivePacket.getData(), receivePacket.getLength() );
				int 	payloadSize = 	receivePacket.getLength();
				
				m_data.setPayload(payload);
				switch( payload[0] ){
					case (byte) 0xB0:
						LOGGER.log(Level.INFO, "Received a discription , ["+	bytesToHex( payload ) +"]");
						DatagramPacket ACK  = new DatagramPacket(def.ACK_DISC_PACK, 4, receivePacket.getAddress(), 7731);
						udplistener.send(ACK);
						LOGGER.log(Level.INFO, " Sent an acknowledgement to Sensor,  ["+	bytesToHex( def.ACK_DISC_PACK ) +"]");
						break;
					case (byte) 0xA1:
						LOGGER.log(Level.INFO, "Discription ACK_DISC  , ["+	bytesToHex( payload ) +"]");
						IQueue.add(m_data);
						//LOGGER.log(Level.INFO, "Grazie,  ["+	bytesToHex( def.ACK_DISC_PACK ) +"]");
						break;
					case (byte) 0xC1:
						LOGGER.log(Level.INFO, "Received Temperature , ["+	bytesToHex( payload ) +"]");
						int num =  getInt( receivePacket.getData(), 2, receivePacket.getLength() ); // big-endian by default
						LOGGER.log(Level.INFO, "Temperature is ,  ["+	num +"]");
						break;
					case (byte) 0xC2:
						LOGGER.log(Level.INFO, "Received Accelometer , ["+	bytesToHex( payload ) +"]");
						
						//LOGGER.log(Level.INFO, "Grazie,  ["+	bytesToHex( def.ACK_DISC_PACK ) +"]");
						break;
						
					case (byte) 0xA0:
						LOGGER.log(Level.INFO, "Received ACK_PAYLOAD  , ["+	bytesToHex( payload ) +"]");
						
						//LOGGER.log(Level.INFO, "Grazie,  ["+	bytesToHex( def.ACK_DISC_PACK ) +"]");
						break;
						
					default:
						LOGGER.log(Level.INFO, "default, ["+	bytesToHex( payload ) +"]");
						
				}
				
				
				
				
				
			/*	String receiveStr = new String( receivePacket.getData().toString() );
				byte [] data = receivePacket.getData();
							
				dump(data);
				
				System.out.println(bytesToHex(data) );
				System.out.println("Received length = [" + receivePacket.getLength() + ","+ receiveStr +"] from Node ["+receivePacket.getAddress().getHostAddress()+"]");*/
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			
		}
		
	}
	public void stop(){
		listenMode = false;
	}
	
	private int getInt(byte [] payload, int start, int end){
		ByteBuffer buf =  ByteBuffer.wrap(Arrays.copyOfRange( payload,  start, end) ); // big-endian by default
		return buf.getInt();
	}
	private void dump(byte[] payload_data){		 
		 //print the raw received bytes of the payload
		 		System.out.print("Raw bytes of payload:");
		 		for (int i=0;i<payload_data.length;i++) {
		 			System.out.print(Integer.toHexString(payload_data[i]) + "  ");
		 		}
		 		System.out.println();

	}

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
}
