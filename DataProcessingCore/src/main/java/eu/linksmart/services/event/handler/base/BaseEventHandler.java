package eu.linksmart.services.event.handler.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.linksmart.api.event.components.ComplexEventHandler;
import eu.linksmart.api.event.types.Statement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public abstract class BaseEventHandler implements ComplexEventHandler {
    protected   EventExecutor eventExecutor = new EventExecutor();
    protected Thread thread;
    protected Statement query;


    protected Logger loggerService = LogManager.getLogger(this.getClass());


    public BaseEventHandler(Statement statement){

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

    protected abstract void processMessage(Object events);
    protected abstract void processLeavingMessage(Object events);
/*
   public  void update(Map eventMap) {
        initThread();
        loggerService.debug(Utils.getDateNowString() + " update map[] w/ handler " + this.getClass().getSimpleName() + " & query: " + query.getName());
        eventExecutor.stack(eventMap);

    }*/
    protected class EventExecutor implements Runnable{
        private final LinkedBlockingQueue<Object> inserting = new LinkedBlockingQueue<>(), removing =  new LinkedBlockingQueue<>();
        private final Object queues= new Object();
        private boolean active = true;

        public synchronized void insertStack(Object eventMap){
            inserting.add(eventMap);
            synchronized (queues) {
                inserting.notifyAll();
            }
        }
    public synchronized void removeStack(Object eventMap){
        inserting.add(eventMap);
        synchronized (queues) {
            inserting.notifyAll();
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
                    if(!inserting.isEmpty())
                        processMessage(inserting.take());
                    if(!removing.isEmpty())
                        processLeavingMessage(removing.take());

                    synchronized (this) {
                        active = this.active;
                    }
                    if (inserting.size() == 0 && removing.size() == 0)
                        synchronized (queues) {
                            queues.wait(500);
                        }

                } catch (InterruptedException e) {
                    loggerService.error(e.getMessage(), e);
                }
            }
        }
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
