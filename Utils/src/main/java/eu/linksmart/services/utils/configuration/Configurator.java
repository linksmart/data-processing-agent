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
        if(!loadedFiles.contains(filePath)) {
            this.addConfiguration(new Configurator(filePath));
            loadedFiles.add(filePath);
        }
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
    private List<String> loadedFiles = new ArrayList<>();
    private boolean enableEnvironmentalVariables =false;


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
                            addConfiguration(factoryBuilder(filename).getConfiguration(),filename);
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
    public void enableEnvironmentalVariables(){
        if(!enableEnvironmentalVariables){
            this.addConfiguration(new EnvironmentConfiguration(), EnvironmentConfiguration.class.getCanonicalName());
            this.loadedFiles.add(EnvironmentConfiguration.class.getCanonicalName());
        }
        enableEnvironmentalVariables = true;
    }

    public boolean isEnvironmentalVariablesEnabled(){
        return enableEnvironmentalVariables;
    }


    static public  FileBasedConfigurationBuilder<? extends FileBasedConfiguration> factoryBuilder(String filename, Class<? extends FileBasedConfiguration> confType){
        return new FileBasedConfigurationBuilder<>(confType)
                        .configure(new Parameters().properties()
                                .setFileName(filename)
                                .setThrowExceptionOnMissing(false)
                                .setListDelimiterHandler(new DefaultListDelimiterHandler(ConfigurationConst.ListDelimiter))
                                .setIncludesAllowed(true)

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
    synchronized private <T> Object getProperty(String key, Class<T> propertyClass){

        if (this.containsKey(key)) {
            if (super.getProperty(key) instanceof Object[]) {
                Object[] aux = (Object[])super.getProperty(key);

                return aux[aux.length>0?aux.length-1:0];

            }else if (super.getProperty(key) instanceof ArrayList ) {

                ArrayList aux = (ArrayList)super.getProperty(key);

                if(List.class.isAssignableFrom(propertyClass)){
                    if(!aux.isEmpty() && !List.class.isAssignableFrom(aux.iterator().next().getClass()))
                        return super.getList(key);

                }

                return aux.get(aux.size()>0?aux.size()-1:0);

            }
            if(List.class.isAssignableFrom(propertyClass) && !(super.getProperty(key) instanceof List)) {

                return super.getList(key);
            }
            return super.get(propertyClass,key);

        }

        return null;
    }
    synchronized private int mostRecentConf(String key){
        for(int i= super.getConfigurationNameList().size()-1; i>=0; i--){
            if(super.getConfiguration(i).containsKey(key))
                return i;
        }
        return -1; // not found; return default conf
    }

    @Override
    public boolean getBoolean(String key) {
        int i = mostRecentConf(key);

        return i > -1 ? this.getConfiguration(i).getBoolean(key) : null;
    }

    @Override
    public byte getByte(String key) {
        int i = mostRecentConf(key);

        return i > -1 ? this.getConfiguration(i).getByte(key) : null;
    }

    @Override
    public double getDouble(String key) {
        int i = mostRecentConf(key);

        return i>-1?this.getConfiguration(i).getDouble(key) : null;
    }

    @Override
    public float getFloat(String key) {
        int i = mostRecentConf(key);

        return i>-1?this.getConfiguration(i).getFloat(key) :null;
    }

    @Override
    public int getInt(String key) {
        int i = mostRecentConf(key);

        return i>-1?this.getConfiguration(i).getInt(key) : null;
    }

    @Override
    public long getLong(String key) {
        int i = mostRecentConf(key);

        return i>-1?this.getConfiguration(i).getLong(key) : null;
    }

    @Override
    public short getShort(String key) {
        int i = mostRecentConf(key);

        return i>-1?this.getConfiguration(i).getShort(key) : null;
    }

    @Override
    public BigDecimal getBigDecimal(String key) {
        int i = mostRecentConf(key);

        return i>-1?this.getConfiguration(i).getBigDecimal(key) : null;
    }

    @Override
    public BigInteger getBigInteger(String key) {
        int i = mostRecentConf(key);

        return i>-1?this.getConfiguration(i).getBigInteger(key):null;
    }

    @Override
    public String getString(String key) {
        int i = mostRecentConf(key);

        return i>-1?this.getConfiguration(i).getString(key):null;
    }

    @Override
    public String[] getStringArray(String key) {
        int i = mostRecentConf(key);

        return i>-1?this.getConfiguration(i).getStringArray(key) :null;
    }

    @Override
    public  List<Object> getList(String key) {
        int i = mostRecentConf(key);

        return i>-1?this.getConfiguration(i).getList(key):null;
    }
    public boolean containsKeyAnywhere(String key){

        return mostRecentConf(key)>-1;
    }



}
