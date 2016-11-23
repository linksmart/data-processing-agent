package eu.linksmart.services.utils.mqtt.broker;

import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.constants.Const;
import eu.linksmart.services.utils.function.Utils;
import org.apache.commons.lang.ArrayUtils;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import javax.net.ssl.SSLSocketFactory;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by José Ángel Carvajal on 23.09.2016 a researcher of Fraunhofer FIT.
 */
public class BrokerConfiguration {
    // id of the broker
    protected String id = UUID.randomUUID().toString();
    // alias (human readable) name of the broker
    protected String alias = "local";
    // hostname or IP of the broker
    protected String hostname = "localhost";
    // port of the broker
    protected int port = 1883;
    // secure port of the broker
    protected int securePort = 8883;
    // if the persistence is file base, otherwise is memory based
    protected boolean filePersistence = false;
    // default subscription quality of service I[0,2]
    protected int subQoS = 0;
    // default subscription quality of service I[0,2]
    protected int pubQoS = 0;
    // default retain policy
    protected boolean retainPolicy = false;
    // define security configuration, otherwise null no security
    protected BrokerSecurityConfiguration secConf = null;
    // keep alive configuration
    protected int keepAlive = 60000;
    // time out alive configuration
    protected int timeOut = 60000;
    //no. reconnect tries
    protected int noTries=10;
    //reconnect waiting time
    protected int reconnectWaitingTime=60000;
    //maximum messages can be one the messaging queue waiting to be sent
    protected int maxInFlightMessages=10;
    //version of the Mqtt protocol possible are 3.1 or 3.1.1. Default goes to 3.1.1, if fails then 3.1. Version 3 is a non-existing dummy enumeration member
    private MqttVersion version = MqttVersion.DEFAULT;

    private transient MqttConnectOptions mqttOptions = null;
    private transient static Configurator conf = Configurator.getDefaultConfig();


    public static Map<String,BrokerConfiguration> loadConfigurations() throws UnknownError{
        try {
            List aux = conf.getList(Const.BROKERS_ALIAS);
            List<String> aliases = new ArrayList<>();
            aliases.addAll(aux);

            Map<String,BrokerConfiguration> configurations = aliases.stream().collect(Collectors.toMap(i->i, BrokerConfiguration::loadConfiguration));

            return configurations;
        }catch (Exception e){
            throw new UnknownError(e.getMessage());
        }


    }

