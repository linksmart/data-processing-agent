package it.ismb.pertlab.pwal.api.events.pubsub;

import com.mycila.event.Dispatcher;
import com.mycila.event.Dispatchers;

public class PWALEventDispatcher
{
    private Dispatcher dispacher;
    private static PWALEventDispatcher instance;
    private static Object lockIstance = new Object();
    
    private PWALEventDispatcher()
    {
        this.dispacher = Dispatchers.asynchronousSafe();
    }
    
    public static PWALEventDispatcher getInstance()
    {
        synchronized (lockIstance)
        {
            if(instance == null)
                instance = new PWALEventDispatcher();
            return instance;
        }
    }
    
    public Dispatcher getDispatcher()
    {
        return this.dispacher;
    }
}
