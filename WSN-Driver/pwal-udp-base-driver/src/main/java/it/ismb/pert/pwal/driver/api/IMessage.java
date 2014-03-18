/**
 * This file is a part of the basic OSGi tutorial. This is tutorial will be
 * consist of many breakdowns. This tutorial is growing step
 * by step to cover all the technical directive for building a good
 * OSGi applications.
 */
package it.ismb.pert.pwal.driver.api;
import java.net.DatagramPacket;
public interface IMessage {
	int sqn = 0;
	final int length = 1024; //buffer length in bytes	
	

	public  DatagramPacket getDatagramPacket();
	public  void setDatagramPacket(DatagramPacket packet);
	public byte [] getPayload();
	public void setPayload(byte [] payload);
	public int getPayloadSize();
	public void setPayloadSize(int payloadSize);
	public MessageType getType();
	public String getSrcAddress();
	public String getDstAddress();
}
