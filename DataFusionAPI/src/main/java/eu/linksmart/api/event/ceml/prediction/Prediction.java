package eu.linksmart.api.event.ceml.prediction;

/**
 * Created by José Ángel Carvajal on 28.07.2016 a researcher of Fraunhofer FIT.
 */
public interface Prediction {
    default double getCertaintyDegree() {
        return certaintyDegree;
    }

    default void setCertaintyDegree(double certaintyDegree) {
        this.certaintyDegree = certaintyDegree;
    }

    default boolean isAcceptedPrediction() {
        return acceptedPrediction;
    }

    default void setAcceptedPrediction(boolean acceptedPrediction) {
        this.acceptedPrediction = acceptedPrediction;
    }
}
