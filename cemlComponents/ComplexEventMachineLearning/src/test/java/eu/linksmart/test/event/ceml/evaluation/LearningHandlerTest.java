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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Created by José Ángel Carvajal on 16.02.2017 a researcher of Fraunhofer FIT.
 */
public class LearningHandlerTest {

    @Test
    public void listLearningHandling(){

        // simple test
        updateTestBase(2,1,1,0);
        updateTestBase(3,1,1,1);
        // more complex test
        updateTestBase(504,336,168,0);
        updateTestBase(672,336,168,168);

        // todo: fail test?


    }
    private void updateTestBase(int updateSize, int inputSize, int targetSize, int groundTruth ){
        LearningStatement learningStatement = new LearningStatement();
        Model model = Mockito.mock(ModelInstance.class);
        Evaluator evaluator = Mockito.mock(Evaluator.class);
        CEMLRequest request =Mockito.mock(CEMLRequest.class);
        DataDescriptors descriptors = DataDescriptors.factory("Test",inputSize,targetSize, DataDescriptor.DescriptorTypes.NUMBER);

        when(request.getModel()).thenReturn(model);
        when(request.getDescriptors()).thenReturn(descriptors);
        learningStatement.setStatement("");
        learningStatement.setRequest(request);

        PredictionInstance prediction = Mockito.mock(PredictionInstance.class);

        prediction.setOriginalInput(0);
        prediction.setCertaintyDegree(1.0);
        prediction.setPredictedBy("Test");



        when(prediction.getPrediction()).thenReturn(0.0);
        when(evaluator.evaluate(prediction.getPrediction(),listSize(inputSize))).thenReturn(0.0);
        when(model.getEvaluator()).thenReturn(evaluator);

        try {
            when(model.predict(listSize(updateSize).subList(groundTruth,inputSize+groundTruth))).thenReturn(prediction);
        } catch (TraceableException | UntraceableException e) {
            e.printStackTrace();
        }

        ListLearningHandler handler = new ListLearningHandler(learningStatement);
        handler.update(new Object[][]{listSize(updateSize).toArray()},null);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            verify(model,atLeastOnce()).predict(listSize(updateSize).subList(groundTruth,inputSize+groundTruth));
        } catch (TraceableException | UntraceableException e) {
            e.printStackTrace();
        }
        try {
            verify(model,atLeastOnce()).learn(listSize(updateSize).subList(0,targetSize+inputSize));
        } catch (TraceableException | UntraceableException e) {
            e.printStackTrace();
        }
        verify(evaluator,atLeastOnce()).evaluate(0.0,listSize(updateSize).subList(inputSize+groundTruth,inputSize+targetSize+groundTruth));

    }
    private List listSize(int size){
        List list = new ArrayList<>();
        for(int i=0; i<size; i++)
            list.add(i);
        return list;
    }
}
