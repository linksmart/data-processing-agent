package eu.linksmart.services.event.connectors;

import eu.linksmart.api.event.components.IncomingConnector;
import eu.linksmart.api.event.exceptions.UnknownUntraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.services.event.intern.AgentUtils;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by José Ángel Carvajal on 05.12.2016 a researcher of Fraunhofer FIT.
 */
public abstract class IncomingSyncConnector implements IncomingConnector {
   // protected Map<String,Map<String, Map<String, List<Observer>>>> protocolHostTopicToObserver = new ConcurrentHashMap<>();

    static final private String ALL = "ALL";
    protected Logger loggerService = AgentUtils.initLoggingConf(this.getClass());

    //protected Map<String, Map<String, List<Observer>>> protocolTopicToObserver = new ConcurrentHashMap<>();
    protected Map<String, List<Observer>> topicToObserver = new ConcurrentHashMap<>();
    //protected Map<Observer, Map> observerRegisteredIn = new ConcurrentHashMap<>();

    public void subscribe(String protocol, String host, String topic, Observer observer) throws UntraceableException {


        create(url(protocol,host,topic));

        topicToObserver.get(topic).add(observer);

    }


    public void subscribe(String protocol, String topic, Observer observer) throws UntraceableException{

        create(url(protocol,topic));

        topicToObserver.get(topic).add(observer);
    }


    public void subscribe(String topic, Observer observer) throws UntraceableException{

        create(topic);

        create(url(topic));

        topicToObserver.get(topic).add(observer);

    }


    private void create(String topic){
        if(topicToObserver.get(topic)==null)
            topicToObserver.put(topic, new ArrayList<>());
    }



    public boolean unsubscribe(Observer observer) throws UntraceableException {
        try {
            return topicToObserver.values().remove(observer);
        }catch (Exception e){
            throw new UnknownUntraceableException(e);
        }


    }


    private String anyHost(String protocol){
        return  protocol+"://"+ALL+"/";
    }

    private String base(String protocol, String host){
        return  protocol+"://"+host+"/";
    }
    private String url(String protocol, String host, String topic){
        return  base(protocol, host)+topic;
    }
    private String url(String protocol, String topic){
        return  anyHost(protocol)+topic;
    }
    private String url(String topic){
        return  anyProtocolAnyHost()+topic;
    }
    private String anyProtocolAnyHost(){
        return  ALL+"://"+ALL+"/";
    }
}
