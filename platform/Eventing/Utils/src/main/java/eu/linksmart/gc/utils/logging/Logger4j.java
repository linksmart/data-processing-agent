package eu.linksmart.gc.utils.logging;

import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * Created by José Ángel Carvajal on 10.08.2015 a researcher of Fraunhofer FIT.
 */
public class Logger4j  implements Logger {
    protected org.apache.log4j.Logger logger;

    public Logger4j(org.apache.log4j.Logger logger){
        this.logger = logger;
    }
    @Override
    public String getName() {
        return logger.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public void trace(String s) {
        logger.trace(s);
    }

    @Override
    public void trace(String s, Object o) {
        logger.trace(s);
    }

    @Override
    public void trace(String s, Object o, Object o1) {
        logger.trace(s);
    }

    @Override
    public void trace(String s, Object... objects) {
        logger.trace(s);
    }

    @Override
    public void trace(String s, Throwable throwable) {
        logger.trace(s,throwable);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
       return false;
    }

    @Override
    public void trace(Marker marker, String s) {
        logger.error("trace message:\n "+s+"\n Because the trace is not implemented");
    }

    @Override
    public void trace(Marker marker, String s, Object o) {
        logger.error("trace message:\n "+s+"\n Because the trace is not implemented");
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1) {
        logger.error("trace message:\n "+s+"\n Because the trace is not implemented");
    }

    @Override
    public void trace(Marker marker, String s, Object... objects) {
        logger.error("trace message:\n "+s+"\n Because the trace is not implemented");
    }

    @Override
    public void trace(Marker marker, String s, Throwable throwable) {
        logger.error("trace message:\n "+s+"\n Because the trace is not implemented");
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public void debug(String s) {
        logger.debug(s);

    }

    @Override
    public void debug(String s, Object o) {
        logger.debug(s);
    }

    @Override
    public void debug(String s, Object o, Object o1) {
        logger.debug(s);

    }

    @Override
    public void debug(String s, Object... objects) {
        logger.debug(s);

    }

    @Override
    public void debug(String s, Throwable throwable) {
        logger.debug(s,throwable);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return false;
    }

    @Override
    public void debug(Marker marker, String s) {
        logger.error("debug message:\n "+s+"\n Because the debug is not implemented");
    }

    @Override
    public void debug(Marker marker, String s, Object o) {
        logger.error("debug message:\n "+s+"\n Because the debug is not implemented");
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1) {
        logger.error("debug message:\n "+s+"\n Because the debug is not implemented");
    }

    @Override
    public void debug(Marker marker, String s, Object... objects) {
        logger.error("debug message:\n "+s+"\n Because the debug is not implemented");
    }

    @Override
    public void debug(Marker marker, String s, Throwable throwable) {
        logger.error("debug message:\n "+s+"\n Because the debug is not implemented");
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public void info(String s) {
        logger.info(s);

    }

    @Override
    public void info(String s, Object o) {
        logger.info(s);
    }

    @Override
    public void info(String s, Object o, Object o1) {
        logger.info(s);
    }

    @Override
    public void info(String s, Object... objects) {
        logger.info(s);
    }

    @Override
    public void info(String s, Throwable throwable) {
        logger.info(s,throwable);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return false;
    }

    @Override
    public void info(Marker marker, String s) {
        logger.error("Info message:\n "+s+"\n Because the info is not implemented");
    }

    @Override
    public void info(Marker marker, String s, Object o) {
        logger.error("Info message:\n "+s+"\n Because the info is not implemented");
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1) {
        logger.error("Info message:\n "+s+"\n Because the info is not implemented");
    }

    @Override
    public void info(Marker marker, String s, Object... objects) {
        logger.error("Info message:\n "+s+"\n Because the info is not implemented");
    }

    @Override
    public void info(Marker marker, String s, Throwable throwable) {
        logger.error("Info message:\n "+s+"\n Because the info is not implemented");
    }

    @Override
    public boolean isWarnEnabled() {
        return true;

    }

    @Override
    public void warn(String s) {

        logger.warn(s);
    }

    @Override
    public void warn(String s, Object o) {

        logger.warn(s);
    }

    @Override
    public void warn(String s, Object... objects) {

        logger.warn(s);
    }

    @Override
    public void warn(String s, Object o, Object o1) {

        logger.warn(s);
    }

    @Override
    public void warn(String s, Throwable throwable) {

        logger.warn(s,throwable);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return false;
    }

    @Override
    public void warn(Marker marker, String s) {
        logger.error("warn message:\n "+s+"\n Because the warn is not implemented");
    }

    @Override
    public void warn(Marker marker, String s, Object o) {
        logger.error("warn message:\n "+s+"\n Because the warn is not implemented");
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1) {
        logger.error("warn message:\n "+s+"\n Because the warn is not implemented");
    }

    @Override
    public void warn(Marker marker, String s, Object... objects) {
        logger.error("warn message:\n "+s+"\n Because the warn is not implemented");
    }

    @Override
    public void warn(Marker marker, String s, Throwable throwable) {
        logger.error("warn message:\n "+s+"\n Because the warn is not implemented");
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    @Override
    public void error(String s) {

        logger.error(s);
    }

    @Override
    public void error(String s, Object o) {

        logger.error(s);
    }

    @Override
    public void error(String s, Object o, Object o1) {

        logger.error(s);
    }

    @Override
    public void error(String s, Object... objects) {

        logger.error(s);
    }

    @Override
    public void error(String s, Throwable throwable) {

        logger.error(s,throwable);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return false;
    }

    @Override
    public void error(Marker marker, String s) {
        logger.error("error message:\n "+s+"\n Because the error is not implemented");
    }

    @Override
    public void error(Marker marker, String s, Object o) {
        logger.error("error message:\n "+s+"\n Because the error is not implemented");
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1) {
        logger.error("error message:\n "+s+"\n Because the error is not implemented");
    }

    @Override
    public void error(Marker marker, String s, Object... objects) {
        logger.error("error message:\n "+s+"\n Because the error is not implemented");
    }

    @Override
    public void error(Marker marker, String s, Throwable throwable) {
        logger.error("error message:\n "+s+"\n Because the error is not implemented");
    }
}
