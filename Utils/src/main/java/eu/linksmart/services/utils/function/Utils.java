package eu.linksmart.services.utils.function;

import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.constants.Const;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
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
        TimeZone tz = getTimeZone();
        if(Configurator.getDefaultConfig().getString(Const.TIME_FORMAT_CONF_PATH) == null)

            dateFormat= new SimpleDateFormat(Const.TIME_ISO_FORMAT);

        else
             dateFormat =new SimpleDateFormat(Configurator.getDefaultConfig().getString(Const.TIME_FORMAT_CONF_PATH) );

        dateFormat.setTimeZone(tz);

        return dateFormat;

    }
    static public TimeZone getTimeZone(){
        String tzs = Configurator.getDefaultConfig().getString(Const.TIME_TIMEZONE_CONF_PATH);
        if(tzs == null || tzs.equals(""))
            tzs = "UTC";

        return TimeZone.getTimeZone(tzs);
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
    static boolean isLoggingConfLoaded =false;
   /* static public Logger createMqttLogger(Class lass) throws MalformedURLException, MqttException {

        return MqttLogger.getLogger(
                lass.getName(),
                new StaticBroker(
                        Configurator.getDefaultConfig().getString(Const.LOG_OUT_BROKER_CONF_PATH),
                        Configurator.getDefaultConfig().getString(Const.LOG_OUT_BROKER_PORT_CONF_PATH)
                )
        );
    }*/
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
    public static Logger initLoggingConf(Class lass){
        Logger loggerService = null;
        try {
            Properties p = new Properties();
            String message=null;

            if(!isLoggingConfLoaded) {

                if (isFile(Configurator.getDefaultConfig().getString(Const.LoggingDefaultLoggingFile))) {
                    //loading from file system
                    final FileInputStream configStream = new FileInputStream(Configurator.getDefaultConfig().getString(Const.LoggingDefaultLoggingFile));
                    p.load(configStream);
                    PropertyConfigurator.configure(p);
                    configStream.close();
                    System.setProperty("log4j.configuration", Configurator.getDefaultConfig().getString(Const.LoggingDefaultLoggingFile));
                    message = "Loading from configuration from given file";
                } else if (isResource(Configurator.getDefaultConfig().getString(Const.LoggingDefaultLoggingFile), lass)) {
                    //loading from class resource file
                    InputStream in = lass.getClassLoader().getResourceAsStream(Configurator.getDefaultConfig().getString(Const.LoggingDefaultLoggingFile));
                    p.load(in);
                    PropertyConfigurator.configure(p);
                    in.close();
                    message = "Loading from configuration from jar default file";
                } else if (isResource(Configurator.getDefaultConfig().getString(Const.LoggingDefaultLoggingFile))) {
                    //loading from Utils class resource file
                    InputStream in = Utils.class.getClassLoader().getResourceAsStream(Configurator.getDefaultConfig().getString(Const.LoggingDefaultLoggingFile));
                    p.load(in);
                    System.setProperty("log4j.configuration", Configurator.getDefaultConfig().getString(Const.LoggingDefaultLoggingFile));
                    PropertyConfigurator.configure(p);
                    in.close();
                    message = "Loading from configuration from Utils jar default file (last resort!)";
                } else //not loading any configuration file
                    message="No logging configuration file found!";

                loggerService = LoggerFactory.getLogger(lass);
                loggerService.info(message);
                isLoggingConfLoaded =true;
            }else {
                loggerService = LoggerFactory.getLogger(lass);
                loggerService.debug("Ignoring reloading of logging configuration file because has bean already loaded");
            }
        }catch (Exception e) {
            e.printStackTrace();
            return null;

        }


/*
        if (Configurator.getDefaultConfig().getBoolean(Const.LOG_ONLINE_ENABLED_CONF_PATH)) {
            try {
                //loggerService.addLoggers(Utils.createMqttLogger(lass));
            } catch (Exception e) {
                loggerService.error(e.getMessage(), e);
            }
        }*/
        loggerService.info("Logging configuration file had been initialized");
        return loggerService;

    }
    public static void initLoggingConf(){
        initLoggingConf(Utils.class);

    }
    static public SSLSocketFactory getSocketFactory (final String caCrtFile, final String crtFile, final String keyFile, final String caPassword, final String crtPassword, final String keyPassword) throws Exception
    {

        // todo check if the CA needs or can use the password
        final FileInputStream crtStream = new FileInputStream(crtFile);
        final FileInputStream keyStream = new FileInputStream(keyFile);
        // CA certificate is used to authenticate server
        final KeyStore caKs = KeyStore.getInstance("JKS");
        caKs.load(crtStream, crtPassword.toCharArray());
        final TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX");
        tmf.init(caKs);

        crtStream.close();

        // client key and certificates are sent to server so it can authenticate us
        final KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(keyStream, keyPassword.toCharArray());
        final KeyManagerFactory kmf = KeyManagerFactory.getInstance("PKIX");
        kmf.init(ks, keyPassword.toCharArray());

        keyStream.close();

        // finally, create SSL socket factory
        final SSLContext context = SSLContext.getInstance("TLSv1.2");
        context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        return context.getSocketFactory();
    }
    public static boolean fileExists(String filename){

        return isFile(filename)||isResource(filename);
    }

    public static boolean isFile(String filename){
        File f = new File(filename);
        return (f.exists() && f.isDirectory());
    }
    public static boolean isResource(String filename){

        return   Utils.class.getClassLoader().getResource(filename)!=null;
    }

    public static boolean isResource(String filename,Class clazz){

        return   clazz.getClassLoader().getResource(filename)!=null;
    }
}
