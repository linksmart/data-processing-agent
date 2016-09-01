package eu.linksmart.services.event.ceml.api;

import eu.linksmart.ceml.core.CEML;
import eu.linksmart.ceml.core.CEMLManager;
import eu.linksmart.ceml.intern.Const;
import eu.almanac.event.datafusion.utils.generic.Component;
import eu.linksmart.api.event.ceml.CEMLRequest;
import eu.linksmart.api.event.datafusion.types.impl.MultiResourceResponses;
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
public class MqttCemlAPI extends Component {

    static MqttCemlAPI me;
    protected StaticBroker brokerService;
    private Configurator conf = Configurator.getDefaultConfig();
    static private Logger loggerService = Utils.initLoggingConf(MqttCemlAPI.class);

    private List<Observer> observers;
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
    protected MqttCemlAPI() throws MalformedURLException, MqttException, ClassNotFoundException {
        super(MqttCemlAPI.class.getSimpleName(),"Provides a MQTT light API to the CEML logic", "MqttCemlAPI");
        Class.forName(CEML.class.getCanonicalName());
        brokerService = new StaticBroker(conf.getString(Const.CEML_MQTT_BROKER_HOST),conf.getString(Const.CEML_MQTT_BROKER_PORT));
        observers = new ArrayList<>();
        initAddRequest();
        initGetRequest();
        initRemoveRequest();
        loggerService.info("MQTT CEML API started!");
    }

    protected void initAddRequest(){
        Observer aux= (o, arg) -> {
            MqttMessage mqttMessage =(MqttMessage)arg;
            try {
                CEMLRequest request = CEML.getMapper().readValue(mqttMessage.getPayload(), CEMLManager.class);

                MultiResourceResponses<CEMLRequest> response = CEML.create(request);
                reportFeedback(CEML.getMapper().writeValueAsString(response));

            } catch (Exception e) {
                loggerService.error(e.getMessage(),e);
                reportError(e.getMessage());
            }

        };
        brokerService.addListener(conf.getString(Const.CEML_MQTT_INPUT_TOPIC) + "add/#",aux);
        observers.add(aux);
    }
    public void reportError(String message){
        try {
            brokerService.publish(conf.getString(Const.CEML_MQTT_ERROR_TOPIC),message);
        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
        }

    }


    public void reportFeedback(String message){

        try {
            brokerService.publish(conf.getString(Const.CEML_MQTT_OUTPUT_TOPIC),message);
        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
        }

    }

    protected void initRemoveRequest(){
        Observer aux= (o, arg) -> {
            try {

                throw new NoSuchMethodException("not yet implemented");
            } catch (Exception e) {
                loggerService.error(e.getMessage(),e);
                reportError(e.getMessage());
            }


        };

        brokerService.addListener(conf.getString(Const.CEML_MQTT_INPUT_TOPIC) + "remove",aux);
        observers.add(aux);
    }
    protected void initGetRequest(){
        Observer aux= (o, arg) -> {
            try {

                throw new NoSuchMethodException("not yet implemented");
            } catch (Exception e) {
                loggerService.error(e.getMessage(),e);
                reportError(e.getMessage());
            }
        };
        brokerService.addListener(conf.getString(Const.CEML_MQTT_INPUT_TOPIC) + "get", aux);
        observers.add(aux);
    }


}
