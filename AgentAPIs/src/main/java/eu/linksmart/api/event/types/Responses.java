package eu.linksmart.api.event.types;

import eu.linksmart.api.event.types.impl.GeneralRequestResponse;

import java.util.Collection;

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
 * The interface represents responses to a CRUD request of a service provided by the an Agent.
 *
 * The Resource is the type of the resource/object that the request responds to.
 * E.g. If the responses is a response to an <code>add</code> function that add a <code>Statement</code>,
 * then the Resource is the Statement.
 *
 * @see eu.linksmart.api.event.types.JsonSerializable
 * @see eu.linksmart.api.event.types.HTTPResponses
 *
 * @author Jose Angel Carvajal Soto
 * @since  1.0.0
 *
 *
 * */
public interface Responses <Resource> extends JsonSerializable {

    /**
     * returns the a collection of responses as GeneralRequestResponse.
     *
     * @return the a collection of responses.
     * */
    Collection<GeneralRequestResponse> getResponses() ;
    /**
     * setts the a collection of responses as GeneralRequestResponse.
     *
     * @param generalRequestResponses is the collection of responses to set.
     * */
    void setResponses(Collection<GeneralRequestResponse> generalRequestResponses) ;
    /**
     * adds a collection of responses to the existing ones
     *
     * @param generalRequestResponses is the collection of responses to be added.
     * */
    void addAllResponses(Collection<GeneralRequestResponse> generalRequestResponses) ;
    /**
     * returns the resource/s that the CRUD request is working with
     *
     * @return the resource/s as the Resource type
     * */
    Resource getResources() ;
    /**
     * setts the resource/s that the CRUD request is working with
     *
     * @param resources is/are resources that the CRUD request is working with.
     * */
    void setResources(Resource resources) ;
    /**
     * adds a response to the existing ones
     *
     * @param generalRequestResponse is the response to be added.
     * */
    void addResponse(GeneralRequestResponse generalRequestResponse);
    /**
     * Efficient way to determined if any of the responses if a successful response.
     *
     * @return if any of the responses is successful.
     * */
    boolean containsSuccess();
    /**
     * Provided the last request that had being added to the responses
     *
     * @return the last response as GeneralRequestResponse
     * */
    GeneralRequestResponse getResponsesTail();

}
