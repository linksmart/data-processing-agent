package eu.linksmart.gc.api.types;

import java.io.*;
import java.util.UUID;

/**
 * Created by Caravajal on 25.03.2015.
 */
public class MqttTunnelledMessage implements Serializable {
    private static final long serialVersionUID = 5813599804744554953L;

    private String topic = null;
    private byte[] payload = null;
    private int QoS = -1;

    private boolean retained ;
    private long sequence ;

    private final UUID originProtocol;

    public MqttTunnelledMessage() {
        this.originProtocol = null;
    }

    public MqttTunnelledMessage(String topic, byte[] payload, int qoS, boolean retained, long sequence, UUID originProtocol) {
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
            e.printStackTrace();
            return null;
        }
    }

    public static MqttTunnelledMessage deserialize(byte[] bytes)  {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream in = null;
        Object o = null;
        try {
            in = new ObjectInputStream(bis);
            o = in.readObject();
            bis.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return (MqttTunnelledMessage)o;
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
