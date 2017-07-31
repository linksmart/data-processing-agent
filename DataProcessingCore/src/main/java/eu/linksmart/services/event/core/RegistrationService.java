package eu.linksmart.services.event.core;

import eu.linksmart.api.event.components.Enveloper;
import eu.linksmart.api.event.components.Publisher;
import eu.linksmart.api.event.types.Statement;
import eu.linksmart.api.event.types.impl.MultiResourceResponses;
import eu.linksmart.services.event.feeders.StatementFeeder;
import eu.linksmart.services.event.handler.DefaultEnveloper;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.DynamicConst;
import eu.linksmart.services.event.intern.Utils;
import eu.linksmart.services.payloads.ogc.sensorthing.*;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.Interval;
import eu.linksmart.services.payloads.ogc.sensorthing.linked.DatastreamImpl;
import eu.linksmart.services.payloads.ogc.sensorthing.linked.ObservedPropertyImpl;
import eu.linksmart.services.payloads.ogc.sensorthing.linked.SensorImpl;
import eu.linksmart.services.payloads.ogc.sensorthing.linked.ThingImpl;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.serialization.DefaultDeserializer;
import eu.linksmart.services.utils.serialization.DefaultSerializer;
import eu.linksmart.services.utils.serialization.Deserializer;
import eu.linksmart.services.utils.serialization.Serializer;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by José Ángel Carvajal on 28.07.2017 a researcher of Fraunhofer FIT.
 */
public class RegistrationService {

    private Publisher publisher;
    private Serializer serializer= new DefaultSerializer();
    private Configurator conf = Configurator.getDefaultConfig();
    private Thing thing = new ThingImpl();
    private Logger loggerService = Utils.initLoggingConf(RegistrationService.class);
    private Timer timer;
    private static RegistrationService registrationService = new RegistrationService();

    public RegistrationService() {
        timer = new Timer();

    }


    static public RegistrationService getReference(){

        return registrationService;
    }
    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
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

        StatementFeeder.getStatements().getResources().values().stream().filter(Statement::isRegistrable).forEach(this::addDatastream);

        try {
            publisher.publish(serializer.serialize(thing));
        } catch (IOException e) {
           loggerService.error(e.getMessage(),e);
        }
        loggerService.info("Registration info sent!");

    }
    private void constructBaseThing(){
        thing.setId(DynamicConst.getId());
        thing.setDescription(conf.getString(Const.AGENT_DESCRIPTION));
        thing.setHistoricalLocations(null);
        thing.setLocations(null);
    }
    private void addDatastream( Statement result){
        Datastream datastream = new DatastreamImpl();
        Sensor sensor = new SensorImpl();
        sensor.setId(result.getID());
        sensor.addDatastream(datastream);
        sensor.setMetadata(result);
        sensor.setName(result.getName());
        sensor.setDescription("The Sensor is a virtual sensor generated by a Statement (see it in the metadata) deployed in the LinkSmart(R) IoT Agent with the ID same as the Sensor");
        datastream.setSensor(sensor);
        datastream.setThing(thing);
        datastream.setPhenomenonTime(new Interval());
        datastream.setResultTime(new Interval());
        datastream.setDescription("The Datastream generated by the Statement " +(result.getName()!=null?" named "+result.getName():" without name ")+" in the LinkSmart(R) IoT Agent with the ID of the Thing and with ID same as this Datastream");
        datastream.setId(result.getID());
        thing.addDatastreams(datastream);

    }

    public void startTimer(){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateRegistration();
            }
        },0, conf.getInt(Const.LOG_HEARTBEAT_TIME_CONF_PATH));

    }

    public void stopTimer(){
        timer.cancel();

    }




}
