package eu.linksmart.ceml.handlers;

import eu.almanac.event.datafusion.handler.base.BaseListEventHandler;
import eu.linksmart.api.event.ceml.CEMLRequest;
import eu.linksmart.api.event.ceml.LearningStatement;
import eu.linksmart.api.event.ceml.data.DataDescriptor;
import eu.linksmart.api.event.ceml.data.DataDescriptors;
import eu.linksmart.api.event.ceml.model.Model;
import eu.linksmart.api.event.ceml.prediction.Prediction;
import eu.linksmart.api.event.datafusion.EventType;
import eu.linksmart.api.event.datafusion.Statement;
import eu.linksmart.ceml.intern.Const;
import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.function.Utils;
import org.slf4j.Logger;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public  class ListLearningHandler extends BaseListEventHandler {

    static protected Configurator conf = Configurator.getDefaultConfig();
    static protected Logger loggerService = Utils.initLoggingConf(ListLearningHandler.class);
    final protected LearningStatement statement;
    final protected CEMLRequest originalRequest;
    final protected Model model;
    final protected DataDescriptors descriptors;

    protected String columnNameTime = "";
    protected List<Object> lastTarget =null;
    public ListLearningHandler(Statement statement) {
        super(statement);

        this.statement = (LearningStatement) statement;
        this.originalRequest =((LearningStatement)statement).getRequest();
        model = originalRequest.getModel();
        descriptors = originalRequest.getDescriptors();

        if(conf.getString(Const.CEML_EngineTimeProveded)!= null ||conf.getString(Const.CEML_EngineTimeProveded).equals("") )
            columnNameTime = conf.getString(Const.CEML_EngineTimeProveded);

    }

    @Override
    protected void processMessage(List input) {

        learn(flattList(input));

    }
    private List<Object> flattList(List input){
        List aux;
        if(input.iterator().hasNext()&&input.iterator().next() instanceof List) {
            aux= new ArrayList<>();
            ((List<List>)input).forEach(l->aux.addAll(flattList(l)));

        }else
            aux = input;

        return aux;
    }
    protected void learn(List input) {
        if(input!=null&&input.size()>=descriptors.size()+descriptors.getTargetSize()){

            try {
                synchronized (originalRequest) {
                    List auxInput;
                    if(input.get(0) instanceof EventType)
                        auxInput = (List) input.stream().map(m->((EventType)m).getValue()).collect(Collectors.toList());
                    else
                        auxInput = input;
                    List learningInput = auxInput.subList(0, descriptors.size());
                    model.learn(learningInput);

                  //  if(auxInput.size()>= descriptors.size()+descriptors.getTargetSize()) {
                    List groundTruth = auxInput.subList(descriptors.size(), descriptors.size()+descriptors.getTargetSize());
                    List predictionInput = auxInput.subList(descriptors.getTargetSize(), descriptors.getTargetSize()+descriptors.getInputSize());
                    Prediction prediction = model.predict(predictionInput);
                    model.getEvaluator().evaluate(prediction.getPrediction(), groundTruth);


                  //  }

                }



                if(model.getEvaluator().isDeployable())
                    originalRequest.deploy();
                else
                    originalRequest.undeploy();

            } catch (Exception e) {
                loggerService.error(e.getMessage(),e);
            }

            originalRequest.report();
        }
    }
    static public void processMessage(List input, DataDescriptors  descriptors,Model model, CEMLRequest originalRequest) {
        if(input!=null&&input.size()>=descriptors.size()){
            try {
                List measuredTargets =  input.subList(descriptors.getInputSize(),input.size());
                List withoutTarget = input.subList(0, descriptors.getInputSize());

                List prediction = (List) model.predict(withoutTarget).getPrediction();

                model.learn(input);

                model.getEvaluator().evaluate(prediction, measuredTargets);

                if(model.getEvaluator().isDeployable())
                    originalRequest.deploy();
                else
                    originalRequest.undeploy();

            } catch (Exception e) {
                loggerService.error(e.getMessage(),e);
            }

            originalRequest.report();
        }
    }

}
