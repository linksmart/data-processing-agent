package eu.almanac.event.datafusion.utils.payload.SenML;


import eu.almanac.event.datafusion.utils.generic.GenericCEP;
import eu.almanac.event.datafusion.utils.payload.IoTPayload.IoTEntityEvent;
import eu.almanac.event.datafusion.utils.payload.IoTPayload.IoTProperty;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Caravajal on 13.02.2015.
 *
 *                +---------------------------+------+--------+
 |                     SenML | JSON | Type   |
 +---------------------------+------+--------+
 |                 Base Name | bn   | String |
 |                 Base Time | bt   | Number |
 |                Base Units | bu   | Number |
 |                   Version | ver  | Number |
 | Measurement or Parameters | e    | Array  |
 +---------------------------+------+--------+
 */
public class Event implements java.io.Serializable, GenericCEP<Event> {
    private static final long serialVersionUID = -1510508593277110230L;
    private String bn;
    private Long bt;
    private String bu;
    private Short ver;
    private Vector<Measurement> e;
    private Boolean eNameChanged;
    private Map<String,Integer> index;

    public  Event(){
        index = null;
        eNameChanged = false;
        bn = null;
        bt = null;
        bu = null;
        ver = null;
        e = null;
    }

    public Event(String baseName){

        index = null;
        this.bn = baseName;
        e = new Vector<Measurement>();
        eNameChanged = false;
        bt = null;
        bu = null;
        ver = null;

    }
    public Event(IoTEntityEvent entityEvent){
        index = new HashMap<String, Integer>();
        this.bn = entityEvent.getAbout();

        e = new Vector<Measurement>(entityEvent.getProperties().size());
        for (IoTProperty p : entityEvent.getProperties())
            addValue(p.getAbout(),p.getValue(p.getAbout()));



        eNameChanged = true;
        indexing();
        bt = new Date().getTime();
        bu = null;
        ver = 1;
    }
    public String getBn() {
        return bn;
    }

    public void setBn(String bn) {
        this.bn = bn;
    }

    public Long getBt() {
        return bt;
    }

    public void setBt(Long bt) {
        this.bt = bt;
    }

    public String getBu() {
        return bu;
    }

    public void setBu(String bu) {
        this.bu = bu;
    }

    public Short getVer() {
        return ver;
    }

    public void setVer(Short ver) {
        this.ver = ver;
    }

    public Vector<Measurement> getE() {
        return e;
    }

    public void setE(Vector<Measurement> e) {
        this.e = e;
    }

    public String getBaseName() {
        return bn;
    }

    public Long getBaseTime() {
        return bt;
    }

    public String getBaseUnits() {
        return bu;
    }

    public Short getVersion() {
        return ver;
    }

    public Vector<Measurement> getElements() {
        return e;
    }

    public Object getValue(){
        for (Measurement m : e){
            if (! m.getName().startsWith("@") )
                return m.getAutoValue();

        }
        return null;

    }
    @Override
    public Event aggregateToAnEvent(Event cepEvent) {
        cepEvent.addValue(bn,e.get(0));

        return cepEvent;
    }

    @Override
    public void addValue(String key, Object value) {
        addProperty(key).setAutoValue(value);

    }

    @Override
    public Object getValue(String key) {
        return getEbyName(key);
    }
    @Override
    public boolean isGenerated() {

        return getValue(GenericCEP.GENERATED) != null;
    }
    @Override
    public void setBaseName(String baseName) {
        this.bn = baseName;
    }

    public void setBaseTime(Long baseTime) {
        this.bt = baseTime;
    }

    public void setBaseUnits(String baseUnits) {
        this.bu = baseUnits;
    }

    public void setVersion(Short version) {
        this.ver = version;
    }

    public void setElements(Vector<Measurement> elements) {
        this.e = elements;
    }

    public Measurement addProperty(String name){
        e.add(new Measurement(name));
        return e.lastElement();
    }

    public Measurement getEbyName(String name){

        indexing();

        return e.get(index.get(name));
    }
    private  void  indexing(){
        synchronized(eNameChanged) {
            if (index.size() != e.size() || eNameChanged)
                for (int i = 0; i < e.size(); i++) {
                    index.put(e.get(i).getSv(), i);
                }
            eNameChanged = false;
        }

    }
    /**
     * Created by Caravajal on 13.02.2015.
     *   +---------------+------+----------------+
     |         SenML | JSON | Notes          |
     +---------------+------+----------------+
     |          Name | n    | String         |
     |         Units | u    | String         |
     |         Value | v    | Floating point |
     |  String Value | sv   | String         |
     | Boolean Value | bv   | Boolean        |
     |     Value Sum | s    | Floating point |
     |          Time | t    | Number         |
     |   Update Time | ut   | Number         |
     +---------------+------+----------------+
     */
    public class Measurement  implements java.io.Serializable{
        private static final long serialVersionUID = -3921246268323727465L;
        private String n;
        private String u;
        private Double v;
        private String sv;
        private Boolean bv;
        private Double s;
        private Long t;
        private Long ut;


        public  Measurement(){

        }
        public  Measurement(String name){
            n = name;
        }


        public String getN() {
            return n;
        }

        public void setN(String n) {
            synchronized(eNameChanged) {
                this.n = n;
                eNameChanged = true;
            }
        }

        public String getU() {
            return u;
        }

        public void setU(String u) {
            this.u = u;
        }

        public Double getV() {
            return v;
        }

        public void setV(Double v) {
            this.v = v;
        }

        public String getSv() {
            return sv;
        }

        public void setSv(String sv) {
            this.sv = sv;
        }

        public Boolean getBv() {
            return bv;
        }

        public void setBv(Boolean bv) {
            this.bv = bv;
        }

        public Double getS() {
            return s;
        }

        public void setS(Double s) {
            this.s = s;
        }

        public Long getT() {
            return t;
        }

        public void setT(Long t) {
            this.t = t;
        }

        public Long getUt() {
            return ut;
        }

        public void setUt(Long ut) {
            this.ut = ut;
        }

        //============================================
        public String getName() {
            return n;
        }

        public void setName(String n) {
            synchronized(eNameChanged) {
                this.n = n;
                eNameChanged = true;
            }
        }

        public String getUnit() {
            return u;
        }

        public void setUnit(String u) {
            this.u = u;
        }

        public Double getValue() {
            return v;
        }

        public void setValue(Double v) {
            this.v = v;
        }

        public String getStringValue() {
            return sv;
        }

        public void setStringValue(String sv) {
            this.sv = sv;
        }

        public Boolean getBooleanValue() {
            return bv;
        }

        public void setBooleanValue(Boolean bv) {
            this.bv = bv;
        }

        public Double getSum() {
            return s;
        }

        public void setSum(Double s) {
            this.s = s;
        }

        public Long getTime() {
            return t;
        }

        public void setTime(Long t) {
            this.t = t;
        }

        public Long getUpdateTime() {
            return ut;
        }

        public void setUpdateTime(Long ut) {
            this.ut = ut;
        }
        public void setAutoValue(Object value){
             if (value instanceof Long){
                t = (Long)value;
            }else if (value instanceof Double){
                v = (Double)value;

            }else if (value instanceof Integer){
                v = ((Integer)value).doubleValue();

            } else if (value instanceof Boolean){
                bv = (Boolean)value;

            }else if (value instanceof String){
                sv = value.toString();

            }else
                sv = value.toString();

        }
        public Object getAutoValue(){
            if (t != null){
               return t;
            }else if (v != null){
                return v;

            } else if (bv != null){
                return bv;

            }else if (sv != null){
                return sv;

            }


            return sv;
        }

    }

}
