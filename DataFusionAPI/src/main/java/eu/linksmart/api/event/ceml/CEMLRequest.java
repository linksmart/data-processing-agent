package eu.linksmart.api.event.ceml;

import eu.linksmart.api.event.ceml.data.DataDescriptors;
import eu.linksmart.api.event.ceml.evaluation.Evaluator;
import eu.linksmart.api.event.ceml.model.Model;
import eu.linksmart.api.event.datafusion.JsonSerializable;
import eu.linksmart.api.event.datafusion.Statement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public interface CEMLRequest<ValueType, ReturnValueType, LearningObject> extends JsonSerializable {
    public DataDescriptors getDescriptors();
    public Model<ValueType, ReturnValueType,LearningObject>  getModel();
   // public Evaluator<ReturnValueType> getEvaluator();
    public String getName();

    public Collection<LearningStatement> getLearningStreamStatements();
    public Collection<Statement> getDeploymentStreamStatements();
    public Collection<Statement> getAuxiliaryStreamStatements();
    public Statement getStreamStatement(String StatementId);
    public void deploy() throws Exception;
    public void undeploy() throws Exception;

    void setName(String name);
}
