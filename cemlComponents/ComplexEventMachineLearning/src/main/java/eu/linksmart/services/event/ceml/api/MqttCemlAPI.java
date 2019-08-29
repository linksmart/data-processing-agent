package eu.linksmart.services.event.ceml.api;

import eu.linksmart.api.event.components.IncomingConnector;
import eu.linksmart.api.event.types.impl.AsyncRequest;
import eu.linksmart.services.event.ceml.core.CEML;
import eu.linksmart.services.event.ceml.core.CEMLManager;
import eu.linksmart.services.event.ceml.intern.Const;
import eu.almanac.event.datafusion.utils.generic.Component;
import eu.linksmart.api.event.ceml.CEMLRequest;
import eu.linksmart.api.event.types.impl.MultiResourceResponses;
import eu.linksmart.services.event.connectors.MqttIncomingConnectorService;
import eu.linksmart.services.event.connectors.mqtt.IncomingMqttObserver;
import eu.linksmart.services.event.intern.AgentUtils;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.mqtt.broker.StaticBroker;
import org.apache.logging.log4j.LogManager;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by José Ángel Carvajal on 02.02.2016 a researcher of Fraunhofer FIT.
 */
public class MqttCemlAPI extends Component implements IncomingConnector {

    static MqttCemlAPI me;
    protected StaticBroker brokerService;
    private static Configurator conf ;
    static private Logger loggerService ;
    static {
        try {
            loggerService = LogManager.getLogger(MqttCemlAPI.class);
            conf = Configurator.getDefaultConfig();
            if(conf.getBoolean(Const.CEML_MQTT_API_ENABLED)) {
                me = new MqttCemlAPI();
            }
        } catch (Exception | NoClassDefFoundError e) {
            if(conf==null || (conf!=null && !conf.getBoolean("Test")))
                if(loggerService!=null)
                    loggerService.error(e.getMessage(),e);
                else
                    e.printStackTrace();
        }
    }

    static public MqttCemlAPI getMeDefault(){
        return me;
    }
    public static void reportError(StaticBroker brokerService,String message){
        try {
            brokerService.publish(AgentUtils.topicReplace(conf.getString(Const.CEML_MQTT_ERROR_TOPIC), ""),message);
        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
        }

    }
    public static void reportFeedback(StaticBroker brokerService, String id, String message){

        try {
            brokerService.publish(AgentUtils.topicReplace(conf.getString(Const.CEML_MQTT_OUTPUT_TOPIC), id),message);
        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
        }

    }

    protected MqttCemlAPI() throws MalformedURLException, MqttException, ClassNotFoundException {
        super(MqttCemlAPI.class.getSimpleName(), "Provides a MQTT light API to the CEML logic", "MqttCemlAPI");
        if(conf.getBoolean(Const.CEML_MQTT_API_ENABLED)) {
            Class.forName(CEML.class.getCanonicalName());
            brokerService = new StaticBroker(conf.getString(Const.CEML_MQTT_BROKER_HOST), SharedSettings.getWill(), SharedSettings.getWillTopic());
            initAddRequest();
            initGetRequest();
            initRemoveRequest();
            loggerService.info("MQTT CEML API started!");
        }else
            loggerService.info("MQTT CEML API deactivated!");
    }
    private byte[] prepareRequest(byte[] payload) throws IOException {
        AsyncRequest request;

            request = SharedSettings.getDeserializer().deserialize(payload,AsyncRequest.class);
            if(request.getTargets()==null || (!request.getTargets().isEmpty() && !request.getTargets().contains(SharedSettings.getId())))
                return null;

            return request.getResource();

    }
    protected void initAddRequest()  {

        try {
            MqttIncomingConnectorService.getReference().addListener(
                    conf.getString(Const.CEML_MQTT_BROKER_HOST),
                    conf.getString(Const.CEML_MQTT_INPUT_TOPIC) + "add/",
                    new IncomingMqttObserver(   conf.getString(Const.CEML_MQTT_INPUT_TOPIC) + "add/") {
                        @Override
                        protected void mangeEvent(String topic, byte[] payload) {
                            try {
                                byte[] rawEvent;
                                try {

                                    if( (rawEvent = prepareRequest(payload) )== null) // not for me
                                        return;
                                }catch (Exception e){
                                    loggerService.error(e.getMessage(),e);
                                    return;
                                }

                                CEMLRequest request = SharedSettings.getDeserializer().deserialize(rawEvent, CEMLManager.class);

                                MultiResourceResponses<CEMLRequest> response = CEML.create(request);
                                reportFeedback(brokerService,response.getHeadResource().getName(),SharedSettings.getSerializer().toString(response));

                            } catch (Exception e) {
                                loggerService.error(e.getMessage(),e);
                                reportError(brokerService,e.getMessage());
                            }
                        }
                    }
            );
        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
            reportError(brokerService,e.getMessage());
        }
    }

