package eu.linksmart.gc.supernode;

import org.apache.log4j.Logger;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Created by carlos on 24.11.14.
 */
public class Message {

    public String topic;
    public byte version;
    public byte type;
    public long timestamp;
    public String sender;
    public String requestID;
    public byte[] payload;

    private static Logger LOG = Logger.getLogger(Message.class.getName());

    public static long deserializeTimestamp(byte[] raw){

        ByteBuffer unixTime = ByteBuffer.wrap(raw);
        return unixTime.getLong();

    }
    public static byte[] serializeTimestamp(){
        long timestamp = System.currentTimeMillis();
        return ByteBuffer.allocate(8).putLong(timestamp).array();
    }

    public static void printMessage(Message msg) {
        LOG.trace("*********************************************");
        LOG.trace("message topic   : " + msg.topic);
        LOG.trace("message version : " + String.format("%02x", msg.version & 0xff));
        LOG.trace("message type    : " + String.format("%02x", msg.type & 0xff));
        LOG.trace("message time    : " + msg.timestamp);
        LOG.trace("message sender  : " + msg.sender);
        LOG.trace("message reqID   : " + msg.requestID);
        LOG.trace("message payload : " + new String(msg.payload));
        LOG.trace("*********************************************");
    }

    public static boolean isUUID(String rawUUID) {
        try {
            UUID parsed = java.util.UUID.fromString(rawUUID);
            LOG.trace("parsed UUID : " + parsed.toString());
            return true;
        } catch (IllegalArgumentException ex) {
            LOG.warn("Could not parse an UUID.",ex);
            return false;
        }
    }
}

