package eu.linksmart.test.services.utils.serialization;

import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.function.Utils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by José Ángel Carvajal on 02.03.2017 a researcher of Fraunhofer FIT.
 */
public class ConfigurationTest {

    final static private String resourceConfFile1 = "testConf1.cfg",resourceConfFile2 ="testConf2.cfg",ConfFile1 ="testConf1F.cfg",ConfFile2 = "testConf2F.cfg";

    @Test
    public void configurationJarResourceFileTest(){
        test(resourceConfFile1,resourceConfFile2,resourceConfFile1,resourceConfFile2);

    }
    private void createFiles(String resourceName,String fileName){
        try{
            Properties properties = Utils.createPropertyFiles(resourceName);
            PrintWriter writer = new PrintWriter(fileName, "UTF-8");
            properties.store(writer, "property file for integration test");
            writer.close();

        } catch (IOException e) {
           e.printStackTrace();
            fail();
        }
    }

    @Test
    public void configurationFileSystemTest(){
        createFiles(resourceConfFile2,ConfFile2);
        createFiles(resourceConfFile1,ConfFile1);

        test(ConfFile1,ConfFile2,resourceConfFile1,resourceConfFile2);


        (new File(ConfFile1)).delete();
        (new File(ConfFile2)).delete();
    }
    private void test(String confFile1,String confFile2,String strCmp1, String strCmp2){
        Configurator configurator = new Configurator(confFile2);

        // check if the prop of file 1 is in file 2
        assertEquals(false, Configurator.getDefaultConfig().containsKeyAnywhere("my.test.property"));
        // check if the shared prop is in file 2
        assertEquals(true, configurator.containsKeyAnywhere("my.test.shared.property"));
        // check if the shared prop is the one in file 2
        assertEquals(strCmp2, configurator.getString("my.test.shared.property"));

        Configurator.getDefaultConfig().addConfigurationFile(confFile1);

        // check if the prop of file 1 is in file 1
        assertEquals(true, Configurator.getDefaultConfig().containsKeyAnywhere("my.test.property"));
        // check (again) if the prop of file 1 is in file 2
        assertEquals(false, configurator.containsKeyAnywhere("my.test.property"));
        // check if the prop of file 2 is in file 2
        assertEquals(true, configurator.containsKeyAnywhere("my.test.property2"));

        // check if the shared prop is in file 1
        assertEquals(true, Configurator.getDefaultConfig().containsKeyAnywhere("my.test.shared.property"));
        // check if the shared prop is the one in file 1
        assertEquals(strCmp1, Configurator.getDefaultConfig().getString("my.test.shared.property"));

        // check (again) if the shared prop is in file 2
        assertEquals(true, configurator.containsKeyAnywhere("my.test.shared.property"));
        // check (again) if the shared prop is the one in file 2
        assertEquals(strCmp2, configurator.getString("my.test.shared.property"));

        configurator.addConfigurationFile(confFile1);
        // check if the prop of file 1 had being added to the ones of file 2
        assertEquals(true, configurator.containsKeyAnywhere("my.test.property"));

        // check (again) if the shared prop is in file 2
        assertEquals(true, Configurator.getDefaultConfig().containsKeyAnywhere("my.test.shared.property"));
        // check if the shared prop of file 1 had overwrite the one of file 2
        assertEquals(strCmp1, Configurator.getDefaultConfig().getString("my.test.shared.property"));

        // check if fake properties return errors
        assertEquals(false, configurator.containsKeyAnywhere("fake"));
        // check if fake properties return errors
        assertEquals(null, configurator.getString("fake"));
        // check if fake files return errors
        assertEquals(false, Configurator.addConfFile("fake"));

        // testing with environmental variables

        // check if fake properties return errors
        assertEquals(false, configurator.containsKeyAnywhere("JAVA_HOME"));
        // enable the environmental variables
        configurator.enableEnvironmentalVariables();
        // Loading JAVA_HOME content from env var for testing
        String sy = System.getenv("JAVA_HOME");
        // checking if JAVA_HOME exist
        assertEquals(true, configurator.containsKeyAnywhere("JAVA_HOME"));
        // compering loaded value with the one loaded by conf
        assertEquals(sy, configurator.getString("JAVA_HOME"));
        // ensure that the conf files are still loaded
        assertEquals(true, Configurator.getDefaultConfig().containsKeyAnywhere("my.test.shared.property"));

        // release resources
        Configurator.getDefaultConfig().clear();
        configurator.clear();

    }
}
