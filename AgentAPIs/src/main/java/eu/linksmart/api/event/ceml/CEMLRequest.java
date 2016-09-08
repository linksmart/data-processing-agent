package eu.linksmart.api.event.ceml;

import eu.linksmart.api.event.ceml.data.DataDescriptors;
import eu.linksmart.api.event.ceml.model.Model;
import eu.linksmart.api.event.ceml.prediction.Prediction;
import eu.linksmart.api.event.exceptions.InternalException;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.JsonSerializable;
import eu.linksmart.api.event.types.Statement;

import java.util.Collection;
import java.util.Map;

/**
 *  Copyright [2013] [Fraunhofer-Gesellschaft]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */
/**
 * This interface represent the needed data to trigger a Complex-Event Machine Learning processes.
 * This means, real-time analysis or learn based on streams.
 *
 * The Complex-Event Machine Learning processes consist in 5 phases.
 * 1) data propagation:
 *      a) Introduce propagate data into the process
 *      b) Propagate the results and the state of the process out
 * 2) Pre-processing data
 * 3) Continuous Learning
 * 4) Evaluation
 * 5) Deployment
 *
 * To realize this process the CEML leverage in the IoT Data-Processing core functionality.
 *
 * Phase 1.a)
 *      The data acquisition from a communication protocol is done by a IncomingConnector and process into the CEPEngine by a Feeder
 * Phase 2)
 *      The pre-processing is done in a CEPEngine by simple Statements without handlers or with LearningStatements
 * Phase 3)
 *      The Learning uses the result of the LearningStatement given by the CEPEngine and introducing it to the Models, which uses the DataDescriptors to configure themselves.
 * Phase 4)
 *      Made by the Evaluators defined in each models.
 * Phase 5)
 *      The actions taken by the agent with the model can be defined by typical Statements
 * Phase 1.b)
 *      The data is propagate to a communication protocol by a Handler.
 *
 *
 * @author Jose Angel Carvajal Soto
 * @since       1.1.1
 *
 * @see eu.linksmart.api.event.types.JsonSerializable
 * @see eu.linksmart.api.event.types.Statement
 * @see eu.linksmart.api.event.components.IncomingConnector
 * @see eu.linksmart.api.event.components.Feeder
 * @see eu.linksmart.api.event.components.CEPEngine
 * @see eu.linksmart.api.event.components.CEPEngineAdvanced
 * @see eu.linksmart.api.event.components.ComplexEventHandler
 * @see eu.linksmart.api.event.ceml.data.DataDescriptors
 * @see eu.linksmart.api.event.ceml.model.Model
 * @see eu.linksmart.api.event.ceml.LearningStatement
 * @see eu.linksmart.api.event.ceml.prediction.Prediction
 */
public interface CEMLRequest  extends JsonSerializable {
    /***
     * Setts/inserts/starts the deployment statements and objects in to the CEPEngine.
     *
     * @exception eu.linksmart.api.event.exceptions.TraceableException if an error happens while the deployment is being done.
     *      As ID error producer the name of the CEMLRequest must be given
     *
     * @see eu.linksmart.api.event.exceptions.TraceableException
     * */
    public void deploy() throws TraceableException;
    /***
     * Unsetts/remove/pause the deployment statements and objects in to the CEPEngine.
     *
     * @exception eu.linksmart.api.event.exceptions.TraceableException if an error happens while the deployment is being done.
     *      As ID error producer the name of the CEMLRequest must be given
     *
     * @see eu.linksmart.api.event.exceptions.TraceableException
     * */
    public void undeploy() throws TraceableException;
    /***
     * Provide a report of the state of the statement. Where the report must be generated is implementation dependent.
     *
     * */
    public void report();
    /***
     * Description of the data expected by the model
     *
     * @return the description of the data as DataDescriptors
     * */
    public DataDescriptors getDescriptors();
    /***
     * Define how the data will be analysed/learned/studied.
     *
     * @return an instantiation of an Algorithm.
     * */
    public Model getModel();
    /***
     * The name of the request, it is equivalent to an ID
     *
     * @return the name the request
     * */
    public String getName();
    /***
     * Setts the name of the request, it is equivalent to an ID
     *
     * @param name of the request. Once the Request has being built the name cannot be changed!
     * */
    void setName(String name);
    /***
     * The Last Prediction made by a model in this request. The prediction must be seated in the deployment phase!
     *
     * @return The LastPrediction as Prediction
     * @see eu.linksmart.api.event.ceml.prediction.Prediction
     * */
    public Prediction  getLastPrediction();
    /***
     * The Last Prediction made by a model in this request. The prediction must be seated in the deployment phase!
     *
     * @param  prediction as Prediction
     * @see eu.linksmart.api.event.ceml.prediction.Prediction
     * */
    public void setLastPrediction(Prediction  prediction);
    /***
     * Set of statements/rules that define how the model will get the data to further analysis. The results of the streams produce by this statements must match with the Descriptors (for learning).
     *
     * @return a collection of LearningStatements.
     * @see eu.linksmart.api.event.ceml.LearningStatement
     * */
    public Collection<LearningStatement> getLearningStreamStatements();
    /***
     * Set of statements/rules that define how the model will be used with the data the data. The results of the streams produce by this statements must match with the Descriptors (for predicting).
     *
     * @return a collection of Statements.
     * @see eu.linksmart.api.event.types.Statement
     * */
    public Collection<Statement> getDeploymentStreamStatements();
    /***
     * Set of middle steps may be used by the DeploymentStreams or LearningStreams. No handler is given to this statements.
     *
     * @return a collection of Statements.
     * @see eu.linksmart.api.event.types.Statement
     * */
    public Collection<Statement> getAuxiliaryStreamStatements();
    /***
     * Reruns any Statement defined either as AuxiliaryStatements, LearningStreams, or DeploymentStreams.
     *
     * @return the selected statement as Statements.
     * @see eu.linksmart.api.event.types.Statement
     * */
    public Statement getStreamStatement(String StatementId);
    /***
     * Models or evaluation may have custom setting. Here such setting are defined. The semantic of the settings are completely implementation dependent.
     *
     * @return a map of objects where the key represent the name of a setting property and the object the setting value
     *
     * */
    public Map<String,Object> getSettings();




}
