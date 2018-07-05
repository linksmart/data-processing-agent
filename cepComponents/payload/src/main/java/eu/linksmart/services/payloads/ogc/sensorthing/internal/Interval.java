package eu.linksmart.services.payloads.ogc.sensorthing.internal;

import com.fasterxml.jackson.databind.util.StdDateFormat;
import eu.linksmart.services.utils.function.Utils;
//import org.apache.sis.io.IO;

//import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.time.temporal.TemporalAccessor;
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

    protected Date start = new Date();
    protected Date end=null;
    private static StdDateFormat formatter = new StdDateFormat();
    public String format(){
        if(end==null)
            return formatter.format(start);
        return formatter.format(start)+"/"+formatter.format(end);
    }
    public static Interval parse(String interval) throws IOException {
        Interval inter = new Interval();
        String[] strings = interval.split("/");
        if(strings.length== 0 || strings.length> 2) {
            throw new IOException("Expecting start and end of a interval");
        }



        inter.setStart(Utils.formISO8601(strings[0]));

        if(strings.length==2)
            inter.setEnd(Utils.formISO8601(strings[0]));
        return inter;
    }

}
