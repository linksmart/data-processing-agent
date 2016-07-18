package eu.linksmart.api.event.ceml.evaluation.metrics;

public interface ClassEvaluationAlgorithmExtended {
    long getFalsePositives(int classIndex) ;

    void setFalsePositives(int classIndex,long FalsePositives) ;

    long getFalseNegatives(int classIndex);

    void setFalseNegatives(int classIndex,long FalseNegatives);

    long getTruePositives(int classIndex) ;

    void setTruePositives(int classIndex,long TruePositives) ;

    long getTrueNegatives(int classIndex) ;

    void setTrueNegatives(int classIndex, long TrueNegatives);

}