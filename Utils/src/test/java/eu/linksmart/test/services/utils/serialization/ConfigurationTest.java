package eu.linksmart.test.services.utils.serialization;

import eu.linksmart.services.utils.configuration.Configurator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by José Ángel Carvajal on 02.03.2017 a researcher of Fraunhofer FIT.
 */
public class ConfigurationTest {
    @Test
    public void configurationTest(){
        Configurator configurator = new Configurator("testConf2.cfg");

        assertEquals(false, Configurator.getDefaultConfig().containsKey("my.test.property"));

        Configurator.getDefaultConfig().addConfigurationFile("testConf1.cfg");

        assertEquals(true, Configurator.getDefaultConfig().containsKey("my.test.property"));
        assertEquals(false, configurator.containsKey("my.test.property"));
        assertEquals(true, configurator.containsKey("my.test.property2"));

        configurator.addConfigurationFile("testConf1.cfg");
        assertEquals(true, configurator.containsKey("my.test.property"));

        assertEquals(false, configurator.containsKey("fake"));
        assertEquals(false, Configurator.addConfFile("fake"));

    }
}
