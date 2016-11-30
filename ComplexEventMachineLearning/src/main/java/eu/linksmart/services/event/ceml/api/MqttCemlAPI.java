package eu.linksmart.services.event.ceml.api;

import eu.linksmart.api.event.components.Feeder;
import eu.linksmart.api.event.components.IncomingConnector;
import eu.linksmart.api.event.exceptions.InternalException;
import eu.linksmart.services.event.ceml.core.CEML;
import eu.linksmart.services.event.ceml.core.CEMLManager;
import eu.linksmart.services.event.ceml.intern.Const;
import eu.almanac.event.datafusion.utils.generic.Component;
import eu.linksmart.api.event.ceml.CEMLRequest;
import eu.linksmart.api.event.types.impl.MultiResourceResponses;
import eu.linksmart.services.event.connectors.MqttIncomingConnectorService;
import eu.linksmart.services.event.connectors.Observers.IncomingMqttObserver;
import eu.linksmart.services.event.intern.DynamicConst;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.function.Utils;
import eu.linksmart.services.utils.mqtt.broker.StaticBroker;
import eu.linksmart.services.utils.mqtt.types.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

/**
 * Created by José Ángel Carvajal on 02.02.2016 a researcher of Fraunhofer FIT.
 */
public class MqttCemlAPI extends Component implements IncomingConnector {

    static MqttCemlAPI me;
    protected StaticBroker brokerService;
    private static Configurator conf = Configurator.getDefaultConfig();
    static private Logger loggerService = Utils.initLoggingConf(MqttCemlAPI.class);
    static {
        try {
            me= new MqttCemlAPI();
        } catch (MalformedURLException | ClassNotFoundException | MqttException e) {
            loggerService.error(e.getMessage(),e);
        }
    }

    static public MqttCemlAPI getMeDafault(){
        return me;
    }
    public static void reportError(StaticBroker brokerService,String message){
        try {
            brokerService.publish(conf.getString(Const.CEML_MQTT_ERROR_TOPIC),message);
        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
        }

    }
    public static void reportFeedback(StaticBroker brokerService, String id, String message){

        try {
            brokerService.publish(conf.getString(Const.CEML_MQTT_OUTPUT_TOPIC)+id,message);
        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
        }

    }

    protected MqttCemlAPI() throws MalformedURLException, MqttException, ClassNotFoundException {
        super(MqttCemlAPI.class.getSimpleName(), "Provides a MQTT light API to the CEML logic", "MqttCemlAPI");
        Class.forName(CEML.class.getCanonicalName());
        brokerService = new StaticBroker(conf.getString(Const.CEML_MQTT_BROKER_HOST), DynamicConst.getWill(),DynamicConst.getWillTopic());
        initAddRequest();
        initGetRequest();
        initRemoveRequest();
        loggerService.info("MQTT CEML API started!");
    }

    protected void initAddRequest()  {

        try {
            MqttIncomingConnectorService.getReference(DynamicConst.getWill(),DynamicConst.getWillTopic()).addAddListener(
                    conf.getString(Const.CEML_MQTT_BROKER_HOST),
                    conf.getString(Const.CEML_MQTT_INPUT_TOPIC) + "add/",
                    new IncomingMqttObserver(   conf.getString(Const.CEML_MQTT_INPUT_TOPIC) + "add/") {
                        @Override
                        protected void mangeEvent(String topic, byte[] payload) {
                            try {
                                CEMLRequest request = CEML.getMapper().readValue(payload, CEMLManager.class);

                                MultiResourceResponses<CEMLRequest> response = CEML.create(request);
                                reportFeedback(brokerService,response.getHeadResource().getName(),CEML.getMapper().writeValueAsString(response));

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
            MqttIncomingConnectorService.getReference(DynamicConst.getWill(),DynamicConst.getWillTopic()).addAddListener(
                    conf.getString(Const.CEML_MQTT_BROKER_HOST),
                  //  conf.getString(Const.CEML_MQTT_BROKER_PORT),
                    conf.getString(Const.CEML_MQTT_INPUT_TOPIC) + "remove/+",
                    new IncomingMqttObserver(conf.getString(Const.CEML_MQTT_INPUT_TOPIC) + "remove/+") {
                        @Override
                        protected void mangeEvent(String topic, byte[] payload) {
                            try {
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
            MqttIncomingConnectorService.getReference(DynamicConst.getWill(),DynamicConst.getWillTopic()).addAddListener(
                    conf.getString(Const.CEML_MQTT_BROKER_HOST),
                   // conf.getString(Const.CEML_MQTT_BROKER_PORT),
                    conf.getString(Const.CEML_MQTT_INPUT_TOPIC) + "get/+",
                    new IncomingMqttObserver(conf.getString(Const.CEML_MQTT_INPUT_TOPIC) + "get/+") {
                        @Override
                        protected void mangeEvent(String topic, byte[] payload) {
                            try {
                                String[] parts = topic.split("/");

                                String id = null;
                                for(int i = parts.length-1 ; i>-1&& id == null;i--)
                                    if (!"".equals(parts[i]))
                                        id = parts[i];
                                    else if ("get".equals(parts[i]))
                                        break;
                                if (id!=null)
                                    reportFeedback(brokerService,id,CEML.getMapper().writeValueAsString(CEML.get(id, "")));
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
