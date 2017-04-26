package eu.linksmart.test.services.event.ceml.models;

import eu.linksmart.api.event.ceml.evaluation.TargetRequest;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.services.event.ceml.models.LinearRegressionModel;
import eu.linksmart.services.event.ceml.models.RegressorModel;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.junit.Test;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by José Ángel Carvajal on 20.04.2017 a researcher of Fraunhofer FIT.
 */
public class LinearRegressionTest {
    @Test
    public void linearRegressionTest(){
        RegressorModel<List<Double>,List<Double>,SimpleRegression> regressor = new LinearRegressionModel(Arrays.asList(new TargetRequest(0.5,"RMSE","less")), new Hashtable<>(),new SimpleRegression());

        try {
            regressor.learn(Arrays.asList(0.0, 0.0));
            regressor.learn(Arrays.asList(1.0,1.0));
            regressor.learn(Arrays.asList(2.0,2.0));
            regressor.learn(Arrays.asList(4.0,4.0));
            assertEquals(3.0,regressor.predict(Arrays.asList(3.0,3.0)).getPrediction().get(0),0.5);
        } catch (TraceableException | UntraceableException e) {
            e.printStackTrace();
            fail();
        }

    }
}
