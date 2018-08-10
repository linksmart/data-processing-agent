package eu.linksmart.services.payloads.ogc.sensorthing.internal.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import java.text.DateFormat;
import eu.linksmart.services.utils.function.Utils;

import java.io.IOException;
import java.util.Date;

public class DateSerializer extends JsonSerializer<Date> {
    private static DateFormat formatter = Utils.isoFormatMSWTZ;

    public DateSerializer() {
        super();
    }

    @Override
    public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        formatter.setTimeZone(Utils.getTimeZone());
        jsonGenerator.writeString(formatter.format(date));
    }
}