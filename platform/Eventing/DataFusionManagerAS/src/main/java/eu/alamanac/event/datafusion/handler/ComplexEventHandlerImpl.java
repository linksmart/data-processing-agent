package eu.alamanac.event.datafusion.handler;

import com.google.gson.Gson;
import eu.almanac.event.datafusion.utils.IoTEntityEvent;
import eu.almanac.event.datafusion.utils.IoTProperty;
import eu.almanac.event.datafusion.utils.IoTValue;
import eu.linksmart.api.event.datafusion.ComplexEventHandler;
import eu.linksmart.api.event.datafusion.Query;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Caravajal on 06.10.2014.
 */
public class ComplexEventHandlerImpl implements ComplexEventHandler{
    private MqttClient CEPHandler;
    private final Query query;
    private IoTEntityEvent response;
    private Gson parser;
    private String dateOfCreation;

    public ComplexEventHandlerImpl(Query query) throws RemoteException {
        try {
            this.CEPHandler= new MqttClient("tcp://localhost:1883","list");
        } catch (MqttException e) {
            throw new RemoteException(e.getMessage());
        }
        this.query=query;
        parser = new Gson();
        response = new IoTEntityEvent();
        response.setAbout(query.getName());
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);
        dateOfCreation = df.format(new Date());
    }
    public void update(Map event) {

        try {
            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
            df.setTimeZone(tz);
            String nowAsISO = df.format(new Date());

            if (!CEPHandler.isConnected())
                CEPHandler.connect();
            IoTEntityEvent cepEvent = new IoTEntityEvent(0,"DataFusionManager");
            int n=0;
            for(Object key : event.keySet()) {
                try {

                    IoTEntityEvent ent = (IoTEntityEvent) event.get(key);

                    if (cepEvent.getProperties("IoTEntities") == null ) {
                        cepEvent.getProperties(n).setAbout("IoTEntities");
                        n++;
                    }

                    cepEvent.getProperties("IoTEntities").addIoTStateObservation(ent.getAbout(), dateOfCreation, nowAsISO);

                    for(IoTProperty p: ent.getProperties()){

                        for (IoTValue v: p.getIoTStateObservation())
                            cepEvent.addProperty(p.getAbout(), 0).addIoTStateObservation(v.getValue(),v.getPhenomenonTime(),v.getResultTime());

                    }

                    // CEPHandler.publish("/almanac/local/iotentity/dataFusionManager/" + query.getName(), parser.toJson(event).getBytes(), 2, false);

                } catch (Exception eEntity) {

                    try {

                        IoTProperty ent = (IoTProperty) event.get(key);

                        for (IoTValue v : ent.getIoTStateObservation())
                            cepEvent.addProperty(ent.getAbout(), 0).addIoTStateObservation(v.getValue(), v.getPhenomenonTime(), v.getResultTime());


                    } catch (Exception eBoolean) {

                        try {

                            Boolean ent = (Boolean) event.get(key);
                            cepEvent.addProperty(key.toString(), 0).addIoTStateObservation(ent.toString(), dateOfCreation, nowAsISO);


                        } catch (Exception eProperty) {

                            try {

                                Integer ent = (Integer) event.get(key);
                                cepEvent.addProperty(key.toString(), 0).addIoTStateObservation(ent.toString(), dateOfCreation, nowAsISO);

                            } catch (Exception eString) {
                                try {

                                    Double ent = (Double) event.get(key);
                                    cepEvent.addProperty(key.toString(), 0).addIoTStateObservation(ent.toString(), dateOfCreation, nowAsISO);

                                } catch (Exception e) {
                                    try {

                                        Float ent = (Float) event.get(key);
                                        cepEvent.addProperty(key.toString(), 0).addIoTStateObservation(ent.toString(), dateOfCreation, nowAsISO);

                                    } catch (Exception eFloat) {

                                        try {

                                            String ent = (String) event.get(key);
                                            cepEvent.addProperty(key.toString(), 0).addIoTStateObservation(ent, dateOfCreation, nowAsISO);

                                        } catch (Exception eInteger) {

                                            try {

                                                cepEvent.addProperty(key.toString(), 0  ).addIoTStateObservation(parser.toJson(event), dateOfCreation, nowAsISO);

                                            } catch (Exception eMqtt) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }

                            }

                        }
                    }

                }
            }
            CEPHandler.publish("/almanac/local/observation/iotentity/" + query.getName(), parser.toJson(cepEvent).getBytes(), 0, false);

        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean publishError(String errorMessage) {

            try {
                if (!CEPHandler.isConnected())
                    CEPHandler.connect();
                CEPHandler.publish("/almanac/local/errors/string/dataFusionManager/info", errorMessage.getBytes(),0,false);
                return true;
            } catch (MqttException e) {
                e.printStackTrace();
            }
        return false;
    }
}
