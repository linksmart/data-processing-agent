package eu.linksmart.gc.supernode;

/**
 * Created by carlos on 25.11.14.
 */
public class Constants {

    public static final byte VERSION = 0x01;

//    Topic (string)
//    Version (byte)
//    Type (byte)
//    Timestamp (64-bit int)
//    Sender (string)
//    RequestID (int?)
//    Payload (byte array)
//
//    @see   https://linksmart.eu/redmine/projects/linksmart-globalconnect/wiki/ZMQ_Backbone



    public static final String mXSUB = "tcp://127.0.0.1:7000";
    public static final String mXPUB = "tcp://127.0.0.1:7001";

    public static final String HEARTBEAT_TOPIC            = "HEARTBEAT";
    public static final String BROADCAST_TOPIC            = "BROADCAST";
    public static final long   HEARTBEAT_INTERVAL         = 2000;
    public static final long   HEARTBEAT_TIMEOUT          = 5000;
    public static final byte   MSG_UNICAST                = 0x01;
    public static final byte   MSG_PEER_DISCOVERY         = 0x02;
    public static final byte   MSG_HEARTBEAT              = 0x03;
    public static final byte   MSG_PEERDOWN               = 0x04;
    public static final byte   MSG_SERVICE_DISCOVERY      = 0x05;

}
