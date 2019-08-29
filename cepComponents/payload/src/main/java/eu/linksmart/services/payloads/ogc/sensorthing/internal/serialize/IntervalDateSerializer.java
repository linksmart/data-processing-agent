package eu.linksmart.services.payloads.ogc.sensorthing.internal.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.Interval;


import java.io.IOException;

public class IntervalDateSerializer extends JsonSerializer<Interval> {


    public IntervalDateSerializer() {
        super();
    }

    @Override
    public void serialize(Interval interval, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(interval.format());
    }
}