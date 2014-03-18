/**
 * This file is a part of the basic OSGi tutorial. This is tutorial will be
 * consist of many breakdowns. This tutorial is growing step
 * by step to cover all the technical directive for building a good
 * OSGi applications.
 */
/**
*@author Shadi Atalla 
*@place ISMB
*/
package it.ismb.pert.pwal.driver.osgi;

//import it.ismb.pert.pwal.driver.api.*;
import it.ismb.pert.pwal.driver.api.IMessage;
import it.ismb.pert.pwal.driver.api.Request;
import it.ismb.pert.pwal.driver.impl.*;
import it.ismb.pert.pwal.driver.udptester.UDPServer;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import sun.applet.Main;




public class Activator implements BundleActivator{
	
	private static final Logger LOG = Logger.getLogger(Activator.class.getName());	
	private BundleContext bc;
	
	TelosBDriver telosb  = null;
	static UDPServer udplistener = null;
	Queues queues = Queues.getInstance();
	Queue<IMessage> IQueue = queues.getIQueue();
	Queue<IMessage> OQueue = queues.getOQueue();
	
	@Override
	public void start (BundleContext bc){
		this.bc	= bc;
		System.out.println("PWAL Activator ->  Started!!!");
		
		Properties props = new Properties();
		props.put("ip", "127.0.0.1");
		props.put("port", "10002");
		//this.telosb = new TelosBDriver(props);

		
		javax.swing.SwingUtilities.invokeLater(
				
				
				
				new Runnable() {					
					@Override
					public void run() {
						Activator.udplistener 	= new UDPServer(7730);
						AUDPBaseDriver dr 		= new AUDPBaseDriver();
						System.out.println("##############################################" );
						
						while (true){
							if (IQueue.isEmpty() == false){	
								LOG.log(Level.INFO, "IQueue is not empty");
								IMessage IM = (IMessage) IQueue.poll();
								byte [] payload= IM.getPayload();
								//DatagramPacket receivePacket = IM.getDatagramPacket();
						 		//String receiveStr = new String( receivePacket.getData().toString() );
								//byte [] data = receivePacket.getData();
								//
								LOG.log(Level.INFO, IM.getType()+" is received from ["+ IM.getSrcAddress() +"]");
								String dstIP = IM.getSrcAddress();
								
								IMessage OM = new Request("01201".getBytes(), dstIP , 7731);
								OQueue.add(OM);  
								//System.out.println("## ###### #######################################" );
							}
							try {
								Thread.sleep(1);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							};
						}		
					}
				}
				);
		
		
	
		
		
		//bc.registerService(CommandProvider.class.getName(), new Tester(telosb), null);		
		
//		telosb.getTemprature();
//		System.out.println("After get");
//		telosb.getLuminosity();
//		telosb.getHumidity();
		//Hashtable<String, String> deviceProperties = new Hashtable<String, String>();
/*		deviceProperties.put(org.osgi.service.device.Constants.DEVICE_CATEGORY, "tuner");
		deviceProperties.put(org.osgi.service.device.Constants.DEVICE_SERIAL, "A12");*/
		//deviceProperties.put(org.osgi.framework.Constants.SERVICE_PID, "my.device.tuner");
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

	@Override
	public void stop(BundleContext bc){
		this.bc = null;
		this.udplistener.stop();
		System.out.println("PWAL Activator ->  !!!");
	}
}


