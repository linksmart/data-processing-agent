package eu.linksmart.services.utils.mqtt.broker;

/**
 * Created by José Ángel Carvajal on 06.08.2015 a researcher of Fraunhofer FIT.
 */
public interface BrokerServiceConst {
    public static final String BROKERS_ALIAS = "connection.brokers.aliases";

    public static final String DEFAULT_HOSTNAME= "connection.broker.mqtt.hostname";
    public static final String DEFAULT_PORT= "connection.broker.mqtt.port";
    public static final String DEFAULT_PORT_SECURE= "connection.broker.mqtt.security.port";
    public static final String DEFAULT_CONNECTION_PERSISTENCY= "connection.broker.mqtt.enableFileConnectionPersistency";
    public static final String RECONNECTION_TRY = "connection.broker.mqtt.noReconnectTry";
    public static final String RECONNECTION_MQTT_RETRY_TIME = "connection.broker.mqtt.reconnectWaitingTime";
    public static final String CONNECTION_MQTT_KEEP_ALIVE_TIMEOUT = "connection.broker.mqtt.keepAlive";
    public static final String CONNECTION_MQTT_CONNECTION_TIMEOUT = "connection.broker.mqtt.timeOut";

    public static final String DEFAULT_PUBLISH_QOS = "messaging.client.mqtt.pub.qos";
    public static final String DEFAULT_RETAIN_POLICY= "messaging.client.mqtt.enableRetainPolicy";
    public static final String DEFAULT_SUBSCRIPTION_QoS ="messaging.client.mqtt.sub.qos";

    public static final String CA_CERTIFICATE_PATH ="connection.broker.mqtt.security.caCertificatePath";
    public static final String CERTIFICATE_FILE_PATH ="connection.broker.mqtt.security.certificatePath";
    public static final String KEY_FILE_PATH ="connection.broker.mqtt.security.keyPath";
    public static final String CERTIFICATE_BASE_SECURITY ="connection.broker.mqtt.security.certificateBaseSecurityEnabled";

    public static final String CERTIFICATE_PASSWORD = "connection.broker.mqtt.security.certificatePassword";
    public static final String KEY_PASSWORD = "connection.broker.mqtt.security.keyPassword";
    public static final String CA_CERTIFICATE_PASSWORD = "connection.broker.mqtt.security.caCertificatePassword";


    public static final String MAX_IN_FLIGHT = "messaging.client.mqtt.maxInFlightMessages";
    public static final String MQTT_VERSION = "messaging.client.mqtt.version";
}
