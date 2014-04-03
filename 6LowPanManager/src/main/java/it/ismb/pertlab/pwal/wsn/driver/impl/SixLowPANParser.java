package it.ismb.pertlab.pwal.wsn.driver.impl;

import it.ismb.pertlab.pwal.wsn.driver.api.IMessage;
import it.ismb.pertlab.pwal.wsn.driver.api.impl.Convertor;

import java.nio.ByteBuffer;


public class SixLowPANParser extends Convertor {

	public SixLowPANParser() {
			
	}
	
	@Override
	public IMessage net2host(Object input) {
		
		return null;
	}

	@Override
	public Object host2net(IMessage input) {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * 
	 * @param bytes
	 * @param index
	 * @return bytes[index]
	 */
	public byte bytes2byte( byte [] bytes, int index){
		return bytes[index];		
	}
	/**
	 * 
	 * @param bytes
	 * @param index
	 * @return
	 */
	public short bytes2short ( byte [] bytes, int index){
		return  (short) ((bytes[index] << 8) | (bytes[index+1] & 0xFF));		
	}
	/**
	 * 
	 * @param bytes
	 * @param index
	 * @return
	 */	
	public int bytes2int ( byte [] bytes, int index){
		//return  (short) ((bytes[index] << 8) | (bytes[index+1] & 0xFF));
		return (int) (bytes[index] << 24) | (bytes[index+1] << 16) | 
				     (bytes[index+2] << 8) | bytes[index+3];
	}
	
	public byte[] int2bytes(int value){
		return ByteBuffer.allocate(4).putInt(value).array();
	}
	public byte[] shor2bytes(short value){
		return ByteBuffer.allocate(2).putShort(value).array();
	}
	
}