    static public BrokerConfiguration loadConfiguration(String alias){
       BrokerConfiguration brokerConfiguration = new BrokerConfiguration();

       return loadConfiguration(alias,brokerConfiguration);
    }
    static public MqttConnectOptions initMqttOptions(BrokerConfiguration brokerConf) throws InternalError, UnknownError{
        MqttConnectOptions mqttOptions;
        try {
            mqttOptions = new MqttConnectOptions();

            mqttOptions.setConnectionTimeout(brokerConf.timeOut/1000);
            mqttOptions.setKeepAliveInterval(brokerConf.keepAlive/1000);
            mqttOptions.setMqttVersion(brokerConf.version.ordinal());
            //mqttOptions.setCleanSession();
            //mqttOptions.setPassword();
            //mqttOptions.setUserName();
            //mqttOptions.setWill();

            if(brokerConf.secConf!=null) {
                SSLSocketFactory socketFactory;
                try {
                    socketFactory = Utils.getSocketFactory(brokerConf.secConf.CApath, brokerConf.secConf.clientCertificatePath,brokerConf.secConf.keyPath, brokerConf.secConf.CAPassword,brokerConf.secConf.clientCertificatePassword,brokerConf.secConf.keyPassword);
                } catch (Exception e) {
                    throw new InternalError(e);
                }
                mqttOptions.setSocketFactory(socketFactory);
            }

        }catch (Exception e){

            throw new UnknownError(e.getMessage());
        }

        return mqttOptions;
    }
    static protected BrokerConfiguration loadConfiguration(String alias, BrokerConfiguration brokerConf){
        try {
            String aux = "".equals(alias)|| alias==null ? "":"." + alias;


            brokerConf.hostname = getString(Const.DEFAULT_HOSTNAME, aux);
            brokerConf.port = getInt(Const.DEFAULT_PORT, aux);
            brokerConf.securePort = getInt(Const.DEFAULT_PORT_SECURE, aux);
            brokerConf.filePersistence = getBoolean(Const.DEFAULT_CONNECTION_PERSISTENCY, aux);
            brokerConf.pubQoS = getInt(Const.DEFAULT_PUBLISH_QOS, aux);
            brokerConf.subQoS = getInt(Const.DEFAULT_SUBSCRIPTION_QoS, aux);
            brokerConf.retainPolicy = getBoolean(Const.DEFAULT_RETAIN_POLICY, aux);
            brokerConf.noTries = getInt(Const.RECONNECTION_TRY, aux);
            brokerConf.reconnectWaitingTime = getInt(Const.RECONNECTION_MQTT_RETRY_TIME,  aux);
            brokerConf.pubQoS = getInt(Const.DEFAULT_PUBLISH_QOS, aux);
            brokerConf.timeOut = getInt(BrokerServiceConst.CONNECTION_MQTT_CONNECTION_TIMEOUT, aux);
            brokerConf.keepAlive = getInt(BrokerServiceConst.CONNECTION_MQTT_KEEP_ALIVE_TIMEOUT, aux);
            brokerConf.maxInFlightMessages = getInt(BrokerServiceConst.MAX_IN_FLIGHT, aux);
            brokerConf.version =  MqttVersion.valueOf(getString(BrokerServiceConst.MQTT_VERSION, aux));
            if ((conf.containsKey(Const.CERTIFICATE_BASE_SECURITY) ||  conf.containsKey(Const.CERTIFICATE_BASE_SECURITY + aux))&& getBoolean(Const.CERTIFICATE_BASE_SECURITY, aux)) {
                brokerConf.secConf = brokerConf.getInitSecurityConfiguration();
                brokerConf.secConf.CApath = getString(Const.CA_CERTIFICATE_PATH, aux);
                brokerConf.secConf.clientCertificatePath = getString(Const.CERTIFICATE_FILE_PATH, aux);
                brokerConf.secConf.keyPath = getString(Const.KEY_FILE_PATH, aux);
                brokerConf.secConf.CAPassword = getString(Const.CA_CERTIFICATE_PASSWORD, aux);
                brokerConf.secConf.clientCertificatePassword = getString(Const.CERTIFICATE_PASSWORD, aux);
                brokerConf.secConf.keyPassword = getString(Const.KEY_PASSWORD, aux);
            }

            return brokerConf;
        }catch (Exception e){
            throw new UnknownError(e.getMessage());
        }
    }
    static private String getString(String key, String postFix){
        if(conf.containsKey(key + postFix))
            return conf.getString(key + postFix);

        return conf.getString(key);
    }
    static private boolean getBoolean(String key, String postFix){
        if(conf.containsKey(key + postFix))
            return conf.getBoolean(key + postFix);

        return conf.getBoolean(key);
    }
    static private int getInt(String key, String postFix){
        if(conf.containsKey(key + postFix))
            return conf.getInt(key + postFix);

        return conf.getInt(key);
    }
    static public MqttClient initClient(BrokerConfiguration brokerConf) throws MqttException {
        MqttClient mqttClient;
        if (brokerConf.filePersistence)
            mqttClient = new MqttClient(Broker.getBrokerURL(brokerConf.getHostname(),brokerConf.getPort()),brokerConf.getId(),new MqttDefaultFilePersistence());
        else
            mqttClient = new MqttClient(Broker.getBrokerURL(brokerConf.getHostname(),brokerConf.getPort()),brokerConf.getId(),new MemoryPersistence());

        mqttClient.connect(initMqttOptions(brokerConf));

        return mqttClient;
    }
    protected BrokerConfiguration(){
        // nothing
    }
    public BrokerConfiguration(String alias){
        loadConfiguration(alias,this);
    }

    public BrokerConfiguration(String alias, String ID){
        this.id =ID;
        loadConfiguration(alias, this);
    }
    public MqttClient initClient() throws MqttException {
        return initClient(this);
    }
    public BrokerSecurityConfiguration getInitSecurityConfiguration(){

        if(secConf==null)
            secConf = new BrokerSecurityConfiguration();

        return secConf;

    }

    public String getId() {
        return id;
    }

