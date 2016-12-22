package eu.linksmart.testing.tooling;

import java.time.Instant;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;

/**
 * Created by José Ángel Carvajal on 22.12.2016 a researcher of Fraunhofer FIT.
 */
public class CounterTask extends TimerTask {

    volatile protected long i = 0,n =1, total;
    protected Logger loggerService = null;
    volatile protected Map<String, Integer> counters = new ConcurrentHashMap<>();

    public CounterTask() {
    }

    public CounterTask(Logger logger) {
        loggerService = logger;
    }

    public  void newEventInTopic(String topic) {
        if (!counters.containsKey(topic))
            counters.put(topic, 0);
        counters.put(topic, counters.get(topic) + 1);
        i++;
    }


    public void run() {
        double avg;
        long messages =0;
        Date before = new Date(), after;
        String counterStr = "";

        if((messages = i) == 0)
            return;

        i=0;
        total += messages;

        avg = total / n;
        n++;
        after = new Date();


        counterStr = counters.entrySet()
                .stream()
                .map(entry -> "\"" + entry.getKey() + "\"" + " : " + entry.getValue())
                .collect(Collectors.joining(", "));


        String message = "{\"total\": " + String.valueOf(total) +
                // ", \"lapsed\": "+(after.getTime()-before.getTime())/1000.0+
                ", \"messages\": " + String.valueOf(messages) +
                ", \"avg\": " + String.valueOf(avg) +
                ", \"time\":\"" + Instant.now().toString() +"\""+
                ", \"counters\":{ " + counterStr + " } }";
        if (loggerService != null)
            loggerService.info(message);
        else
            System.out.println(message);
        before = new Date();


    }

}