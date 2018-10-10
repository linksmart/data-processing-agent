package eu.linksmart.services.event.handler;

/**
 * Created by José Ángel Carvajal on 13.08.2015 a researcher of Fraunhofer FIT.
 */
public interface HandlerConst {
    String EVENT_OUT_TOPIC_CONF_PATH = "api_events_mqtt_topic_outgoing";

    String EVENTS_OUT_HTTP_SERVERS_ALIASES_CONF_PATH ="connection_hosts_rest_aliases";
    String EVENTS_OUT_HTTP_SERVERS_CONF_PATH ="connection_host_rest_endpoint";
    String AGGREGATE_EVENTS_CONF = "handler_events_output_aggregation";
}
