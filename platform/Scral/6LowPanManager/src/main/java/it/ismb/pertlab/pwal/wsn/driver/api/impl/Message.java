package it.ismb.pertlab.pwal.wsn.driver.api.impl;

import it.ismb.pertlab.pwal.wsn.driver.api.IMessage;
import it.ismb.pertlab.pwal.wsn.driver.api.MessageType;

import java.net.DatagramPacket;

public abstract class Message implements IMessage {
	
	private DatagramPacket packet = null;
	private byte [] payload;
	private int payloadSize;
//	protected MessageType type;
	protected String srcAddress = null;
	String dstAddress = null;
	protected MessageType type = MessageType.ANNOUNCEMENT;
	
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
