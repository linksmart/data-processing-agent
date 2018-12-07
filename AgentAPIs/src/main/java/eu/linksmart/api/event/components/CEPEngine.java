package eu.linksmart.api.event.components;

import eu.linksmart.api.event.exceptions.*;
import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.api.event.types.Statement;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.Map;
/**
 * Copyright [2013] [Fraunhofer-Gesellschaft]
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 *
 * The interface provide access to an implementation of a Complex-Event Processing (CEP) engine service.
 * This interface is the basic functionality needed by the agent to use an CEP engine. The implementation of
 * this interface should be a service.
 * Any engine that don't provide all functionality that is not marked as deprecated, it is not a good fit for
 * the agents.
 *
 * @author Jose Angel Carvajal Soto
 * @since 0.03
 * @see  AnalyzerComponent
 *
 * */


public interface CEPEngine extends AnalyzerComponent {

    /**
     *
     * All CEP engines that implement this interface must add themselves into this Map.
     *
     * */
    MutablePair<String, CEPEngine> instancedEngine = new MutablePair<String, CEPEngine>("ClassName",null);

    /**
     * Return the name of the CEP which implement the interface
     *
     * @return Name of the CEP engine wrapped
     *
     * */
    String getName();

    /**
     * Add send and event to the CEP engine
     *
     * @param event is the event itself. A map of values and values names
     * @param type the native type of the event.
     *
     * @return <code>true</code> if the event was added to the CEP engine. <code>false</code> otherwise.
     * @throws UntraceableException  if any error that cannot be trace occurs {@link UntraceableException}
     * @throws TraceableException  if any error that can be trace occurs {@link TraceableException}
     * */
    boolean addEvent(EventEnvelope event, Class type) throws TraceableException, UntraceableException;

    /**
     * Configure a particular type in the engine.
     *
     *  This functionality is consider deprecated due to the uncleanness of this concept as general concept for all CEWrapper.<p>
     *  More research must be done about this to discard this feature or not.
     *
     * @param nameType are the names of type added in the engine
     * @param eventSchema are the names of the inner values of the event
     * @param eventTypes are the types of the inner values of the event
     *
     * @return <code>true</code> if the event was added to the CEP engine. <code>false</code> otherwise.
     * @throws StatementException the exception was produce by the statement processed in the CEP engine and it can be traced {@link StatementException}
     * */
    @Deprecated
    boolean addEventType(String nameType, String[] eventSchema, Class[] eventTypes) throws StatementException;

    /**
     * Configure a particular type in the engine.
     *
     * @param nameType are the alias name of type added in the engine (canonical name is recommended)
     * @param type the java type to be added
     * @param <T> the native type of the configured event type.
     *
     * @return <code>true</code> if the event was added to the CEP engine. <code>false</code> otherwise.
     * */
    <T> boolean addEventType(String nameType, Class<T> type);

    /**
     * Add a statement to the CEP engine which would be later handler with a handler.<p>
     *
     * @param statement to add in the engine
     *
     * @return <code>true</code> if the query is successfully deployed in the CEP engine. <code>false</code> otherwise.
     *
     * @exception eu.linksmart.api.event.exceptions.StatementException is thrown if any of the statement
     * properties don't complaint with the underlying implementation of the CEP engine. E.g. if the syntax of the property
     * statement do not match with the language of the underlying implementation.
     * @exception eu.linksmart.api.event.exceptions.InternalException is thrown if an known exception happens, but the source of
     * the exception is not related to the input itself. In other words, exceptions which are known and expected by the developer.
     * @exception eu.linksmart.api.event.exceptions.UnknownException is thrown when an exception happens but the reason of
     * the exception is unknown. In other words, this exception is used to mark all exceptions that had not being yet handled by
     * the developer.
     *
     * */
    boolean addStatement(Statement statement) throws StatementException, UnknownException, InternalException;

    /**
     * Update the statement handler to reflect changes to the statement object affecting the handler.<p>
     *
     * @param statement to update
     *
     * @return <code>true</code> if the handler was replaced successfully. <code>false</code> otherwise.
     *
     * @exception eu.linksmart.api.event.exceptions.InternalException is thrown if an known exception happens, but the source of
     * the exception is not related to the input itself. In other words, exceptions which are known and expected by the developer.
     *
     * */
    boolean updateHandler(Statement statement) throws InternalException;

    /**
     * Removes an statement form the CEP engine.
     *
     * @param id of the statement to be removed.
     * @param statement additional instruction for a successful removal, null if none is needed.
     *
     * @return if the statement with the id had been removed by this operation request
     *
     * @exception eu.linksmart.api.event.exceptions.StatementException is thrown if any of the statement
     * properties don't complaint with the underlying implementation of the CEP engine. E.g. if the syntax of the property
     * @exception eu.linksmart.api.event.exceptions.UnknownException is thrown when an exception happens but the reason of
     * the exception is unknown. In other words, this exception is used to mark all exceptions that had not being yet handled by
     * the developer.
     * @exception eu.linksmart.api.event.exceptions.InternalException is thrown if an known exception happens, but the source of
     * the exception is not related to the input itself. In other words, exceptions which are known and expected by the developer.
     * */
    boolean removeStatement(String id, Statement statement) throws UnknownException, StatementException, InternalException;

    /**
     * Pauses an statement form the CEP engine.
     *
     * @param id of the statement to be pause.
     *
     * @return if the statement with the id had been paused by this operation request
     * @exception eu.linksmart.api.event.exceptions.StatementException is thrown if any of the statement
     * properties don't complaint with the underlying implementation of the CEP engine. E.g. if the syntax of the property
     * @exception eu.linksmart.api.event.exceptions.UnknownException is thrown when an exception happens but the reason of
     * the exception is unknown. In other words, this exception is used to mark all exceptions that had not being yet handled by
     * the developer.
     * */
    boolean pauseStatement(String id) throws UnknownException, StatementException;

    /**
     * Starts an statement form the CEP engine.
     *
     * @param id of the statement to be started.
     *
     * @return if the statement with the id had been started by this operation request
     *
     * @exception eu.linksmart.api.event.exceptions.StatementException is thrown if any of the statement
     * properties don't complaint with the underlying implementation of the CEP engine. E.g. if the syntax of the property
     * @exception eu.linksmart.api.event.exceptions.UnknownException is thrown when an exception happens but the reason of
     * the exception is unknown. In other words, this exception is used to mark all exceptions that had not being yet handled by
     * the developer.
     * */
    boolean startStatement(String id) throws UnknownException, StatementException;

    /***
     *
     * Terminate the Wrapper, releasing any resource us by it.
     *
     * */
    void destroy();

    /**
     *
     * Returns all statements deployed in the engine regardless if they are active (stared) or paused.
     * Removed statements will not be listed by this Map.
     *
     * @return the Map of all statement. The key is the id of the statement.
     *
     * */
    Map<String, Statement> getStatements();

    /**
     * Return the advanced features of the DataFusionManager if the implementation support it.
     * If the implementation do no support it, then the will return null
     *
     * @return an instance of DataFusionWrapperAdvanced or null in case the Wrapper do not support those features
     *
     * @see eu.linksmart.api.event.components.CEPEngineAdvanced
     */
    CEPEngineAdvanced getAdvancedFeatures();


    boolean executeStatement(Statement statement) throws StatementException, InternalException;
}
