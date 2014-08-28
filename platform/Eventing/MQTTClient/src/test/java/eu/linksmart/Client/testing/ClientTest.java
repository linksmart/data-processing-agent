package eu.linksmart.Client.testing;

import eu.linksmart.event.mqtt.impl.MqttServiceProvider;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.ops4j.pax.exam.Configuration;

import javax.inject.Inject;

import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.*;

public class ClientTest {

    @Inject
    private MqttServiceProvider backboneJXTA;

    @Configuration
    public org.ops4j.pax.exam.Option[] config() {
        return options(
                mavenBundle("org.eclipse.paho", "mqtt-client", "0.4.0"),
                mavenBundle("eu.linksmart", "eu.linksmart.api.networkmanager", "2.2.0-SNAPSHOT"),
                mavenBundle("com.google.code.gson", "gson", "1.7.1"),
                mavenBundle("eu.linksmart", "eu.linksmart.api.utils", "2.2.0-SNAPSHOT"),
                mavenBundle("eu.linksmart", "eu.linksmart.api.event", "1.0-SNAPSHOT"),
                junitBundles()
        );
    }

    private Logger mlogger = Logger.getLogger(ClientTest.class.getName());

    @Test
    public void testSomething() {
        //TODO dummy assertion. Add your proper tests
        assertTrue(true);

    }
}