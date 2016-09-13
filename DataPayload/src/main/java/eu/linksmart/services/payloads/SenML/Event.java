package eu.linksmart.services.payloads.SenML;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.EventEnvelope;

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
public class Event extends eu.linksmart.services.payloads.generic.Event<String,Vector<Event.Measurement>> implements EventEnvelope<String,Vector<Event.Measurement>>{
    private static final long serialVersionUID = -1510508593277110230L;
   // @JsonProperty("bn")
    //private String bn;
    //@JsonProperty("bt")




    //private Long bt;
    @JsonProperty("bu")
    private String bu;
    @JsonProperty("ver")
    private Short ver;
   // @JsonProperty("e")
   // private Vector<Measurement> e;
    @JsonIgnore
    private transient Boolean eNameChanged;
    @JsonIgnore
    private transient Map<String,Integer> index;
    @JsonIgnore
    final private transient Object lock = new Object();

    public  Event(){
        super();
        index = null;
        eNameChanged = false;
        bu = null;
        ver = null;
    }


    @JsonProperty("bn")
    public String getBn() {
        return id;
    }
    @JsonProperty("bn")
    public void setBn(String bn) {
        this.id = bn;
    }
    @JsonProperty("bt")
    public Long getBt() {
        return date.getTime();
    }
    @JsonProperty("bt")
    public void setBt(Long bt) {
        date = new Date(bt);
    }
    @JsonProperty("bu")
    public String getBu() {
        return bu;
    }
    @JsonProperty("bu")
    public void setBu(String bu) {
        this.bu = bu;
    }

    @JsonProperty("ver")
    public Short getVer() {
        return ver;
    }
    @JsonProperty("ver")
    public void setVer(Short ver) {
        this.ver = ver;
    }
    @JsonProperty("e")
    public Vector<Measurement> getE() {
        return value;
    }
    @JsonProperty("e")
    public void setE(Vector<Measurement> e) {
        this.value = e;
    }
    @JsonProperty("bn")
    public String getBaseName() {
        return id;
    }
    @JsonProperty("bt")
    public Long getBaseTime() {
        return date.getTime();
    }
    @JsonProperty("bu")
    public String getBaseUnits() {
        return bu;
    }
    @JsonProperty("ver")
    public Short getVersion() {
        return ver;
    }
    @JsonProperty("e")
    public Vector<Measurement> getElements() {
        return value;
    }

    @JsonIgnore
    public void addValue(String key, Object value) {
        addProperty(key).setAutoValue(value);

    }

    @JsonIgnore
    public Object getValue(String key) {
        return getEbyName(key);
    }

    @JsonProperty("bn")
    public void setBaseName(String baseName) {
        setBn(baseName);
    }
    @JsonProperty("bt")
    public void setBaseTime(Long baseTime) {
        setBt(baseTime);
    }
    @JsonProperty("bu")
    public void setBaseUnits(String baseUnits) {
        setBu(baseUnits);
    }
    @JsonProperty("ver")
    public void setVersion(Short version) {
        this.ver = version;
    }
    @JsonProperty("e")
    public void setElements(Vector<Measurement> elements) {
        setE(elements);
    }
    @JsonIgnore
    public Measurement addProperty(String name){
        value.add(new Measurement());
        value.lastElement().setName(name);
        return value.lastElement();
    }
    @JsonIgnore
    public Measurement getEbyName(String name){

        indexing();

        try {

            return value.get(index.get(name));
        }catch (Exception e){}
        return null;
    }
    @JsonIgnore
    private  void  indexing(){
        synchronized(lock) {
            if (index.size() != value.size() || eNameChanged)
                for (int i = 0; i < value.size(); i++) {
                    index.put(value.get(i).getName(), i);
                }
            eNameChanged = false;
        }

    }
    @JsonIgnore
    @Override
    public Event build() throws TraceableException, UntraceableException {
         indexing();
        return this;
    }
    @JsonIgnore
    @Override
    public void destroy() throws Exception {

    }
    @JsonIgnore
    public Measurement getFirst(){
        return value.firstElement();
    }

    @JsonIgnore
    public Measurement getLast(){
        return value.lastElement();
    }

    /**
     * Created by Carvajal on 13.02.2015.
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
    public class Measurement  extends eu.linksmart.services.payloads.generic.Event<String,Number>  implements EventEnvelope<String,Number>{
        private static final long serialVersionUID = -3921246268323727465L;

        private String u;
        private String sv;
        private Boolean bv;
        private Double s;
        private Long ut;


        public  Measurement(){
            super();
            id = Event.this.id;
        }

        @JsonProperty("n")
        public String getN() {
            return attributeId;
        }
        @JsonProperty("n")
        public void setN(String n) {
            synchronized(lock) {
                this.attributeId = n;
                eNameChanged = true;
            }
        }
        @JsonProperty("u")
        public String getU() {
            return u;
        }
        @JsonProperty("u")
        public void setU(String u) {
            this.u = u;
        }

        @JsonProperty("v")
        public Number getV() {
            return value;
        }

        @JsonProperty("v")
        public void setV(Double v) {
            this.value = v;
        }

        @JsonProperty("sv")
        public String getSv() {
            return sv;
        }

        @JsonProperty("sv")
        public void setSv(String sv) {
            this.sv = sv;
        }

        @JsonProperty("bv")
        public Boolean getBv() {
            return bv;
        }

        @JsonProperty("bv")
        public void setBv(Boolean bv) {
            this.bv = bv;
        }

        @JsonProperty("s")
        public Double getS() {
            return s;
        }

        @JsonProperty("s")
        public void setS(Double s) {
            this.s = s;
        }

        @JsonProperty("t")
        public Long getT() {
            return date.getTime();
        }

        @JsonProperty("t")
        public void setT(Long t) {
            this.date = new Date(Event.this.date.getTime()+t);
        }

        @JsonProperty("ut")
        public Long getUt() {
            return ut;
        }

        @JsonProperty("ut")
        public void setUt(Long ut) {
            this.ut = ut;
        }

        //============================================
        public String getName() {
            return attributeId;
        }

        public void setName(String n) {
            synchronized(lock) {
                this.attributeId = n;
                eNameChanged = true;
            }
        }

        public String getUnit() {
            return u;
        }

        public void setUnit(String u) {
            this.u = u;
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
            return getT();
        }

        public void setTime(Long t) {
            setT(t);
        }

        public Long getUpdateTime() {
            return ut;
        }

        public void setUpdateTime(Long ut) {
            this.ut = ut;
        }
        public void setAutoValue(Object value){
             if (value instanceof Long){
                setT((Long)value);
            }else if (value instanceof Number){
                this.value = (Number)value;

            } else if (value instanceof Boolean){
                bv = (Boolean)value;

            }else if (value instanceof String){
                sv = value.toString();

            }else
                sv = value.toString();

        }
        public Object getAutoValue(){
            if (value != null){
                return value;

            } else if (bv != null){
                return bv;

            }else if (sv != null){
                return sv;

            } else if (date != null){
                return date.getTime();
            }


            return null;
        }

        @Override
        public Measurement build() throws TraceableException, UntraceableException {
            return this;
        }

        @Override
        public void destroy() throws Exception {
            /// nothing
        }
    }

}
