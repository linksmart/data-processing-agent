package eu.linksmart.api.event.ceml.evaluation;

/**
 * Created by angel on 2/12/15.
 */
public interface TumbleEvaluator extends Evaluator {
    boolean trySliding();
    boolean readyToSlide();
}
