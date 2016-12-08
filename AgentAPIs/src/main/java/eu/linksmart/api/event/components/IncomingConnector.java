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

import eu.linksmart.api.event.exceptions.UntraceableException;

import java.util.Observer;

/**
 * This interface indicates that the implementation of it is a IncomingConnector of the agent.
 * A IncomingConnector is the service that receives the payload from an specific protocol,
 * and sends the payload to the corresponding feeder for further processing.
 * The connectors are the services provide the connection to a protocol that gives access to the API provided by a Feeder.
 * There should be at least one feeder per connection/access made by a protocol.
 *
 * @author Jose Angel Carvajal Soto
 * @since 1.3.1
 * @see eu.linksmart.api.event.components.Feeder
 *
 * */
public interface IncomingConnector {
    /**
     * returns if a IncomingConnector is running and "connected"
     *
     * @return <code>true</code> if is run and connected, <code>false</code> otherwise.
     * */
    boolean isUp();
    /**
     * Creates a subscription to data arriving from the given protocol in the given host to the given topic, the result will be notified to the given observer.
     *
     * @param protocol the desired protocol sources of the messages
     * @param host the desired host sources of the messages
     * @param topic the desired topic sources of the messages
     * @param observer the callbacker that will receive the messages when arrived
     *
     * @exception UntraceableException if the given observer is already in use in other subscription
     *
     * */
    //void subscribe(String protocol, String host, String topic, Observer observer) throws UntraceableException;
    /*
     * Creates a subscription to data arriving from the given protocol in the given host to the given topic, the result will be notified to the given observer.
     *
     * @param protocol the desired protocol sources of the messages
     * @param topic the desired topic sources of the messages
     * @param observer the callbacker that will receive the messages when arrived
     *
     * @exception UntraceableException if the given observer is already in use in other subscription
     *
     * */
   // void subscribe(String protocol, String topic, Observer observer) throws UntraceableException;
    /*
     * Creates a subscription to data arriving from the given protocol in the given host to the given topic, the result will be notified to the given observer.
     *
     * @param topic the desired topic sources of the messages
     * @param observer the callbacker that will receive the messages when arrived
     *
     * @exception UntraceableException if the given observer is already in use in other subscription
     * */
    //void subscribe(String topic, Observer observer) throws UntraceableException;
    /*
     * Remove a subscription of a observer
     * @param observer to remove
     *
     * * */

    //boolean unsubscribe(Observer observer)throws UntraceableException;

}
