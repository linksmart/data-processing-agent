/**
 * This file is a part of the basic OSGi tutorial. This is tutorial will be
 * consist of many breakdowns. This tutorial is growing step
 * by step to cover all the technical directive for building a good
 * OSGi applications.
 */
package it.ismb.pert.pwal.driver.impl;

import it.ismb.pert.pwal.driver.api.IMessage;
import it.ismb.pert.pwal.driver.api.IUDPBaseDriver;
import it.ismb.pert.pwal.driver.api.Response;
import it.ismb.pert.pwal.driver.udptester.UDPServer;
import it.ismb.pert.pwal.driver.wsn.customUDP.PacketBuilder;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AUDPBaseDriver implements IUDPBaseDriver {	
	Queues queues = Queues.getInstance();
	Queue<IMessage> OQueue = queues.OQueue;
	Queue<IMessage> IQueue = queues.IQueue;	
	private final int m_port	=	7731; 
	private final static Logger  LOGGER = Logger.getLogger( AUDPBaseDriver.class.getName() );
	
	
	public AUDPBaseDriver() {
	// infinity looping thread in order to monitor if there are any Message to be sent to the WSN. 
	
		new Thread(){
			DatagramPacket request;			
			public void run(){ 
				while(true){		
					
					if ( OQueue.isEmpty() == false) // There are requests process them in order
					{
						IMessage req = OQueue.poll();  
						LOGGER.log(Level.INFO, "UDP Client is Sending to " + req.getDstAddress() + " @Port: "+ m_port);
						try {
							IMessage OM = OQueue.poll();	
							
							byte [] payload ;
							
							//request = new DatagramPacket(data, 2 , InetAddress.getByName("127.0.0.1"), m_port );
							
							DatagramPacket response = new DatagramPacket(new byte [1024], 1024);
							
							DatagramSocket client = new DatagramSocket();
							payload = PacketBuilder.getAllRequest();
							request = new DatagramPacket(payload, payload.length , InetAddress.getByName(req.getDstAddress()), m_port );							
							prettyPrint(request);
							client.send(request);	
							
							payload = PacketBuilder.getTempratureRequest();
							request = new DatagramPacket(payload, payload.length , InetAddress.getByName(req.getDstAddress()), m_port );							
							prettyPrint(request);
							client.send(request);	
							payload = PacketBuilder.getAcclerometerRequest();
							request = new DatagramPacket(payload, payload.length , InetAddress.getByName(req.getDstAddress()), m_port );							
							prettyPrint(request);
							
							//client.setSoTimeout(1);
							client.send(request);							
							
							//client.receive(response);
							//prettyPrint(response);
							
							//IMessage resp = new Response(response.getAddress().getHostAddress());
							//resp.setPayload(response.getData());
							//IQueue.add(resp);
							client.close();			
						} catch (SocketException e) {
							System.out.println("Socket Error");
							e.printStackTrace();  
						}
						catch (IOException e) {
							e.printStackTrace();
						}
						
					}
					else{
						try {
							sleep(1);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				}
			}
		}.start();
	}
	/**
	 * @brief This method is inherited from the Interface method stub
	 * @see {@code}
	 */
	@Override
	public DatagramPacket send(DatagramPacket request) {
		
			new ProcessRequest(request).start();
		
	return null;
}

	public void prettyPrint(DatagramPacket packet){
		System.out.println("Dst IP: " + packet.getAddress().getHostAddress());
		System.out.println("Dst Port: " + packet.getPort());
		System.out.println("Payload: " + bytesToHex(packet.getData() ));
		
		
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

class ProcessRequest extends Thread{
	private DatagramPacket request = null;
	 public ProcessRequest(DatagramPacket request){
		 this.request = request;
	 }
	 public void run(){
		 
		 DatagramPacket response = new DatagramPacket(new byte [1024], 1024);		
			try {			
				DatagramSocket client = new DatagramSocket();
				client.send(request);
				client.setSoTimeout(MIN_PRIORITY);
				client.receive(response);
				client.close();			
			} catch (SocketException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			
		 
	 }
	 public void done(){
		 
	 }
	 public void error(){
		 
	 }
}


