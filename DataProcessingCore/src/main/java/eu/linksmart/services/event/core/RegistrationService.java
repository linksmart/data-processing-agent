package eu.linksmart.services.event.core;

import eu.linksmart.api.event.components.Publisher;
import eu.linksmart.api.event.types.Statement;
import eu.linksmart.services.event.feeders.StatementFeeder;
import eu.linksmart.services.event.handler.DefaultMQTTPublisher;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.services.event.intern.Utils;
import eu.linksmart.services.payloads.ogc.sensorthing.*;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.Interval;
import eu.linksmart.services.payloads.ogc.sensorthing.linked.DatastreamImpl;
import eu.linksmart.services.payloads.ogc.sensorthing.linked.SensorImpl;
import eu.linksmart.services.payloads.ogc.sensorthing.linked.ThingImpl;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.serialization.DefaultSerializer;
import eu.linksmart.services.utils.serialization.Serializer;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.*;

/**
 * Created by José Ángel Carvajal on 28.07.2017 a researcher of Fraunhofer FIT.
 */
public class RegistrationService {

    private Publisher publisher;
    private Serializer serializer= new DefaultSerializer();
    private Configurator conf = Configurator.getDefaultConfig();


    private Thing thing = new ThingImpl();
    private Map<String,Datastream> datastreamMap = new Hashtable<>();

    private Logger loggerService = Utils.initLoggingConf(RegistrationService.class);
    private Timer timer;
    private boolean changed=true;
    private static RegistrationService registrationService = new RegistrationService();

    private RegistrationService() {
        timer = new Timer();
        constructBaseThing();



    }


    static public RegistrationService getReference(){

        if(registrationService==null)
            registrationService = new RegistrationService();

        return registrationService;
    }

    public void registerStatement(Statement statement){
        addDatastream(statement);
        updateRegistration();

    }
    public void registerDatastream(Datastream datastream){
        datastream.setThing(thing);
        thing.addDatastreams(datastream);

        updateRegistration();

    }
    public void unregisterDatastream(Object id){
        thing.removeDatastream(id);
        updateRegistration();
    }
    private void updateRegistration(){
        if(thing.getDescription() == null)
            constructBaseThing();

        thing.setDatastreams(new ArrayList<>());


       checkDatastream(StatementFeeder.getStatements().getResources());

        if(changed) {
            try {
                publisher.publish(serializer.serialize(thing));
            } catch (IOException e) {
                loggerService.error(e.getMessage(), e);
            }
            loggerService.info("Registration info sent!");
            changed =false;
        }

    }

    private void checkDatastream(Map<String, Statement> resources) {
        if(!datastreamMap.keySet().containsAll(resources.keySet())){
            resources.keySet().stream().filter(d->!datastreamMap.keySet().contains(d)).forEach(match->datastreamMap.put(match,addDatastream(resources.get(match))));
            changed =true;
        }
        if(!resources.keySet().containsAll(datastreamMap.keySet())){
            datastreamMap.keySet().stream().filter(d->!resources.keySet().contains(d)).forEach(match->datastreamMap.remove(match));
            changed=true;
        }
        if(changed)
            thing.setDatastreams(new ArrayList<>(datastreamMap.values()));
    }

    private void constructBaseThing(){
        thing.setId(SharedSettings.getId());
        thing.setDescription(conf.getString(Const.AGENT_DESCRIPTION));
        thing.setHistoricalLocations(null);
        thing.setLocations(null);
    }
    private Datastream addDatastream( Statement result){

        Datastream datastream = new DatastreamImpl();
        Sensor sensor = new SensorImpl();
        sensor.setId(result.getID());
        sensor.addDatastream(datastream);
        sensor.setMetadata(result);
        sensor.setName(result.getName());
        sensor.setDescription("The Sensor is a virtual sensor generated by a Statement (see it in the metadata) deployed in the LinkSmart(R) IoT Agent with the ID same as the Sensor");
        datastream.setSensor(sensor);
        datastream.setPhenomenonTime(new Interval());
        datastream.setResultTime(new Interval());
        datastream.setDescription("The Datastream generated by the Statement " +(result.getName()!=null?" named "+result.getName():" without name ")+" in the LinkSmart(R) IoT Agent with the ID of the Thing and with ID same as this Datastream");
        datastream.setId(result.getID());

        datastream.setThing(thing);
        return datastream;

    }

    public void startTimer() {

        publisher = new DefaultMQTTPublisher(
                SharedSettings.getId(),
                SharedSettings.getId(),
                new String[]{conf.getString(Const.REGISTRATION_TOPIC).replace("<id>", SharedSettings.getId())+thing.getId()},
                new String[]{"control"},
                SharedSettings.getWill(),
                SharedSettings.getWillTopic()

        );
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateRegistration();
            }
        }, 0, 1000);

    }

    public void stopTimer(){
        timer.cancel();

    }
    public Thing getThing() {
        return thing;
    }
    public String getThingString() {
        try {
            return serializer.toString(thing);
        } catch (IOException e) {
            loggerService.error(e.getMessage(),e);
        }
        return null;
    }



}
