package eu.almanac.event.cep.esper.utils;

import eu.almanac.event.cep.esper.EsperEngine;
import eu.almanac.event.datafusion.utils.payload.IoTPayload.IoTEntityEvent;
import eu.almanac.event.datafusion.utils.payload.IoTPayload.IoTProperty;
import eu.almanac.ogc.sensorthing.api.datamodel.Observation;
import eu.linksmart.api.event.types.EventEnvelope;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.time.DateUtils;
//import eu.almanac.ogc.sensorthing.api.datamodel.*;
//import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation;
//import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Sensor;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Tools {
    static private Map<String, Object> variables= new HashMap<>();
    static public Random Random = new Random();
        static public IoTEntityEvent CreateIoTEntity(String entity, String property, String observation ){

            try {

                IoTEntityEvent ret = new IoTEntityEvent(entity);

                ret.addProperty(  entity+"##"+property );
                ret.getProperties(0).addIoTStateObservation(observation);

                return ret;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    static public IoTEntityEvent CreateIoTEntity(String entity, String property, Boolean observation ){

       return  CreateIoTEntity(entity,property,observation.toString());
    }
    static public IoTEntityEvent CreateIoTEntity(String entity, String property, Integer observation ){

        return  CreateIoTEntity(entity,property,observation.toString());
    }

    static public IoTEntityEvent CreateIoTEntity(String entity, String property, Float observation ){

        return  CreateIoTEntity(entity,property,observation.toString());
    }

    static public IoTEntityEvent CreateIoTEntity(String entity, String property, Double observation ){

        return  CreateIoTEntity(entity,property,observation.toString());
    }
    static public IoTEntityEvent CreateIoTEntity(String entity, String property, Object observation ){

        return  CreateIoTEntity(entity,property,observation.toString());
    }
    static public IoTEntityEvent CreateIoTEntity( String property, Boolean observation ){

        return  CreateIoTEntity(generateRandomAbout(),property,observation.toString());
    }

    static public IoTEntityEvent CreateIoTEntity(String property, Integer observation ){

        return  CreateIoTEntity(generateRandomAbout(),property,observation.toString());
    }

    static public IoTEntityEvent CreateIoTEntity( String property, Float observation ){

        return  CreateIoTEntity(generateRandomAbout(),property,observation.toString());
    }

    static public IoTEntityEvent CreateIoTEntity( String property, Double observation ){

        return  CreateIoTEntity(generateRandomAbout(),property,observation.toString());
    }

    static public Map<String, IoTEntityEvent> CreateIoTEntities(Map<String, IoTEntityEvent> entities, String property, String observation){


        Map<String,IoTEntityEvent> arg =entities;
        Map<String, IoTEntityEvent> ret = new HashMap<String,IoTEntityEvent>();

        for (String key: arg.keySet()){
            ret.put(key,CreateIoTEntity(key,generateRandomAbout()+property,observation));
        }

        return ret;
    }
    static public Map<String, IoTEntityEvent> CreateIoTEntities(Map<String, IoTEntityEvent> entities, String property, Float observation){

        return CreateIoTEntities(entities,property,observation.toString());
    }

    static public Map<String, IoTEntityEvent> CreateIoTEntities(Map<String, IoTEntityEvent> entities, String property, Double observation){

        return CreateIoTEntities(entities,property,observation.toString());
    }

    static public Map<String, IoTEntityEvent> CreateIoTEntities(Map<String, IoTEntityEvent> entities, String property, Integer observation){

        return CreateIoTEntities(entities,property,observation.toString());
    }
    static public Map<String, IoTEntityEvent> CreateIoTEntities(Map<String, IoTEntityEvent> entities, String property, Boolean observation){

        return CreateIoTEntities(entities,property,observation.toString());
    }

    static public IoTEntityEvent CreateIoTEntities(IoTEntityEvent entities, String property, Object observation ){

        return CreateIoTEntity(entities.getAbout(),property,observation.toString());
    }

    static public Boolean containsProperty(IoTEntityEvent entity, String property) {

        System.out.println(entity.hasProperty(property));
        return entity.hasProperty(property);
    }
    static public Boolean containsProperty(IoTProperty[] properties, String property) {


        for (IoTProperty p: properties)
            if(p.getAbout().equals(property)) {
                System.out.println(true);
                return true;
            }
        System.out.println(false);
        return false;
    }
    static public long getMilliseconds(Date date){
        return date.getTime();
    }

    static public Boolean likeProperty(IoTEntityEvent entity, String property) {


        return entity.hasLikeProperty(property);
    }
    static public Boolean likeProperty(IoTProperty[] properties, String property) {


        for (IoTProperty p: properties)
            if(p.getAbout().contains(property)) {
                return true;
            }
        return false;
    }
    static public String lastObservationOfProperty(IoTEntityEvent entity, String property ){

      return  entity.getProperties(property).getIoTStateObservation(0).getValue();
    }
    static public String lastObservationLikeProperty(IoTEntityEvent entity, String property ){

        for (IoTProperty p: entity.getProperties())
            if(p.getAbout().contains(property)) {

                return p.getIoTStateObservation().get(0).getValue();
            }

        return null;
    }
    static public String generateRandomAbout(){

        return UUID.randomUUID().toString().replace("-","_").replace("#","_");
    }

    static public Observation ObservationFactory(Object event, String resultType, String StreamID, String sensorID, long time){
        Observation ob = Observation.factory(event,resultType,StreamID,sensorID,time);
         /*if(Configurator.getDefaultConfig().getBoolean(Const.SIMULATION_EXTERNAL_CLOCK))
           try {
                if(ob.getPhenomenonTime().after(getDateNow()))
                    EsperEngine.getEngine().setEngineTimeTo(ob.getDate());
            } catch (Exception e) {
                Utils.initLoggingConf(Tools.class).error(e.getMessage(),e.getCause());
            }*/
        return ob;

    }
    static public long getTimeNow(){
        return EsperEngine.getEngine().getEngineCurrentDate().getTime();
    }

    static public Date getDateNow(){
        return EsperEngine.getEngine().getEngineCurrentDate();
    }
    static public String getIsoTimeFormat(){
        return "yyyy-MM-dd'T'HH:mm:ss.S'Z'";
    }
    static public String getEsperTimeFormat(){
        return "yyyy-MM-dd HH:mm:ss.SSS";
    }
    static private DateFormat dateFormat =null;

    static private DateFormat getDateFormat(String timeFormat, String gmt){

        if(dateFormat == null){
            TimeZone tz = TimeZone.getTimeZone(gmt);
            dateFormat  = new SimpleDateFormat(getIsoTimeFormat());
            dateFormat.setTimeZone(tz);
        }
        if (!dateFormat.getTimeZone().getID().equals(gmt)){
            TimeZone tz = TimeZone.getTimeZone(gmt);
            dateFormat.setTimeZone(tz);
        }


        return dateFormat;

    }
    static public String getDateNowString(){
        return getDateFormat(getIsoTimeFormat(), "UTC").format(new Date());
    }
    static private String toTimestamp(Date date){

        return getDateFormat(getEsperTimeFormat(), "UTC").format(date);

    }
    static public String hashIt(String string){
        MessageDigest SHA256 = null;
        try {
            SHA256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return (new BigInteger(1,SHA256.digest((string).getBytes()))).toString();
    }
    static public boolean insertMultipleEvents(long noEvents,Object event){
         EsperEngine engine =EsperEngine.getEngine();
        EventEnvelope eventEnvelope;
        if(! (event instanceof EventEnvelope) )
            eventEnvelope = ObservationFactory(event,event.getClass().getCanonicalName(),UUID.randomUUID().toString(),engine.getName(),engine.getAdvancedFeatures().getEngineCurrentDate().getTime());
        else
            eventEnvelope = (EventEnvelope) event;
        for (int i=0; i< noEvents; i++) {

            engine.addEvent(  eventEnvelope, eventEnvelope.getClass());
            eventEnvelope.setDate(DateUtils.addHours(eventEnvelope.getDate(),1));
        }
        return true;

    }
    static public EventEnvelope[] creatMultipleEvents(long noEvents,Object event){
        EventEnvelope eventEnvelope;
        EsperEngine engine =EsperEngine.getEngine();
        EventEnvelope[] result = new Observation[(int)noEvents];
        if(! (event instanceof EventEnvelope) )
            eventEnvelope = ObservationFactory(event,event.getClass().getCanonicalName(),UUID.randomUUID().toString(),engine.getName(),engine.getAdvancedFeatures().getEngineCurrentDate().getTime());
        else
            eventEnvelope = (EventEnvelope) event;

        result[0] = ObservationFactory(event,event.getClass().getCanonicalName(),UUID.randomUUID().toString(),engine.getName(),DateUtils.addHours(eventEnvelope.getDate(), 1).getTime());

        for (int i=1; i< noEvents; i++) {

            //engine.addEvent("", eventEnvelope, eventEnvelope.getClass());
            result[i] = ObservationFactory(event,event.getClass().getCanonicalName(),UUID.randomUUID().toString(),engine.getName(),DateUtils.addHours(result[i-1].getDate(), 1).getTime());

        }
        return result;

    }
 /*   static public Observation generateObservation(String id, Date date, Object observation, String resultType){

        Sensor sen = new Sensor();
        sen.setId(id);
        sen.setObservations(null);
        Datastream ds = new Datastream();
        ds.setObservations(null);
        ds.setId(hashIt(UUID.randomUUID().toString()));
        Observation ob = new Observation();
        ob.setDatastream(ds);
        ob.setSensor(sen);
        ob.setPhenomenonTime(new Date());
        ob.setResultType(observation);
        ob.setResultValue(resultType);
        ob.setFeatureOfInterest(null);
        return ob;

    }
    static public Observation generateObservation( Date date, Object observation, String resultType){
       return generateObservation(UUID.randomUUID().toString(),date,observation,resultType);
    }
    static public Observation generateObservation( Date date, Object observation){
        return  generateObservation(new Date(),observation, "Measure");
    }
    static public Observation generateObservation(  Object observation){

        return  generateObservation(new Date(),observation);
    }*/
    static private Map<String, Map> used = new Hashtable<String,Map>();
    static public boolean hadBeanUsed(String queryName, Object[] objects){
        boolean hadBeanUsed ;
        if(!used.containsKey(queryName))
            used.put(queryName,null);
        if(used.get(queryName)==null) {
            used.put(queryName,new Hashtable());
            for (Object object: objects)
                used.get(queryName).put(object,object);

            hadBeanUsed = false;
        }else {
            hadBeanUsed = false;
            for (int i = objects.length-1; i >0 && !hadBeanUsed; i--)
                hadBeanUsed = used.get(queryName).containsKey(objects[i]);

        }

        return hadBeanUsed;
    }
    static public boolean isTimeContinuous(EventEnvelope[] events){
        EventEnvelope previous= null;

        for (EventEnvelope event: Arrays.asList(events)) {

            if (previous!=null && !DateUtils.addHours(previous.getDate(),2 ).after(event.getDate()))
                return false;

            previous = event;

        }
        return true;
    }
    static public boolean isTimeContinuous(EventEnvelope[][] eventss){
        EventEnvelope previous= null;

        for(EventEnvelope[] events: Arrays.asList(eventss))
            if(!isTimeContinuous(events))
                return false;
        return true;
    }
    static public boolean isTimeContinuous(EventEnvelope[] events1,EventEnvelope[] events2,boolean inBetweenOnly){

        return (inBetweenOnly || (isTimeContinuous(events1) && isTimeContinuous(events2)) )&&
                (DateUtils.addHours(  events1[events1.length-1].getDate(),2 ).after(  events2[0].getDate()) &&
                        events1[events1.length-1].getDate().before(events2[0].getDate())
                );


    }
    static public boolean isTimeContinuous(EventEnvelope[] events1,EventEnvelope[] events2){

        return isTimeContinuous(events1,events2,false);


    }
    static public boolean isTimeContinuous(Object events1,Object events2) {
        return events1 instanceof EventEnvelope[] && events2 instanceof EventEnvelope[] && isTimeContinuous((EventEnvelope[]) events1, (EventEnvelope[]) events2);
    }
    static public boolean isTimeContinuous(Object events1,Object events2, Object inBetweenOnly) {
        return events1 instanceof EventEnvelope[] && events2 instanceof EventEnvelope[] && inBetweenOnly instanceof Boolean && isTimeContinuous((EventEnvelope[]) events1, (EventEnvelope[]) events2,(boolean)inBetweenOnly);
    }
    static public EventEnvelope[] flattArrays(EventEnvelope[][] eventss){


        EventEnvelope[] result  =null;
        if(eventss!=null && eventss.length>0) {
            if (eventss.length > 1) {
                for (int i = 1; i < eventss.length; i++)
                    result = (EventEnvelope[]) ArrayUtils.addAll(result, eventss[i]);
            }
            else result = eventss[0];
        }

            return result;
    }
    static public Object[] addAll(Object o, Object o2){
        if(o instanceof Object[] && o2 instanceof Object[] ){
            return ArrayUtils.addAll((Object[])o,(Object[])o2);
        }
        return new Object[]{o,o2};
    }
    static public Object[] removeAll(Object o, Object o2){
        List<Object> list=null, list2=null, result;
        if(o instanceof Object[]){
             list= Arrays.asList((Object[])o);
        }else if(o instanceof List){
            list= (List<Object>)o;
        }

        if(o2 instanceof Object[]){
            list2= Arrays.asList((Object[])o2);
        }else if(o2 instanceof List){
            list2= (List<Object>)o2;
        }

        if(list!= null && list2!=null){
            list.removeAll(list2);
            return list.toArray();
        }else if(list!=null){
            list.remove(o2);
            return list.toArray();
        }else if(list2!=null){
            list = new LinkedList<>();
            list.add(o);
            list.removeAll(list2);
            return list.toArray();
        }

        return new Object[0];
    }

}