package de.fraunhofer.fit.event.ceml.core;

import eu.linksmart.api.event.ceml.LearningStatement;
import eu.linksmart.api.event.ceml.data.DataDescriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public class LearningMapHandler extends LearningHandlerBase<Map<String,Object>,Map<String,Object>,List<Object>>{


    public LearningMapHandler(LearningStatement<Map<String, Object>, List<Object>> statement) {
        super(statement);
    }

    @Override
    protected void processMessage(Map<String,Object> eventMap) {
        if(eventMap!=null){
            try {

                Map<String,Object>  withoutTarget= new HashMap<>();
                List<Object> measuredTargets = new ArrayList<>();
                for(DataDescriptor descriptor:descriptors.getDescriptors())
                    if(descriptor.isTarget())
                        measuredTargets.add(eventMap.get(descriptor.getName()));
                    else
                        withoutTarget.put(descriptor.getName(), eventMap.get(descriptor.getName()));


                List<Object> prediction = model.predict(withoutTarget);
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
