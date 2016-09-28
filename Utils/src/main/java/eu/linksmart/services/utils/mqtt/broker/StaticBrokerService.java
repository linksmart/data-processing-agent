package eu.linksmart.services.utils.mqtt.broker;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.MalformedURLException;
import java.util.*;

/**
 * Created by José Ángel Carvajal on 07.08.2015 a researcher of Fraunhofer FIT.
 */
public class StaticBrokerService extends BrokerService implements Broker {
    static Map<BrokerConfiguration, StaticBrokerService> brokerServices = new Hashtable<>();

    protected static Map<UUID,BrokerConfiguration> clients = new Hashtable<>();


    private StaticBrokerService(String alias, UUID ID) throws MqttException {
        super(alias, ID);

    }
    static public StaticBrokerService getBrokerService(UUID uuid, String alias) throws MalformedURLException, MqttException {

        BrokerConfiguration bConf = new BrokerConfiguration(alias);

        if (!Broker.isBrokerURL(bConf.getURL()))
            throw new MalformedURLException(bConf.getURL() + " is not an broker URL");
        if (brokerServices.containsKey(bConf)) {
            if (!clients.containsKey(uuid))
                clients.put(uuid, bConf);
            return brokerServices.get(bConf);
        }

        brokerServices.put(bConf, new StaticBrokerService(alias, uuid));

        if (!clients.containsKey(uuid))
            clients.put(uuid, bConf);

        return brokerServices.get(bConf);

    }
    static public StaticBrokerService getBrokerService( String brokerName, String port) throws MalformedURLException, MqttException {
        return getBrokerService(UUID.randomUUID(), "");

    }
    static public StaticBrokerService getBrokerService(String alias) throws MalformedURLException, MqttException {
        return getBrokerService("localhost","1883");

    }
    static public StaticBrokerService getDefaultBrokerService(String clientID) throws MalformedURLException, MqttException {
        return getBrokerService("localhost","1883");

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

    @Override
    public String getAlias() {
        throw new UnsupportedOperationException("The method is not possible for the class "+StaticBrokerService.class.getCanonicalName());
    }
    public String getAlias(UUID clientID) {
        if(clients.containsKey(clientID))
            return brokerServices.get(clients.get(clientID)).getAlias();

        return null;
    }
    public boolean connect(UUID clientID) throws Exception {

        if(clients.containsKey(clientID)){

            _connect();

        }else
            throw new Exception("This BrokerService do not contain any client with ID: "+clientID);

        return isConnected(clientID);
    }
    public void disconnect(UUID clientID) throws Exception {
        if(clients.containsKey(clientID)){

            if(clients.size() == 1){

                _disconnect();
            }

        }else
            throw new Exception("This BrokerService do not contain any client with ID: "+clientID);
    }
    public void destroy(UUID clientID) throws Exception {
            if(clients.containsKey(clientID)){

                if(clients.size() == 1){
                    remove(this);
                    _destroy();
                }
                clients.remove(clientID);

            }else
                throw new Exception("This BrokerService do not contain any client with ID: "+clientID);

    }
    protected static void remove( StaticBrokerService staticBrokerService) throws Exception {
        if(brokerServices.get(staticBrokerService.getBrokerURL()) != staticBrokerService)
            throw new Exception("The BrokerService "+
                    String.valueOf((Object)staticBrokerService.hashCode())+" is not the same as "+
                    String.valueOf((Object) brokerServices.get(staticBrokerService.getBrokerURL()).hashCode())+ ". Therefore is not able to remove from the service pool"
        );

        brokerServices.remove(staticBrokerService.getBrokerURL());
    }
    public boolean isConnected()  {

        throw new UnsupportedOperationException("The method is not possible for the class "+StaticBrokerService.class.getCanonicalName());
    }
    public boolean isConnected(UUID clientID)  {

        return clients.containsKey(clientID) && mqttClient.isConnected();


    }


}
