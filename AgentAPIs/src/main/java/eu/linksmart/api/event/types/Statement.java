package eu.linksmart.api.event.types;


import java.util.List;
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
 * This interface represents an data processing statement, the implementation may varies with the underlying implementation of the CEPEngine.
 * The property statement is completely CEPEngine dependent. The other features are common to all the data processing framework of the agents.
 * This interface is a generalization of any statement of a CEPengine.<p>
 *
 *
 * @author Jose Angel Carvajal Soto
 * @version     0.03
 * @since       0.03
 * @see  eu.linksmart.api.event.components.CEPEngine
 * @see eu.linksmart.api.event.types.JsonSerializable
 *
 * */
public interface Statement extends JsonSerializable {
    /***
     * Name of the statement
     *
     * @return  returns the name of the statement as string
     * */
    public String getName();
    /***
     * The native statement for specific underling implementation of a CEP engine (e.g. EPL)
     *
     * @return  returns the statement in the native CEP language as string
     * */
    public String getStatement();
    /***
     * The source message handler (broker/http server) where the events are coming
     *
     * @return  The alias of message handler (broker/http server) as string
     * */
    public String getSource();
    /***
     * The input topics/paths of the statement (DEPRECATED)
     *
     * @return  return the list of topics/paths that are needed in the statement
     * */
    @Deprecated
     public String[] getInput();
    /***
     * return the scope. The scope is the output endpoints (e.g. broker, http server) where the events will be published
     *
     * @return  aliases of the endpoints as string
     * */
    public String[] getScope();
    /***
     * The input topic/path number i of the statement (DEPRECATED)
     *
     * @param index of the topic selected to return
     *
     * @return  return the selected topic
     * */
    @Deprecated
    public String getInput(int index);
    /***
     * The output message handler (broker/http server) number i of the statement
     *
     * @param index of the broker selected to return
     *
     * @return  The selected broker URL or alias as string
     * */
    public String getScope(int index);
    /***
     * The output topics/paths where the events will be published/posted
     *
     * @return  The output topics/paths as string
     * */
    public String[] getOutput();
    /***
     * The Input value is a optional value. This return if the value has been defined or not (DEPRECATED)
     *
     * @return  <code>true</code> if there is Input has being defined, <code>false</code> otherwise
     * */

    @Deprecated
    public boolean haveInput();
    /***
     * The output value is a optional value. This return if the value has been defined or not
     *
     * @return  <code>true</code> if there is output has being defined, <code>false</code> otherwise
     * */
    public boolean haveOutput();
    /***
     * The Scope value is a optional value. This return if the value has been defined or not
     *
     * @return  <code>true</code> if there is Scope has being defined, <code>false</code> otherwise
     * */
    public boolean haveScope();
    /***
     * Returns the hash ID of the statement. By default this is the SHA256 of the statement.
     *
     * @return  The ID as string.
     * */
    public String getID();
    /***
     * Return the handler selected to process the result of the complex event, @Default ComplexEventHandlerImpl.
     * Note: The value "" or null is a valid response, this value represent silent events, events that just happen inside the CEP engine.
     *
     * @return  the handler cannonic name of ComplexEventHandler of the statement, @Default ComplexEventHandlerImpl..
     * */
    public String getCEHandler();
    /***
     * Return the IDs selected to agents which are targeted to process this statement. If the array is empty means all receivers @Default Empty String[].
     *
     * @return  List of targeted Agents address to process the statement, otherwise empty (all available agents):
     * */
    public List<String> getTargetAgents();
    /***
     * Return the state of the Statement, which determines how the statement will be at runtime.
     *
     * @return  Lifecycle Statement State @see StatementLifecycle .
     * */
    public StatementLifecycle getStateLifecycle();
    /***
     * In case the LifeCycle of the statement is Sync, the response will be collected by this function instead of an handler.
     *
     * @return the response as an object (the type an semantic depends on the statement).
     * */
    public Object getSynchronousResponse();
    /**
     * Alike to the equals from the Object class.
     * The frameworks needs to determinate if two statements are logically equal or not.
     *
     * @see java.lang.Object
     * */
    public boolean equals(Object org);
    /**
     * Alike to the hashCode from the Object class.
     * The frameworks needs to determinate if two statements are logically equal or not,
     * and if equal is overwritten then hashCode must be overwrite too.
     *
     *
     * @see java.lang.Object
     * */
    public int hashCode();

    /***
     * Setts the scope. The scope is the output endpoints (e.g. broker, http server) where the events will be published
     *
     * @param scopes  are a list of  aliases of the endpoints where will be published
     * */
    public void setScope(String[] scopes);
    /***
     * The output topics/paths where the events will be published/posted
     *
     * @param output array of output topics/paths as string
     * */
    public void setOutput(String[] output);
    /***
     * The input topics/paths of the statement (DEPRECATED)
     *
     * @param  input is the list of topics/paths to be set in the statement
     * */
    @Deprecated
    public void setInput(String[] input);
    /***
     * Sets the sources. The source message handler (broker/http server) where the events are coming.
     *
     * @param source as alias of message handler (broker/http server) as string
     * */
    public void setSource(String source);
    /***
     * Set the statement. The native statement for specific underling implementation of a CEP engine (e.g. EPL)
     *
     * @param statement in the native CEP language as string
     * */
    public void setStatement(String statement);
    /***
     * sets the name of the statement. The user defined name of the statement
     *
     * @param name is the new given name of the statement as string
     * */
    public void setName(String name);
    /***
     * setts the handler selected to process the result of the complex event, @Default ComplexEventHandlerImpl.
     * Note: The value "" or null is a valid response, this value represent silent events, events that just happen inside the CEP engine.
     *
     *
     * @param  CEHandler is the canonical name of ComplexEventHandler of the statement, @Default ComplexEventHandlerImpl..
     *
     * */
    public void setCEHandler(String CEHandler);
    /***
     * sets the state of the Statement, which determines how the statement will be at runtime.
     *
     * @see StatementLifecycle .
     * @param  stateLifecycle is the Lifecycle Statement State
     *
     * */
    public void setStateLifecycle(StatementLifecycle stateLifecycle);
    /***
     * In case the LifeCycle of the statement is Sync, the response will be set by this function instead of an handler.
     *
     * @param response the response as an object (the type an semantic depends on the statement).
     * */
    public void setSynchronousResponse(Object response) ;
    /***
     * setts the hash ID of the statement. By default this is the SHA256 of the statement.
     *
     * @param id as string.
     * */
    public void setId(String id);
    /***
     * setts the IDs of the selected agents which are targeted to process this statement. If the array is empty means all receivers @Default Empty String[].
     *
     * @param targetAgents is the list of targeted agents address to process the statement, otherwise empty (all available agents):
     * */
    public void setTargetAgents(List<String> targetAgents);

    /***
     * Represent the possible States of a Statement can be in runtime.
     * The states for a new Statements represent the state how they will be deployed in the engine.
     * For an exiting Statement, the statements represent a change of state
     *
     * */
     public enum StatementLifecycle {
        /**
         * RUN Execute the statement adding a Handler, which adds a actuate or reacts to the triggered statement.
         */
        RUN,
        /**
         * ONCE Execute once, generating a default the response at the moment
         */
        ONCE,
        /**
         * SYNCHRONOUS similar to ONCE, but returns the generated request.
         */
        SYNCHRONOUS,
        /**
         * PAUSE deploy the query in but do not start it
         */
        PAUSE,
        /**
         * REMOVE makes sens in an existing Statement.
         * This will remove the Statement form the CEP engine realising all other resources related to it
         */
        REMOVE
    }

}
