package eu.linksmart.gc.utils.mqtt.broker;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.MalformedURLException;
import java.util.*;

/**
 * Created by José Ángel Carvajal on 07.08.2015 a researcher of Fraunhofer FIT.
 */
public class StaticBrokerService extends BrokerService {
    static Map<String,StaticBrokerService> brokerServices = new Hashtable<String, StaticBrokerService>();
    static UUID ID = UUID.randomUUID();
    protected  ArrayList<String> clients = new  ArrayList<String>();

    private StaticBrokerService(String brokerName, String brokerPort, UUID ID, String serviceDescription) throws MqttException {
        super(brokerName, brokerPort, ID, serviceDescription);
    }

    static void setUUID(UUID uuid){
        ID = uuid;
    }

    static public StaticBrokerService getBrokerService(String clientID, UUID uuid, String name, String port) throws MalformedURLException, MqttException {

        String url = BrokerService.getBrokerURL(name,port);



        if(BrokerService.isBrokerURL(url))
            throw new MalformedURLException(url+" is not an broker URL");
        if(brokerServices.containsKey(url))
            return brokerServices.get(url);

        brokerServices.put(url,new StaticBrokerService(name,port,uuid,url));


        return brokerServices.get(url);

    }
    static public StaticBrokerService getBrokerService(String clientID, String brokerName, String port) throws MalformedURLException, MqttException {
        return getBrokerService(clientID,ID, brokerName, port);

    }
    static public StaticBrokerService getDefaultBrokerService(String clientID) throws MalformedURLException, MqttException {
        return getBrokerService(clientID,"localhost","1883");

    }
    public void connect() throws Exception {
        throw new UnsupportedOperationException("The method is not possible for the class "+StaticBrokerService.class.getCanonicalName());

    }
    public void disconnect() throws Exception {
        throw new UnsupportedOperationException("The method is not possible for the class "+StaticBrokerService.class.getCanonicalName());

    }
    public void destroy() throws Exception {

        throw new UnsupportedOperationException("The method is not possible for the class "+StaticBrokerService.class.getCanonicalName());

    }
    public boolean connect(String clientID) throws Exception {

        if(clients.contains(clientID)){

            _connect();

        }else
            throw new Exception("This BrokerService do not contain any client with ID: "+clientID);

        return isConnect();
    }
    public void disconnect(String clientID) throws Exception {
        if(clients.contains(clientID)){

            if(clients.size() == 1){

                _disconnect();
            }


        }else
            throw new Exception("This BrokerService do not contain any client with ID: "+clientID);
    }
    public void destroy(String clientID) throws Exception {
            if(clients.contains(clientID)){

                if(clients.size() == 1){
                    remove(this);
                    _destroy();
                }
                clients.remove(clientID);

            }else
                throw new Exception("This BrokerService do not contain any client with ID: "+clientID);

    }
    protected static void remove( StaticBrokerService staticBrokerService) throws Exception {
        if(brokerServices.get(staticBrokerService.getBrokerURL()) == staticBrokerService)
            throw new Exception("The BrokerService "+
                    String.valueOf((Object)staticBrokerService.hashCode())+" is not the same as "+
                    String.valueOf((Object) brokerServices.get(staticBrokerService.getBrokerURL()).hashCode())+ ". Therefore is not able to remove from the service pool"
        );

        brokerServices.remove(staticBrokerService.getBrokerURL());
    }


}
