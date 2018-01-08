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

import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

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

public interface Feeder <T> extends AnalyzerComponent {
    Map<String,Feeder> feeders = new Hashtable<>();

    static Feeder factory(String feederName) throws Exception {
        if (!feeders.containsKey(feederName)) {
            Class.forName("eu.linksmart.services.event.feeders." + feederName).getConstructor().newInstance();
        }
        return feeders.get(feederName);
    }
    void feed(String topicURI, String payload)throws TraceableException, UntraceableException;
    void feed(String topicURI, T payload)throws TraceableException, UntraceableException;


}
