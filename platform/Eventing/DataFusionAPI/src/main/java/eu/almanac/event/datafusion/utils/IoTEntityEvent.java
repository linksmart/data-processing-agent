package eu.almanac.event.datafusion.utils;

import java.util.ArrayList;

/**
 * Created by Caravajal on 29.09.2014.
 */
 public class IoTEntityEvent implements java.io.Serializable  {

    private ArrayList<IoTProperty> Properties;
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
        if (!haveProperty(about)) {
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


    public ArrayList<IoTProperty> getProperties() {
        return Properties;
    }
    public boolean haveLikeProperty(String property){
        for (IoTProperty p: this.Properties)
            if(p.getAbout().contains(property)) {
                return true;
            }


        return false;
    }
    public boolean haveProperty(String propertyName){
        if(Properties != null)
            for(IoTProperty i : Properties) {
                if (i.getAbout() != null)
                    if (i.getAbout().equals(propertyName))
                        return true;
            }
        return false;
    }
}
