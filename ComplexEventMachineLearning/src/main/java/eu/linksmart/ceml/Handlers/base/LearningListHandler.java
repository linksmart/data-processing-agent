package eu.linksmart.ceml.handlers.base;

import eu.linksmart.api.event.ceml.LearningStatement;

import java.util.List;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public abstract class LearningListHandler<Input, Output> extends LearningHandlerBase<List,List<Input>,List<Output>,Object> {

    protected final Class<Input> inputType;
    protected final Class<Output> outputClass;
    public LearningListHandler(LearningStatement<List<Input>, List<Output>, Object> statement) {
        super(statement);
        inputType = (Class<Input>) genericClass()[0];
        outputClass = (Class<Output>) genericClass()[1];

    }

    @Override
    protected void processMessage(List input) {
        if(input!=null){
            try {
                List<Output> measuredTargets = (List<Output>) input.subList(descriptors.getInputSize(),descriptors.getTargetSize());
                List<Input> withoutTarget = input.subList(0, descriptors.getInputSize());


                List<Output> prediction = model.predict(withoutTarget);

                model.learn(input);



                model.getEvaluator().evaluate(prediction, measuredTargets);

                if(model.getEvaluator().isDeployable())
                    originalRequest.deploy();
                else
                    originalRequest.undeploy();

            } catch (Exception e) {
                loggerService.error(e.getMessage(),e);
            }

        }
    }
}
