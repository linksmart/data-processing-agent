package eu.almanac.event.datafusion.esper.utils;

import eu.almanac.event.datafusion.utils.payload.IoTPayload.IoTEntityEvent;
import eu.almanac.event.datafusion.utils.payload.IoTPayload.IoTProperty;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation;

import javax.xml.crypto.Data;
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

    static public Map CreateIoTEntities(Map entities, String property, String observation ){


        Map<String,IoTEntityEvent> arg =entities;
        Map<String, IoTEntityEvent> ret = new HashMap<String,IoTEntityEvent>();

        for (String key: arg.keySet()){
            ret.put(key,CreateIoTEntity(key,generateRandomAbout()+property,observation));
        }

        return ret;
    }
    static public Map CreateIoTEntities(Map entities, String property, Float observation ){

        return CreateIoTEntities(entities,property,observation.toString());
    }

    static public Map CreateIoTEntities(Map entities, String property, Double observation ){

        return CreateIoTEntities(entities,property,observation.toString());
    }

    static public Map CreateIoTEntities(Map entities, String property, Integer observation ){

        return CreateIoTEntities(entities,property,observation.toString());
    }
    static public Map CreateIoTEntities(Map entities, String property, Boolean observation ){

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

    static public String IoTAVG(Map entities){
        System.out.println("test");
        return "hola";
    }
    static public String IoTAVG(Object[] entities){
        System.out.println("test");
        return "hola";
    }
   /* static public boolean growing(Object[] objects){
        if (objects.length<2)
            return true;
        Observation[] observations = (Observation[])objects;
        for (int i=1;i<objects.length;i++)
            if((Double)observations[i-1].getResultValue()> (Double)observations[i].getResultValue())
                return false;

        return true;
    }
*/
    static public boolean growing(Object[] objects){
        if (objects.length<2)
            return true;
        Observation[] observations = (Observation[])objects;
        Map<String,Map<Integer,Observation> > accumulatedTruth = new HashMap<>();
        for (Observation observation: observations) {
            if (!accumulatedTruth.containsKey(observation.getId()))
                accumulatedTruth.put(observation.getId(), new HashMap<Integer, Observation>());

            accumulatedTruth.get(observation.getId()).put(accumulatedTruth.get(observation.getId()).size() - 1, observation);
        }
        for (int i=1;i<objects.length;i++)
            if((Double)observations[i-1].getResultValue()> (Double)observations[i].getResultValue())
                return false;

        return true;
    }
   static public Map growingSamples(Object[] objects){

       Map<String,Double > lastPerID = new HashMap<>();
       Map<String,Boolean > accumulatedTruth = new HashMap<>();
       for (Observation ob :(Observation[])objects) {
           if (lastPerID.containsKey(ob.getId())) {
               if(accumulatedTruth.containsKey(ob.getId())) {
                   if (accumulatedTruth.get(ob.getId())) {
                       accumulatedTruth.put(ob.getId(), lastPerID.get(ob.getId()) < (Double) ob.getResultValue());
                   }
               }else
                   accumulatedTruth.put(ob.getId(),lastPerID.get(ob.getId())<(Double)ob.getResultValue());

           } else {
               lastPerID.put(ob.getId(),(Double)ob.getResultValue());
           }
       }

       return accumulatedTruth;
   }
    static public boolean compareObservationsInLapsedTime(Object ob2){
        return true;

    }


}