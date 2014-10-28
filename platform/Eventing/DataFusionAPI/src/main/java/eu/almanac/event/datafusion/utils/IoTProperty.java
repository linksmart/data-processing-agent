package eu.almanac.event.datafusion.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class IoTProperty implements java.io.Serializable {
    private ArrayList<IoTValue> IoTStateObservation =null;
    private String About = null;

    public IoTProperty (){
        this.About = "";
        this.IoTStateObservation = null;
    }
    public IoTProperty ( String about){
        this.About = about;
        this.IoTStateObservation = new ArrayList<IoTValue>();
    }
    public IoTProperty (ArrayList<IoTValue> IoTStateObservation, String about){
        this.About = about;
        this.IoTStateObservation = IoTStateObservation;
    }
    public String getAbout() {
           return About;
       }

    public void setAbout(String value) {
         About = value;
    }
    public void addIoTStateObservation(String value, String phenomenonTime, String resultTime){

        IoTStateObservation.add( new IoTValue(value,phenomenonTime,resultTime));

    }


    public void addIoTStateObservation(String value){

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());
        IoTStateObservation.add( new IoTValue(value,nowAsISO,nowAsISO));
    }

    public ArrayList<IoTValue> getIoTStateObservation() {

           return IoTStateObservation;
       }
    public void setIoTStateObservation( IoTValue[] value) {
        this.IoTStateObservation = new ArrayList<IoTValue>(value.length);
        for (IoTValue v : value)
            this.IoTStateObservation.add(v);
    }

    public IoTValue getIoTStateObservation(int index) {
        return IoTStateObservation.get(index);
    }
    public void setIoTStateObservation(int index, IoTValue value) {
        IoTStateObservation.add( value);
    }


   }