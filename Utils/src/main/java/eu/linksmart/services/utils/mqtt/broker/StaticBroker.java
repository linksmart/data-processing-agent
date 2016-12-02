package eu.linksmart.services.utils.mqtt.broker;

import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.constants.Const;
import eu.linksmart.services.utils.mqtt.types.Topic;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.MalformedURLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by José Ángel Carvajal on 23.10.2015 a researcher of Fraunhofer FIT.
 */
public class StaticBroker implements Broker{

    protected StaticBrokerService brokerService;
    protected UUID clientID;
    protected Map<Topic,ArrayList<Observer>> observersByTopic = new Hashtable<>();
    protected Map<Observer,Topic> observers = new Hashtable<>();
    protected boolean needsReconnect = false;
    private Configurator conf = Configurator.getDefaultConfig();

    public StaticBroker(String brokerName, String brokerPort, String will, String willTopic) throws MalformedURLException, MqttException {
        clientID = UUID.randomUUID();
        brokerService = StaticBrokerService.getBrokerService(clientID, "",will,willTopic);

    }
    public StaticBroker(String alias, String will, String willTopic) throws MalformedURLException, MqttException {
        clientID = UUID.randomUUID();

        brokerService = StaticBrokerService.getBrokerService(clientID, alias,will,willTopic);


    }
    @Override
    public boolean isConnected() {
        return brokerService.isConnected(clientID);
    }

    @Override
    public void connect() throws Exception {


       reconnectClients();

        brokerService.connect(clientID);
    }
    private void disconnectClients(){
        for(ArrayList<Observer> array: observersByTopic.values())
            for(Observer observer: array)
                brokerService.removeListener(observer);
        needsReconnect = true;

    }
    private void reconnectClients(){
        if(needsReconnect)
            for(Topic key: observersByTopic.keySet())
                for(Observer observer: observersByTopic.get(key))
                    brokerService.addListener(key.getTopic(),observer);
        needsReconnect= false;

    }

    @Override
    public void disconnect() throws Exception {
        disconnectClients();
        brokerService.disconnect(clientID);
    }

    @Override
    public void destroy() throws Exception {
        disconnectClients();
        brokerService.destroy(clientID);
    }

    @Override
    public String getBrokerURL() {
        return brokerService.getBrokerURL();
    }

    @Override
    public void createClient() throws MqttException {
        brokerService.createClient();

    }

    //@Override
    public boolean isWatchdog() {
        return brokerService.isWatchdog();
    }

    @Override
    public void startWatchdog() {

        brokerService.startWatchdog();
    }

    @Override
    public void stopWatchdog() {
        brokerService.stopWatchdog();

    }

    @Override
    public void publish(String topic, byte[] payload, int qos, boolean retained) throws Exception {

        brokerService.publish(topic,payload,qos,retained);
    }

    @Override
    public void publish(String topic, byte[] payload) throws Exception {

        brokerService.publish(topic,payload);
    }

    @Override
    public void publish(String topic, String payload) throws Exception {

        brokerService.publish(topic,payload);
    }

    @Override
    public String getBrokerName() {
        return brokerService.getBrokerName();
    }

    @Override
    public void setBrokerName(String brokerName) throws Exception {

        brokerService.setBrokerName(brokerName);
    }

    @Override
    public String getBrokerPort() {
        return brokerService.getBrokerPort();
    }

    @Override
    public void setBrokerPort(String brokerPort) throws Exception {

        brokerService.setBrokerPort(brokerPort);

    }

    @Override
    public void setBroker(String brokerName, String brokerPort) throws Exception {

        brokerService.setBroker(brokerName,brokerPort);
    }

    @Override
    public boolean addListener(String topic, Observer stakeholder) {
        topicObserversManagement(topic,stakeholder);

        return brokerService.addListener(topic,stakeholder);
    }
    @Override
    public boolean addListener(String topic, Observer stakeholder, int QoS) {
       topicObserversManagement(topic,stakeholder);

        return brokerService.addListener(topic,stakeholder,QoS);
    }
    private void topicObserversManagement(String topic, Observer stakeholder){
        Topic t = new Topic(topic);
        if(!observersByTopic.containsKey(t))
            observersByTopic.put(t,new ArrayList<>());
        if(!observersByTopic.get(t).contains(stakeholder)) {
            observers.put(stakeholder,t);
            observersByTopic.get(t).add(stakeholder);
        }
    }
    @Override
    public boolean removeListener(String topic, Observer stakeholder) {
        observersByTopic.get(new Topic(topic)).remove(stakeholder);
        observers.remove(stakeholder);

        return brokerService.removeListener(topic,stakeholder);
    }

    @Override
    public void removeListener(Observer stakeholder) {
        Topic t = observers.get(stakeholder);
        observers.remove(stakeholder);
        observersByTopic.get(t).remove(stakeholder);
        if(observersByTopic.get(t).isEmpty())
            observersByTopic.remove(t);

         brokerService.removeListener(stakeholder);

    }

    @Override
    public BrokerConfiguration getConfiguration() {
        return brokerService.brokerConf;
    }

    @Override
    public String getAlias() {
        return brokerService.getAlias();
    }

    @Override
    public void update(Observable o, Object arg) {
        brokerService.update(o,arg);

    }
    @Override
    public boolean equals(Object o) {

        if (o == this)
            return true;
        if (o!=null && o instanceof StaticBroker) {
            StaticBroker aux = (StaticBroker) o;

            return brokerService.equals(aux.brokerService);
        }
        return false;
    }
    @Override
    public String toString(){

        return brokerService.toString();

    }
    @Override
    public int hashCode(){

        return toString().hashCode();
    }
}
