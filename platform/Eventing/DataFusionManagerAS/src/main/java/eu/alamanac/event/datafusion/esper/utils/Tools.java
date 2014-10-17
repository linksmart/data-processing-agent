package eu.alamanac.event.datafusion.esper.utils;

import eu.almanac.event.datafusion.utils.IoTEntityEvent;
import eu.almanac.event.datafusion.utils.IoTProperty;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Tools {
    static public Random Random = new Random();
        static public IoTEntityEvent CreateIoTEntity(String entity, String property, String observation ){

            try {


                TimeZone tz = TimeZone.getTimeZone("UTC");
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
                df.setTimeZone(tz);
                String nowAsISO = df.format(new Date());

                IoTEntityEvent ret = new IoTEntityEvent(entity);

                ret.addProperty(  property);
                ret.getProperties(0).addIoTStateObservation(observation, nowAsISO, nowAsISO);


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
    static public Boolean containsProperty(IoTEntityEvent entity, String property) {

        System.out.println(entity.haveProperty(property));
        return entity.haveProperty(property);
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


        return entity.haveLikeProperty(property);
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


}