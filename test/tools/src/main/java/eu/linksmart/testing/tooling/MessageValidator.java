package eu.linksmart.testing.tooling;




import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by José Ángel Carvajal on 15.11.2016 a researcher of Fraunhofer FIT.
 */
public class MessageValidator implements Runnable{

    protected Map<String, Boolean[]> completenessValidator = new ConcurrentHashMap<>();
    protected Map<String, Boolean> starts = new ConcurrentHashMap<>();
    protected Map<String,Integer> orderValidator = new ConcurrentHashMap<>();
    protected Map<String,Integer[]> orderRecorder = new ConcurrentHashMap<>();
    protected Date last;
    protected final long lotSize;
    protected boolean order =true, complete=true,start=false;
    protected Thread validationThread = null;
    final private String id , sourceClass;
    final private long DEFAULT_LOT_SIZE= 100000;

    public MessageValidator(Class baseClass, String id){
        sourceClass = baseClass.getCanonicalName();
        this.id = UUID.randomUUID().toString();
        this.lotSize =DEFAULT_LOT_SIZE;

        System.out.println("Validation started with lot size "+ lotSize + " for "+sourceClass+"("+ id +")");
    }
    public MessageValidator(Class baseClass, String id, long lotSize){
        sourceClass = baseClass.getCanonicalName();
        this.id = UUID.randomUUID().toString();
        this.lotSize =lotSize;
        System.out.println("Validation started with lot size "+ lotSize + " for "+sourceClass+"("+ id +")");
    }
    public MessageValidator(Class baseClass, long lotSize){
        sourceClass = baseClass.getCanonicalName();
        this.id = UUID.randomUUID().toString();
        this.lotSize =lotSize;
        System.out.println("Validation started with lot size "+ lotSize + " for "+sourceClass+"("+ id +")");
    }
    public MessageValidator(Class baseClass){
        sourceClass = baseClass.getCanonicalName();
        this.id = UUID.randomUUID().toString();
        this.lotSize =DEFAULT_LOT_SIZE;
        System.out.println("Validation started with lot size "+ lotSize + " for "+sourceClass+"("+ id +")");
    }

    public synchronized void addMessage(String topic, int i){
        try {

            if(validationThread==null) {
                validationThread = new Thread(this);
                validationThread.start();
                System.out.println(sourceClass + "(" + id + "): Validation Starts!");
            }
            if(!completenessValidator.containsKey(topic)) {
                Boolean[] booleans = new Boolean[(int)lotSize];
                Integer[] integers = new Integer[(int)lotSize];
                Arrays.fill(booleans,false);
                Arrays.fill(integers,0);
                completenessValidator.put(topic,booleans);
                orderRecorder.put(topic,integers);
                orderValidator.put(topic, 0);
                starts.put(topic,false);



            }
            if(!starts.get(topic)) {
                    starts.put(topic,i == -1);
                    return;
            }

            completenessValidator.get(topic)[i]= true;
            if(orderValidator.get(topic)<lotSize)
                orderRecorder.get(topic)[orderValidator.get(topic)] = i;
            else
                System.out.println("Anomaly on received value: "+i);

            last = new Date();


            order = order && (orderValidator.get(topic) <= i);
            //System.out.println("Validation of class " + sourceClass + "(" + id + "), order: fails");

            orderValidator.put(topic, orderValidator.get(topic)+1);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void validate(){
        complete = completenessValidator.values().stream().allMatch(v-> v != null && Arrays.stream(v).allMatch(b -> b!=null && b));
        for (String key : completenessValidator.keySet()){
           // System.out.print(sourceClass + "(" + id + ") Topic " + key + " failed the complete test at: " );
            String output = "";
            int n=0;

            for (int i =0; i<completenessValidator.get(key).length;i++) {
                if (!completenessValidator.get(key)[i]) {
                    output +=i+", ";
                    complete = complete && completenessValidator.get(key)[i];
                    n++;
                    //System.out.print(sourceClass + "(" + id + ") Topic " + key + " failed rate of " + ((double) n) / ((double) lotSize) * 100 + " % times; messages lost are: " + output);
                    //break;
                }
            }
            if(!complete)
                System.out.println(sourceClass + "(" + id + ") Topic " + key + " failed rate of " + ((double) n) / ((double) lotSize) * 100 + " % times; messages lost are: " +output);

        }
        if(!order) {
            System.out.print(sourceClass + "(" + id + ") Order test fail in: ");
            for (String k:orderRecorder.keySet()) {
                System.out.print(" topic " + k + " order: ");
                for (int i = 1; i < orderRecorder.get(k).length; i++) {
                    if(orderRecorder.get(k)[i-1] > orderRecorder.get(k)[i])
                        System.out.print("["+String.valueOf(i-1)+", "+i+"], ");
                }
            }
            System.out.println();
        }
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
        }while (last==null||(((double) before.getTime() - last.getTime() ) / (1000))<3);
        validate();
        System.out.println("Validation of class " + sourceClass+"("+ id + ") finished, order: " + order + " complete: " + complete);
    }
}
