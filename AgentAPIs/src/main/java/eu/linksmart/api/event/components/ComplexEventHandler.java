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
 * Interfaces which represents the API of the Complex Event Handler (CEH).
 * 
 * The CEH make an abstraction between the CEP-Engine/s and the action taken by the component which receives the repose of the CEP (Handler implementation).<p>
 * The handler takes the needed steps to handle the event
 * 
 * @author Jose Angel Carvajal Soto
 * @since       0.03
 * @see CEPEngine
 *
 * */

public interface ComplexEventHandler extends AnalyzerComponent{

    /***
     *
     * Terminate the Handler, releasing any resource us by it.
     *
     * */
    void destroy();




}
