package de.fraunhofer.fit.event.ceml.type.requests.evaluation.impl;


import de.fraunhofer.fit.event.ceml.type.requests.evaluation.Evaluator;

import java.util.Collection;

/**
 * Created by angel on 30/11/15.
 *  TODO: must be fixed
 */
public class BinaryEvaluator  implements Evaluator {

    private double currentAccuracy =0.0;
    private double currentPrecision =0.0;
    private long falsePositives =0;
    private long falseNegatives=0;
    private long truePositives=0;
    private long trueNegatives=0;
    private long samples =0;

    public boolean evaluate(double predicted, double expected,boolean positive) throws Exception {
        if(predicted==expected)
            if(positive)
                truePositives++;
            else
                trueNegatives++;
        else
            if(positive)
                falsePositives++;
            else
                falseNegatives++;

        throw new Exception("Wrong!");
       // return sample()>targetSamples && calculatePrecision() >= targetPrecision && calculateAccuracy() >= targetAccuracy;


    }

    public double calculateAccuracy(){

        double denominator = (trueNegatives+truePositives+falseNegatives+falsePositives);
        if(denominator>0)
            return (currentAccuracy=((double)(trueNegatives+truePositives))/denominator);
        return Double.NEGATIVE_INFINITY;


    }
    public double calculatePrecision(){
        long denominator = (truePositives+falsePositives);
        if(denominator>0)
            return (currentPrecision =((double)truePositives)/denominator);
        return Double.NEGATIVE_INFINITY;
    }


    public long getSamples() {
        return samples;
    }

    public long sample(){
        return samples+=1;
    }


    @Override
    public boolean evaluate(int predicted, int actual) {
        return false;
    }

    @Override
    public boolean isDeployable() {
        return false;
    }


    @Override
    public void build(Collection<String> classesNames) {

    }

}
