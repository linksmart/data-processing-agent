package eu.linksmart.ceml.handlers;

import eu.almanac.event.datafusion.handler.base.BaseListEventHandler;
import eu.linksmart.api.event.ceml.CEMLRequest;
import eu.linksmart.api.event.ceml.LearningStatement;
import eu.linksmart.api.event.ceml.data.DataDescriptors;
import eu.linksmart.api.event.ceml.model.Model;
import eu.linksmart.api.event.datafusion.Statement;
import eu.linksmart.ceml.intern.Const;
import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.function.Utils;
import org.slf4j.Logger;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    protected int leadingLerner = -1;
    protected Map<String, Integer> modelByName = new Hashtable<>();
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
