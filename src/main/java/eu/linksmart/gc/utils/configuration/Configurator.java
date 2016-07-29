package eu.linksmart.gc.utils.configuration;


import eu.linksmart.gc.utils.function.Utils;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;

import javax.naming.ConfigurationException;
import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by José Ángel Carvajal on 06.08.2015 a researcher of Fraunhofer FIT.
 */
public class Configurator extends ConfigurationConst {

    static protected Configurator def= init();

    static public Configurator getDefaultConfig(){

            for(String confFile: ConfigurationConst.DEFAULT_CONFIGURATION_FILE)
                if(confFile!=null)
                    if(!DEFAULT_INTERN_CONFIGURATION_FILE.equals(confFile))
                        def.appendConfig(new Configurator(confFile));


        return def;
    }
    static public boolean addConfFile(String filePath) {
        return !DEFAULT_CONFIGURATION_FILE.contains(filePath) && DEFAULT_CONFIGURATION_FILE.add(filePath);
    }
    static protected void setDefaultConfig(Configurator configurator){
         def= configurator;
    }
    static protected Configurator appendDefaultConfig(Configurator configurator){

        appendConfig(def,configurator);

        return def;

    }
    static protected void appendConfig( Configurator conf, Configurator configurator){

        Iterator<String> stringIterator = configurator.config.getKeys();
        for(String key = null ;stringIterator.hasNext(); key = stringIterator.next() ) {
            if(key!=null )
                if (!conf.config.containsKey(key)){
                    conf.config.addProperty(key, configurator.config.getProperty(key));
                }else
                    conf.config.setProperty(key, configurator.config.getProperty(key));

        }

    }
     public Configurator appendConfig(Configurator configurator){


         appendConfig(this,configurator);

         return this;

    }
    static protected Configurator init(){
        Configurator configurator = new Configurator();


        for(String confFile: DEFAULT_CONFIGURATION_FILE)
            if(!DEFAULT_INTERN_CONFIGURATION_FILE.equals(confFile))
                if(confFile!=null)
                    configurator.appendConfig(new Configurator(confFile));


        return configurator;

    }
    protected Configurator() {
        this(DEFAULT_INTERN_CONFIGURATION_FILE);

    }
    protected Configurator(String configurationFile) {


        try
        {

             config = new CompositeConfiguration();
            config.addConfiguration(new PropertiesConfiguration(configurationFile));


        }
        catch(Exception cex)
        {
            try
            {

                config = new CompositeConfiguration();
                config.addConfiguration(new PropertiesConfiguration("etc/"+configurationFile));


            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
    private CompositeConfiguration config = null;


    public  Object get(String key){

        return  config.getProperty(key);

    }
    public  int getInt(String key){

        return  config.getInt(key);

    }
    public  double getDouble(String key){

        return  config.getDouble(key);

    }
    public  String getString(String key){

        return  config.getString(key);

    }
    public  boolean getBool(String key){

        return  config.getBoolean(key);

    }

    public Date getDate(String key){

        try {
            return Utils.getDateFormat().parse( config.getString(key));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }
    public List getList(String key){

        return config.getList(key);


    }
    public boolean containsKey(String key){
        return config.containsKey(key);
    }
}
