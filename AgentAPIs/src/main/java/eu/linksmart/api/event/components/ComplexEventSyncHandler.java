package eu.linksmart.api.event.components;
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
 * Interfaces which represents the API of the ComplexEventSyncHandler (CESH).
 *
 * The CESH does similarly as the @link ComplexEventHandler. <p>
 * The difference between the CESH and the @link ComplexEventHandler is that the implementation of the handling is
 * for synchronous request. While typical are asynchronous where the handling happens any moment after a statement was deployed
 * in a CEPEngine implementation, the CESH handels the event at the moment the statement is deployed.
 *
 *
 * @author Jose Angel Carvajal Soto
 * @since       1.1.1
 * @see CEPEngine
 *
 * */
public interface ComplexEventSyncHandler extends ComplexEventHandler {
    void update(Object events);

}
