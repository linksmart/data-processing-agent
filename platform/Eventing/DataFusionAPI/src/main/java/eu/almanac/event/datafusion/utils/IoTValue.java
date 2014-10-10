package eu.almanac.event.datafusion.utils;

/**
 * Created by Caravajal on 03.10.2014.
 */
public class IoTValue implements java.io.Serializable {
    public String Value;
    public String PhenomenonTime;
    public String ResultTime;
    public  IoTValue(){
        Value = null;
        PhenomenonTime= null;
        ResultTime= null;
    }
    public IoTValue(String value, String phenomenonTime, String resultTime){
        Value = value;
        PhenomenonTime= phenomenonTime;
        ResultTime= resultTime;
    }


    public String getValue(){
        return Value;
    };
    public String getPhenomenonTime(){
        return PhenomenonTime;
    };
    public String getResultTime(){
        return ResultTime;
    };
    public void setValue(String value){
         Value = value;
    };
    public void setPhenomenonTime(String value){
         PhenomenonTime= value;
    };
    public void setResultTime(String value){
        ResultTime= value;
    };
}
