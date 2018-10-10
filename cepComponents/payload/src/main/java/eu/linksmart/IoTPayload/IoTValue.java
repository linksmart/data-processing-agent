package eu.linksmart.IoTPayload;

/**
 * Created by Caravajal on 03.10.2014.
 */
@Deprecated
public class IoTValue implements java.io.Serializable {
    private static final long serialVersionUID = 3048266813171001513L;
    protected String Value;
    protected String PhenomenonTime;
    protected String ResultTime;
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
    }
    @Override
    public String toString(){
        return Value;
    }
    public String getPhenomenonTime(){
        return PhenomenonTime;
    }
    public String getResultTime(){
        return ResultTime;
    }
    public void setValue(String value){
         Value = value;
    }
    public void setPhenomenonTime(String value){
         PhenomenonTime= value;
    }
    public void setResultTime(String value){
        ResultTime= value;
    }
}
