package eu.linksmart.services.event.ceml.models;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import eu.linksmart.services.utils.serialization.SerializerMode;
import net.razorvine.pyro.PyroProxy;

import java.io.IOException;

/**
 * Created by devasya on 29.11.2016.
 */
public class PyroProxySerializer extends SerializerMode<PyroProxy> {

    @Override
    public void serialize(PyroProxy pyro, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        Object result = pyro.call("exportModel");
        if (result != null) {
            if (result instanceof String) {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeFieldName("result");
                jsonGenerator.writeString((String) result);
                jsonGenerator.writeEndObject();
            } else
                jsonGenerator.writeObject(result);
        }
    }
}
