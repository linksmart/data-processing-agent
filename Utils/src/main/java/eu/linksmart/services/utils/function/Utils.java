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
 *  Copyright [2013] [Fraunhofer-Gesellschaft]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */
/**
 * Provide a set of commonly needed functions. The idea of this Utility class is to centralized all code is used everyplace but not belongs specially in any place.
 *
 * @author Jose Angel Carvajal Soto
 * @since  1.0.0
 *
 * */
public class  Utils {
    static private DateFormat dateFormat = getDateFormat();
    static private DateFormat isoDateFormat = new SimpleDateFormat(Const.TIME_ISO_FORMAT);
    /**
     * Provides the version of the software. The version is linked to the pom version.
     * @return the pom version
     * */
    public static synchronized String getVersion() {
        final Properties properties = new Properties();
        try {
            properties.load(Utils.class.getClassLoader().getResourceAsStream("ver.properties"));
            return properties.getProperty("version");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    /**
     * Provide a quick method to get a data DateFormat. The DateFormat is created using the default values of the conf file if any,
     * otherwise use the hardcoded in the const.
     * @return a default DateFormat
     * */
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
    /**
     * Provide a quick method to get a data TimeZone. The TimeZone is created using the default values of the conf file if any,
     * otherwise use UTC timezone.
     * @return a default TimeZone
     * */
    static public TimeZone getTimeZone(){
        String tzs = Configurator.getDefaultConfig().getString(Const.TIME_TIMEZONE_CONF_PATH);
        if(tzs == null || tzs.equals(""))
            tzs = "UTC";

        return TimeZone.getTimeZone(tzs);
    }
    /**
     * Provide a quick method to transform a Date as String. The String is constructed using the DateFormat obtained by getDateFormat()
     * @param date to transform into a string
     * @return a Date as String
     * */
    static public String getTimestamp(Date date){
        return dateFormat.format(date);
    }
    /**
     * Provide a quick method to transform a Date into timestamp as String. The String is constructed using a DateFormat based on the standard iso 8601
     * @param date to transform into a string
     * @return a Date as String timestamp base in iso 8601
     * */
    static public String getIsoTimestamp(Date date){
        return isoDateFormat.format(date);
    }
    /**
     * Provide a quick method to get a current time as String. The String is constructed using getTimestamp()
     * @return current Date as a String timestamp
     * */
    static public String getDateNowString(){
        return getDateFormat().format(new Date());
    }
    static boolean isLoggingConfLoaded =false;
    /**
     * Provide a quick method to get the hash value of a string as a string. The function is using SH-256 to hash
     * @param string to hash
     * @return the hexadecimal value as string
     * */
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
        return new BigInteger(1,SHA256.digest((string).getBytes())).toString(16);
    }
    // TODO: No Unit test. Not sure how
    /**
     * Provide a default method and unique method to get the logging service regardless of the implementation. Additionally, for the reloading of the logging  configuration
     * @param lass is the class which want to load the logging service
     * @return the logging service
     * */
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
                } else if (Configurator.getDefaultConfig().getString(Const.LoggingDefaultLoggingFile)!=null&&isResource(Configurator.getDefaultConfig().getString(Const.LoggingDefaultLoggingFile))) {
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

        loggerService.info("Logging configuration file had been initialized");
        return loggerService;

    }
    // TODO: No Unit test
    /**
     * Provide a quick method to force the reloading of the logging service infrastructure using initLoggingConf(Utils.class)
     * */
    public static void initLoggingConf(){
        initLoggingConf(Utils.class);

    }

    // TODO: No Unit test
    /**
     * Provide a quick method to construct a SSLSocketFactory which is a TCP socket using TLS/SSL
     * @param caCrtFile location of the CA certificate
     * @param crtFile location of the client certificate
     * @param keyFile location of the key file
     * @param caPassword password to access the CA certificate file
     * @param crtPassword password to access the client certificate file
     * @param keyPassword password to access the key file
     * @return the SSLSocketFactory to create secure sockets with the provided certificates infrastructure
     * @exception java.lang.Exception in case of something wrong happens
     * */
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
    /**
     * Provide a quick method to find out if file exists (in the filesystem or as JAR resource)
     * @param filename the name of the file to check
     * @return true if the file exists
     * */
    public static boolean fileExists(String filename){

        return isFile(filename)||isResource(filename);
    }
    /**
     * Provide a quick method to find out if file exists in the file system
     * @param filename the name of the file to check
     * @return true if the file exists
     * */
    public static boolean isFile(String filename){
        if (filename==null)
            return false;
        File f = new File(filename);
        return (f.exists() && !f.isDirectory());
    }
    /**
     * Provide a quick method to find out if file exists in the current JAR
     * @param filename the name of the file to check
     * @return true if the file exists
     * */
    public static boolean isResource(String filename){

        return   Utils.class.getClassLoader().getResource(filename)!=null;
    }
    /**
     * Provide a quick method to find out if file exists in the JAR of the class loader of the Class clazz
     * @param filename the name of the file to check
     * @param clazz JAR where the file should be located
     * @return true if the file exists
     * */
    public static boolean isResource(String filename,Class clazz) {
        return !(filename == null || clazz == null) && clazz.getClassLoader().getResource(filename) != null;
    }
    /**
     * Provide a quick method to construct a property object out of a file name
     * @param source the name and location of the property file
     * @return the property object if the file exist
     * @exception IOException if the source file do not exist
     * */
    public static Properties createPropertyFiles(String source) throws IOException{
        Properties properties = new Properties();
        if(isFile(source))
            properties.load(new FileInputStream(Configurator.getDefaultConfig().getString(Const.LoggingDefaultLoggingFile)));
        else
            properties.load(Utils.class.getClassLoader().getResourceAsStream(source));

        return properties;
    }
}
