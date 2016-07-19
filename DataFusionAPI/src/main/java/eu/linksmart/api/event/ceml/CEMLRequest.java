package eu.linksmart.api.event.ceml;

import eu.linksmart.api.event.ceml.data.DataDescriptors;
import eu.linksmart.api.event.ceml.evaluation.Evaluator;
import eu.linksmart.api.event.ceml.model.Model;
import eu.linksmart.api.event.datafusion.Statement;

import java.util.Map;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public interface CEMLRequest<ValueType, ReturnValueType> {
    public DataDescriptors getDescriptors();
    public Model<ValueType, ReturnValueType>  getModel();
    public Evaluator<ReturnValueType> getEvaluator();

    public Map<String,LearningStatement> getLearningStreamStatements();
    public Map<String,Statement> getDeploymentStreamStatements();
    public Map<String,Statement> getAuxiliarStreamStatements();
    public Statement getStreamStatement(String StatementId);
    public void deploy() throws Exception;
    public void undeploy() throws Exception;
}
