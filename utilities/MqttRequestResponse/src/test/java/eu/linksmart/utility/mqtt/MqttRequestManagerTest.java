package eu.linksmart.utility.mqtt;


import eu.linksmart.api.event.types.Statement;
import eu.linksmart.api.event.types.impl.MultiResourceResponses;
import eu.linksmart.services.event.core.StatementInstance;
import eu.linksmart.services.utils.serialization.DefaultDeserializer;
import eu.linksmart.services.utils.serialization.DefaultSerializer;
import eu.linksmart.services.utils.serialization.Deserializer;
import eu.linksmart.services.utils.serialization.Serializer;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;

import static org.junit.Assert.fail;

/**
 * Created by José Ángel Carvajal on 07.12.2017 a researcher of Fraunhofer FIT.
 */
public class MqttRequestManagerTest {
    //@Test
    public void broker(){
        try {
            MqttRequestManager requestManager = new MqttRequestManager();
            Serializer serializer = new DefaultSerializer();
            Statement statement = new StatementInstance(
                    "test",
                    "select count(*) as count from Observation output every 1 sec",
                    new String[]{"local"}
            );

            MultiResourceResponses responses = requestManager.request("/statement/new/test/",serializer.serialize(statement), 3,3000,null);
            System.out.println(serializer.toString(responses));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

    }
}
