package de.fraunhofer.fit.event.ceml;

import de.fraunhofer.fit.event.ceml.intern.Const;
import de.fraunhofer.fit.event.ceml.type.requests.LearningRequest;
import de.fraunhofer.fit.event.ceml.type.requests.LearningStatement;
import eu.almanac.event.datafusion.utils.generic.Component;
import eu.linksmart.api.event.datafusion.ComplexEventHandler;
import eu.linksmart.api.event.datafusion.Statement;
import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.function.Utils;
import eu.linksmart.gc.utils.logging.LoggerService;
import weka.core.Instance;

import java.util.Map;

/**
 * Created by angel on 26/11/15.
 */
public class LearningHandler extends Component implements ComplexEventHandler {

    static private Configurator conf = Configurator.getDefaultConfig();
    static private LoggerService loggerService = Utils.initDefaultLoggerService(LearningHandler.class);

    private Statement statement;
    private LearningRequest originalRequest;


    public LearningHandler(Statement statement) {
        super(LearningHandler.class.getSimpleName(),"Learning Handler processes the data and input it to the learning objects");
        this.statement = statement;
        this.originalRequest =((LearningStatement)statement).getLearningRequest();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void update(Map eventMap) {

        Instance instance = CEML.populateInstance(eventMap,originalRequest);

        Object target =null;
        if(eventMap.containsKey("target"))
            target=eventMap.get("target");
        else if (eventMap.containsKey(originalRequest.getData().getLearningTarget().name()))
            target =eventMap.get(originalRequest.getData().getLearningTarget().name());
        else {
            loggerService.error("No target found in the learning rule");
            return;
        }
        int prediction = CEML.predict(originalRequest.getModel().getLerner(),instance);
        int itShould =  originalRequest.getData().getLearningTarget().indexOfValue(target.toString());
/*
        Instances instances =originalRequest.getData().getInstances();

       // Weka dose the evaluation in a linear way, this means that the evaluation cost by weka is linear!
     try {
            instances.add(instance);
            evaluation evaluation = new evaluation(instances);
            evaluation.evaluateModel((Classifier) originalRequest.getModel().getLerner(),instances);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        if(originalRequest.getEvaluation().evaluate(prediction, itShould))
             originalRequest.deploy();
        else
            originalRequest.undeploy();

        if(conf.getBool(Const.GenerateReports))
            loggerService.info(originalRequest.getEvaluation().report());

        CEML.learn(originalRequest.getModel().getLerner(),instance);


    }
    public static void update(Map eventMap, LearningRequest originalRequest) {

        Instance instance = CEML.populateInstance(eventMap,originalRequest);

        Object target =null;
        if(eventMap.containsKey("target"))
            target=eventMap.get("target");
        else if (eventMap.containsKey(originalRequest.getData().getLearningTarget()))
            target =eventMap.get(originalRequest.getData().getLearningTarget());
        else {
            loggerService.error("No target found in the learning rule");
            return;
        }
        int prediction = CEML.predict(originalRequest.getModel().getLerner(),instance);
        int itShould =  originalRequest.getData().getLearningTarget().indexOfValue(target.toString());
/*
        Instances instances =originalRequest.getData().getInstances();

       // Weka dose the evaluation in a linear way, this means that the evaluation cost by weka is linear!
     try {
            instances.add(instance);
            evaluation evaluation = new evaluation(instances);
            evaluation.evaluateModel((Classifier) originalRequest.getModel().getLerner(),instances);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        if(originalRequest.getEvaluation().evaluate(prediction, itShould))
            originalRequest.deploy();
        else
            originalRequest.undeploy();

        CEML.learn(originalRequest.getModel().getLerner(),instance);


    }
    public static int classify(Map eventMap, LearningRequest originalRequest) {

        Instance instance = CEML.populateInstance(eventMap,originalRequest);

        return CEML.predict(originalRequest.getModel().getLerner(),instance);


    }



    @Override
    public void destroy() {

    }

}
