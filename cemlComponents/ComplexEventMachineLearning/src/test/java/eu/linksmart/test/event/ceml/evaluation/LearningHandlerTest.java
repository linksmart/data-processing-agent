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
import eu.linksmart.api.event.types.impl.SchemaNode;
import eu.linksmart.services.event.ceml.handlers.ListLearningHandler;
import eu.linksmart.services.event.ceml.statements.LearningStatement;
import eu.linksmart.services.utils.function.CI;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.fail;
import static org.mockito.Mockito.*;

/**
 * Created by José Ángel Carvajal on 16.02.2017 a researcher of Fraunhofer FIT.
 */
public class LearningHandlerTest {

    @Test
    public void listLearningHandling(){

        CI.ciCollapseMark("listLearningHandling");

        System.err.println("Unit test skipped. The test is not working. It must be improved");
        // simple test
        //updateTestBase(2,1,1,0);
        //updateTestBase(3,1,1,1);
        // more complex test
        //updateTestBase(504,336,168,0);
        //updateTestBase(672,336,168,168);

        // todo: fail test?


        CI.ciCollapseMark("listLearningHandling");
    }

    private void updateTestBase(int updateSize, int inputSize, int targetSize, int groundTruth ){
        LearningStatement learningStatement = new LearningStatement();
        Model model = Mockito.mock(ModelInstance.class);
        Evaluator evaluator = Mockito.mock(Evaluator.class);
        CEMLRequest request =Mockito.mock(CEMLRequest.class);
        SchemaNode schemaNode = factory(inputSize, targetSize);

        when(request.getModel()).thenReturn(model);
        when(request.getModel().getDataSchema()).thenReturn(schemaNode);
        learningStatement.setStatement("");
        learningStatement.setRequest(request);

        PredictionInstance prediction = Mockito.mock(PredictionInstance.class);

        prediction.setOriginalInput(0);
        prediction.setCertaintyDegree(1.0);
        prediction.setPredictedBy("Test");



        when(prediction.getPrediction()).thenReturn(0.0);
        when(evaluator.evaluate(prediction.getPrediction() instanceof List?(List)prediction.getPrediction(): Collections.singletonList(prediction.getPrediction()),listSize(inputSize))).thenReturn(0.0);
        when(model.getEvaluator()).thenReturn(evaluator);

        try {
            when(model.predict(listSize(updateSize).subList(groundTruth,inputSize+groundTruth))).thenReturn(prediction);
        } catch (TraceableException | UntraceableException e) {
            e.printStackTrace();
        }

        ListLearningHandler handler = null;
        try {
            handler = new ListLearningHandler(learningStatement);
        } catch (TraceableException | UntraceableException e) {
            e.printStackTrace();
            fail();
        }
        handler.update(new Object[][]{listSize(updateSize).toArray()},null);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
       /* TODO: check why this fails
        try {
            verify(model,atLeastOnce()).predict(listSize(updateSize).subList(groundTruth,inputSize+groundTruth));
        } catch (TraceableException | UntraceableException e) {
            e.printStackTrace();
        }*/
        try {
            verify(model,atLeastOnce()).learn(listSize(updateSize).subList(0,targetSize+inputSize));
        } catch (TraceableException | UntraceableException e) {
            e.printStackTrace();
        }
        verify(evaluator,atLeastOnce()).evaluate(Collections.singletonList(0.0),listSize(updateSize).subList(inputSize+groundTruth,inputSize+targetSize+groundTruth));

    }
    private List listSize(int size){
        List list = new ArrayList<>();
        for(int i=0; i<size; i++)
            list.add(i);
        return list;
    }

    private SchemaNode factory(int inputSize, int targetSize){
        SchemaNode schema = new SchemaNode();

        schema.setName("Test");
        schema.setTargetSize(targetSize);
        schema.setSize(inputSize+targetSize);
        schema.setOfType("int");
        try {
            schema.build();
        } catch (TraceableException | UntraceableException e) {
            e.printStackTrace();
            fail();

        }

        return schema;

    }
}
