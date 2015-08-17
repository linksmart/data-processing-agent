package eu.linksmart.gc.utils.logging;
import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.constants.Const;
import org.slf4j.Logger;
import org.slf4j.Marker;

import java.util.ArrayList;

/**
 * Created by José Ángel Carvajal on 07.08.2015 a researcher of Fraunhofer FIT.
 */
public class LoggerService implements Logger {

    protected ArrayList<Logger> loggers;



    public synchronized void addLoggers(Logger logger) {
        loggers.add(logger);
        loggersNames+= (loggersNames.equals(""))?"":","+logger.getName();
    }
    public synchronized void addLoggers(org.apache.log4j.Logger logger) {
        loggers.add(new Logger4j(logger));
        loggersNames+= (loggersNames.equals(""))?"":","+logger.getName();
    }

    public synchronized void destroy(){
        loggers.clear();
    }

    protected String loggersNames;

    @Override
    public String getName() {
        return Configurator.getDefaultConfig().getString(Const.SERVICE_NAME_CONF_PATH)+":["+loggersNames+"]";
    }
    private void init(){

        loggers = new ArrayList<Logger>();
        loggersNames= "";
    }
    public LoggerService(){
        init();

    }
    public LoggerService(Logger logger){
        init();
        addLoggers(logger);
    }

    public LoggerService(Logger... loggers){
        init();
        for (Logger logger: loggers)
            addLoggers(logger);
    }


    @Override
    public boolean isTraceEnabled() {
        for (Logger logger : loggers)
            if(logger != null)
            return logger.isTraceEnabled();
        return false;
    }

    @Override
    public void trace(String s) {
        for (Logger logger : loggers)
            if(logger != null)
            logger.trace(s);

    }

    @Override
    public void trace(String s, Object o) {
        for (Logger logger : loggers)
            if(logger != null)
            logger.trace(s,o);
    }

    @Override
    public void trace(String s, Object o, Object o1) {
        for (Logger logger : loggers)
            if(logger != null)
            logger.trace(s,o,o1);

    }

    @Override
    public void trace(String s, Object... objects) {
        for (Logger logger : loggers)
                 if(logger != null)
            logger.trace(s,objects);

    }

    @Override
    public void trace(String s, Throwable throwable) {

        for (Logger logger : loggers)
                       if(logger != null)
            logger.trace(s,throwable);

    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        for (Logger logger : loggers)
                       if(logger != null)
            return logger.isTraceEnabled();
        return false;
    }

    @Override
    public void trace(Marker marker, String s) {

    }

    @Override
    public void trace(Marker marker, String s, Object o) {

    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1) {

    }

    @Override
    public void trace(Marker marker, String s, Object... objects) {

    }

    @Override
    public void trace(Marker marker, String s, Throwable throwable) {

    }

    @Override
    public boolean isDebugEnabled() {
        for (Logger logger : loggers)
                  if(logger != null)
            return logger.isDebugEnabled();
        return false;
    }

    @Override
    public void debug(String s) {
        for (Logger logger : loggers)
                    if(logger != null)
            logger.debug(s);

    }

    @Override
    public void debug(String s, Object o) {
        for (Logger logger : loggers)
                     if(logger != null)
            logger.debug(s, o);
    }

    @Override
    public void debug(String s, Object o, Object o1) {
        for (Logger logger : loggers)
                    if(logger != null)
            logger.debug(s, o, o1);

    }

    @Override
    public void debug(String s, Object... objects) {
        for (Logger logger : loggers)
                    if(logger != null)
            logger.debug(s, objects);

    }

    @Override
    public void debug(String s, Throwable throwable) {

        for (Logger logger : loggers)
                    if(logger != null)
            logger.debug(s, throwable);

    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        for (Logger logger : loggers)
                    if(logger != null)
            return logger.isDebugEnabled();
        return false;
    }

    @Override
    public void debug(Marker marker, String s) {

    }

    @Override
    public void debug(Marker marker, String s, Object o) {

    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1) {

    }

    @Override
    public void debug(Marker marker, String s, Object... objects) {

    }

    @Override
    public void debug(Marker marker, String s, Throwable throwable) {

    }

