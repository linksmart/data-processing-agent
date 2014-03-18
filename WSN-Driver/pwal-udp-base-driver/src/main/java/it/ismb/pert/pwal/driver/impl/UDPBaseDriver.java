/**
 * This file is a part of the basic OSGi tutorial. This is tutorial will be
 * consist of many breakdowns. This tutorial is growing step
 * by step to cover all the technical directive for building a good
 * OSGi applications.
 */
package it.ismb.pert.pwal.driver.impl;

import java.net.DatagramPacket;
import java.util.LinkedList;
import java.util.Queue;

import it.ismb.pert.pwal.driver.api.IMessage;
import it.ismb.pert.pwal.driver.api.Response;

public class UDPBaseDriver extends AUDPBaseDriver {
	
	private static UDPBaseDriver instance  = null;
	
	
	
	private UDPBaseDriver(){		
	}
	
	public IMessage send(IMessage request) {
		OQueue.add(request);
		IMessage response = new Response(""); 
		//DatagramPacket req  = request.getDatagramPacket();	
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
