package eu.linksmart.services.utils.mqtt.subscription;

import eu.linksmart.services.utils.mqtt.types.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Observer;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by José Ángel Carvajal on 23.03.2016 a researcher of Fraunhofer FIT.
 */
public class TopicMessageDeliverable implements Runnable{
    private LinkedBlockingQueue<MqttMessage> mqttMessages = new LinkedBlockingQueue<>();
    private LinkedList<Observer> observers = new LinkedList<Observer>();
    //static protected ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);// Executors.newCachedThreadPool();

    public TopicMessageDeliverable() {
        LOG.debug("Starting new ");
        Thread thread =new Thread(this);
        thread.start();
    }

    public synchronized void setIsActive(boolean activeTopic) {
        this.activeTopic = activeTopic;
    }

    protected boolean activeTopic = true;
    protected static final Logger LOG=  LoggerFactory.getLogger(TopicMessageDeliverable.class);


    public synchronized void addObserver(Observer observer){
        observers.add(observer);
    }


    @Override
    public void run() {
        MqttMessage message=null;
        boolean active =true;
        synchronized (this) {
             active = activeTopic;
        }
        while (active) {

            LOG.debug(" Started the topic loop");
            try {
                message = mqttMessages.take();
                LOG.debug("Processing incoming message of topic "+ message.getTopic());
                synchronized (this) {
                    for (Observer observer : observers)
                        observer.update(null, message);
                }

            } catch (InterruptedException e) {
               LOG.error(e.getMessage(),e);
            }
            synchronized (this) {
                active = activeTopic;
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        synchronized (this) {
            activeTopic = false;
        }
    }

    public synchronized void deleteObserver(Observer listener) {
        observers.remove(listener);
    }

    public synchronized int countObservers() {
        return observers.size();
    }
    public synchronized void addMessage(MqttMessage mqttMessage){
        mqttMessages.add(mqttMessage);
    }
}
