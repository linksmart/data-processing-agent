package eu.linksmart.test.services.utils.serialization;

import eu.linksmart.services.utils.serialization.DefaultDeserializer;
import eu.linksmart.services.utils.serialization.DefaultSerializer;
import eu.linksmart.services.utils.serialization.Deserializer;
import eu.linksmart.services.utils.serialization.Serializer;

import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by José Ángel Carvajal on 24.02.2017 a researcher of Fraunhofer FIT.
 */
public class SerializerTest {

    public static final String simpleStrTest = "{\"hello\":\"world\",\"int\":1,\"float\":1.0,\"vec\":[1,2,3,4]}";
    public static final byte[] simpleBinTest = simpleStrTest.getBytes();
    @Test
    public void serializerTest(){
        Map mapS, mapB;
        String serialized;
        Deserializer deserializer = new DefaultDeserializer();
        Serializer serializer = new DefaultSerializer();
        try {
            mapS = deserializer.parse(simpleStrTest,Map.class);
            mapB = deserializer.deserialize(simpleBinTest, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
            return;
        }

        runTests(mapB);
        runTests(mapS);

        try {
            assertEquals("The String after serialization is the same as before deserialization",simpleStrTest,serializer.toString(mapS));
        } catch (IOException e) {
            e.printStackTrace();
            fail();
            return;
        }

        serializer.close();
        deserializer.close();


    }
    private void runTests(Map map){

        assertEquals("I'm saying hello ","world",map.get("hello"));
        assertEquals("expecting int ",1,map.get("int"));
        assertEquals("expecting float ",1.0,map.get("float"));
        assertEquals("expecting vector of 4 elements",4, ((List)map.get("vec")).size());

    }
}
