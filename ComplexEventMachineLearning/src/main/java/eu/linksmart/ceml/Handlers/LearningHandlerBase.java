package eu.linksmart.ceml.Handlers;

import eu.linksmart.ceml.intern.Const;
import eu.almanac.event.datafusion.handler.BaseEventHandler;
import eu.linksmart.api.event.ceml.data.DataDescriptors;
import eu.linksmart.api.event.ceml.model.Model;

import eu.linksmart.api.event.ceml.CEMLRequest;
import eu.linksmart.api.event.ceml.evaluation.Evaluator;
import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.function.Utils;
import eu.linksmart.gc.utils.logging.LoggerService;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by angel on 26/11/15.
 */
public abstract class LearningHandlerBase<Coll,Val,RetVal,LearningObject>  extends BaseEventHandler<Coll> {

    static protected Configurator conf = Configurator.getDefaultConfig();
    static protected LoggerService loggerService = Utils.initDefaultLoggerService(LearningHandlerBase.class);
    protected ExecutorService executor = Executors.newCachedThreadPool();
    final protected eu.linksmart.api.event.ceml.LearningStatement statement;
    final protected CEMLRequest<Val,RetVal> originalRequest;
    final protected Model<Val,RetVal,LearningObject> model;
    final protected Evaluator<RetVal> evaluator;
    final protected DataDescriptors descriptors;
    protected String columnNameTime = "";
    protected int leadingLerner = -1;
    protected Map<String, Integer> modelByName = new Hashtable<>();


    public LearningHandlerBase(eu.linksmart.api.event.ceml.LearningStatement<Val, RetVal> statement) {
        super(statement);
        //super(LearningHandlerBase.class.getSimpleName(),"Learning Handler processes the data and input it to the learning objects");
        this.statement = statement;
        this.originalRequest =statement.getRequest();
        model = originalRequest.getModel();
        evaluator = originalRequest.getEvaluator();
        descriptors = originalRequest.getDescriptors();

        if(conf.getString(Const.CEML_EngineTimeProveded)!= null ||conf.getString(Const.CEML_EngineTimeProveded)!="" )
            columnNameTime = conf.getString(Const.CEML_EngineTimeProveded);

    }


    protected void report(String prefix){
        /*for(int i=0; i< originalRequest.getModel().size();i++)
            loggerService.info(prefix + originalRequest.getModel().get(i).report());*/


    }
    static protected <Val,RetVal> void learn(String statementID, CEMLRequest<Val,RetVal> request){
       /* for(Model m: originalRequest.getModel())
            CEML.learn(m.getLerner(),instance);*/

    }

    protected abstract void processMessage(Coll eventMap);


    public static <Val,RetVal> void update(String statementID, CEMLRequest<Val,RetVal> request) {
     /*   String columnNameTime="";
        try {
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

            //int prediction = CEML.predict(originalRequest.getModel().getLerner(),instance);
            Prediction prediction = originalRequest.evaluate(instance);
            int itShould =  originalRequest.getData().getLearningTarget().indexOfValue(target.toString());

            // if(originalRequest.getEvaluation().evaluate(prediction, itShould))
            if(prediction.isAcceptedPrediction())
                originalRequest.deploy();
            else
                originalRequest.undeploy();


            if(conf.getBool(Const.CEML_GenerateReports)) {
                String simulatedTime = "";
                if(eventMap.containsKey(columnNameTime)) {
                    Object aux = eventMap.get(columnNameTime);
                    if(aux instanceof Date)
                        simulatedTime = " --"+Utils.getIsoTimestamp((Date)aux)+"-- ";
                    else if (aux instanceof String)
                        simulatedTime = " --"+((String) aux)+"-- ";
                    else if (aux instanceof Long)
                        simulatedTime= " --"+Utils.getIsoTimestamp(new Date((Long)aux))+"-- ";
                    else
                        loggerService.warn("Selected column as simulated time has unknown time format");
                }
                for(int i=0; i< originalRequest.getModel().size();i++)
                    loggerService.info("REPORT:" +simulatedTime+ originalRequest.getModel().get(i).report());

            }
            learn(originalRequest,instance);


        }catch (NullPointerException e){
            loggerService.error("Ignored instance due null values in some data points");
        }*/
    }



    @Override
    public void destroy() {

    }

}
