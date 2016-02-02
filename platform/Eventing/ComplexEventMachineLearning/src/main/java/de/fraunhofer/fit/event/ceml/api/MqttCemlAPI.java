package de.fraunhofer.fit.event.ceml.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.fit.event.ceml.intern.Const;
import de.fraunhofer.fit.event.ceml.type.requests.LearningRequest;
import eu.almanac.event.datafusion.utils.generic.Component;
import eu.linksmart.api.event.datafusion.StatementResponse;
import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.function.Utils;
import eu.linksmart.gc.utils.logging.LoggerService;
import eu.linksmart.gc.utils.mqtt.broker.StaticBroker;
import eu.linksmart.gc.utils.mqtt.types.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by José Ángel Carvajal on 02.02.2016 a researcher of Fraunhofer FIT.
 */
public class MqttCemlAPI extends Component {
    protected StaticBroker brokerService;
    private Configurator conf = Configurator.getDefaultConfig();
    private LoggerService loggerService = Utils.initDefaultLoggerService(MqttCemlAPI.class);
    private ObjectMapper mapper = new ObjectMapper();
    private ArrayList<Observer> observers;


    public MqttCemlAPI() throws MalformedURLException, MqttException, ClassNotFoundException {
        super(MqttCemlAPI.class.getSimpleName(),"Provides a MQTT light API to the CEML logic", "MqttCemlAPI");
        brokerService = new StaticBroker(conf.getString(Const.CEML_MQTT_BROKER_HOST),conf.getString(Const.CEML_MQTT_BROKER_PORT));
        observers = new ArrayList<>();
        Class.forName(CemlJavaAPI.class.getCanonicalName());
        loggerService.info("MQTT CEML API started!");
    }

    protected void initAddRequest(){
        Observer aux=  new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                MqttMessage mqttMessage =(MqttMessage)arg;
                try {
                    LearningRequest request = mapper.readValue(mqttMessage.getPayload(),LearningRequest.class);

                    StatementResponse response =CemlJavaAPI.create(request);
                    reportFeedback(mapper.writeValueAsString(response));

                } catch (Exception e) {
                    loggerService.error(e.getMessage(),e);
                    reportError(e.getMessage());
                }

            }
        };
        brokerService.addListener(conf.getString(Const.CEML_MQTT_INPUT_TOPIC) + "add",aux);
        observers.add(aux);
    }
    protected void reportError(String message){
        try {
            brokerService.publish(conf.getString(Const.CEML_MQTT_ERROR_TOPIC),message);
        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
        }

    }


    protected void reportFeedback(String message){

        try {
            brokerService.publish(conf.getString(Const.CEML_MQTT_OUTPUT_TOPIC),message);
        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
        }

    }

    protected void initRemoveRequest(){
        Observer aux= new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                MqttMessage mqttMessage =(MqttMessage)arg;
                try {

                    throw new NoSuchMethodException("not yet implemented");
                } catch (Exception e) {
                    loggerService.error(e.getMessage(),e);
                    reportError(e.getMessage());
                }


            }
        };
        brokerService.addListener(conf.getString(Const.CEML_MQTT_INPUT_TOPIC) + "remove",aux);
        observers.add(aux);
    }
    protected void initGetRequest(){
        Observer aux=new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                MqttMessage mqttMessage =(MqttMessage)arg;
                try {

                    throw new NoSuchMethodException("not yet implemented");
                } catch (Exception e) {
                    loggerService.error(e.getMessage(),e);
                    reportError(e.getMessage());
                }
            }
        };
        brokerService.addListener(conf.getString(Const.CEML_MQTT_INPUT_TOPIC) + "get", aux);
        observers.add(aux);
    }

    @Override
    protected void finalize() {
        super.finalize();
        try {
            brokerService.destroy();
        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
        }
    }
}
