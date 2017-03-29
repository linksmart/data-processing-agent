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

    static public Event factory(Object bn, Object bt, Object bu,Object ver, Object n, Object u, Object v, Object sv, Object bv, Object s, Object t, Object ut){
        Event event = new Event();

        if(bn!=null && bn instanceof String)
            event.setBn((String)bn);

        if(bt!=null && bt instanceof Long)
            event.setBt((Long) bt);

        if(bu!=null && bu instanceof String)
            event.setBu((String)bu);

        if(ver!=null && ver instanceof Short)
            event.setVer((Short) ver);

        if(n!=null || v!=null || sv!=null|| bv!=null|| s!=null|| t!=null|| ut!=null){
            Measurement m = new Measurement();
            if(n!=null && n instanceof String)
                m.setN((String) n);
            if(u!=null && u instanceof String)
                m.setU((String) u);
            if(v!=null && v instanceof Number)
                m.setV((Number) v);
            if(sv!=null && sv instanceof String)
                m.setSv((String) sv);
            if(bv!=null && bv instanceof Boolean)
                m.setBv((Boolean) bv);
            if(s!=null && s instanceof Number)
                m.setS((Double) s);
            if(t!=null && t instanceof Long)
                m.setT((Long) t);
            if(ut!=null && ut instanceof Long)
                m.setUt((Long) ut);
            event.setE(m);
        }

        return event;


    }
    static public Event factory(Object bn, Object n, Object autoValue){

        Object v=null,sv=null,bv=null;
        if(autoValue!=null && autoValue instanceof Number)
            v = (Double)autoValue;
        if(autoValue!=null && autoValue instanceof String)
            sv = (String)autoValue;
        if(autoValue!=null && autoValue instanceof Boolean)
            bv = (Boolean)autoValue;
        return factory(bn,n,0L,(short)1,n,null,v,sv,bv,null,(new Date()).getTime(),null);
    }
    static public Event factory(Object bn, Object n, Object autoValue,Object ut){

        Object v=null,sv=null,bv=null;
        if(autoValue!=null && autoValue instanceof Number)
            v = (Double)autoValue;
        if(autoValue!=null && autoValue instanceof String)
            sv = (String)autoValue;
        if(autoValue!=null && autoValue instanceof Boolean)
            bv = (Boolean)autoValue;
        return factory(bn,n,0L,(short)1,n,null,v,sv,bv,null,(new Date()).getTime(),ut);
    }
    public  Event(){
        super();
        index = null;
        eNameChanged = false;
        bu = null;
        ver = null;
        value = new Vector<>();
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
    @JsonIgnore
    public void setE(Vector<Measurement> e) {
        this.value = e;
    }
    @JsonProperty("e")
    public void setE(Measurement[] e) {
        this.value = new Vector<Measurement>(Arrays.asList(e));
    }
    @JsonProperty("e")
    public Measurement getE(int i) {
        return value.get(i);
    }
    @JsonIgnore
    public void setE(Measurement e) {
        this.value.add(e);
    }

    //======================================================
    @JsonIgnore
    public String getBaseName() {
        return id;
    }
    @JsonIgnore
    public Long getBaseTime() {
        return date.getTime();
    }
    @JsonIgnore
    public String getBaseUnits() {
        return bu;
    }
    @JsonIgnore
    public Short getVersion() {
        return ver;
    }
    @JsonIgnore
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
    @JsonIgnore
    public void setBaseName(String baseName) {
        setBn(baseName);
    }
    @JsonIgnore
    public void setBaseTime(Long baseTime) {
        setBt(baseTime);
    }
    @JsonIgnore
    public void setBaseUnits(String baseUnits) {
        setBu(baseUnits);
    }
    @JsonIgnore
    public void setVersion(Short version) {
        this.ver = version;
    }
    @JsonIgnore
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
    public static class Measurement  extends eu.linksmart.services.payloads.generic.Event<String,Number>  implements EventEnvelope<String,Number>{
        private static final long serialVersionUID = -3921246268323727465L;
        @JsonProperty("u")
        private String u;
        @JsonProperty("sv")
        private String sv;
        @JsonProperty("bv")
        private Boolean bv;
        @JsonProperty("s")
        private Double s;
        @JsonProperty("ut")
        private Long ut;


        public  Measurement(){
            super();
        }

        @JsonProperty("n")
        public String getN() {
            return attributeId;
        }
        @JsonProperty("n")
        public void setN(String n) {

                this.attributeId = n;

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
        public void setV(Number v) {
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
            this.date = new Date(t);
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
        @JsonIgnore
        public String getName() {
            return attributeId;
        }
        @JsonIgnore
        public void setName(String n) {
           setN(n);
        }

        @JsonIgnore
        public String getUnit() {
            return u;
        }

        @JsonIgnore
        public void setUnit(String u) {
            this.u = u;
        }



        @JsonIgnore
        public String getStringValue() {
            return sv;
        }

        @JsonIgnore
        public void setStringValue(String sv) {
            this.sv = sv;
        }

        @JsonIgnore
        public Boolean getBooleanValue() {
            return bv;
        }

        @JsonIgnore
        public void setBooleanValue(Boolean bv) {
            this.bv = bv;
        }

        @JsonIgnore
        public Double getSum() {
            return s;
        }

        @JsonIgnore
        public void setSum(Double s) {
            this.s = s;
        }

        @JsonIgnore
        public Long getTime() {
            return getT();
        }

        @JsonIgnore
        public void setTime(Long t) {
            setT(t);
        }

        @JsonIgnore
        public Long getUpdateTime() {
            return ut;
        }

        @JsonIgnore
        public void setUpdateTime(Long ut) {
            this.ut = ut;
        }

        @JsonIgnore
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

        @JsonIgnore
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
