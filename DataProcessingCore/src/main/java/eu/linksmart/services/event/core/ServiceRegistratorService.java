package eu.linksmart.services.event.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.services.event.intern.AgentUtils;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.mqtt.broker.Broker;
import eu.linksmart.services.utils.mqtt.broker.StaticBroker;
import eu.linksmart.services.utils.mqtt.broker.StaticBrokerService;
import io.swagger.client.model.Service;
import io.swagger.client.model.ServiceDocs;
import org.slf4j.Logger;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by José Ángel Carvajal on 13.10.2017 a researcher of Fraunhofer FIT.
 */
public class ServiceRegistratorService implements Observer{

    private transient static Properties info = null;
    private final EditableService myRegistration;
    private static final String restAPIName= "Agent RESTful API";
    private transient final static Logger loggerService = AgentUtils.initLoggingConf(ServiceRegistratorService.class);
    private transient final static Configurator conf = Configurator.getDefaultConfig();
    public static ConcurrentMap<String,Object> meta = new ConcurrentHashMap<>();
    private final StaticBroker broker;
    private final Timer updater = new Timer();

    public final transient static ServiceRegistratorService registrator = new ServiceRegistratorService();
    private ServiceRegistratorService() {
        myRegistration = new EditableService();
        myRegistration.setId(SharedSettings.getId());
        myRegistration.setDescription(conf.getString(Const.AGENT_DESCRIPTION));
        myRegistration.setName("_linksmart-"+SharedSettings.getLs_code().toLowerCase()+".tcp_");
        myRegistration.setMeta(meta);
        myRegistration.setApis(new ConcurrentHashMap<>());
        myRegistration.setDocs(new ArrayList<>());
        myRegistration.setTtl( conf.getLong(Const.LINKSMART_SERVICE_TTL));



        if (conf.getBoolean(Const.ENABLE_REST_API)) {
            try {
                String host = InetAddress.getLocalHost().getHostName(), port = conf.getString("server_port"), protocol = "http";
                if (conf.containsKeyAnywhere("server_ssl_key-store"))
                    protocol = "https";

                myRegistration.getApis().put(restAPIName, protocol + "://" + host + ":" + port + "/");

                ServiceDocs doc = new ServiceDocs();
                doc.setDescription("Open API V2");
                doc.setUrl(myRegistration.getApis().get(restAPIName) + "swagger-ui.html");
                doc.setType("application/json");
                doc.setApis(Arrays.asList(restAPIName));
                myRegistration.setDocs(Arrays.asList(doc));
            } catch (Exception e) {
                loggerService.error(e.getMessage(), e);
            }

        }

        StaticBrokerService.brokerServices.forEach((broker, url) ->
                myRegistration.getApis().put("Agent MQTT API", url.getBrokerURL())
        );

        StaticBroker intent = null;
        try {
            intent = new StaticBroker(conf.getString(Const.LINKSMART_BROKER), SharedSettings.getSerializer().toString(myRegistration), AgentUtils.topicReplace(conf.getString(Const.LINKSMART_SERVICE_WILL_TOPIC)));


        } catch (Exception e) {
            loggerService.error(e.getMessage(), e);
            System.exit(-1);
        }

        broker = intent;
        broker.addConnectionListener(this);

        if(myRegistration.getTtl()>-1){
            updater.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            update();
                        }
                    },
                    myRegistration.getTtl()/3
            );
        }else
            update();
    }

    public static ServiceRegistratorService getRegistrator() {
        return registrator;
    }
    public void update(){
        try {
            loggerService.info("Sending registration message to topic: "+ AgentUtils.topicReplace(conf.getString(Const.LINKSMART_REGISTRATION_TOPIC))+ SharedSettings.getId() + " message: " +SharedSettings.getSerializer().toString(myRegistration));
            broker.publish(AgentUtils.topicReplace(conf.getString(Const.LINKSMART_REGISTRATION_TOPIC))+ SharedSettings.getId(), SharedSettings.getSerializer().serialize(myRegistration));
        } catch (Exception e) {
            loggerService.error(e.getMessage(), e);
        }
    }

    @Override
    public void update(Observable o, Object arg) {

        update();

    }

    private class EditableService extends Service {
        @JsonProperty("id")
        protected String id;
        @Override
        public String getId() {
            return id;
        }

        public void setId(String id){
            this.id=id;
        }

    }
}
