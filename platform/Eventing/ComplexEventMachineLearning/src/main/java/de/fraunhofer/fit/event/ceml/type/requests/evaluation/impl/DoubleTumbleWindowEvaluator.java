package de.fraunhofer.fit.event.ceml.type.requests.evaluation.impl;

import de.fraunhofer.fit.event.ceml.type.requests.evaluation.EvaluatorBase;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.impl.EvaluationAlgorithmBase;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.Evaluator;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.TumbleEvaluator;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.EvaluationAlgorithm;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.ModelEvaluationAlgorithm;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.impl.InitialSamples;


import java.util.Collection;

/**
 * Created by angel on 1/12/15.
 */
public class DoubleTumbleWindowEvaluator extends EvaluatorBase implements TumbleEvaluator  {



    private WindowEvaluator[] windowEvaluators = new WindowEvaluator[2];
    private int learning = 0, learnt =0;
    private ModelEvaluationAlgorithm initialSamples;

    public DoubleTumbleWindowEvaluator() {
    }



    @Override
    public synchronized boolean  evaluate(int predicted, int actual){


        if(initialSamples.isReady()) {
            windowEvaluators[learning].evaluate(predicted, actual);
            if(learnt!=learning)
                windowEvaluators[learnt].evaluate(predicted, actual);

            trySliding();

            return windowEvaluators[learnt].isDeployable();


        }
        initialSamples.calculate();
        return false;


    }


    public synchronized boolean isDeployable(){


        return windowEvaluators[learnt].isDeployable();


    }
    public synchronized boolean trySliding() {

        if(readyToSlide()) {

            if(learning==learnt){
                learning=(learning+1)%2;
            }else{
                learning=(learning+1)%2;
                windowEvaluators[learnt].reset();
                learnt=(learnt+1)%2;
            }
            return true;
        }
        return false;

    }

    @Override
    public synchronized boolean readyToSlide() {
        return windowEvaluators[learning].readyToSlide();

    }


    @SuppressWarnings("unchecked")
    @Override
    public void build(Collection<String> namesClasses) throws Exception {
        boolean isSamples = false;
        for(int i=0; i <targets.size();i++) {
            if (targets.get(i) != null)
                if (targets.get(i).getName().equals(InitialSamples.class.getSimpleName())) {
                    initialSamples = (ModelEvaluationAlgorithm) EvaluationAlgorithmBase.instanceEvaluationAlgorithm(
                            InitialSamples.class.getCanonicalName(),
                            targets.get(i).getMethod(),
                            targets.get(i).getThreshold()
                    );
                    targets.remove(i);

                }
            if (targets.get(i).getName().equals(WindowEvaluator.Samples.class.getSimpleName()))
                isSamples = true;
        }


        if (!isSamples)
            throw  new Exception("For creating sliding evaluator the samples must be defined");

        if(initialSamples== null)
            initialSamples = new InitialSamples(EvaluationAlgorithm.ComparisonMethod.More,-1);

        windowEvaluators[0] = new WindowEvaluator(namesClasses,targets);
        windowEvaluators[1] = new WindowEvaluator(namesClasses,targets);

    }

    @Override
    public void reBuild(Evaluator evaluator) {
        if(evaluator instanceof DoubleTumbleWindowEvaluator){

            windowEvaluators[0].reBuild(evaluator);
            windowEvaluators[1].reBuild(evaluator);
        }

    }

    @Override
    public String report() {
        if(learning == learnt)
            return  "Learning window report > "+windowEvaluators[learning].report();

        return  "Learning window report > "+windowEvaluators[learning].report()+" | "+
                "Learnt window report > "+windowEvaluators[learnt].report();
    }


}