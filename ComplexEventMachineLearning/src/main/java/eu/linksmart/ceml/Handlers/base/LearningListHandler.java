package eu.linksmart.ceml.handlers.base;

import eu.linksmart.api.event.ceml.LearningStatement;

import java.util.List;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public class LearningListHandler extends LearningHandlerBase<List<Object>,List<Object>,List<Object>,Object> {


    public LearningListHandler(LearningStatement<List<Object>, List<Object>> statement) {
        super(statement);
    }

    @Override
    protected void processMessage(List<Object> input) {
        if(input!=null){
            try {
                List<Object> measuredTargets = input.subList(descriptors.getInputSize(),descriptors.getTargetSize());
                List<Object> withoutTarget = input.subList(0, descriptors.getInputSize());


                List<Object> prediction = model.predict(withoutTarget);

                model.learn(input);



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
