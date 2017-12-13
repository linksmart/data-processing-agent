package eu.linksmart.testing;


import eu.linksmart.api.event.types.Statement;
import eu.linksmart.api.event.types.impl.MultiResourceResponses;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.function.Utils;
import eu.linksmart.services.utils.serialization.DefaultDeserializer;
import eu.linksmart.services.utils.serialization.DefaultSerializer;
import eu.linksmart.services.utils.serialization.Deserializer;
import eu.linksmart.services.utils.serialization.Serializer;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.junit.Test;
import org.apache.http.client.fluent.*;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.fail;

/**
 * Created by José Ángel Carvajal on 12.12.2017 a researcher of Fraunhofer FIT.
 */
public class ProxyRequesterTest {

    static transient private Configurator conf = Configurator.getDefaultConfig();
    final static transient private String BASE_URL="base_url", INTEGRATION_TEST="integration_test";

    private static Deserializer deserializer = new DefaultDeserializer();
    private static Serializer serializer = new DefaultSerializer();

    @Test
    public void proxyAgentTest() {
        if(!conf.containsKeyAnywhere("integration_test"))
            return;

        String base = conf.getString(BASE_URL), dynamicStmID;

        Map<String, Object> statement = new HashMap<>();

        statement.put("name","testAdd");
        statement.put("statement","select count(*) as count from Observation output every 1 sec");
        statement.put("scope",new String[]{"local"});

        MultiResourceResponses responses = request(base+"statement/get/", new byte[]{}); // empty get

        assert responses != null;
        if(!responses.containsSuccess())
            fail();

        if(responses.getResources().size() != 0)
            fail();

        responses = request(base+ "statement/add/", statement); // add statement

        dynamicStmID = extractId(responses);

        responses = request(base+"statement/new/test/", statement); // new statement id test

        assert responses != null;
        if(!responses.containsSuccess())
            fail();

        responses = request(base+"statement/get/", new byte[]{}); // get 2 statements

        assert responses != null;
        if(!responses.containsSuccess())
            fail();

        if(responses.getResources().size() != 2)
            fail();

        responses = request(base+"statement/get/test", new byte[]{}); // get test statement

        assert responses != null;
        if(!responses.containsSuccess())
            fail();

        if(responses.getResources().size() != 1)
            fail();

        responses = request(base+"statement/get/"+dynamicStmID, new byte[]{}); // get test statement

        assert responses != null;
        if(!responses.containsSuccess())
            fail();

        if(responses.getResources().size() != 1)
            fail();

        responses = request(base+"statement/delete/test", new byte[]{}); // remove test statement

        assert responses != null;
        if(!responses.containsSuccess())
            fail();

        if(responses.getResources().size() != 1)
            fail();

        responses = request(base+"statement/delete/"+dynamicStmID+"/", new byte[]{}); // remove <dyn stm id> statement

        assert responses != null;
        if(!responses.containsSuccess())
            fail();

        if(responses.getResources().size() != 1)
            fail();

        responses = request(base+"statement/get/", new byte[]{}); // empty get

        assert responses != null;
        if(!responses.containsSuccess())
            fail();

        if(responses.getResources().size() != 0)
            fail();
    }
    private MultiResourceResponses request(String topic, Object body){

        try {

            Request request = Request.Post(topic).bodyByteArray(serializer.serialize(body)).addHeader(new BasicHeader("content-type","application/json"));
            Response response = request.execute();
            MultiResourceResponses responses = deserializer.parse(response.returnContent().asString(),MultiResourceResponses.class);

            if (!responses.containsSuccess())
                fail();
            else
                return responses;

        } catch (Exception e) {
            e.printStackTrace();
            fail();

        }

        return null;
    }
    private String extractId(MultiResourceResponses responses){
        String id= null;
        if(responses.getHeadResource()!= null && responses.getHeadResource() instanceof Map && ((Map)responses.getHeadResource() ).containsKey("ID"))
            id = ((Map)responses.getHeadResource() ).get("ID").toString();
        else{
            fail();
        }
        return id;
    }

}
