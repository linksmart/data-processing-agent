package eu.linksmart.gc.utils.function;

import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.constants.Const;
import eu.linksmart.gc.utils.logging.LoggerService;
import eu.linksmart.gc.utils.logging.MqttLogger;
import eu.linksmart.gc.utils.mqtt.broker.StaticBroker;
import org.apache.log4j.PropertyConfigurator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PasswordFinder;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

/**
 * Created by José Ángel Carvajal on 07.08.2015 a researcher of Fraunhofer FIT.
 */
public class  Utils {
    static private DateFormat dateFormat = getDateFormat();

    static private DateFormat isoDateFormat = new SimpleDateFormat(Const.TIME_ISO_FORMAT);
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
    static public String getTimestamp(Date date){
        return dateFormat.format(date);
    }
    static public String getIsoTimestamp(Date date){
        return isoDateFormat.format(date);
    }
    static public String getDateNowString(){
        return getDateFormat().format(new Date());
    }
    static public LoggerService initDefaultLoggerService(Class lass){


        LoggerService loggerService = new LoggerService(LoggerFactory.getLogger(lass));
        try {
            Properties p = new Properties();
            try {
                p.load(new FileInputStream(Configurator.getDefaultConfig().getString(Const.LoggingDefaultLoggingFile)));
                PropertyConfigurator.configure(p);
                loggerService.info("Loading from configuration from given file");
            }catch(FileNotFoundException ex){
                try {

                    InputStream in = lass.getResourceAsStream(Configurator.getDefaultConfig().getString(Const.LoggingDefaultLoggingFile));
                    p.load(in);
                    PropertyConfigurator.configure(p);
                    loggerService.info("Loading from configuration from jar default file");
                }catch (Exception exx){
                    try {
                        InputStream in = Utils.class.getClassLoader().getResourceAsStream(Configurator.getDefaultConfig().getString(Const.LoggingDefaultLoggingFile));
                        p.load(in);
                        PropertyConfigurator.configure(p);
                        loggerService.info("Loading from configuration from jar default file");
                    }catch (Exception exxx){

                        exxx.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Configurator.getDefaultConfig().getBool(Const.LOG_ONLINE_ENABLED_CONF_PATH)) {
            try {

                loggerService.addLoggers(Utils.createMqttLogger(lass));
            } catch (Exception e) {
                loggerService.error(e.getMessage(), e);
            }
        }

        loggerService.info("Configuration file loaded");
        return loggerService;

    }
    static public Logger createMqttLogger(Class lass) throws MalformedURLException, MqttException {

        return MqttLogger.getLogger(
                lass,
                new StaticBroker(
                        Configurator.getDefaultConfig().getString(Const.LOG_OUT_BROKER_CONF_PATH),
                        Configurator.getDefaultConfig().getString(Const.LOG_OUT_BROKER_PORT_CONF_PATH)
                )
        );
    }
    public static String hashIt( String string){
        if(string == null)
            return "";
        MessageDigest SHA256 = null;
        try {
            SHA256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
        return (new BigInteger(1,SHA256.digest((string).getBytes()))).toString();
    }
    static public SSLSocketFactory getSocketFactory (final String caCrtFile, final String crtFile, final String keyFile, final String password) throws Exception
    {
        Security.addProvider(new BouncyCastleProvider());

        // load CA certificate
        PEMReader reader = new PEMReader(new InputStreamReader(new ByteArrayInputStream(Files.readAllBytes(Paths.get(caCrtFile)))));
        X509Certificate caCert = (X509Certificate)reader.readObject();
        reader.close();

        // load client certificate
        reader = new PEMReader(new InputStreamReader(new ByteArrayInputStream(Files.readAllBytes(Paths.get(crtFile)))));
        X509Certificate cert = (X509Certificate)reader.readObject();
        reader.close();

        // load client private key
        reader = new PEMReader(
                new InputStreamReader(new ByteArrayInputStream(Files.readAllBytes(Paths.get(keyFile)))),
                new PasswordFinder() {
                    public char[] getPassword() {
                        return password.toCharArray();
                    }
                }
        );
        KeyPair key = (KeyPair)reader.readObject();
        reader.close();

        // CA certificate is used to authenticate server
        KeyStore caKs = KeyStore.getInstance("JKS");
        caKs.load(null, null);
        caKs.setCertificateEntry("ca-certificate", caCert);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX");
        tmf.init(caKs);

        // client key and certificates are sent to server so it can authenticate us
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(null, null);
        ks.setCertificateEntry("certificate", cert);
        ks.setKeyEntry("private-key", key.getPrivate(), password.toCharArray(), new java.security.cert.Certificate[]{cert});
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("PKIX");
        kmf.init(ks, password.toCharArray());

        // finally, create SSL socket factory
        SSLContext context = SSLContext.getInstance("TLSv1.2");
        context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        return context.getSocketFactory();
    }
}
