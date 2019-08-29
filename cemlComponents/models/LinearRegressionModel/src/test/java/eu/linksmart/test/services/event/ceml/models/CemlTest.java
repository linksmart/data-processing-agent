package eu.linksmart.test.services.event.ceml.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.linksmart.api.event.ceml.evaluation.metrics.EvaluationMetric;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UnknownUntraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.services.event.ceml.core.CEML;
import eu.linksmart.services.event.ceml.core.CEMLManager;
import eu.linksmart.services.event.ceml.handlers.ListLearningHandler;
import eu.linksmart.services.event.ceml.models.LinearRegressionModel;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.services.utils.configuration.Configurator;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by José Ángel Carvajal on 20.04.2017 a researcher of Fraunhofer FIT.
 */
public class CemlTest {
    @Test
    public void testCEML() {
        Configurator conf = Configurator.getDefaultConfig();
        conf.setSetting("Test",true);
        //test(new String(getRequest("CemlTestLegacy.json")));
        test(new String(getRequest("CemlTestCurrent.json")));
    }
    private void test(String requestStr){
        CEMLManager request;
        try {
            Class.forName(CEML.class.getCanonicalName());
            Class.forName(LinearRegressionModel.class.getCanonicalName());


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {

            // System.out.println("Expecting unimportant exception!");
            // SharedSettings.getDeserializer().defineClassToInterface(Model.class, LinearRegressionModel.class);
            request = SharedSettings.getDeserializer().parse(requestStr, CEMLManager.class);

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

        ListLearningHandler handler = null;
        try {
            handler = new ListLearningHandler(request.getLearningStream().iterator().next());
        } catch (TraceableException | UntraceableException e) {
            e.printStackTrace();
            fail();
        }

        assertTrue(request.getModel().getClass().getSimpleName().equals("LinearRegressionModel"));
        try {

            for(int i=0; i<1000;i++){
                handler.update(new Object[][]{new Object[]{i,i}},null);

            }

            Thread.sleep(500);
            assertEquals(3.0, (Double) ((List) (request.getModel().predict(Arrays.asList(3, 3)).getPrediction())).get(0), 0.5);
        } catch (TraceableException | InterruptedException| UntraceableException e) {
            e.printStackTrace();
            fail();
        }
        assertTrue((double)((EvaluationMetric<Number>) request.getModel().getEvaluator().getEvaluationAlgorithms().get("RMSE")).getResult() < 5);
        assertTrue(request.getModel().getEvaluator().isDeployable());
    }
    private String getRequest(String path){
        try {
            return new String(Thread.currentThread().getContextClassLoader().getResourceAsStream(path).readAllBytes());

        } catch (Exception e) {
            fail(e.getMessage());

        }
        return null;
    }

}
