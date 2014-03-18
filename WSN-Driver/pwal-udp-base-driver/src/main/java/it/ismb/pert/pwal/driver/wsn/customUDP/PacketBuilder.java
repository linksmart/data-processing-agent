package it.ismb.pert.pwal.driver.wsn.customUDP;

public class PacketBuilder {
	byte [] payload = null;
	int cnt = 0;
	public byte[] getPayload(){
		return payload;
	}
	public void addByte(byte data){
		byte [] tmp = new byte[++cnt];
		for( int i = 0; i< cnt - 1;i++ )
			tmp [i]  = payload[i];
		tmp [cnt-1]  = data;
		payload = tmp;
	}
	
	public static byte [] getTempratureRequest(){
		PacketBuilder pckt = new PacketBuilder();
		pckt.addByte((byte) (0xDD));
		pckt.addByte((byte) (0x31));
		pckt.addByte((byte) (0xC1));
		return pckt.getPayload();
	}
	public static byte [] getAcclerometerRequest(){
		PacketBuilder pckt = new PacketBuilder();
		pckt.addByte((byte) (0xDD));
		pckt.addByte((byte) (0x31));
		pckt.addByte((byte) (0xC2));
		return pckt.getPayload();
	}

	public static byte [] getAllRequest(){
		PacketBuilder pckt = new PacketBuilder();
		pckt.addByte((byte) (0xDD));
		pckt.addByte((byte) (0x31));
		pckt.addByte((byte) (0xC0));
		return pckt.getPayload();
	}
	
}
