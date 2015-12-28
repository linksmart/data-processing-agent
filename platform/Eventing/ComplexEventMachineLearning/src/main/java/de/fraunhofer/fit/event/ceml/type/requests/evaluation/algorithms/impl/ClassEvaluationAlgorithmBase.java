package de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.impl;

import de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.ClassEvaluationAlgorithm;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.impl.TargetRequest;


/**
 * Created by José Ángel Carvajal on 23.12.2015 a researcher of Fraunhofer FIT.
 */
public abstract class ClassEvaluationAlgorithmBase extends EvaluationAlgorithmBase<Double[]> implements ClassEvaluationAlgorithm {
    protected Double[] targets ;
    protected Double[] currentValues;

    public ClassEvaluationAlgorithmBase(){
        super();
    }
    public ClassEvaluationAlgorithmBase(ComparisonMethod method, Double[] targets){
        super(method);
        this.targets = targets;
        currentValues = new Double[targets.length];

    }
    @Override
    public abstract Double calculate(int classIndex) ;

    @Override
    public Double[] getTarget() {
        return targets;
    }

    @Override
    public void setTarget(Double[] targets) {
        this.targets =targets;
    }

    @Override
    public Double[] getResult() {
        return currentValues;
    }

    @Override
    public boolean isClassReady(int i) {
        switch (method){

            case Equal:
                return currentValues[i].compareTo( targets[i])==0;
            case More:
                return currentValues[i].compareTo( targets[i])<0;
            case MoreEqual:
                return currentValues[i].compareTo( targets[i])<=0;
            case Less:
                return currentValues[i].compareTo( targets[i])>0;
            case LessEqual:
                return currentValues[i].compareTo( targets[i])>=0;
        }
        return false;
    }

    @Override
    public void reBuild(TargetRequest evaluationAlgorithm) {
        targets = evaluationAlgorithm.getThresholds();

    }

    @Override
    public boolean isReady() {
        boolean ready = true;
        for(int i=0; i<targets.length &&ready;i++)
            switch (method){

                case Equal:
                    ready = currentValues[i].compareTo(targets[i])==0;
                    break;
                case More:
                    ready = currentValues[i].compareTo(targets[i])<0;
                    break;
                case MoreEqual:
                    ready = currentValues[i].compareTo(targets[i])<=0;
                    break;
                case Less:
                    ready = currentValues[i].compareTo(targets[i])>0;
                    break;
                case LessEqual:
                    ready = currentValues[i].compareTo(targets[i])>=0;
            }
        return ready;
    }
}
