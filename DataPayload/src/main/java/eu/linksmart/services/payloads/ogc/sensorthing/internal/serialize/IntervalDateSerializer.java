package eu.linksmart.services.payloads.ogc.sensorthing.internal.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.Interval;


import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class IntervalDateSerializer extends JsonSerializer<Interval> {


    public IntervalDateSerializer() {
        super();
    }

    @Override
    public void serialize(Interval interval, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeString(interval.format());
    }
}