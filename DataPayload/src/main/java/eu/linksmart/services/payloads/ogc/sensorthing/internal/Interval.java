package eu.linksmart.services.payloads.ogc.sensorthing.internal;

import com.fasterxml.jackson.databind.util.StdDateFormat;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.util.Date;

/**
 * Created by José Ángel Carvajal on 05.04.2016 a researcher of Fraunhofer FIT.
 */
public class Interval {
    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    protected Date start;
    protected Date end;
    private static StdDateFormat formatter = new StdDateFormat();
    public String format(){
        return formatter.format(start)+"/"+formatter.format(end);
    }
    public static Interval parse(String interval) throws IOException {
        Interval inter = new Interval();
        String[] strings = interval.split("/");
        if(strings.length!= 2) {
            throw new IOException("Expecting start and end of a interval");
        }
        inter.setStart(DatatypeConverter.parseDateTime(strings[0]).getTime());
        inter.setEnd(DatatypeConverter.parseDateTime(strings[0]).getTime());
        return inter;
    }
}
