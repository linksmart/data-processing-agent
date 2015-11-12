package eu.linksmart.gc.utils.mqtt.types;

import eu.linksmart.gc.utils.logging.LoggerService;
import eu.linksmart.gc.utils.mqtt.subscription.ForwardingListener;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.UUID;

/**
 * Created by Caravajal on 25.03.2015.
 */
public class MqttMessage implements Serializable {
    protected final static LoggerService LOG= new LoggerService(LoggerFactory.getLogger(ForwardingListener.class));
    private static final long serialVersionUID = -1353455745007862618L;
    private String topic = null;
    private byte[] payload = null;
    private int QoS = -1;

    private boolean retained ;
    private long sequence ;

    private final UUID originProtocol;

    public MqttMessage() {
        this.originProtocol = null;
    }

    public MqttMessage(String topic, byte[] payload, int qoS, boolean retained, long sequence, UUID originProtocol) {
        this.topic = topic;
        this.payload = payload;
        QoS = qoS;
        this.retained = retained;
        this.sequence = sequence;
        this.originProtocol = originProtocol;
    }


    public int getQoS() {
        return QoS;
    }

    public void setQoS(int qoS) {
        QoS = qoS;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }



    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public  byte[] toBytes()  {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(this);
            byte[] encoded = bos.toByteArray();
            out.close();
            bos.close();
            return encoded;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    public static MqttMessage deserialize(byte[] bytes)  {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream in;
        Object o ;
        try {
            in = new ObjectInputStream(bis);
            o = in.readObject();
            bis.close();
            in.close();
        } catch (Exception e) {
            LOG.error(e.getMessage(),e);
            return null;
        }

        return (MqttMessage)o;
    }

    public boolean isRetained() {
        return retained;
    }

    public void setRetained(boolean retained) {
        this.retained = retained;
    }

    public long getSequence() {
        return sequence;
    }

    public boolean isGenerated(){
        return sequence==-1L;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public UUID getOriginProtocol() {
        return originProtocol;
    }
    public String getMessageHash(){
        return originProtocol.toString()+"#"+topic+"#"+sequence;
    }


}
