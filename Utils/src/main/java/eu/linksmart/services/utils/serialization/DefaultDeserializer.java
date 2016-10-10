package eu.linksmart.services.utils.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import eu.linksmart.services.utils.function.Utils;

import java.io.IOException;

/**
 * Created by José Ángel Carvajal on 01.09.2016 a researcher of Fraunhofer FIT.
 */
public class DefaultDeserializer implements Deserializer{
    private ObjectMapper mapper = new ObjectMapper();
    public DefaultDeserializer(){
        mapper.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
        mapper.setDateFormat(Utils.getDateFormat());
        mapper.setTimeZone(Utils.getTimeZone());
    }

    @Override
    public <T> T parse(String string, Class<T> tClass) throws IOException {
        return mapper.readValue(string, tClass);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> tClass) throws IOException {
        return mapper.readValue(bytes, tClass);
    }

    @Override
    public <I,C extends I> boolean defineClassToInterface(Class<I> tInterface,Class<C> tClass ) {
        mapper.registerModule(new SimpleModule(tInterface.getName(), Version.unknownVersion()).addAbstractTypeMapping(tInterface, tClass));
        return false;
    }

    @Override
    public void close() {

    }
}