    public String getAlias() {
        return alias;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public int getSecurePort() {
        return securePort;
    }

    public boolean isFilePersistence() {
        return filePersistence;
    }

    public int getSubQoS() {
        return subQoS;
    }

    public int getPubQoS() {
        return pubQoS;
    }

    public boolean isRetainPolicy() {
        return retainPolicy;
    }

    public BrokerSecurityConfiguration getSecConf() {
        return secConf;
    }

    public int getKeepAlive() {
        return keepAlive;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public int getNoTries() {
        return noTries;
    }

    public int getReconnectWaitingTime() {
        return reconnectWaitingTime;
    }

    public String getURL(){
        if(secConf!=null)
            return Broker.getSecureBrokerURL(hostname,port);

        return Broker.getBrokerURL(hostname, port);
    }
    public MqttConnectOptions getInitMqttConnectOptions(){
        if(mqttOptions==null)
            mqttOptions = initMqttOptions(this);
        return mqttOptions;
    }
    public MqttConnectOptions getMqttConnectOptions(){
        return mqttOptions;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setSecurePort(int securePort) {
        this.securePort = securePort;
    }

    public void setFilePersistence(boolean filePersistence) {
        this.filePersistence = filePersistence;
    }

    public void setSubQoS(int subQoS) {
        this.subQoS = subQoS;
    }

    public void setPubQoS(int pubQoS) {
        this.pubQoS = pubQoS;
    }

    public void setRetainPolicy(boolean retainPolicy) {
        this.retainPolicy = retainPolicy;
    }

    public void setSecConf(BrokerSecurityConfiguration secConf) {
        this.secConf = secConf;
    }

    public void setKeepAlive(int keepAlive) {
        this.keepAlive = keepAlive;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public void setNoTries(int noTries) {
        this.noTries = noTries;
    }

    public void setReconnectWaitingTime(int reconnectWaitingTime) {
        this.reconnectWaitingTime = reconnectWaitingTime;
    }
    @Override
    public boolean equals(Object o) {

        if (o == this)
            return true;
        if (o!=null && o instanceof BrokerConfiguration) {
            BrokerConfiguration aux = (BrokerConfiguration) o;
            boolean equal = aux.hostname.equals(hostname) && aux.securePort == securePort && aux.port == port && aux.filePersistence == filePersistence
                    && aux.subQoS == subQoS && aux.pubQoS == pubQoS && aux.retainPolicy == retainPolicy && aux.keepAlive == keepAlive && aux.timeOut == timeOut && aux.noTries == noTries
                    && aux.reconnectWaitingTime == reconnectWaitingTime;
            if (equal && secConf != null)
                return secConf.equals(aux.secConf);

            return equal;
        }
        return false;
    }
    @Override
    public String toString(){

        return "{" +
             //   "\"alias\":\""+alias+"\"," +
                "\"hostname\":\""+hostname+"\"," +
                "\"securePort\":\""+securePort+"\"," +
                "\"port\":\""+port+"\"," +
                "\"filePersistence\":\""+filePersistence+"\"," +
                "\"subQoS\":\""+subQoS+"\"," +
                "\"pubQoS\":\""+pubQoS+"\"," +
                "\"retainPolicy\":\""+retainPolicy+"\"," +
                "\"keepAlive\":\""+keepAlive+"\"," +
                "\"timeOut\":\""+timeOut+"\"," +
                "\"noTries\":\""+noTries+"\"," +
                "\"version\":\""+version.toString()+"\"," +
                "\"inFlightMessages\":\""+maxInFlightMessages+"\"," +
                "\"reconnectWaitingTime\":\""+reconnectWaitingTime +"\""+
                ( ( secConf != null ) ? (",\"brokerSecurityConfiguration\":"+secConf.toString() ): ("") )
                +"}";

    }
    @Override
    public int hashCode(){

        return toString().hashCode();
    }

    public class BrokerSecurityConfiguration{
        protected String CApath = "";

        protected String CAPassword = "";

        protected String clientCertificatePath = "";

        protected String clientCertificatePassword = "";

        protected String keyPath = "";

        protected String keyPassword = "";

        protected BrokerSecurityConfiguration(){
            // nothing
        }

        public String getCApath() {
            return CApath;
        }

        public String getCAPassword() {
            return CAPassword;
        }

        public String getClientCertificatePath() {
            return clientCertificatePath;
        }

        public String getClientCertificatePassword() {
            return clientCertificatePassword;
        }

        public String getKeyPath() {
            return keyPath;
        }

        public String getKeyPassword() {
            return keyPassword;
        }

        public void setCApath(String CApath) {
            this.CApath = CApath;
        }

        public void setCAPassword(String CAPassword) {
            this.CAPassword = CAPassword;
        }

        public void setClientCertificatePath(String clientCertificatePath) {
            this.clientCertificatePath = clientCertificatePath;
        }

        public void setClientCertificatePassword(String clientCertificatePassword) {
            this.clientCertificatePassword = clientCertificatePassword;
        }

        public void setKeyPath(String keyPath) {
            this.keyPath = keyPath;
        }

        public void setKeyPassword(String keyPassword) {
            this.keyPassword = keyPassword;
        }
        @Override
        public boolean equals(Object o){

            if(o==this)
                return true;
            if(o!=null && o instanceof BrokerSecurityConfiguration ) {
                BrokerSecurityConfiguration aux = (BrokerSecurityConfiguration)o;
                return aux.CApath.equals(CApath)  && aux.CAPassword.equals(CAPassword) && aux.clientCertificatePath.equals(clientCertificatePath) && aux.clientCertificatePassword.equals(clientCertificatePassword) && aux.keyPath.equals(keyPath) && aux.keyPassword.equals(keyPassword);
            }
            return false;


        }
        @Override
        public String toString(){

            return "{" +
                    "\"CApath\":\""+CApath+"\"," +
                    "\"CAPassword\":\""+CAPassword+"\"," +
                    "\"clientCertificatePath\":\""+clientCertificatePath+"\"," +
                    "\"clientCertificatePassword\":\""+clientCertificatePassword+"\"," +
                    "\"keyPath\":\""+keyPath+"\"," +
                    "\"keyPassword\":\""+keyPassword+"\"" +
                    "}";

        }
        @Override
        public int hashCode(){

            return toString().hashCode();
        }
    }
    public enum  MqttVersion{
        DEFAULT, V3,V3_1,V3_1_1;


    }

}
