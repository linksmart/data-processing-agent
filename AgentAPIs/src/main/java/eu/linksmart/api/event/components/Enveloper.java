package eu.linksmart.api.event.components;

import eu.linksmart.api.event.types.EventEnvelope;

import java.util.Date;

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
 * Interface hides the underlying Enveloper technology used in the IoT Agents.
 * The Enveloper is the component that packs the compound event in an package named envelope.
 * The envelope can be anything that implements the EventEnvelope class.
 *
 *
 * @author Jose Angel Carvajal Soto
 * @since  1.1.1
 * @see eu.linksmart.api.event.components.Feeder
 * @see eu.linksmart.api.event.components.IncomingConnector
 * @see eu.linksmart.api.event.types.EventEnvelope
 *
 * */
public interface Enveloper {
    /**
     * The function packs the class into an implementation of the EventEnvelope. This function can be seen as a factory of an
     * implementation of an EventEnvelop.
     *
     * @param payload the value or payload of the event to be packed in the envelope.
     * @param date is the time to be set in the envelop
     * @param id to be set in the envelop
     * @param idProperty to be set in the envelop
     * @param description an label describing the payload
     *
     * @return an implementation instance of an EventEnvelop.
     *
     * */
    public <IDType, ValueType> EventEnvelope pack(ValueType payload, Date date, IDType id, IDType idProperty, String description);

    /**
     * Endorse the Enveloper to release resources if is needed.
     * */
    void close();
}
