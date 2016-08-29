package eu.linksmart.gc.utils.logging;

import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.constants.Const;
import eu.linksmart.gc.utils.function.Utils;
import eu.linksmart.gc.utils.mqtt.broker.StaticBroker;
import eu.linksmart.gc.utils.mqtt.broker.StaticBrokerService;


import java.util.Hashtable;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 07.08.2015 a researcher of Fraunhofer FIT.
 */
public class MqttLogger {/* implements Logger {

    protected String name;
    protected String topic;
    protected StaticBroker brokerService;
    protected static Map<String,Logger> loggers = new Hashtable<String, Logger>();
    protected Configurator conf = Configurator.getDefaultConfig();
    protected MqttLogger(Class name, StaticBroker brokerService) {
        this(name.getCanonicalName(),brokerService);
    }
    protected MqttLogger(String name, StaticBroker brokerService) {
        this.name = name;
        topic = name.replace(".","/");
        this.brokerService = brokerService;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        brokerService.destroy();
    }

    static public Logger getLogger(String name, StaticBroker brokerService) {


        if (!loggers.containsKey(name)) {
            MqttLogger logger = new MqttLogger(name, brokerService);
            loggers.put(name,logger );
        }
        return loggers.get(name);
    }

    protected String format(Object... objects){

        String ret = "";
        for(Object o: objects)
            if(o!=null)
                ret+=addColumn(Utils.getDateNowString(),o.toString());
        return ret;


    }
    protected String getTopic(String channel){
       return topic+"/"+channel.toUpperCase()+"/Thread-"+Thread.currentThread().getId();
    }
    protected String addColumn(String... columns){
        String string ="";
        for(String s:columns)
            string+= "\t|"+s;
        return string;
    }
    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isTraceEnabled() {
        return conf.getBoolean(Const.TRACE_LOG_CONF_PATH);
    }

    @Override
    public void trace(String s) {

    }

    @Override
    public void trace(String s, Object o) {

    }

    @Override
    public void trace(String s, Object o, Object o1) {

    }

    @Override
    public void trace(String s, Object... objects) {

    }

    @Override
    public void trace(String s, Throwable throwable) {

    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
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
        return  conf.getBoolean(Const.DEBUG_LOG_CONF_PATH);
    }

    @Override
    public void debug(String s) {

        try {
            publish( getTopic("DEBUG"),format(s).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void debug(String s, Object o) {
        try {
            publish(getTopic("DEBUG"), format(s, o).getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void debug(String s, Object o, Object o1) {
        try {
            publish(getTopic("DEBUG"),format(s,o,o1).getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void debug(String s, Object... objects) {
        try {
            publish(getTopic("DEBUG"),format(objects).getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void debug(String s, Throwable throwable) {
        try {
            publish(getTopic("DEBUG"),format(s,throwable.getLocalizedMessage()).getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
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
        return conf.getBoolean(Const.INFO_LOG_CONF_PATH);
    }

    @Override
    public void info(String s) {
        try {
            publish(getTopic("INFO"), format(s).getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void info(String s, Object o) {
        try {
            publish(getTopic("info"),format(s, o.toString()).getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void info(String s, Object o, Object o1) {
        try {
            publish(getTopic("info"),format(s, format(o, o1)).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void info(String s, Object... objects) {
        try {
            publish(getTopic("info"),format(s, format(objects)).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void info(String s, Throwable throwable) {
        try {
            publish(getTopic("info"),format(s, throwable.getLocalizedMessage()).getBytes());
           } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
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
        return false;
    }

    @Override
    public void warn(String s) {
        try {
            publish(getTopic("warn"),format(s).getBytes());
           } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void warn(String s, Object o) {
        try {
            publish(getTopic("warn"),format(s,o).getBytes());
            } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void warn(String s, Object... objects) {
        try {
            publish(getTopic("warn"),format(s,objects).getBytes());
            } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void warn(String s, Object o, Object o1) {
        try {
            publish(getTopic("warn"),format(s,o,o1).getBytes());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void warn(String s, Throwable throwable) {
        try {
            publish(getTopic("warn"),format(s,throwable.getLocalizedMessage()).getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
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
        return conf.getBoolean(Const.ERROR_LOG_CONF_PATH);
    }

    @Override
    public void error(String s) {
        try {
            publish(getTopic("error"),format(s).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void error(String s, Object o) {
        try {
            publish(getTopic("error"),format(s,o).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void error(String s, Object o, Object o1) {
        try {
            publish(getTopic("error"),format(s,o,o1).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void error(String s, Object... objects) {
        try {
            publish(getTopic("error"),format(s,objects).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void error(String s, Throwable throwable) {
        try {
            publish(getTopic("error"),format(s,throwable.getLocalizedMessage()).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
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
    protected void publish(String topic, byte[] payload) throws Exception {
        brokerService.publish(
                topic,
                payload,
                conf.getInt(Const.SERVICE_MQTT_QOS_CONF_PATH),
                conf.getBoolean(Const.SERVICE_MQTT_RETAIN_POLICY_CONF_PATH)
                );

    }*/
}
