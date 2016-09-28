package eu.linksmart.services.event.handler;

/**
 * Created by José Ángel Carvajal on 13.08.2015 a researcher of Fraunhofer FIT.
 */
public interface HandlerConst {
    public static final String EVENT_OUT_TOPIC_CONF_PATH = "api.events.mqtt.topic.outgoing";

    public static final String EVENTS_OUT_HTTP_SERVERS_ALIASES_CONF_PATH ="EVENTS_OUT_HTTP_SERVERS_ALIASES";
    public static final String EVENTS_OUT_HTTP_SERVERS_CONF_PATH ="EVENTS_OUT_HTTP_SERVERS";
    public static final String EVENTS_OUT_HTTP_SERVERS_PORT_CONF_PATH ="EVENTS_OUT_HTTP_SERVERS_PORTS";
    public static final String AGGREGATE_EVENTS_CONF = "handler.events.output.aggregation";
}
