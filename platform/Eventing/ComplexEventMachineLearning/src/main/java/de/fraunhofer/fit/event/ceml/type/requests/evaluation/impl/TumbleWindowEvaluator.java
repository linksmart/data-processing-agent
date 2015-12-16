package de.fraunhofer.fit.event.ceml.type.requests.evaluation.impl;

import de.fraunhofer.fit.event.ceml.type.requests.builded.EvaluationAlgorithmBase;
import de.fraunhofer.fit.event.ceml.type.requests.builded.InitialSamples;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.Evaluator;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.TumbleEvaluator;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.EvaluationAlgorithm;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by angel on 1/12/15.
 */
public class TumbleWindowEvaluator  implements TumbleEvaluator  {

    protected ArrayList<TargetRequest> targets;


    private WindowEvaluator[] windowEvaluators = new WindowEvaluator[2];
    private int learning = 0, learnt =0;
    private EvaluationAlgorithm  initialSamples;

    public TumbleWindowEvaluator() {
    }

    @Override
    public synchronized boolean  evaluate(int predicted, int actual){


        if(initialSamples.isReady()) {
            windowEvaluators[learning].evaluate(predicted, actual);
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
    public void build(Collection<String> namesClasses){
        for(int i=0; i <targets.size();i++)
            if(targets.get(i)!=null)
                if(targets.get(i).getName().equals(InitialSamples.class.getSimpleName())) {
                    initialSamples = EvaluationAlgorithmBase.instanceEvaluationAlgorithm(
                            InitialSamples.class.getSimpleName(),
                            targets.get(i).getMethod(),
                            targets.get(i).getThreshold()
                    );
                    targets.remove(i);
                }


        if(initialSamples== null)
            initialSamples = new InitialSamples(EvaluationAlgorithm.ComparisonMethod.More,-1);

        windowEvaluators[0] = new WindowEvaluator(namesClasses,targets);
        windowEvaluators[1] = new WindowEvaluator(namesClasses,targets);

    }

    @Override
    public void reBuild(Evaluator evaluator) {
        if(evaluator instanceof  TumbleWindowEvaluator){

            windowEvaluators[0].reBuild(evaluator);
            windowEvaluators[1].reBuild(evaluator);
        }

    }


}