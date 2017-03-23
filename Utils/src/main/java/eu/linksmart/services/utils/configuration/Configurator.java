package eu.linksmart.services.utils.configuration;


import eu.linksmart.services.utils.function.Utils;
import org.apache.commons.configuration2.*;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.combined.CombinedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.tree.MergeCombiner;
import org.apache.commons.configuration2.tree.NodeCombiner;
import org.apache.commons.configuration2.tree.OverrideCombiner;


import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.text.ParseException;
import java.util.*;

/**
 * Created by José Ángel Carvajal on 06.08.2015 a researcher of Fraunhofer FIT.
 */
public class Configurator extends CombinedConfiguration {

    static final protected Configurator def= init();


    static public synchronized Configurator getDefaultConfig(){

       if( def.loadedFiles.stream().allMatch(ConfigurationConst.DEFAULT_CONFIGURATION_FILE::equals) )
           return def;
        else
            ConfigurationConst.DEFAULT_CONFIGURATION_FILE.stream().filter(confFile -> confFile != null).forEach(Configurator::addConfFile);


        return def;
    }

    static public synchronized boolean addConfFile(String filePath) {
     
        ConfigurationConst.DEFAULT_CONFIGURATION_FILE.add(filePath);

        return def.addConfigurationFile(filePath);
    }
    public synchronized boolean addConfigurationFile(String filePath) {
        if(!fileExists(filePath))
            return false;
        if(!loadedFiles.contains(filePath))
            this.addConfiguration(new Configurator(filePath));

       return true;
    }

    static protected Configurator init(){
        Configurator configurator = new Configurator();

        ConfigurationConst.DEFAULT_CONFIGURATION_FILE.stream().filter(confFile -> !ConfigurationConst.DEFAULT_DIRECTORY_CONFIGURATION_FILE.equals(confFile)).filter(confFile -> confFile != null).forEach(confFile -> configurator.append(new Configurator(confFile)));

        return configurator;

    }
    protected Configurator() {
        this(ConfigurationConst.DEFAULT_DIRECTORY_CONFIGURATION_FILE);

    }
    private HashSet<String> loadedFiles = new HashSet<>();


    public Configurator(String configurationFile) {
        super();


        String filename= configurationFile,extension=null;


        if(!fileExists(filename)) {
            System.err.println("File named " + configurationFile + " was not found!'");
            return;
        }
        if(filename!=null){
            int i = filename.lastIndexOf('.');
            if (i > 0) {
                extension = filename.substring(i + 1);
                try {
                    switch (extension) {
                        case "properties":
                        case "cfg":
                            addConfiguration(factoryBuilder(filename).getConfiguration());
                            break;
                        case "xml":
                            try {
                                addConfiguration(factoryBuilder(filename, XMLPropertiesConfiguration.class).getConfiguration());
                            } catch (Exception e) {
                                addConfiguration(factoryBuilder(filename, XMLConfiguration.class).getConfiguration());
                            }
                            break;
                        default:
                            System.err.println("Not known extension of the configuration file trying to load as property file");
                            addConfiguration(factoryBuilder(filename).getConfiguration());
                            break;
                    }
                    loadedFiles.add(filename);
                } catch (Exception e) {

                    e.printStackTrace();

                }
            }else
                System.err.println("File named " + configurationFile + " doesn't have an extension");
        }
    }



    private static boolean fileExists(String filename){
        File f = new File(filename);
        URL u = Utils.class.getClassLoader().getResource(filename);
        return (f.exists() && !f.isDirectory())|| u!=null;
    }

    public Date getDate(String key){

        try {
            return Utils.getDateFormat().parse( getString(key));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    static public  FileBasedConfigurationBuilder<? extends FileBasedConfiguration> factoryBuilder(String filename, Class<? extends FileBasedConfiguration> confType){
        return new FileBasedConfigurationBuilder<>(confType)
                        .configure(new Parameters().properties()
                                .setFileName(filename)
                                .setThrowExceptionOnMissing(true)
                                .setListDelimiterHandler(new DefaultListDelimiterHandler(ConfigurationConst.ListDelimiter))
                                .setIncludesAllowed(false)

                        );

    }
    static public  CombinedConfigurationBuilder factoryBuilder2(String filename){
        return new CombinedConfigurationBuilder()
                .configure(new Parameters().properties()
                        .setFileName(filename)
                        .setThrowExceptionOnMissing(true)
                        .setListDelimiterHandler(new DefaultListDelimiterHandler(ConfigurationConst.ListDelimiter))
                        .setIncludesAllowed(false));
    }
    static public  FileBasedConfigurationBuilder<PropertiesConfiguration> factoryBuilder(String filename){

        return (FileBasedConfigurationBuilder<PropertiesConfiguration>) factoryBuilder(filename, PropertiesConfiguration.class);
    }
    private <T> T getProperty(String key, Class<T> propertyClass){

        if (this.containsKey(key)) {
            if (super.getProperty(key) instanceof Object[]) {
                Object[] aux = (Object[])super.getProperty(key);

                return (T) aux[aux.length>0?aux.length-1:0];

            }else if (super.getProperty(key) instanceof ArrayList ) {

                ArrayList aux = (ArrayList)super.getProperty(key);

                if(List.class.isAssignableFrom(propertyClass)){
                    if(!aux.isEmpty() && !List.class.isAssignableFrom(aux.iterator().next().getClass()))
                        return (T)super.getList(key);

                }

                return (T) aux.get(aux.size()>0?aux.size()-1:0);

            }
            if(List.class.isAssignableFrom(propertyClass) && !(super.getProperty(key) instanceof List)) {

                return (T)super.getList(key);
            }
            return super.get(propertyClass,key);

        }

        return null;
    }


    @Override
    public boolean getBoolean(String key) {
        return getProperty(key,Boolean.class);
    }

    @Override
    public byte getByte(String key) {
        return getProperty(key, Byte.class);
    }

    @Override
    public double getDouble(String key) {
        return getProperty(key, Double.class);
    }

    @Override
    public float getFloat(String key) {
        return getProperty(key, Float.class);
    }

    @Override
    public int getInt(String key) {
        return getProperty(key, Integer.class);
    }

    @Override
    public long getLong(String key) {
        return getProperty(key, Long.class);
    }

    @Override
    public short getShort(String key) {
        return  getProperty(key, Short.class);
    }

    @Override
    public BigDecimal getBigDecimal(String key) {
        return getProperty(key, BigDecimal.class);
    }

    @Override
    public BigInteger getBigInteger(String key) {
        return getProperty(key, BigInteger.class);
    }

    @Override
    public String getString(String key) {
        return getProperty(key, String.class);
    }

    @Override
    public String[] getStringArray(String key) {
        return getProperty(key, String[].class);
    }

    @Override
    public List<Object> getList(String key) {
        return getProperty(key, ArrayList.class);
    }


}