    @Override
    public boolean isInfoEnabled() {
        for (Logger logger : loggers)
                   if(logger != null)
            return logger.isInfoEnabled();
        return false;
    }

    @Override
    public void info(String s) {
        for (Logger logger : loggers)
            if(logger != null)
            logger.info(s);


    }

    @Override
    public void info(String s, Object o) {
        for (Logger logger : loggers)
                   if(logger != null)
            logger.info(s, o);
    }

    @Override
    public void info(String s, Object o, Object o1) {
        for (Logger logger : loggers)
                   if(logger != null)
            logger.info(s, o, o1);

    }

    @Override
    public void info(String s, Object... objects) {
        for (Logger logger : loggers)
                      if(logger != null)
            logger.info(s, objects);

    }

    @Override
    public void info(String s, Throwable throwable) {

        for (Logger logger : loggers)
                        if(logger != null)
            logger.info(s, throwable);

    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        for (Logger logger : loggers)
                      if(logger != null)
            return logger.isInfoEnabled();
        return false;
    }

    @Override
    public void info(Marker marker, String s) {

    }

    @Override
    public void info(Marker marker, String s, Object o) {

    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1) {

    }

    @Override
    public void info(Marker marker, String s, Object... objects) {

    }

    @Override
    public void info(Marker marker, String s, Throwable throwable) {

    }

    @Override
    public boolean isWarnEnabled() {
        for (Logger logger : loggers)
                   if(logger != null)
            return logger.isWarnEnabled();
        return false;
    }

    @Override
    public void warn(String s) {
        for (Logger logger : loggers)
                   if(logger != null)
            logger.warn(s);

    }

    @Override
    public void warn(String s, Object o) {
        for (Logger logger : loggers)
                   if(logger != null)
            logger.warn(s, o);
    }

    @Override
    public void warn(String s, Object o, Object o1) {
        for (Logger logger : loggers)
                   if(logger != null)
            logger.warn(s, o, o1);

    }

    @Override
    public void warn(String s, Object... objects) {
        for (Logger logger : loggers)
                    if(logger != null)
            logger.warn(s, objects);

    }

    @Override
    public void warn(String s, Throwable throwable) {

        for (Logger logger : loggers)
                    if(logger != null)
            logger.warn(s, throwable);

    }


    @Override
    public boolean isWarnEnabled(Marker marker) {
        for (Logger logger : loggers)
                    if(logger != null)
            return logger.isWarnEnabled();
        return false;
    }

    @Override
    public void warn(Marker marker, String s) {

    }

    @Override
    public void warn(Marker marker, String s, Object o) {

    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1) {

    }

    @Override
    public void warn(Marker marker, String s, Object... objects) {

    }

    @Override
    public void warn(Marker marker, String s, Throwable throwable) {

    }

    @Override
    public boolean isErrorEnabled() {
        for (Logger logger : loggers)
                 if(logger != null)
            return logger.isErrorEnabled();
        return false;
    }

    @Override
    public void error(String s) {
        for (Logger logger : loggers)
                   if(logger != null)
            logger.error(s);

    }

    @Override
    public void error(String s, Object o) {
        for (Logger logger : loggers)
                   if(logger != null)
            logger.error(s, o);
    }

    @Override
    public void error(String s, Object o, Object o1) {
        for (Logger logger : loggers)
                   if(logger != null)
            logger.error(s, o, o1);

    }

    @Override
    public void error(String s, Object... objects) {
        for (Logger logger : loggers)
                   if(logger != null)
            logger.error(s, objects);

    }

    @Override
    public void error(String s, Throwable throwable) {

        if(s ==null)
            s = throwable.getClass().getName();
        for (Logger logger : loggers)
            if(logger != null)
                logger.error(s, throwable);

    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        for (Logger logger : loggers)
             if(logger != null)
            return logger.isErrorEnabled();
        return false;
    }

    @Override
    public void error(Marker marker, String s) {

    }

    @Override
    public void error(Marker marker, String s, Object o) {

    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1) {

    }

    @Override
    public void error(Marker marker, String s, Object... objects) {

    }

    @Override
    public void error(Marker marker, String s, Throwable throwable) {

    }
}
