package it.ismb.pertlab.pwal.wsn.driver.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TelosBDriver {	
	private final static Logger  LOGGER = Logger.getLogger( TelosBDriver.class.getName() );
	InetAddress IPAddress   	= null;
	private int port 			= -1;
	private int UDPBufferLength = -1;
	
	private UDPBaseDriver connector = UDPBaseDriver.getInstance();	
	@SuppressWarnings("static-access")
	public TelosBDriver(Properties props){
		LOGGER.setLevel(Level.FINEST);
		String ip = props.getProperty("ip", "127.0.0.1"); //IP address for specific wireless node
		String p =  props.getProperty("port", "8542");    //UDP port number for specific wireless node
		String length =  props.getProperty("UDPBufferLength", "1024");// DataGramPacket Buffer Length
		port = Integer.parseInt(p);
		UDPBufferLength  = Integer.parseInt(length);
		
		try {
			this.IPAddress = InetAddress.getByName(ip).getLocalHost();
		} catch (UnknownHostException e) {			
			e.printStackTrace();
		}
		if(LOGGER.isLoggable(Level.INFO)){
			LOGGER.info( "Add a new TelosBDriver(ip="+ip+", port="+p+")  Node" );
		}
	}
	
	
//	public float getTemprature(){
//		LOGGER.info( "TelosB["+IPAddress.getHostAddress()+"]-->getTemprature()" );
//		return get( 0x00f);
//	}
//	public float getHumidity(){
//		LOGGER.info( "TelosB["+IPAddress.getHostAddress()+"]-->getHumidity()" );
//		return get( 0x00f);
//	}
//	public float getLuminosity(){
//		LOGGER.info( "TelosB["+IPAddress.getHostAddress()+"]-->getLuminosity()" );
//		return get( 0x00f);
//	}
	
//	public float get( int code){
//		LOGGER.info( "calling TelosB["+IPAddress.getHostAddress()+"]-->get()" );
//		int length = 1024;
//		byte [] buffer    = new byte[this.UDPBufferLength];
//		IMessage request  = new Request( buffer, this.IPAddress, this.port  );
//		IMessage response = connector.send(request);
//		LOGGER.info( "return TelosB["+IPAddress.getHostAddress()+"]-->get()" );
//		return 0;
//	}
}
