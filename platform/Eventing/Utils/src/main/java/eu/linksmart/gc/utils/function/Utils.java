package eu.linksmart.gc.utils.function;

import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.constants.Const;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by José Ángel Carvajal on 07.08.2015 a researcher of Fraunhofer FIT.
 */
public class  Utils {
    static public DateFormat getDateFormat(){
        DateFormat dateFormat;
        String tzs = Configurator.getDefaultConfig().getString(Const.TIME_TIMEZONE_CONF_PATH);
        if(tzs == null || tzs.equals(""))
            tzs = "UTC";
        TimeZone tz = TimeZone.getTimeZone(tzs);
        if(Configurator.getDefaultConfig().getString(Const.TIME_FORMAT_CONF_PATH) == null)

            dateFormat= new SimpleDateFormat(Const.TIME_ISO_FORMAT);

        else
             dateFormat =new SimpleDateFormat(Const.TIME_ISO_FORMAT);

        dateFormat.setTimeZone(tz);

        return dateFormat;

    }
    static public String getDateNowString(){
        return getDateFormat().format(new Date());
    }
}
