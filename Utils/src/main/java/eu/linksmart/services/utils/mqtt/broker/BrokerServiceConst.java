package eu.linksmart.services.utils.mqtt.broker;

/**
 * Created by José Ángel Carvajal on 06.08.2015 a researcher of Fraunhofer FIT.
 */
public interface BrokerServiceConst {
    public static final String MULTI_CONNECTION = "connection_brokers_service_connection_multiple";

    public static final String BROKERS_ALIAS = "connection_brokers_aliases";

    public static final String DEFAULT_HOSTNAME= "connection_broker_mqtt_hostname";
    public static final String DEFAULT_PORT= "connection_broker_mqtt_port";
    public static final String DEFAULT_PORT_SECURE= "connection_broker_mqtt_security_port";
    public static final String DEFAULT_CONNECTION_PERSISTENCY= "connection_broker_mqtt_enableFileConnectionPersistency";
    public static final String RECONNECTION_TRY = "connection_broker_mqtt_noReconnectTry";
    public static final String RECONNECTION_MQTT_RETRY_TIME = "connection_broker_mqtt_reconnectWaitingTime";
    public static final String CONNECTION_MQTT_KEEP_ALIVE_TIMEOUT = "connection_broker_mqtt_keepAlive";
    public static final String CONNECTION_MQTT_CONNECTION_TIMEOUT = "connection_broker_mqtt_timeOut";

    public static final String DEFAULT_PUBLISH_QOS = "messaging_client_mqtt_pub_qos";
    public static final String DEFAULT_RETAIN_POLICY= "messaging_client_mqtt_enableRetainPolicy";
    public static final String DEFAULT_SUBSCRIPTION_QoS ="messaging_client_mqtt_sub_qos";
    public static final String CLEAN_SESSION = "messaging_client_mqtt_session_clean_enabled";

    public static final String CA_CERTIFICATE_PATH ="connection_broker_mqtt_security_caCertificatePath";
    public static final String CERTIFICATE_FILE_PATH ="connection_broker_mqtt_security_certificatePath";
    public static final String KEY_FILE_PATH ="connection_broker_mqtt_security_keyPath";
    public static final String CERTIFICATE_BASE_SECURITY ="connection_broker_mqtt_security_certificateBaseSecurityEnabled";
    public static final String CERTIFICATE_PASSWORD = "connection_broker_mqtt_security_certificatePassword";
    public static final String KEY_PASSWORD = "connection_broker_mqtt_security_keyPassword";
    public static final String CA_CERTIFICATE_PASSWORD = "connection_broker_mqtt_security_caCertificatePassword";


    public static final String MAX_IN_FLIGHT = "messaging_client_mqtt_maxInFlightMessages";
    public static final String MQTT_VERSION = "messaging_client_mqtt_version";
    public static final String AUTOMATIC_RECONNECT = "messaging_client_mqtt_automaticReconnect";

    public static final String USER = "messaging_client_mqtt_security_user";
    public static final String PASSWORD = "messaging_client_mqtt_security_password";
}
