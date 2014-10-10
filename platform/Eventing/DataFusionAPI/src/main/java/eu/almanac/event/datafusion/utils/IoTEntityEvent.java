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

    public IoTEntityEvent (int n, String about){
        this.About = about;
        this.Properties = new ArrayList<IoTProperty>(n);
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

    public IoTProperty addProperty(String about, int n) {
        Properties.add(new IoTProperty(n,about));
        return Properties.get(Properties.size()-1);
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


    public IoTProperty[] getProperties() {
        IoTProperty[] v = null;
        return Properties.toArray(v);
    }
}
