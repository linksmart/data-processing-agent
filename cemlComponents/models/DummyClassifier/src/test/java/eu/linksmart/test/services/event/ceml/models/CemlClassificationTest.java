package eu.linksmart.test.services.event.ceml.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.linksmart.api.event.ceml.prediction.Prediction;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UnknownUntraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.EventBuilder;
import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.services.event.ceml.core.CEML;
import eu.linksmart.services.event.ceml.core.CEMLManager;
import eu.linksmart.services.event.ceml.handlers.ListLearningHandler;
import eu.linksmart.services.event.ceml.models.ClassifierModel;
import eu.linksmart.services.event.connectors.MqttIncomingConnectorService;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.services.payloads.ogc.sensorthing.OGCEventBuilder;
import eu.linksmart.services.payloads.ogc.sensorthing.linked.ObservationImpl;
import eu.linksmart.services.utils.configuration.Configurator;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by José Ángel Carvajal on 09.10.2018 a researcher of Fraunhofer FIT.
 */
public class CemlClassificationTest {
    @Test
    public void ITTest() {
        Random random = new Random();
        CEMLManager request = null;
        Configurator conf = Configurator.getDefaultConfig();
        conf.setSetting("Test",true);
        try {
            Integer ints[] = new Integer[n];
            ListLearningHandler handler = initHandler(request = initRequest(strReq));

            double acc=0.0;

            for (int i = 0; i < tries; i++) {
                int r=0;
                for (int j = 0; j < n - 1; j++) {
                    ints[j] = random.nextInt(n);
                    r +=ints[j];
                }


                acc += r % 2;
                ints[n - 1] = 1;

                handler.update(new Object[][]{ints}, null);
                try{Thread.sleep(100);}catch (Exception ignored){}

            }
            acc=1.0 - acc/tries;

            ClassifierModel<List<Number>, Number, Function<List<Number>, Integer>> classifier = ((ClassifierModel<List<Number>, Number, Function<List<Number>, Integer>>) request.getModel());

            assertEquals("Mismatch between test acc and evaluator acc", acc, classifier.getEvaluator().getEvaluationAlgorithms().get("Accuracy").getResult().doubleValue(), 0.05);
            assertTrue("Although the acc is over the threshold system do not deploy!", classifier.getEvaluator().isDeployable());

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    @Test
    public void initITTest() {
        CEMLManager request = null;
        try {

            request = initRequest(initial);
            ClassifierModel<List<Number>, Number, Function<List<Number>, Integer>> classifier = ((ClassifierModel<List<Number>, Number, Function<List<Number>, Integer>>) request.getModel());

            assertTrue("Initial Confusion Matrix is deployabe!", classifier.getEvaluator().isDeployable());

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    private static final int n = 1000, tries =150;
    private static final String initial = "{\n" +
                    "  \"Name\":\"test\",\n" +
                    "  \"DataSchema\":\n" +
                    "{" +
                    "\"type\": \"array\"," +
                    "\"size\": " + n + "," +
                    "\"targetSize\": 1," +
                    "\"ofType\": \"int\"" +
                    "}," +
                    "  \"Model\":{\n" +
                    "    \"Name\":\"DummyClassifier\",\n" +
                    "      \"InitialConfusionMatrix\":[\n" +
                    "        [26, 25, 25, 25],\n" +
                    "        [25, 25, 25, 26]\n" +
                    "      ]," +
                    "    \"Targets\":[\n" +
                    "      {\n" +
                    "        \"Name\":\"Accuracy\",\n" +
                    "        \"Threshold\":0.40,\n" +
                    "        \"Method\":\"more\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"Name\":\"SlideAfter\",\n" +
                    "        \"Threshold\":100,\n" +
                    "        \"Method\":\"more\"\n" +
                    "      }\n" +
                    "\n" +
                    "    ]\n" +
                    "  },\n" +
                    "  \"LearningStreams\":[\n" +
                    "    {\n" +
                    "      \"statement\":\" \"\n" +
                    "    }\n" +
                    "  ],\n" +
                    "  \"DeploymentStreams\":[\n" +
                    "    {\n" +
                    "      \"statement\":\"\"\n" +
                    "    }\n" +
                    "  ],\n" +
                    "  \"Settings\":\n" +
                    "  {\n" +
                    "     \"BuildTillPhase\": 5,\n" +
                    "     \"ReportingEnabled\": false\n" +
                    "  }" +
                    "}";
    private static final String strReq = "{\n" +
            "  \"Name\":\"test\",\n" +
            "  \"DataSchema\":\n" +
            "{" +
            "\"type\": \"array\"," +
            "\"size\": " + n + "," +
            "\"targetSize\": 1," +
            "\"ofType\": \"int\"" +
            "}," +
            "  \"Model\":{\n" +
            "    \"Name\":\"DummyClassifier\",\n" +
            "    \"Targets\":[\n" +
            "      {\n" +
            "        \"Name\":\"Accuracy\",\n" +
            "        \"Threshold\":0.40,\n" +
            "        \"Method\":\"more\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"Name\":\"SlideAfter\",\n" +
            "        \"Threshold\":100,\n" +
            "        \"Method\":\"more\"\n" +
            "      }\n" +
            "\n" +
            "    ]\n" +
            "  },\n" +
            "  \"LearningStreams\":[\n" +
            "    {\n" +
            "      \"statement\":\" \"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"DeploymentStreams\":[\n" +
            "    {\n" +
            "      \"statement\":\"\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"Settings\":\n" +
            "  {\n" +
            "     \"BuildTillPhase\": 5,\n" +
            "     \"ReportingEnabled\": false\n" +
            "  }" +
            "}";

    private CEMLManager initRequest(String req) {
        CEMLManager request;
        try {
            Class.forName(CEML.class.getCanonicalName());
            Class.forName(DummyClassifierTest.class.getCanonicalName());


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {

            System.out.println("Expecting unimportant exception!");
            request = SharedSettings.getDeserializer().parse(req, CEMLManager.class);

        } catch (IOException e) {
            e.printStackTrace();
            fail();
            return null;
        }
        try {
            request.build();
        } catch (TraceableException | UnknownUntraceableException e) {
            e.printStackTrace();
            fail();
        }

        return request;
    }

    private ListLearningHandler initHandler(CEMLManager request) {
        ListLearningHandler handler = null;
        try {
            handler = new ListLearningHandler(request.getLearningStream().iterator().next());
        } catch (TraceableException | UntraceableException e) {
            e.printStackTrace();
            fail();
        }

        assertTrue(request.getModel().getClass().getSimpleName().equals("DummyClassifier"));
        return handler;
    }
}
