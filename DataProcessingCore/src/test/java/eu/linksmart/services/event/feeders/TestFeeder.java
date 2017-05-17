package eu.linksmart.services.event.feeders;

import eu.almanac.ogc.sensorthing.api.datamodel.Datastream;
import eu.almanac.ogc.sensorthing.api.datamodel.Observation;
import eu.linksmart.api.event.components.CEPEngine;
import eu.linksmart.api.event.components.Feeder;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.impl.StatementInstance;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 09.03.2016 a researcher of Fraunhofer FIT.
 */
@Deprecated
public class TestFeeder implements Feeder, Runnable {
    private final Thread simulation;
    private Map<String, CEPEngine> dataFusionWrappers = new Hashtable<>();




    public TestFeeder(){
        System.out.println("start testfeeder");
       simulation =  new Thread(this);
    }

    @Override
    public void run() {
        System.out.println("start simulation feeders");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Observation observation = new Observation();
        observation.setDate(new Date());
        observation.setResultType("simulation");
        Datastream ds = new Datastream();
        ds.setId("1");
        observation.setDatastream(ds);
        observation.setResultValue(1);
        observation.setId("1");
        long acc =0;
        int i=0;
        StatementInstance statement = new StatementInstance();
        statement.setStatement("select total as messagesPerSecond from Observation.win:time(1 sec).stat:uni(cast(resultValue,double)) output last every 1 sec");
        statement.setName("test");

        for (CEPEngine dfw : dataFusionWrappers.values())
            try {
                dfw.addStatement(statement);
            } catch (Exception e) {
                e.printStackTrace();
            }

        while (true){

            long start = System.nanoTime();
            i++;
            for (CEPEngine dfw : dataFusionWrappers.values())
                try {
                    dfw.addEvent( observation, observation.getClass());
                } catch (TraceableException e) {
                    e.printStackTrace();
                } catch (UntraceableException e) {
                    e.printStackTrace();
                }
            acc+=System.nanoTime()-start;
            if(acc/1000000>1000){
                System.out.println((i*1000000000.0)/acc);
                acc =0;
                i=0;
            }
        }

    }

    @Override
    public void feed(String topicURI, String payload) throws TraceableException, UntraceableException {

    }

    @Override
    public void feed(String topicURI, Object payload) throws TraceableException, UntraceableException {

    }
}
