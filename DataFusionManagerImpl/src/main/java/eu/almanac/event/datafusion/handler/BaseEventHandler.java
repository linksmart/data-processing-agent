package eu.almanac.event.datafusion.handler;

import eu.almanac.event.datafusion.intern.Utils;
import eu.linksmart.api.event.datafusion.ComplexEventHandler;
import eu.linksmart.api.event.datafusion.Statement;
import eu.linksmart.gc.utils.logging.LoggerService;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public abstract class BaseEventHandler<T>  implements ComplexEventHandler<T> {
    protected   EventExecutor eventExecutor = new EventExecutor();
    protected Statement query;

    public  BaseEventHandler(Statement statement){
        query = statement;
    }
    public  void update(T eventMap) {
        loggerService.info( Utils.getDateNowString() + " Simple update query: " + query.getName());
        eventExecutor.stack(eventMap);



    }
    private class EventExecutor implements Runnable{
        private Map eventMap;
        private LinkedBlockingQueue<T> queue = new LinkedBlockingQueue();
        private boolean active = true;

        synchronized void stack(T eventMap){
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

    protected abstract void processMessage(T events); protected LoggerService loggerService = Utils.initDefaultLoggerService(this.getClass());

    public void update(T[] insertStream, T[] removeStream){
        loggerService.info( Utils.getDateNowString() + " Multi-update query: " + query.getName());
        if(insertStream!=null)
            for (T m: insertStream)
                eventExecutor.stack(m);
        if(removeStream!=null)
            for (T m: removeStream)
                eventExecutor.stack(m);
    }
    @Override
    public void destroy() {

    }
}
