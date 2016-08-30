package eu.linksmart.api.event.ceml.evaluation;

/**
 * Created by angel on 2/12/15.
 */
public interface TumbleEvaluator<T> extends Evaluator<T> {
    boolean trySliding();
    boolean readyToSlide();
}
