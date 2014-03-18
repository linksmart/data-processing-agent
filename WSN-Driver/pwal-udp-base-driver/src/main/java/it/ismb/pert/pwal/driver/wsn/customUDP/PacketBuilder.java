package it.ismb.pert.pwal.driver.wsn.customUDP;

public class PacketBuilder {
	
	byte [] payload = null;//payload place holder 
	int cnt = 0; //number of inserted bytes
	
	/**
	 * 
	 * @return
	 */
	public byte[] getPayload(){
		return payload;
	}
	/**
	 * 
	 * @param data
	 */
	public void addByte(byte data){
		byte [] tmp = new byte[++cnt];
		for( int i = 0; i< cnt - 1;i++ )
			tmp [i]  = payload[i];
		tmp [cnt-1]  = data;
		payload = tmp;
	}
	/**
	 * 
	 * @return
	 */
	public static byte [] getTempratureRequest(){
		PacketBuilder pckt = new PacketBuilder();
		pckt.addByte((byte) (Definitions.REQ_DATA));
		pckt.addByte((byte) (0x31));
		pckt.addByte((byte) (Definitions.SENSOR_TEMP));
		return pckt.getPayload();
	}
	/**
	 * 
	 * @return
	 */
	
	public static byte [] getAcclerometerRequest(){
		PacketBuilder pckt = new PacketBuilder();
		pckt.addByte((byte) (Definitions.REQ_DATA));
		pckt.addByte((byte) (0x31));
		pckt.addByte((byte) (Definitions.SENSOR_ACCEL));
		return pckt.getPayload();
	}
	/**
	 * 
	 * @return byte [] of payload of UDP packet
	 */
	public static byte [] getAllRequest(){
		PacketBuilder pckt = new PacketBuilder();
		pckt.addByte((byte) (Definitions.REQ_DATA));
		pckt.addByte((byte) (0x31));
		pckt.addByte((byte) (Definitions.SENSOR_ALL));
		return pckt.getPayload();
	}
	
	
	
}
