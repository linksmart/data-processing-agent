package eu.linksmart.services.utils.serialization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import eu.linksmart.services.utils.constants.Const;
import eu.linksmart.services.utils.function.Utils;
import eu.linksmart.services.utils.configuration.Configurator;

import java.io.IOException;

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
    public byte[] serialize(Object object) throws IOException {

        try {
            return parser.writeValueAsString(object).getBytes();
        } catch (JsonProcessingException e) {
            throw new IOException(e.getMessage(),e);
        }
    }

    @Override
    public String toString(Object object) throws IOException {
        try {
            return parser.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IOException(e.getMessage(),e);
        }
    }

    @Override
    public void close() {

    }
}
