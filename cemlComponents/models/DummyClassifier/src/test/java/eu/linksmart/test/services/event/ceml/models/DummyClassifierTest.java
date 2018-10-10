package eu.linksmart.test.services.event.ceml.models;

import eu.linksmart.api.event.ceml.data.DataDescriptors;
import eu.linksmart.api.event.ceml.evaluation.TargetRequest;
import eu.linksmart.api.event.ceml.prediction.Prediction;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.impl.SchemaNode;
import eu.linksmart.services.event.ceml.models.ClassifierModel;
import eu.linksmart.services.event.ceml.models.DummyClassifier;
import org.junit.Test;

import java.util.*;
import java.util.function.Function;



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by José Ángel Carvajal on 05.10.2018 a researcher of Fraunhofer FIT.
 */
public class DummyClassifierTest {
    @Test
    public void ITTest(){
        ClassifierModel<List<Number>,Number,Function<List<Number>,Integer>> classifier = new DummyClassifier(Arrays.asList(new TargetRequest(0.4,"Accuracy","more"), new TargetRequest(100,"SlideAfter","more")), new Hashtable<>(),new Object());
        classifier.setName("Test");

        try {
            SchemaNode schema = new SchemaNode();
            schema.setName(classifier.getName());
            int n = 1000;
            schema.setSize(n);
            schema.setTargetSize(1);
            schema.setType("array");
            schema.setOfType("int");
            schema.build();
            classifier.setDataSchema(schema);
            classifier.build();
            Random random = new Random();
            ArrayList<Number> ints = new ArrayList<>();

            double acc = 0.0;
            for (int j=0; j <n; j++) {
                ints.add(random.nextInt(n));
                Prediction p =classifier.predict(ints);
                classifier.getEvaluator().evaluate((Number) p.getPrediction(),1.0);
                acc += classifier.predict(ints).getPrediction().doubleValue()== 1 ? 1.0 :0.0 ;


            }
            acc = acc/n;

            assertEquals("Highly unprovable result of test calculated Accuracy (run again, if happens twice this is an error)",0.5, acc,0.1);
            assertEquals("Highly unprovable result of evaluator calculated Accuracy (run again, if happens twice this is an error)",0.5, classifier.getEvaluator().getEvaluationAlgorithms().get("Accuracy").getResult().doubleValue(),0.1);
            assertEquals("Mismatch between test acc and evaluator acc",acc, classifier.getEvaluator().getEvaluationAlgorithms().get("Accuracy").getResult().doubleValue(),0.05);
            assertEquals("Although the acc is over the threshold system do not deploy!",classifier.getEvaluator().isDeployable(),true);
        } catch (TraceableException | UntraceableException e) {
            e.printStackTrace();
            fail();
        }    }

}
