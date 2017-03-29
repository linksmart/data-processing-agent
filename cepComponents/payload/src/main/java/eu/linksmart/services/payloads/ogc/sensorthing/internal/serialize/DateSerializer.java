package eu.linksmart.services.payloads.ogc.sensorthing.internal.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.util.StdDateFormat;

import java.io.IOException;
import java.util.Date;

public class DateSerializer extends JsonSerializer<Date> {
    private static StdDateFormat formatter = new StdDateFormat();

    public DateSerializer() {
        super();
    }

    @Override
    public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeString(formatter.format(date));
    }
}