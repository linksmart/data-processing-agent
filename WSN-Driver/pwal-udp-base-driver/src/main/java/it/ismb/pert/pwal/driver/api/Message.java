/**
 * This file is a part of the basic OSGi tutorial. This is tutorial will be
 * consist of many breakdowns. This tutorial is growing step
 * by step to cover all the technical directive for building a good
 * OSGi applications.
 */
package it.ismb.pert.pwal.driver.api;

import java.net.DatagramPacket;

public abstract class Message implements IMessage {
	
	private DatagramPacket packet = null;
	private byte [] payload;
	private int payloadSize;
//	protected MessageType type;
	String srcAddress = null;
	String dstAddress = null;
	MessageType type = MessageType.ANNOUNCEMENT;
	
	@Override
	public DatagramPacket getDatagramPacket() {
		if (packet == null) throw new NullPointerException();
		return packet;
	}
	@Override
	public void setDatagramPacket(DatagramPacket packet) {
		if (packet == null) throw new NullPointerException();
		this.packet = packet;
		
	}
	public byte [] getPayload() {
		return payload;
	}
	public void setPayload(byte [] payload) {
		this.payload = payload;
	}
	public int getPayloadSize() {
		return payloadSize;
	}
	public void setPayloadSize(int payloadSize) {
		this.payloadSize = payloadSize;
	}
	public void setDstAddress(String dstAddress) {
		this.dstAddress = dstAddress;
		
	}
	@Override
	public MessageType getType() {
		return type;
		
	}
	@Override
	public String getSrcAddress() {
		return srcAddress;
		
	}
	@Override
	public String getDstAddress() {
		
		return dstAddress;
	}
	

}
