package eu.linksmart.test.event.ceml.evaluation;

import eu.linksmart.api.event.ceml.CEMLRequest;
import eu.linksmart.api.event.ceml.data.DataDescriptor;
import eu.linksmart.api.event.ceml.data.DataDescriptors;
import eu.linksmart.api.event.ceml.evaluation.Evaluator;
import eu.linksmart.api.event.ceml.model.Model;
import eu.linksmart.api.event.ceml.model.ModelInstance;
import eu.linksmart.api.event.ceml.prediction.PredictionInstance;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.services.event.ceml.handlers.ListLearningHandler;
import eu.linksmart.services.event.ceml.statements.LearningStatement;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.mockito.Mockito.*;

/**
 * Created by José Ángel Carvajal on 16.02.2017 a researcher of Fraunhofer FIT.
 */
public class LearningHandlerTest {
    @Test
    public void listLearningHandlingTest(){
        LearningStatement learningStatement = new LearningStatement();
        Model model = Mockito.mock(ModelInstance.class);
        Evaluator evaluator = Mockito.mock(Evaluator.class);
        CEMLRequest request =Mockito.mock(CEMLRequest.class);
        DataDescriptors descriptors = DataDescriptors.factory("Test",1,1, DataDescriptor.DescriptorTypes.NUMBER);

        when(request.getModel()).thenReturn(model);
        when(request.getDescriptors()).thenReturn(descriptors);
        learningStatement.setStatement("");
        learningStatement.setRequest(request);

        PredictionInstance prediction = Mockito.mock(PredictionInstance.class);

        prediction.setOriginalInput(0);
        prediction.setCertaintyDegree(1.0);
        prediction.setPredictedBy("Test");

        when(evaluator.evaluate(prediction.getPrediction(),Arrays.asList(0))).thenReturn(0.0);
        when(model.getEvaluator()).thenReturn(evaluator);

        try {
            when(model.predict(Arrays.asList(0,0))).thenReturn(prediction);
        } catch (TraceableException | UntraceableException e) {
            e.printStackTrace();
        }

        ListLearningHandler handler = new ListLearningHandler(learningStatement);
        handler.update(new Object[][]{{0,0}},null);
        try {
            verify(model,atLeastOnce()).predict(Arrays.asList(0, 0).subList(0, 2));
        } catch (TraceableException | UntraceableException e) {
            e.printStackTrace();
        }
        try {
            verify(model,atLeastOnce()).learn(Arrays.asList(0,0).subList(0,2));
        } catch (TraceableException | UntraceableException e) {
            e.printStackTrace();
        }
        verify(evaluator,atLeastOnce()).evaluate(null,Arrays.asList(0, 0).subList(0, 1));
    }
}
