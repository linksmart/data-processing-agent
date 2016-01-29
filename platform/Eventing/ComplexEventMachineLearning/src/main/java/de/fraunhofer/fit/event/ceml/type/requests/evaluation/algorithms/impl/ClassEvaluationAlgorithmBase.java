package de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.impl;

import de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.ClassEvaluationAlgorithm;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.impl.TargetRequest;

import java.lang.reflect.Array;


/**
 * Created by José Ángel Carvajal on 23.12.2015 a researcher of Fraunhofer FIT.
 */
public abstract class ClassEvaluationAlgorithmBase<T extends  Comparable<T>> extends EvaluationAlgorithmBase<T[]> implements ClassEvaluationAlgorithm<T> {


    public ClassEvaluationAlgorithmBase(){
        super();
    }
    public ClassEvaluationAlgorithmBase(ComparisonMethod method, T[] targets){
        super(method,targets);
        @SuppressWarnings("unchecked")
        final T[] a = (T[]) Array.newInstance(target.getClass(), targets.length);
        target = a;

    }
    @Override
    public abstract T calculate(int classIndex) ;


    @Override
    public boolean isClassReady(int i) {
        switch (method){

            case Equal:
                return currentValue[i].compareTo( target[i])==0;
            case More:
                return currentValue[i].compareTo( target[i])<0;
            case MoreEqual:
                return currentValue[i].compareTo( target[i])<=0;
            case Less:
                return currentValue[i].compareTo( target[i])>0;
            case LessEqual:
                return currentValue[i].compareTo( target[i])>=0;
        }
        return false;
    }

    @Override
    public T getClassResult(int classIndex) {
        return currentValue[classIndex];
    }


    @Override
    public boolean isReady() {
        boolean ready = true;
        for(int i=0; i<target.length &&ready;i++)
            switch (method){

                case Equal:
                    ready = currentValue[i].compareTo(target[i])==0;
                    break;
                case More:
                    ready = currentValue[i].compareTo(target[i])<0;
                    break;
                case MoreEqual:
                    ready = currentValue[i].compareTo(target[i])<=0;
                    break;
                case Less:
                    ready = currentValue[i].compareTo(target[i])>0;
                    break;
                case LessEqual:
                    ready = currentValue[i].compareTo(target[i])>=0;
            }
        return ready;
    }

    @Override
    public void reset() {
        @SuppressWarnings("unchecked")

        final T[] a = (T[]) Array.newInstance(target.getClass(), target.length);
        target = a;
    }
}
