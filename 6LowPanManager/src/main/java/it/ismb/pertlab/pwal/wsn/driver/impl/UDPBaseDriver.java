/**
 * This file is a part of the basic OSGi tutorial. This is tutorial will be
 * consist of many breakdowns. This tutorial is growing step
 * by step to cover all the technical directive for building a good
 * OSGi applications.
 */
package it.ismb.pertlab.pwal.wsn.driver.impl;

import it.ismb.pertlab.pwal.wsn.driver.api.IMessage;
import it.ismb.pertlab.pwal.wsn.driver.api.Response;

import java.net.DatagramPacket;

public class UDPBaseDriver extends AUDPBaseDriver {
	
	private static UDPBaseDriver instance  = null;
	
	
	
	private UDPBaseDriver(){		
	}
	
	public IMessage send(IMessage request) {
		OQueue.add(request);
		IMessage response = new Response(""); 
		DatagramPacket resp = super.send(request.getDatagramPacket());
		response.setDatagramPacket(resp);
		return response;
	}
	
	
	public IMessage receive() {
		return IQueue.poll();		
	}
	
	public static UDPBaseDriver getInstance() {
		 if (instance == null) return new UDPBaseDriver(); 		
		 return instance;		
	}
	

}
