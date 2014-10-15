package eu.alamanac.event.datafusion.handler;

import com.google.gson.Gson;
import eu.almanac.event.datafusion.utils.IoTEntityEvent;
import eu.almanac.event.datafusion.utils.IoTProperty;
import eu.almanac.event.datafusion.utils.IoTValue;
import eu.linksmart.api.event.datafusion.ComplexEventHandler;
import eu.linksmart.api.event.datafusion.Statement;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Caravajal on 06.10.2014.
 */
public class ComplexEventHandlerImpl implements ComplexEventHandler{
    private MqttClient CEPHandler;
    private final Statement query;
    private IoTEntityEvent response;
    private Gson parser;
    private String dateOfCreation;
    private Boolean sendPerProperty = false;

    private final String DFM_TOPIC = "/almanac/observation/iotentity/dataFusionManager";
    private final String EVENT_TOPIC = "/almanac/observation/iotentity";
    private final String ERROR_TOPIC = "/almanac/error/json/dataFusionManager";
    private final String INFO_TOPIC = "/almanac/info/json/dataFusionManager";
    private static Map<String,String> knownInstances= new HashMap<String,String>();

    public ComplexEventHandlerImpl(Statement query) throws RemoteException {
        if(!knownInstances.containsKey("local"))
            knownInstances.put("local","tcp://localhost:1883");


        if(!knownInstances.containsKey("ismb_public") )
            knownInstances.put("ismb_public","tcp://130.192.86.227:1883");
        this.query=query;
        parser = new Gson();
        response = new IoTEntityEvent();
        response.setAbout(query.getName());
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);
        dateOfCreation = df.format(new Date());
        try {
            this.CEPHandler= new MqttClient(knownInstances.get(query.getScope(0).toLowerCase()),query.getName());
        } catch (MqttException e) {
            throw new RemoteException(e.getMessage());
        }
    }

    public void update(Map event) {

        System.out.println("Updating query: "+query.getName());
        try {

            if(event.containsKey((Object)(new String("SetEventPerEntity")))){
                sendPerProperty = (Boolean)event.get((Object)(new String("SetEventPerEntity")));

                event.remove((Object) (new String("SetEventPerEntity")));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
            df.setTimeZone(tz);
            String nowAsISO = df.format(new Date());

            if (!CEPHandler.isConnected())
                CEPHandler.connect();
            IoTEntityEvent cepEvent = new IoTEntityEvent("DataFusionManager");
            int n=0;
            for(Object key : event.keySet()) {
                try {

                    if(sendPerProperty){
                        IoTEntityEvent ent = (IoTEntityEvent) event.get(key);
                        for(String output : query.getOutput()) {
                            CEPHandler.publish(output + ent.getAbout(), parser.toJson(ent).getBytes(), 0, false);
                        }
                        continue;
                    }else {
                        IoTEntityEvent ent = (IoTEntityEvent) event.get(key);

                        if (cepEvent.getProperties("IoTEntities") == null) {
                            cepEvent.getProperties(n).setAbout("IoTEntities");
                            n++;
                        }

                        cepEvent.getProperties("IoTEntities").addIoTStateObservation(ent.getAbout(), dateOfCreation, nowAsISO);

                        for (IoTProperty p : ent.getProperties()) {

                            for (IoTValue v : p.getIoTStateObservation())
                                cepEvent.addProperty(p.getAbout()).addIoTStateObservation(v.getValue(), v.getPhenomenonTime(), v.getResultTime());

                        }
                    }
                    // CEPHandler.publish("/almanac/local/iotentity/dataFusionManager/" + query.getName(), parser.toJson(event).getBytes(), 2, false);

                } catch (Exception eEntity) {

                    try {

                        IoTProperty ent = (IoTProperty) event.get(key);

                        for (IoTValue v : ent.getIoTStateObservation())
                            cepEvent.addProperty(ent.getAbout()).addIoTStateObservation(v.getValue(), v.getPhenomenonTime(), v.getResultTime());


                    } catch (Exception eBoolean) {

                        try {

                            Boolean ent = (Boolean) event.get(key);
                            cepEvent.addProperty(key.toString()).addIoTStateObservation(ent.toString(), dateOfCreation, nowAsISO);


                        } catch (Exception eProperty) {

                            try {

                                Integer ent = (Integer) event.get(key);
                                cepEvent.addProperty(key.toString()).addIoTStateObservation(ent.toString(), dateOfCreation, nowAsISO);

                            } catch (Exception eString) {
                                try {

                                    Double ent = (Double) event.get(key);
                                    cepEvent.addProperty(key.toString()).addIoTStateObservation(ent.toString(), dateOfCreation, nowAsISO);

                                } catch (Exception e) {
                                    try {

                                        Float ent = (Float) event.get(key);
                                        cepEvent.addProperty(key.toString()).addIoTStateObservation(ent.toString(), dateOfCreation, nowAsISO);

                                    } catch (Exception eFloat) {

                                        try {

                                            String ent = (String) event.get(key);
                                            cepEvent.addProperty(key.toString()).addIoTStateObservation(ent, dateOfCreation, nowAsISO);

                                        } catch (Exception eInteger) {

                                            try {

                                                cepEvent.addProperty(key.toString()  ).addIoTStateObservation(parser.toJson(event), dateOfCreation, nowAsISO);

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
                if(query.haveOutput())
                    for(String output : query.getOutput()) {
                        CEPHandler.publish(output + cepEvent.getAbout(), parser.toJson(cepEvent).getBytes(), 0, false);
                    }
                else
                    for(String output : query.getOutput()) {
                        CEPHandler.publish(ERROR_TOPIC + cepEvent.getAbout(), parser.toJson(cepEvent).getBytes(), 0, false);
                    }
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
    public void update(IoTEntityEvent event) {


        try {
            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
            df.setTimeZone(tz);
            String nowAsISO = df.format(new Date());

            if (!CEPHandler.isConnected())
                CEPHandler.connect();

            if(query.haveOutput())
                for(String output : query.getOutput()) {
                    CEPHandler.publish(output + event.getAbout(), parser.toJson(event).getBytes(), 0, false);
                }
            else
                for(String output : query.getOutput()) {
                    CEPHandler.publish(ERROR_TOPIC + event.getAbout(), parser.toJson(event).getBytes(), 0, false);
                }
        }catch (Exception e){

        }

    }

    @Override
    public boolean publishError(String errorMessage) {

            try {
                if (!CEPHandler.isConnected())
                    CEPHandler.connect();

                HashMap<String,String> error = new HashMap<String, String>();
                error.put("ErrorTopic","Exception");
                error.put("Message",errorMessage);

                CEPHandler.publish(ERROR_TOPIC, errorMessage.getBytes(),0,false);
                return true;
            } catch (MqttException e) {
                e.printStackTrace();
            }
        return false;
    }

    public void destroy(){
        try {
            if(CEPHandler.isConnected())
                CEPHandler.disconnect();

        } catch (MqttException e) {
            e.printStackTrace();
        }

        try {
            CEPHandler.close();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