    protected void initRemoveRequest()  {

        try {
            MqttIncomingConnectorService.getReference().addListener(
                    conf.getString(Const.CEML_MQTT_BROKER_HOST),
                  //  conf.getString(Const.CEML_MQTT_BROKER_PORT),
                    conf.getString(Const.CEML_MQTT_INPUT_TOPIC) + "remove/+",
                    new IncomingMqttObserver(conf.getString(Const.CEML_MQTT_INPUT_TOPIC) + "remove/+") {
                        @Override
                        protected void mangeEvent(String topic, byte[] payload) {
                            try {
                                try {

                                    if( (prepareRequest(payload) )== null) // not for me
                                        return;
                                }catch (Exception e){
                                    loggerService.error(e.getMessage(),e);
                                    return;
                                }

                                String[] parts = topic.split("/");

                                String id = null;
                                for(int i = parts.length-1 ; i>-1&& id == null;i--)
                                    if (!"".equals(parts[i]))
                                        id = parts[i];
                                     else if ("remove".equals(parts[i]))
                                        break;
                                if(id!=null)
                                    reportFeedback(brokerService, id,CEML.delete(id,"").getResponsesTail().getMessage());
                                else
                                    reportError(brokerService,"An add request was received but no ID found in the topic");

                            } catch (Exception e) {
                                loggerService.error(e.getMessage(),e);
                                reportError(brokerService,e.getMessage());
                            }
                        }
                    }
            );
        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
            reportError(e.getMessage());
        }
    }
    protected void initGetRequest()  {

        try {
            MqttIncomingConnectorService.getReference().addListener(
                    conf.getString(Const.CEML_MQTT_BROKER_HOST),
                   // conf.getString(Const.CEML_MQTT_BROKER_PORT),
                    conf.getString(Const.CEML_MQTT_INPUT_TOPIC) + "get/+",
                    new IncomingMqttObserver(conf.getString(Const.CEML_MQTT_INPUT_TOPIC) + "get/+") {
                        @Override
                        protected void mangeEvent(String topic, byte[] payload) {
                            try {
                                try {

                                    if( (prepareRequest(payload) )== null) // not for me
                                        return;
                                }catch (Exception e){
                                    loggerService.error(e.getMessage(),e);
                                    return;
                                }
                                String[] parts = topic.split("/");

                                String id = null;
                                for(int i = parts.length-1 ; i>-1&& id == null;i--)
                                    if (!"".equals(parts[i]))
                                        id = parts[i];
                                    else if ("get".equals(parts[i]))
                                        break;
                                if (id!=null)
                                    reportFeedback(brokerService,id,SharedSettings.getSerializer().toString(CEML.get(id, "")));
                                else
                                    reportError(brokerService,"An get request was received but no ID found in the topic");

                            } catch (Exception e) {
                                loggerService.error(e.getMessage(),e);
                                reportError(brokerService,e.getMessage());
                            }
                        }
                    }
            );
        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
            reportError(e.getMessage());
        }
    }


    public void reportError(String message){
        reportError(brokerService,message);

    }


    public void reportFeedback(String id,String message){

        reportFeedback(brokerService,id,message);

    }
    @Override
    public boolean isUp() {
        return brokerService.isConnected();
    }
}
