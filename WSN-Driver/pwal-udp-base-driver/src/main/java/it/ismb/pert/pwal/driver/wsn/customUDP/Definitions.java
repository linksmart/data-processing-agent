package it.ismb.pert.pwal.driver.wsn.customUDP;

public  class Definitions {
	//#########################################;
	public static final byte REQ_DISC 	 	= 	(byte) 0xB0;
	public static final byte REQ_SENSORS 	= 	(byte) 0xB1;
	public static final byte REQ_DATA	 	= 	(byte) 0xDD;
	//#########################################;
	public static final byte SENSOR_ALL 	=	(byte) 0xC0;
	public static final byte SENSOR_TEMP 	=	(byte) 0xC1;
	public static final byte SENSOR_ACCEL 	=	(byte) 0xC2;
	public static final byte ACK_PAYLOAD 	=	(byte) 0xA1;
	public static final byte ACK_DISC 		=	(byte) 0xA0;
	//#########################################;
	// DISCOVERY BROADCAST MSG FROM SENSOR (0):
	// DISCOVERY = { REQ_DISC, null, null, null};
	public byte[] DISCOVERY = { REQ_DISC, 0x00, 0x00, 0x00};
	// JAVA’S ACKNOWLEDGMENT MSG TO THE SENSOR (0.1):
	// ACKNOWLEDGMENT = { ACK_DISC, SEQ_NUM, null, null};
	public static final byte []  ACK_DISC_PACK = { ACK_DISC , 0x00, 0x00, 0x00 };
	
	//SENSOR’S ACKNOWLEDGMENT MSG TO THE JAVA (0.2) // NOT YET IMPLEMENTED
//		Byte1
//		ACK_PAYLOAD ( all
//		acks from sensor node
//		will carry this as type)
//		Byte2
//		SEQ_NUM ( same
//		seq_num of the ack
//		received)

		

}


