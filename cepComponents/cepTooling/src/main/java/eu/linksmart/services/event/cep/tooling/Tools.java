package eu.linksmart.services.event.cep.tooling;


import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.EventBuilder;
import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.services.payloads.ogc.sensorthing.Datastream;
import eu.linksmart.services.payloads.ogc.sensorthing.OGCEventBuilder;
import eu.linksmart.services.payloads.ogc.sensorthing.Observation;
import eu.linksmart.services.payloads.ogc.sensorthing.Sensor;
import eu.linksmart.services.payloads.ogc.sensorthing.linked.DatastreamImpl;
import eu.linksmart.services.payloads.ogc.sensorthing.linked.ObservationImpl;
import eu.linksmart.services.payloads.ogc.sensorthing.linked.SensorImpl;
import eu.linksmart.services.utils.function.Utils;
import io.swagger.models.auth.In;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.util.Assert;
//import eu.almanac.ogc.sensorthing.api.datamodel.*;
//import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation;
//import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Sensor;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Tools {
    static private Map<String, Object> variables= new HashMap<>();
    static public Random Random = new Random();

    static public long getMilliseconds(Date date){
        return date.getTime();
    }


    static public String generateRandomAbout(){

        return UUID.randomUUID().toString().replace("-","_").replace("#","_");
    }



    static public String getIsoTimeFormat(){
        return Utils.isoFormatMSTZ.toString();
    }
    static public String getEsperTimeFormat(){
        return Utils.getDateFormat().toString();
    }
    static private DateFormat dateFormat =null;

    static private DateFormat getDateFormat(String timeFormat, String gmt){

        if(dateFormat == null){
            TimeZone tz = TimeZone.getTimeZone(gmt);
            dateFormat  = new SimpleDateFormat(getIsoTimeFormat());
            dateFormat.setTimeZone(tz);
        }
        if (!dateFormat.getTimeZone().getID().equals(gmt)){
            TimeZone tz = TimeZone.getTimeZone(gmt);
            dateFormat.setTimeZone(tz);
        }


        return dateFormat;

    }
    static public EventEnvelope randomEvent(String thingId, String streamID,int min, int max){
        try {
            return EventBuilder.getBuilder().factory(thingId, streamID,  ThreadLocalRandom.current().nextInt(min, max + 1),(new Date()).getTime(),null, new Hashtable<>());
        } catch (UntraceableException e) {
            return null;
        }
    }

    static public String getDateNowString(){
        return getDateFormat(getIsoTimeFormat(), "UTC").format(new Date());
    }

    //give current timestamp in a format that GOST server understands
    static public String getDateNowWithoutTimeZone() {
        return Utils.isoFormatMSWTZ.format(Date.from(java.time.Instant.now()));
    }

    static private String toTimestamp(Date date){

        return getDateFormat(getEsperTimeFormat(), "UTC").format(date);

    }
    static public String hashIt(String string){
        MessageDigest SHA256 = null;
        try {
            SHA256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return (new BigInteger(1,SHA256.digest((string).getBytes()))).toString();
    }


    static private Map<String, Map> used = new Hashtable<String,Map>();
    static public boolean hadBeanUsed(String queryName, Object[] objects){
        boolean hadBeanUsed ;
        if(!used.containsKey(queryName))
            used.put(queryName,null);
        if(used.get(queryName)==null) {
            used.put(queryName,new Hashtable());
            for (Object object: objects)
                used.get(queryName).put(object,object);

            hadBeanUsed = false;
        }else {
            hadBeanUsed = false;
            for (int i = objects.length-1; i >0 && !hadBeanUsed; i--)
                hadBeanUsed = used.get(queryName).containsKey(objects[i]);

        }

        return hadBeanUsed;
    }
    static public boolean isTimeContinuous(EventEnvelope[] events){
        EventEnvelope previous= null;

        for (EventEnvelope event: Arrays.asList(events)) {

            if (previous!=null && !DateUtils.addHours(previous.getDate(),2 ).after(event.getDate()))
                return false;

            previous = event;

        }
        return true;
    }
    static public boolean isTimeContinuous(EventEnvelope[][] eventss){
        EventEnvelope previous= null;

        for(EventEnvelope[] events: Arrays.asList(eventss))
            if(!isTimeContinuous(events))
                return false;
        return true;
    }
    static public boolean isTimeContinuous(EventEnvelope[] events1,EventEnvelope[] events2,boolean inBetweenOnly){

        return (inBetweenOnly || (isTimeContinuous(events1) && isTimeContinuous(events2)) )&&
                (DateUtils.addHours(  events1[events1.length-1].getDate(),2 ).after(  events2[0].getDate()) &&
                        events1[events1.length-1].getDate().before(events2[0].getDate())
                );


    }
    static public boolean isTimeContinuous(EventEnvelope[] events1,EventEnvelope[] events2){

        return isTimeContinuous(events1,events2,false);


    }
    static public boolean isTimeContinuous(Object events1,Object events2) {
        return events1 instanceof EventEnvelope[] && events2 instanceof EventEnvelope[] && isTimeContinuous((EventEnvelope[]) events1, (EventEnvelope[]) events2);
    }
    static public boolean isTimeContinuous(Object events1,Object events2, Object inBetweenOnly) {
        return events1 instanceof EventEnvelope[] && events2 instanceof EventEnvelope[] && inBetweenOnly instanceof Boolean && isTimeContinuous((EventEnvelope[]) events1, (EventEnvelope[]) events2,(boolean)inBetweenOnly);
    }
    static public EventEnvelope[] flattArrays(EventEnvelope[][] eventss){


        EventEnvelope[] result  =null;
        if(eventss!=null && eventss.length>0) {
            if (eventss.length > 1) {
                for (int i = 1; i < eventss.length; i++)
                    result =  ArrayUtils.addAll(result, eventss[i]);
            }
            else result = eventss[0];
        }

            return result;
    }

    static public List addAll(Object o, Object o2) {
        List ret = new ArrayList();
        if (Object[].class.isAssignableFrom(o.getClass())) {
            return addAll(Arrays.stream((Object[]) o).collect(Collectors.toCollection(ArrayList::new)), o2);
        } else if (Object[].class.isAssignableFrom(o2.getClass())) {
            return addAll(o, Arrays.stream((Object[]) o2).collect(Collectors.toCollection(ArrayList::new)));
        }
        if (!List.class.isAssignableFrom(o.getClass()) && Collection.class.isAssignableFrom(o.getClass())) {
            return addAll(new ArrayList((Collection) o), o2);

        } else if (!List.class.isAssignableFrom(o2.getClass()) && Collection.class.isAssignableFrom(o2.getClass())) {
            return addAll(o, new ArrayList((Collection) o2));

        } else if (!List.class.isAssignableFrom(o.getClass())) {
            ret.add(o);
            return addAll(ret, o2);

        } else if (List.class.isAssignableFrom(o.getClass()) && List.class.isAssignableFrom(o2.getClass())) {
            ret.addAll(((List) o));
            ret.addAll(((List) o2));

        } else if (List.class.isAssignableFrom(o.getClass()) && !List.class.isAssignableFrom(o2.getClass())) {
            ret.addAll(((List) o));
            ret.add(o2);

        }else {
            ret.add(o);
            ret.add(o2);
        }
        return ret;
    }
    static public List flatArrayOfListToList(List[] array){
        List ret = new ArrayList();
        for(List subList: array)
            ret.addAll(subList);

        return ret;
    }
    static public List ifNullReplaceList(Object original, int size){
        if(original==null) {


            return new ArrayList<Integer>(Collections.nCopies(size, 0));
        }
        return new ArrayList((Collection) original);

    }
    static public List addAll(List o, Object o2) {
        return addAll((Object) o,(Object)o2);
    }
    static public List addAll(List o, List o2) {
        return addAll((Object) o,(Object)o2);
    }

    static public List addAll(Object o, List o2) {
        return addAll((Object) o,(Object)o2);
    }
    static public Object[] removeAll(Object o, Object o2){
        List<Object> list=null, list2=null, result;
        if(o instanceof Object[]){
             list= Arrays.asList((Object[])o);
        }else if(o instanceof List){
            list= (List<Object>)o;
        }

        if(o2 instanceof Object[]){
            list2= Arrays.asList((Object[])o2);
        }else if(o2 instanceof List){
            list2= (List<Object>)o2;
        }

        if(list!= null && list2!=null){
            list.removeAll(list2);
            return list.toArray();
        }else if(list!=null){
            list.remove(o2);
            return list.toArray();
        }else if(list2!=null){
            list = new LinkedList<>();
            list.add(o);
            list.removeAll(list2);
            return list.toArray();
        }

        return new Object[0];
    }
    public static Object[] sortArray(Object array, Object order, Object reorder){
        if(array==null || order==null)
            return new Object[0];


        final int[] newOrder = sortArrayLike(order,reorder);
        final  Object[] ret = new Object[newOrder.length];

        if(array instanceof Object[] ){
            final Object[] toOrder =(Object[])array;
            Arrays.stream(newOrder).forEach(i->ret[i] = toOrder[newOrder[i]] );

        } else if(array instanceof List){
            final List toOrder =(List)array;
            Object[] oldOrder = (Object[]) order;
            Arrays.stream(newOrder).forEach(i->ret[i] = toOrder.get(newOrder[i]));

        }
        return ret;
    }
    public static int[] sortArrayLike(Object order, Object reorder){
        int[] sorted = null;
        if(reorder instanceof Object[] && order instanceof Object[]){
            Object[] toOrder =(Object[])reorder, newOrder = (Object[]) order;
            sorted = new int[toOrder.length];

            for(int i=0;i<sorted.length;i++){
                for(int j=0;j<sorted.length;j++){
                    if(toOrder[i].equals(newOrder[j])){
                        sorted[j] = i;
                        break;
                    }

                }
            }

        } else if((reorder instanceof List) && order instanceof Object[]){
            List toOrder =(List)reorder;
            Object[] newOrder = (Object[]) order;
            sorted = new int[toOrder.size()];

            for(int i=0;i<toOrder.size();i++){
                for(int j=0;j<toOrder.size();j++){
                    if(toOrder.get(i).equals(newOrder[j])){
                        sorted[j] = i;
                        break;
                    }

                }
            }
        }else if(reorder instanceof Object[] && order instanceof List){
            Object[] toOrder =(Object[])reorder;
            List newOrder = (List) order;
            sorted = new int[toOrder.length];

            for(int i=0;i<toOrder.length;i++){
                for(int j=0;j<toOrder.length;j++){
                    if(toOrder[i].equals(newOrder.get(j))){
                        sorted[j] = i;
                        break;
                    }

                }
            }
        }else if(reorder instanceof List && order instanceof List){
            List toOrder =(List)reorder, newOrder = (List) order;
            sorted = new int[toOrder.size()];

            for(int i=0;i<sorted.length;i++){
                for(int j=0;j<sorted.length;j++){
                    if(toOrder.get(i).equals(newOrder.get(j))){
                        sorted[j] = i;
                        break;
                    }

                }
            }
        }

        return sorted;

    }
    public static Map toMap(Object keys, Object values ){
        final Map map =new HashMap();
        if(keys instanceof Object[] && values instanceof Object[]){
            final Object[] keyss =(Object[])keys, valuess = (Object[]) values;
            for(int i=0;i<keyss.length;i++)
                map.put(keyss[i],valuess[i]);


        } else if((keys instanceof List) && values instanceof Object[]){
            List keyss =(List)keys;
            Object[] valuess = (Object[]) values;
            for(int i=0;i<keyss.size();i++)
                map.put(keyss.get(i),valuess[i]);

        }else if(keys instanceof Object[] && values instanceof List){
            Object[] keyss =(Object[])keys;
            List valuess = (List) values;
            for(int i=0;i<keyss.length;i++)
                map.put(keyss[i], valuess.get(i));

        }else if(keys instanceof List && values instanceof List){
            List keyss =(List)keys, valuess = (List) values;
            for(int i=0;i<keyss.size();i++)
                map.put(keyss.get(i), valuess.get(i));

        }
        return map;
    }
    Map applyTo(Object values ){
        final Map map =new HashMap();
        if( values instanceof Object[]){
            final Object[]  valuess = (Object[]) values;
            for(int i=0;i<valuess.length;i++)
                map.put("D"+String.valueOf(i),valuess[i]);


        }else if( values instanceof List){

            List valuess = (List) values;
            for(int i=0;i<valuess.size();i++)
                map.put("D"+String.valueOf(i), valuess.get(i));

        }
        return map;
    }

    static public Observation[] gapFillUp(Observation[] observations, int finalSize){
        if(finalSize<=observations.length)
            return observations;

        Observation[] ret = new Observation[finalSize];
        int pointer =0;

        for (int i=0; i < observations.length-1;i++){
            int diff=0;
            if(( diff = (Integer.valueOf(observations[i+1].getDatastream().getId().toString().split("-")[1]) - Integer.valueOf(observations[i].getDatastream().getId().toString().split("-")[1]) ) )> 1) {
                for (int j = 0; j < diff; j++) {
                    if (pointer + j > ret.length)
                        return ret;
                    else {
                        Sensor sensor = new SensorImpl();
                        sensor.setId(observations[i].getDatastream().getSensor().getId().toString().split("-")[0]+"-"+observations[i].getDatastream().getSensor().getId().toString().split("-")[1]+j);
                        Datastream datastream = new DatastreamImpl();
                        datastream.setId(observations[i].getDatastream().getId().toString().split("-")[0]+"-"+observations[i].getDatastream().getId().toString().split("-")[1]+j);
                        Observation observation = new ObservationImpl();
                        observation.setDate(observations[i].getDate());
                        observation.setResult(0);
                        observations[pointer] = observation;
                        pointer++;
                    }
                }


            }

            ret[pointer] = observations[i];
            pointer++;

        }

        ret[pointer] = observations[observations.length-1];

        return ret;
    }
    static public ArrayList<Observation> fillUp(ArrayList<Observation> observations, int startIndex , int finalSize){

        try {

            if(finalSize<=observations.size()  || observations.size() < 1)
                return observations;
            ArrayList<Observation>  ret = new ArrayList<Observation>(finalSize);
            String idDSBase = observations.get(0).getDatastream().getId().toString().split("-")[0], idSBase = idDSBase.replace("ds_", "");

            int pointer = 0;

            for(int i=0; i<finalSize;i++) {

                if(pointer < observations.size() && observations.get(pointer).getDatastream().getId().equals(idDSBase+"-"+(startIndex+i))){
                    if(ret.size()>=startIndex+i)
                        ret.set(i, observations.get(pointer));
                    else
                            ret.add(observations.get(pointer));
                    pointer++;
                } else {

                    ret.add((Observation) EventBuilder.getBuilder(Observation.class).factory(idSBase + "-" + (startIndex + i),idDSBase + "-" + (startIndex + i),0,observations.get(0).getDate(),"",new Hashtable<>()));

                }
                Assert.isTrue(i!=ret.size(), "Mismatch adding values in the list");
            }
            if(ret.size()!=finalSize)
                System.err.println("The final list expected "+ret.size()+" is "+finalSize);

            return ret;
        }catch (Exception e){

            e.printStackTrace();

            return observations;
        }
    }


    static public Collection<Observation> singleFillUp(Observation observation, int startIndex , int finalSize){
        ArrayList<Observation> ret = new ArrayList();
        ret.add(observation);
        return fillUp(ret,startIndex,finalSize);
    }
    static public Collection<Observation> multiFillUp(Object[] observations, int startIndex , int finalSize){
        ArrayList<Observation> ret = new ArrayList(Arrays.asList(observations));

        return fillUp(ret,startIndex,finalSize);
    }
    static public Collection<Observation> multiFillUp(ArrayList observations, int startIndex , int finalSize){

        return fillUp(observations,startIndex,finalSize);
    }
    static public Collection<Observation> multiFillUp(Collection observations, int startIndex , int finalSize){
        ArrayList<Observation> ret = new ArrayList(observations);

        return fillUp(ret,startIndex,finalSize);
    }
    static public Object[] mapToArray(Map map, List<String> order){
        Object[] re = new Object[order.size()];
        int i=0;
        for(String key: order ){
            re[i]=map.get(key);
            i++;
        }

        return re;
    }
    static private int sort(Map<Object,Integer> map, Object values ){


        if(values instanceof ObservationImpl){
            return map.get(((ObservationImpl) values).getDatastream().getId().toString());
        } else if(values instanceof String){
            return map.get(values);
        }
        return -1;
    }
    static public int sortBy(Object values, Object order ){
        Map<Object,Integer> map = new HashMap<>();
        Stream stream;
        if(order instanceof Collection){
            stream= ((Collection) order).stream();
        }else if (order instanceof Object[]){
            stream = Arrays.stream((Object[]) order);
        }else
            return -1;

        stream.forEach(i->map.put(i,map.size()));

        return sort(map,values);
    }
    static public Collection<Observation> emptyListOf(Object value,String StreamBase, String SensorBase, int startIndex , int finalSize){
        ArrayList<Observation> ret = new ArrayList<>();
        for(int i=0; i<finalSize;i++){
            try {
                ret.add((Observation) EventBuilder.getBuilder(Observation.class).factory(SensorBase + "-" + (startIndex + i), StreamBase + "-" + (startIndex + i),value,(new Date()),"",new Hashtable<>()));
            } catch (UntraceableException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }
    static private Date lastKnownTime = null;
    static private Set<String> sent = new HashSet<>();
    static synchronized public Collection<Observation> timeSegmentation(ObservationImpl[] observations) {

        if (observations.length != 0) {
            Observation first = observations[0];
            if (lastKnownTime == null)
                lastKnownTime = first.getPhenomenonTime();
            else if (lastKnownTime.before(first.getPhenomenonTime())) {
                sent = new HashSet<>();
            }
            ArrayList<Observation> ret = new ArrayList<>();
            ret.add(first);

            for (Observation i : observations)
                if (i != first)
                    if (i.getPhenomenonTime().equals(first.getPhenomenonTime())) {
                        return ret;
                    } else if (!sent.contains(i.getId().toString())) {
                        ret.add(i);
                        sent.add(i.getId().toString());
                    }

            return ret;
        } else
            return null;
    }
    static public Object test(Object observations){
        if(observations instanceof Collection && ((Collection)observations).size()!= 196)
            System.out.println("");
        else if (observations instanceof Object[] && ((Collection)observations).size()!= 196)
            System.out.println("");
        return observations;
    }

    static public boolean validate(Collection observations) {
        int j = 0;
        int batchNo = -1;
        boolean collections =false;
        for (Object i : observations) {
            if (i instanceof Collection) {
                validate((Collection) i);
                collections = true;
            }else if (i instanceof Integer) {
                if (!i.equals(j))
                   return false;
            } else if (i instanceof Double) {
                String[] vals = i.toString().split(".");
                if (vals.length == 2) {
                    if (batchNo == -1)
                        batchNo = Integer.valueOf(vals[1]);
                    if (!Integer.valueOf(vals[1]).equals(batchNo) && !Integer.valueOf(vals[0]).equals(j))
                        return false;
                }

            } else
                return false;
            if(batchNo!=-1 && j>240){
                batchNo++;
            }
            if(j>240)
                j=0;
            else
                j++;
        }

        return true;
    }
}