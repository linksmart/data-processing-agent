package eu.linksmart.testing.tooling;



import org.apache.commons.lang3.time.DateUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by José Ángel Carvajal on 15.11.2016 a researcher of Fraunhofer FIT.
 */
public class MQTTMessageValidator implements Runnable{

    protected Map<String, Boolean[]> completenessValidator = new ConcurrentHashMap<>();
    protected Map<String,Integer> orderValidator = new ConcurrentHashMap<>();
    protected Map<String,Integer[]> orderRecorder = new ConcurrentHashMap<>();
    protected Date last;
    protected final long lotSize;
    protected boolean order =true, complete=true;
    protected Thread validationThread = null;
    final private String id , sourceClass;
    final private long DEFAULT_LOT_SIZE= 100000;

    public MQTTMessageValidator(Class baseClass, String id){
        sourceClass = baseClass.getCanonicalName();
        this.id = UUID.randomUUID().toString();
        this.lotSize =DEFAULT_LOT_SIZE;

        System.out.println("Validation started with lot size "+ lotSize + " for "+sourceClass+"("+ id +")");
    }
    public MQTTMessageValidator(Class baseClass, String id, long lotSize){
        sourceClass = baseClass.getCanonicalName();
        this.id = UUID.randomUUID().toString();
        this.lotSize =lotSize;
        System.out.println("Validation started with lot size "+ lotSize + " for "+sourceClass+"("+ id +")");
    }
    public MQTTMessageValidator(Class baseClass, long lotSize){
        sourceClass = baseClass.getCanonicalName();
        this.id = UUID.randomUUID().toString();
        this.lotSize =lotSize;
        System.out.println("Validation started with lot size "+ lotSize + " for "+sourceClass+"("+ id +")");
    }
    public MQTTMessageValidator(Class baseClass){
        sourceClass = baseClass.getCanonicalName();
        this.id = UUID.randomUUID().toString();
        this.lotSize =DEFAULT_LOT_SIZE;
        System.out.println("Validation started with lot size "+ lotSize + " for "+sourceClass+"("+ id +")");
    }

    public synchronized void addMessage(String topic, int i){
        if(validationThread==null) {
            validationThread = new Thread(this);
            validationThread.start();
        }
        if(!completenessValidator.containsKey(topic)) {
            Boolean[] booleans = new Boolean[(int)lotSize];
            Integer[] integers = new Integer[(int)lotSize];
            Arrays.fill(booleans,false);
            Arrays.fill(integers,0);
            completenessValidator.put(topic,booleans);
            orderRecorder.put(topic,integers);
            orderValidator.put(topic, 0);


        }
        completenessValidator.get(topic)[i]= true;
        orderRecorder.get(topic)[orderValidator.get(topic)] = i;

        last = new Date();

        order = order && orderValidator.get(topic) == i;

        orderValidator.put(topic, orderValidator.get(topic)+1);

    }

    protected void validate(){
        complete = completenessValidator.values().stream().allMatch(v-> v != null && Arrays.stream(v).allMatch(b -> b!=null && b));
        for (String key : completenessValidator.keySet()){
            for (int i =0; i<completenessValidator.get(key).length;i++) {
                if (!completenessValidator.get(key)[i]) {
                    System.out.println(sourceClass + "(" + id + "): Topic " + key + " failed the complete test at " + i);
                    complete = complete && completenessValidator.get(key)[i];
                    break;
                }
            }


        }
      //  if(!order)
      //      orderRecorder.forEach((k,v)->System.out.println("Validation of class " + sourceClass+"("+ id + "): order of topic: "+k+" order: "+Arrays.stream(v).map(String::valueOf).collect(Collectors.joining(","))));


    }

    @Override
    public void run() {
        Date before= new Date();

        do{
            try {
                before= new Date();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }while ((((double) before.getTime() - last.getTime() ) / (DateUtils.MILLIS_PER_SECOND))<10);
        validate();
        System.out.println("Validation of class " + sourceClass+"("+ id + ") finished, order: " + order + " complete: " + complete);
    }
}
