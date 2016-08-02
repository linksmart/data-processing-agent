package eu.almanac.event.datafusion.handler.base;

import eu.almanac.event.datafusion.intern.Utils;
import eu.linksmart.api.event.datafusion.ComplexEventHandler;
import eu.linksmart.api.event.datafusion.Statement;
import org.slf4j.Logger;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by José Ángel Carvajal on 27.07.2016 a researcher of Fraunhofer FIT.
 */
public abstract class BaseArrayEventHandler implements ComplexEventHandler<Object[]> {
    protected   EventExecutor eventExecutor = new EventExecutor();
    protected Thread thread;
    protected Statement query;
    protected Logger loggerService = Utils.initLoggingConf(this.getClass());



    public BaseArrayEventHandler(Statement statement){

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

    public  void update(Object[] eventMap) {
        loggerService.info( Utils.getDateNowString() + " Simple update query: " + query.getName());
        initThread();

        if(eventMap instanceof Map[]) {
            Map[] aux = (Map[]) eventMap;
            Object[] objects = new Object[eventMap.length];
            int i=0;
            for (Map map: aux) {
                objects[i]= map.values().iterator().next();
                i++;
            }
            eventExecutor.stack(objects);
        }else
            eventExecutor.stack(eventMap);





    }
    private class EventExecutor implements Runnable{
        private final LinkedBlockingQueue<Object[]> queue = new LinkedBlockingQueue<>();
        private boolean active = true;

        synchronized void stack(Object[] eventMap){
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

    protected abstract void processMessage(Object[] events);

    public void update(Object[][] insertStream, Object[][] removeStream){
        loggerService.info( Utils.getDateNowString() + " Multi-update query: " + query.getName());
        if(insertStream!=null)
            for (Object[] m: insertStream)
                eventExecutor.stack(m);
        if(removeStream!=null)
            for (Object[] m: removeStream)
                eventExecutor.stack(m);
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
