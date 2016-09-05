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
 * This interface indicates that the implementation of it is a Feeder of the agent.
 * A Feeder is a service that inputs data (control, event, metadata, etc.) from payloads coming from some protocol and it is inserted to the CEPEngine.
 * There should be one Feeder per payload type.
 * 
 * 
 * @author Jose Angel Carvajal Soto
 * @version     0.03
 * @since       0.03
 * @see  eu.linksmart.api.event.components.CEPEngine
 * @see eu.linksmart.api.event.components.IncomingConnector
 * 
 * */

public interface Feeder extends AnalyzerComponent {


}
