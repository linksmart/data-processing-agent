package eu.linksmart.services.utils.serialization;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
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
        if(conf !=null && conf.containsKeyAnywhere(Const.TIME_EPOCH_CONF_PATH))
            parser.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, conf.getBoolean(Const.TIME_EPOCH_CONF_PATH));
        parser.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
        parser.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
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
    public <T> void addModule(String name, Class<T> tClass, SerializerMode<T> serializerMode) {

        parser.registerModule(new SimpleModule(name, Version.unknownVersion()).addSerializer(tClass, serializerMode));
    }
    @Override
    public <I,C extends I> void addModule(String name, Class<I> tInterface, Class<C> tClass) {
        parser.registerModule(new SimpleModule(name, Version.unknownVersion()).addAbstractTypeMapping(tInterface,tClass));

    }
    @Override
    public void close() {

    }
}
