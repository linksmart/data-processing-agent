package eu.linksmart.services.event.ceml.evaluation.evaluators;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import eu.linksmart.services.utils.serialization.DeserializerMode;

import java.io.IOException;

/**
 * Created by José Ángel Carvajal on 01.03.2019 a researcher of Fraunhofer FIT.
 */
public class WindowEvaluatorDeserializer extends DeserializerMode<WindowEvaluator> {
    @Override
    public WindowEvaluator deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        EvaluatorBase evaluator = EvaluatorBaseDeserializer.process(p,ctxt);

        if(evaluator instanceof WindowEvaluator)
            return (WindowEvaluator) evaluator;

        return null;
    }
}
