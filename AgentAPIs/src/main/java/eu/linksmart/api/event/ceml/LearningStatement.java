package eu.linksmart.api.event.ceml;

import eu.linksmart.api.event.types.JsonSerializable;
import eu.linksmart.api.event.types.Statement;

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
 * This interfaces represent a learning statement in a CEML learning request.
 * The Learning Statement has different default Handler and require the reference to
 * the CEMLRequest where the statement was defined.
 *
 *
 * @author Jose Angel Carvajal Soto
 * @since       1.1.1
 * @see  eu.linksmart.api.event.components.CEPEngine
 * @see eu.linksmart.api.event.types.JsonSerializable
 * @see eu.linksmart.api.event.ceml.CEMLRequest
 *
 * */
public interface LearningStatement  extends Statement,JsonSerializable {
    /**
     * Returns the CEMLRequest where this LearningStatement was created
     *
     * @return the CEMLRequest where this LearningStatement was created
     * */
    CEMLRequest  getRequest();
    /**
     * Returns the CEMLRequest where this LearningStatement was created
     *
     * @param request that is the CEMLRequest where this LearningStatement was created
     * */
    void setRequest(CEMLRequest  request);
}
