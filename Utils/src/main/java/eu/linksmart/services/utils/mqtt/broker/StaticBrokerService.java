package eu.linksmart.services.utils.mqtt.broker;

import eu.linksmart.services.utils.configuration.Configurator;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.MalformedURLException;
import java.util.*;

/**
 * Created by José Ángel Carvajal on 07.08.2015 a researcher of Fraunhofer FIT.
 */
public class StaticBrokerService extends BrokerService implements Broker {
    static Map<String, StaticBrokerService> brokerServices = new Hashtable<>();

    protected static Map<UUID,BrokerConfiguration> clients = new Hashtable<>();


    private StaticBrokerService(String alias, UUID ID, String will, String willTopic) throws MqttException {
        super(alias, ID, will, willTopic);

    }
    static public StaticBrokerService getBrokerService(UUID uuid, String alias, String will, String willTopic) throws MalformedURLException, MqttException {

        BrokerConfiguration bConf = new BrokerConfiguration(alias);
        bConf.setWill(will);
        bConf.setWillTopic(willTopic);
        loggerService.info("Searching for proper broker...");
        boolean optimizeConnections =(Configurator.getDefaultConfig().containsKeyAnywhere(BrokerServiceConst.MULTI_CONNECTION) && !Configurator.getDefaultConfig().getBoolean(BrokerServiceConst.MULTI_CONNECTION) || !Configurator.getDefaultConfig().containsKeyAnywhere(BrokerServiceConst.MULTI_CONNECTION));
        if (!Broker.isBrokerURL(bConf.getURL()))
            throw new MalformedURLException(bConf.getURL() + " is not an broker URL");
        if (brokerServices.containsKey(bConf.toString()) && optimizeConnections) {
            loggerService.info("Selecting existing broker...");
            loggerService.info("No. of brokers stay "+brokerServices.size()+" .");
            if (!clients.containsKey(uuid))
                clients.put(uuid, bConf);
            return brokerServices.get(bConf.toString());
        }

        loggerService.info("Creating a new broker connection...");
        brokerServices.put(bConf.toString()+(!optimizeConnections?uuid.toString():""), new StaticBrokerService(alias, uuid,will,willTopic));

        if (!clients.containsKey(uuid))
            clients.put(uuid, bConf);

        loggerService.info("No. of brokers now is "+brokerServices.size()+" .");
        return brokerServices.get(bConf.toString()+(!optimizeConnections?uuid.toString():""));

    }
    static public StaticBrokerService getBrokerService( String brokerName, String port, String will, String willTopic) throws MalformedURLException, MqttException {
        return getBrokerService(UUID.randomUUID(), "",will,willTopic);

    }
    static public StaticBrokerService getBrokerService(String alias,String will, String willTopic) throws MalformedURLException, MqttException {
        return getBrokerService("localhost","1883",will,willTopic);

    }
    static public StaticBrokerService getDefaultBrokerService(String clientID,String will, String willTopic) throws MalformedURLException, MqttException {
        return getBrokerService("localhost","1883",will,willTopic);

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
