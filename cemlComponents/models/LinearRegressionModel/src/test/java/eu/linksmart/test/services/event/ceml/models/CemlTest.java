package eu.linksmart.test.services.event.ceml.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.linksmart.api.event.ceml.evaluation.metrics.EvaluationMetric;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UnknownUntraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.services.event.ceml.core.CEML;
import eu.linksmart.services.event.ceml.core.CEMLManager;
import eu.linksmart.services.event.ceml.handlers.ListLearningHandler;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by José Ángel Carvajal on 20.04.2017 a researcher of Fraunhofer FIT.
 */
public class CemlTest {
    private static final String requestStr = "{\n" +
            "  \"Name\":\"test\",\n" +
            "  \"Descriptors\":\n" +
            "  {\n" +
            "    \"TargetSize\":1,\n" +
            "    \"InputSize\":1,\n" +
            "    \"Type\":\"NUMBER\"\n" +
            "  },\n" +
            "  \"Model\":{\n" +
            "    \"Name\":\"LinearRegressionModel\",\n" +
            "    \"Targets\":[\n" +
            "      {\n" +
            "        \"Name\":\"RMSE\",\n" +
            "        \"Threshold\":5.0,\n" +
            "        \"Method\":\"less\"\n" +
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
            "  \"Settings\":\n"+
            "  {\n" +
            "     \"BuildTillPhase\": 5,\n"+
            "     \"ReportingEnabled\": false\n"+
            "  }"+
            "}";

    @Test
    public void testCEML() {
        ObjectMapper mapper = CEML.getMapper();
        CEMLManager request;

        try {
            System.out.println("Expecting unimportant exception!");
            request = mapper.readValue(requestStr, CEMLManager.class);

        } catch (IOException e) {
            e.printStackTrace();
            fail();
            return;
        }
        try {
            request.build();
        } catch (TraceableException | UnknownUntraceableException e) {
            e.printStackTrace();
            fail();
        }

        ListLearningHandler handler = new ListLearningHandler(request.getLearningStreamStatements().iterator().next());

        assertTrue(request.getModel().getClass().getSimpleName().equals("LinearRegressionModel"));
        try {

            for(int i=0; i<1000;i++){
                handler.update(new Object[][]{new Object[]{i*1.0,i*1.0}},null);

            }

            Thread.sleep(500);
            assertEquals(3.0, (Double) ((List) (request.getModel().predict(Arrays.asList(3.0, 3.0)).getPrediction())).get(0), 0.5);
        } catch (TraceableException | InterruptedException| UntraceableException e) {
            e.printStackTrace();
            fail();
        }
        assertTrue((double)((EvaluationMetric<Number>) request.getModel().getEvaluator().getEvaluationAlgorithms().get("RMSE")).getResult() < 5.0);
        assertTrue(request.getModel().getEvaluator().isDeployable());
    }

}
