package eu.alamanac.event.datafusion.esper.utils;

import eu.almanac.event.datafusion.utils.IoTEntityEvent;
import eu.almanac.event.datafusion.utils.IoTProperty;
import eu.almanac.event.datafusion.utils.IoTValue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

public class Tools {
        static public IoTEntityEvent CreateIoTEntity(String entity, String property, String observation ){

            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");

            IoTEntityEvent ret = new IoTEntityEvent(1,entity);
            df.setTimeZone(tz);
            String nowAsISO = df.format(new Date());
            ret.setProperties(0, new IoTProperty(1, property));
            ret.getProperties(0).setIoTStateObservation(0, new IoTValue(observation,nowAsISO,nowAsISO));

            return ret;
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

        return entity.getProperties(property)!=null;
    }
    static public Boolean containsProperty(IoTProperty[] properties, String property) {

        for (IoTProperty p: properties)
            if(p.getAbout().equals(property))
                return true;
        return false;
    }
    static public String lastObservationOfProperty(IoTEntityEvent entity, String property ){

      return  entity.getProperties(property).getIoTStateObservation(0).getValue();
    }

    static public String generateRandomAbout(){
        return UUID.randomUUID().toString().replace("-","_").replace("#","_");
    }

    static public String generateRandomAbout(String name){
        return UUID.fromString(name).toString().replace("-","_").replace("#","_");
    }

}