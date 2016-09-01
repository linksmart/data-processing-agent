package eu.linksmart.api.event.ceml;

import eu.linksmart.api.event.ceml.data.DataDescriptors;
import eu.linksmart.api.event.ceml.model.Model;
import eu.linksmart.api.event.ceml.prediction.Prediction;
import eu.linksmart.api.event.types.JsonSerializable;
import eu.linksmart.api.event.types.Statement;

import java.util.Collection;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public interface CEMLRequest<ValueType, ReturnValueType, LearningObject> extends JsonSerializable {
    public DataDescriptors getDescriptors();
    public Model<ValueType, ReturnValueType,LearningObject>  getModel();
   // public Evaluator<ReturnValueType> getEvaluator();
    public String getName();

    public Prediction<ReturnValueType> getLastPrediction();
    public void setLastPrediction(Prediction<ReturnValueType> prediction);
    public Collection<LearningStatement> getLearningStreamStatements();
    public Collection<Statement> getDeploymentStreamStatements();
    public Collection<Statement> getAuxiliaryStreamStatements();
    public Statement getStreamStatement(String StatementId);
    public Map<String,Object> getSettings();
    public void deploy() throws Exception;
    public void undeploy() throws Exception;

    public void report();

    void setName(String name);

}
