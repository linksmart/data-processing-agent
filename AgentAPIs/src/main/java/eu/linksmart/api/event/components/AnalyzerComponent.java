package eu.linksmart.api.event.components;

import eu.almanac.event.datafusion.utils.generic.Component;
import eu.almanac.event.datafusion.utils.generic.ComponentInfo;

import java.util.Hashtable;
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
 *
 * The objects that implements this class of this interfaces are treated as Analytical components of the Agents.
 * The classes that implement this interface must add themselves into loaddedComponets Map. This map register all
 * analytical engine loaded in the agent. The reason to use such interface is to allow dynamic loading of
 * extensions in the engine.
 *
 * NOTES: I'm not happy (Angel) the way this features solved the problem. It is not traceable and require the
 * developer to implement correctly. This had being proof bit difficult. Not all components have implemented the
 * interface, therefore the information is incomplete. Anyhow, the need to know all loaded components in the agent
 * is still there. Till no new methodology replace this one this method will stay, and marked as deprecated.
 * @author Jose Angel Carvajal Soto
 * @since 1.0.0
 * @see eu.almanac.event.datafusion.utils.generic.Component
 *
 * */
@Deprecated
public interface AnalyzerComponent {
    Map<String,Map<Component,ComponentInfo>> loadedComponents = new Hashtable();


}
