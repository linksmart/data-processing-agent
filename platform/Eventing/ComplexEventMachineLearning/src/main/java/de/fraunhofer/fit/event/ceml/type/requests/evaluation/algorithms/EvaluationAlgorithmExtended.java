package de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms;

/**
 * Created by angel on 4/12/15.
 */
public interface EvaluationAlgorithmExtended {
    long getTotalFalsePositives() ;

    void setTotalFalsePositives(long totalFalsePositives) ;

    long getTotalFalseNegatives();

    void setTotalFalseNegatives(long totalFalseNegatives);

    long getTotalTruePositives() ;

    void setTotalTruePositives(long totalTruePositives) ;

    long getTotalTrueNegatives() ;

    void setTotalTrueNegatives(long totalTrueNegatives);

    double[][] getConfusionMatrix();

    void setConfusionMatrix(double[][] sequentialConfusionMatrix);
}
