package eu.almanac.event.datafusion.utils.payload.IoTPayload;

import eu.almanac.event.datafusion.utils.generic.GenericCEP;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Caravajal on 29.09.2014.
 */
@Deprecated
 public class IoTEntityEvent implements java.io.Serializable, GenericCEP<IoTEntityEvent> {

    private List<IoTProperty> Properties;
    private String About;

   public IoTEntityEvent (){
        this.About = null;
        this.Properties = null;
    }

    public IoTEntityEvent ( String about){
        this.About = about;
        this.Properties = new ArrayList<IoTProperty>();
    }
    public IoTEntityEvent (IoTProperty[] properties, String about){
        this.About = about;
        this.Properties = new ArrayList<IoTProperty>(properties.length);
        for (IoTProperty v : properties)
            this.Properties.add(v);
    }

    public String getAbout() {
        return About;
    }

    public void setAbout( String value){
        this.About = value;
    }
    public void setProperties(int index, IoTProperty value) {
        Properties.set(index, value);
    }

    public IoTProperty addProperty(String about) {
        if (!hasProperty(about)) {
            Properties.add(new IoTProperty(about));
            return Properties.get(Properties.size() - 1);
        }else{
            return getProperties(about);
        }
    }
    public IoTProperty getProperties(int index) {
        return Properties.get(index);
    }

    public IoTProperty getProperties(String key) {
        if(Properties != null)
            for(IoTProperty i : Properties) {
                if (i.getAbout() != null)
                    if (i.getAbout().equals(key))
                        return i;

            }
        return null;
    }
    public void setProperties( IoTProperty[] value) {
        this.Properties = new ArrayList<IoTProperty>(value.length);
        for (IoTProperty v : value)
            this.Properties.add(v);
    }


    public List<IoTProperty> getProperties() {
        return Properties;
    }
    public boolean hasLikeProperty(String property){
        for (IoTProperty p: this.Properties)
            if(p.getAbout().contains(property)) {
                return true;
            }


        return false;
    }
    public boolean hasProperty(String propertyName){
        if(Properties != null)
            for(IoTProperty i : Properties) {
                if (i.getAbout() != null)
                    if (i.getAbout().equals(propertyName))
                        return true;
            }
        return false;
    }
    @Override
    public IoTEntityEvent aggregateToAnEvent(IoTEntityEvent cepEvent){


        if (cepEvent.getProperties("IoTEntities") == null) {
            cepEvent.addProperty("IoTEntities");

        }

        cepEvent.getProperties("IoTEntities").addIoTStateObservation(About, getDateNowString(), getDateNowString());
        for (IoTProperty p : Properties) {

            p.aggregateToAnEvent(cepEvent);

        }
        return cepEvent;

    }

    @Override
    public void addValue(String key, Object value) {
        addProperty(key).addIoTStateObservation(value.toString());
    }

    @Override
    public Object getValue(String key) {
           return getProperties(key).getValue(key);


    }

    @Override
    public void setBaseName(String name) {
        About = name;
    }
    @Override
    public boolean isGenerated() {

        return getValue(GenericCEP.GENERATED) != null;
    }
    public void add(IoTEntityEvent addEntity){

        for (IoTProperty p : addEntity.getProperties()) {

           this.add(addEntity);

        }

    }
    private String getDateNowString(){
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
        df.setTimeZone(tz);
        // creating DateTimeNow string
        return df.format(new Date());
    }
}
