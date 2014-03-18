/**
 * This file is a part of the basic OSGi tutorial. This is tutorial will be
 * consist of many breakdowns. This tutorial is growing step
 * by step to cover all the technical directive for building a good
 * OSGi applications.
 */
package it.ismb.pert.pwal.driver.api;

import java.net.DatagramPacket;

public class Response extends Message {
	public Response (String srcAddress){
		this.srcAddress = srcAddress;
	}

	@Override
	public MessageType getType() {
		
		return type;
	}

	
}
