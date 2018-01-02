package eu.linksmart.services.event.handler.base;

import eu.linksmart.services.event.intern.AgentUtils;
import eu.linksmart.api.event.components.ComplexEventHandler;
import eu.linksmart.api.event.types.Statement;
import org.slf4j.Logger;

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
/*
   public  void update(Map eventMap) {
        initThread();
        loggerService.debug(Utils.getDateNowString() + " update map[] w/ handler " + this.getClass().getSimpleName() + " & query: " + query.getName());
        eventExecutor.stack(eventMap);

    }*/
    protected class EventExecutor implements Runnable{
        private final LinkedBlockingQueue<Object> queue = new LinkedBlockingQueue<>();
        private boolean active = true;

        public synchronized void stack(Object eventMap){
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


    protected Logger loggerService = AgentUtils.initLoggingConf(this.getClass());


    @Override
    public void destroy() {

    }
    private Type[] genericClass() {
        ParameterizedType parameterizedType = (ParameterizedType)getClass()
                .getGenericSuperclass();
        return parameterizedType.getActualTypeArguments();
    }
}
