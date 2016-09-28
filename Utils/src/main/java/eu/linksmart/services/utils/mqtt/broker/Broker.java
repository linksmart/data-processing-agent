package eu.linksmart.services.utils.mqtt.broker;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Observable;
import java.util.Observer;
import java.util.regex.Pattern;

/**
 * Created by José Ángel Carvajal on 23.10.2015 a researcher of Fraunhofer FIT.
 */
public interface Broker extends Observer{

    static final Pattern ipPattern = Pattern.compile("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+"), urlPattern = Pattern.compile("\\b(tcp|ws|ssl)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|][:[0-9]+]?");



    public boolean isConnected() ;
    public void connect() throws Exception ;
    public void disconnect() throws Exception ;
    public void destroy() throws Exception;
    public String getBrokerURL();

    public void createClient() throws MqttException;


    public boolean isWatchdog() ;

    public void startWatchdog() ;
    public void stopWatchdog();



    public void publish(String topic, byte[] payload, int qos, boolean retained) throws Exception;
    public void publish(String topic, byte[] payload) throws Exception ;
    public void publish(String topic, String payload) throws Exception;
    public String getBrokerName();

    public void setBrokerName(String brokerName) throws Exception ;

    public String getBrokerPort();

    public void setBrokerPort(String brokerPort) throws Exception ;
    public void setBroker(String brokerName, String brokerPort) throws Exception;

    public  boolean addListener(String topic, Observer stakeholder)  ;
    public  boolean addListener(String topic, Observer stakeholder, int QoS);
    public  boolean removeListener(String topic, Observer stakeholder);
    public  void removeListener( Observer stakeholder);

    public String getAlias();

    @Override
    public void update(Observable o, Object arg) ;

    public static String getBrokerURL(String brokerName, String brokerPort){

        if (ipPattern.matcher(brokerName).find())
            return "tcp://"+brokerName+":"+brokerPort;
        else
            return "tcp://"+brokerName;
    }
    public static String getSecureBrokerURL(String brokerName, String brokerPort){

        if (ipPattern.matcher(brokerName).find())
            return "ssl://"+brokerName+":"+brokerPort;
        else
            return "ssl://"+brokerName;
    }
    public static String getBrokerURL(String brokerName, int brokerPort){
        return getBrokerURL(brokerName,String.valueOf(brokerPort));
    }
    public static String getSecureBrokerURL(String brokerName, int brokerPort){
        return getSecureBrokerURL(brokerName, String.valueOf(brokerPort));
    }
    public static String getBrokerURL(String brokerName, String brokerPort, boolean isSSL_URL){
        if (isSSL_URL)
            return getSecureBrokerURL(brokerName,brokerPort);
        else
            return getBrokerURL(brokerName, brokerPort);
    }
    public static boolean isBrokerURL(String string){
        return  urlPattern.matcher(string).find();

    }
}
