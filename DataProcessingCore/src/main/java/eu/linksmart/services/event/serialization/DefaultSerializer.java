package eu.linksmart.services.event.serialization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import eu.linksmart.api.event.components.Serializer;
import eu.linksmart.api.event.exceptions.StatementException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.Utils;
import eu.linksmart.services.utils.configuration.Configurator;

/**
 * Created by José Ángel Carvajal on 23.08.2016 a researcher of Fraunhofer FIT.
 */
public class DefaultSerializer implements Serializer{

    protected ObjectMapper parser = new ObjectMapper();
    protected Configurator conf = Configurator.getDefaultConfig();

    public DefaultSerializer() {

        parser.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        parser.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, conf.getBoolean(Const.TIME_EPOCH_CONF_PATH));
        parser.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
        parser.setDateFormat(Utils.getDateFormat());
        parser.setTimeZone(Utils.getTimeZone());
    }

    @Override
    public byte[] serialize(Object object) throws UntraceableException {

        try {
            return parser.writeValueAsString(object).getBytes();
        } catch (JsonProcessingException e) {
            throw new UntraceableException(e.getMessage(),e);
        }
    }

    @Override
    public String toString(Object object) throws UntraceableException {
        try {
            return parser.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new UntraceableException(e.getMessage(),e);
        }
    }

    @Override
    public void close() {

    }
}
