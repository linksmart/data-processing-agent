package eu.linksmart.ceml.handlers.base;

import eu.linksmart.api.event.ceml.LearningStatement;
import eu.linksmart.api.event.ceml.data.DataDescriptor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public abstract class LearningMapHandler<Input, Output> extends LearningHandlerBase<Map,Map<String,Input>,List<Output>,Object>{

    protected final Class<Input> inputType;
    protected final Class<Output> outputClass;

    public LearningMapHandler(LearningStatement<Map<String,Input>, List<Output>> statement) throws Exception{
        super(statement);


        inputType = (Class<Input>) genericClass()[0];
        outputClass = (Class<Output>) genericClass()[1];


    }

    @Override
    protected void processMessage(Map eventMap) {

        if(eventMap!=null){
            try {


                Map<String,Input>  withoutTarget= new HashMap<>();
                List<Output> measuredTargets = new ArrayList<>();
                for(DataDescriptor descriptor:descriptors)
                    if(descriptor.isTarget()) {
                        if (outputClass.isAssignableFrom(eventMap.get(descriptors.getName()).getClass()))
                            measuredTargets.add((Output) eventMap.get(descriptor.getName()));
                        else
                            loggerService.error("Type mismatch between the the expected output and received one");
                    }else
                        if( inputType.isAssignableFrom(eventMap.get(descriptors.getName()).getClass()))
                            withoutTarget.put(descriptor.getName(), (Input) eventMap.get(descriptor.getName()));
                        else
                            loggerService.error("Type mismatch between the the expected input and received one");


                List<Output> prediction = model.predict(withoutTarget);
                model.learn(eventMap);

                evaluator.evaluate( prediction,measuredTargets);

                if(evaluator.isDeployable())
                    originalRequest.deploy();
                else
                    originalRequest.undeploy();

            } catch (Exception e) {
               loggerService.error(e.getMessage(),e);
            }

        }

    }

}
