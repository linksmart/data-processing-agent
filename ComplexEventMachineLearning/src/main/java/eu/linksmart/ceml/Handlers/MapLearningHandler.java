package eu.linksmart.ceml.handlers;

import eu.almanac.event.datafusion.handler.base.BaseMapEventHandler;
import eu.linksmart.api.event.ceml.CEMLRequest;
import eu.linksmart.api.event.ceml.LearningStatement;
import eu.linksmart.api.event.ceml.data.DataDescriptor;
import eu.linksmart.api.event.ceml.data.DataDescriptors;
import eu.linksmart.api.event.ceml.model.Model;
import eu.linksmart.api.event.datafusion.Statement;
import eu.linksmart.ceml.intern.Const;
import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.function.Utils;
import eu.linksmart.gc.utils.logging.LoggerService;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public  class MapLearningHandler extends BaseMapEventHandler {

    static protected Configurator conf = Configurator.getDefaultConfig();
    static protected LoggerService loggerService = Utils.initDefaultLoggerService(MapLearningHandler.class);
    final protected LearningStatement statement;
    final protected CEMLRequest originalRequest;
    final protected Model model;
    final protected DataDescriptors descriptors;

    protected String columnNameTime = "";
    public MapLearningHandler(Statement statement) {
        super(statement);

        this.statement = (LearningStatement) statement;
        this.originalRequest =((LearningStatement)statement).getRequest();
        model = originalRequest.getModel();
        descriptors = originalRequest.getDescriptors();

        if(conf.getString(Const.CEML_EngineTimeProveded)!= null ||conf.getString(Const.CEML_EngineTimeProveded)!="" )
            columnNameTime = conf.getString(Const.CEML_EngineTimeProveded);
    }


    @Override
    protected void processMessage(Map eventMap) {

        if(eventMap!=null){
            try {


                Map  withoutTarget= new HashMap<>();
                List measuredTargets = new ArrayList<>();
                for(DataDescriptor descriptor:descriptors)
                    if(descriptor.isTarget()) {
                        if (descriptor.getNativeType().isAssignableFrom(eventMap.get(descriptors.getName()).getClass()))
                            measuredTargets.add(eventMap.get(descriptor.getName()));
                        else
                            loggerService.error("Type mismatch between the the expected output and received one");
                    }else
                        if( descriptor.getNativeType().isAssignableFrom(eventMap.get(descriptors.getName()).getClass()))
                            withoutTarget.put(descriptor.getName(),  eventMap.get(descriptor.getName()));
                        else
                            loggerService.error("Type mismatch between the the expected input and received one");


                List prediction = (List) model.predict(withoutTarget);
                model.learn(eventMap);

               model.getEvaluator().evaluate(prediction, measuredTargets);

                if( model.getEvaluator().isDeployable())
                    originalRequest.deploy();
                else
                    originalRequest.undeploy();

            } catch (Exception e) {
               loggerService.error(e.getMessage(),e);
            }
            originalRequest.report();

        }

    }
    private Type[] genericClass() {
        ParameterizedType parameterizedType = (ParameterizedType)getClass()
                .getGenericSuperclass();
        return parameterizedType.getActualTypeArguments();
    }
}
