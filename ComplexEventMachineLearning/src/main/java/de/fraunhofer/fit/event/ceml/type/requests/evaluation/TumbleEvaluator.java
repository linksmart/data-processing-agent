package de.fraunhofer.fit.event.ceml.type.requests.evaluation;

/**
 * Created by angel on 2/12/15.
 */
public interface TumbleEvaluator extends Evaluator {
    boolean trySliding();
    boolean readyToSlide();
}
