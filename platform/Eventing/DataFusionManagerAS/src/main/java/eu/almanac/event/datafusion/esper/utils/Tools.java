package eu.almanac.event.datafusion.esper.utils;

import eu.almanac.event.datafusion.utils.payload.IoTPayload.IoTEntityEvent;
import eu.almanac.event.datafusion.utils.payload.IoTPayload.IoTProperty;

import java.util.*;

public class Tools {
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


}