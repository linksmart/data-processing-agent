package eu.linksmart.api.event.components;

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
 * Interface hides the underlying propagation technology used in the IoT Agents.
 * E.g. MQTT, HTTP-REST, files, etc.
 *
 *
 * @author Jose Angel Carvajal Soto
 * @since  1.1.1
 * @see eu.linksmart.api.event.components.ComplexEventHandler
 *
 * */
public interface Publisher {

    /**
     * Transmits the payload in the implementation propagation protocol.
     *
     * @param payload bytes to be propagated
     *
     * @return if the event has been published
     *
     * */
    boolean publish(byte[] payload);
    /**
     * Transmits the payload in the implementation propagation protocol into given output (path) and scope (endpoint).
     *
     * @param payload bytes to be propagated
     * @param output the path where the message will be propagated.
     * @param scope the service which receives the given output.
     *
     * @return if the event has been published
     *
     * */
    boolean publish(byte[] payload, String output, String scope);
    /**
     * Transmits the payload in the implementation propagation protocol into given output (path).
     *
     * @param payload bytes to be propagated
     * @param output the path where the message will be propagated.
     *
     * @return if the event has been published
     *
     * */
    boolean publish(byte[] payload, String output);

    /**
     * returns the pre-configured outputs of the Publisher.
     *
     * @return list of already loaded outputs
     * */
    List<String> getOutputs() ;
    /**
     * sets the outputs of the Publisher.
     *
     * @param outputs is the list of the predefined outputs
     *
     * */
     void setOutputs(List<String> outputs) ;
    /**
     * returns the pre-configured scopes of the Publisher.
     *
     * @return list of already loaded scopes
     * */
     List<String> getScopes() ;
    /**
     * sets the scopes of the Publisher.
     *
     * @param scopes is the list of the predefined scopes
     *
     * */
     void setScopes(List<String> scopes);
    /**
     * returns the pre-configured id of the Publisher.
     * The id provides the additional ID (e.g. Statement ID) where the Publisher will propagate the payload
     *
     * @return id as string
     * */
     String getId();
    /**
     * sets the pre-configured id of the Publisher.
     * The id provides the additional ID (e.g. Statement ID) where the Publisher will propagate the payload
     *
     * @param  id as string
     * */
     void setId(String id) ;
    /**
     * Endorse the Deserializer to release resources if is needed.
     * */
    void close();
}
