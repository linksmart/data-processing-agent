package eu.linksmart.api.event.types;

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
 * Interface hides the underlying http response implementation. This interface provide the needed value to return an HTTP responses.
 * The interface represent an aggregation of HTTP responses
 * This interface extends from Response.
 *
 * @see eu.linksmart.api.event.types.Responses
 *
 * @author Jose Angel Carvajal Soto
 * @since  1.0.0
 * */
public interface HTTPResponses<ResourceObject> extends Responses<ResourceObject> {

    /**
     * return the combined status of the responses
     * @return the code of the combined status of the responses
     * */
    int getOverallStatus();

}
