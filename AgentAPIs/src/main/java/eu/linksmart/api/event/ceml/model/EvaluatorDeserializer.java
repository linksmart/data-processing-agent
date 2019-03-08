package eu.linksmart.api.event.ceml.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.linksmart.api.event.ceml.evaluation.Evaluator;
import eu.linksmart.services.utils.serialization.DeserializerMode;

import java.io.IOException;

/**
 * Created by José Ángel Carvajal on 05.03.2019 a researcher of Fraunhofer FIT.
 */
public abstract class EvaluatorDeserializer extends DeserializerMode<Evaluator> {
    public abstract Evaluator deserialize(ObjectMapper mapper, JsonNode node) throws IOException, JsonProcessingException;
}
