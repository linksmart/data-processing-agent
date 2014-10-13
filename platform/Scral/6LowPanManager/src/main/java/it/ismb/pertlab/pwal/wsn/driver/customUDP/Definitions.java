package it.ismb.pertlab.pwal.wsn.driver.customUDP;

import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;

public  class Definitions {
	//#########################################;
	public static final byte REQ_DISC 	 	= 	(byte) 0xB0;
	public static final byte REQ_SENSORS 	= 	(byte) 0xB1;
	public static final byte REQ_DATA	 	= 	(byte) 0xDD;
	//#########################################;
	public static final byte SENSOR_ALL 	=	(byte) 0xC0;
	public static final byte ACK_DISC_ACK 	=	(byte) 0xA1;
	public static final byte ACK_DISC 		=	(byte) 0xA0;
	public static final byte SENSOR_TEMP 	=	(byte) 0xC1;
	public static final byte SENSOR_ACCEL 	=	(byte) 0xC2;
	public static final byte SENSOR_LIGHT 	= 	(byte) 0xC3;
	public static final byte SENSOR_HUMIDITY= 	(byte) 0xC4;
	public static final byte SENSOR_DISTANCE= 	(byte) 0xC5;
	
	
	//#########################################;
	// DISCOVERY BROADCAST MSG FROM SENSOR (0):
	// DISCOVERY = { REQ_DISC, null, null, null};
	public byte[] DISCOVERY = { REQ_DISC, 0x00, 0x00, 0x00};
	// JAVA’S ACKNOWLEDGMENT MSG TO THE SENSOR (0.1):
	// ACKNOWLEDGMENT = { ACK_DISC, SEQ_NUM, null, null};
	public static final byte []  ACK_DISC_PACK = { ACK_DISC , 0x00, 0x00, 0x00 };

	
	public static String getCorresponsidngDeviceType(byte wsn_type)
	{
		switch(wsn_type){
			case Definitions.SENSOR_TEMP:
				return DeviceType.THERMOMETER;
			case Definitions.SENSOR_ACCEL:
				return DeviceType.ACCELEROMETER;
			case Definitions.SENSOR_LIGHT:
				return DeviceType.LIGHT_SENSOR;
			case Definitions.SENSOR_HUMIDITY:
				return DeviceType.HUMIDITY_SENSOR;
			case Definitions.SENSOR_DISTANCE:
				return DeviceType.DISTANCE_SENSOR;
		}
		return null;
	}
	
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

