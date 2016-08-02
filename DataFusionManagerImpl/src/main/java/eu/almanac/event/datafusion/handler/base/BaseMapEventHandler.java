package eu.almanac.event.datafusion.handler.base;

import eu.almanac.event.datafusion.intern.Utils;
import eu.linksmart.api.event.datafusion.ComplexEventHandler;
import eu.linksmart.api.event.datafusion.Statement;
import org.slf4j.Logger;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public abstract class BaseMapEventHandler  implements ComplexEventHandler<Map> {
    protected   EventExecutor eventExecutor = new EventExecutor();
    protected Thread thread;
    protected Statement query;



    public BaseMapEventHandler(Statement statement){

        query = statement;
        initThread();
    }

    private  void initThread(){

        if(thread == null)
            thread = new Thread(eventExecutor);

        if(!thread.isAlive()){
            thread = new Thread(eventExecutor);
            thread.start();
        }
    }

   public  void update(Map eventMap) {
        initThread();
        loggerService.info( Utils.getDateNowString() + " Simple update query: " + query.getName());
        eventExecutor.stack(eventMap);





    }
    private class EventExecutor implements Runnable{
        private final LinkedBlockingQueue<Map> queue = new LinkedBlockingQueue<>();
        private boolean active = true;

        synchronized void stack(Map eventMap){
            queue.add(eventMap);
            synchronized (queue) {
                queue.notifyAll();
            }
        }

        public synchronized void setActive(boolean value){
            active = value;
        }


        @Override
        public void run() {
            boolean active = true;
            synchronized (this) {
                active = this.active;
            }
            while (active) {

                try {
                    processMessage(queue.take());


                    synchronized (this) {
                        active = this.active;
                    }
                    if (queue.size() == 0)
                        synchronized (queue) {
                            queue.wait(500);
                        }

                } catch (InterruptedException e) {
                    loggerService.error(e.getMessage(), e);
                }
            }
        }
    }

    protected abstract void processMessage(Map events); protected Logger loggerService = Utils.initLoggingConf(this.getClass());

    @Override
    public void update(Map[] insertStream, Map[] removeStream){
        loggerService.info( Utils.getDateNowString() + " Multi-update query: " + query.getName());
        if(insertStream!=null)
            for (Map m: insertStream)
                eventExecutor.stack(m);
        if(removeStream!=null)
            for (Map m: removeStream)
                eventExecutor.stack(m);
    }
    public void update(Object[][] insertStream, Object[][] removeStream){
        loggerService.info( Utils.getDateNowString() + " Last resort Multi-update query: " + query.getName());
        Map aux = new HashMap<>(),aux2 = new HashMap<>();
        if(insertStream!=null) {
            int i=0;
            for (Object o : insertStream)
                aux.put(String.valueOf(i),o);

        }
        if(removeStream!=null) {
            int i=0;
            for (Object o : insertStream)
                aux2.put(String.valueOf(i), o);
        }

        update(new Map[]{aux},new Map[]{aux});

    }
    @Override
    public void destroy() {

    }
    private Type[] genericClass() {
        ParameterizedType parameterizedType = (ParameterizedType)getClass()
                .getGenericSuperclass();
        return parameterizedType.getActualTypeArguments();
    }
}
