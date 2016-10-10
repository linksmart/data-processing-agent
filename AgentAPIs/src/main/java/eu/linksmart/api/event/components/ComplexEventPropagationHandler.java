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
 * Interfaces which represents the API of the Complex Event Propagation Handler (CEPH).
 *
 * The CEPH does similarly as the @link ComplexEventHandler. <p>
 * The handler takes the needed steps to handle the event as the ComplexEventHandler.<p>
 * Additionally, the end objective of the CEPH is to propagate end result of the handling through the Network.<p>
 *
 * The handling result, aka complex/compound event will be packed in an enveloped by @link Enveloper, serialized into bytes by
 * a @link Serializer and propagate through a transport protocol using a @link Publisher.
 *
 * @author Jose Angel Carvajal Soto
 * @since      1.3.1
 * @see CEPEngine
 * @see eu.linksmart.api.event.components.Enveloper
 * @see eu.linksmart.api.event.components.Publisher
 *
 */
public interface ComplexEventPropagationHandler extends ComplexEventHandler {

    /**
     * Sets the Enveloper used by the handler. If non is given an Default implementation should be used.
     *
     * @param enveloper used to pack the compound event received by the @link CEPEngine.
     *
     * */
    public void setEnveloper(Enveloper enveloper);
    /**
     * Sets the Publisher used by the handler. If non is given an Default implementation should be used.
     *
     * @param publisher used to propagate the already packed and serialized compound-event received by the @link CEPEngine.
     *
     * */
    public void setPublisher(Publisher publisher);
    /**
     * Provide the Enveloper used by the handler. If non is given an Default implementation should be used.
     *
     * @return an Enveloper used to pack the compound event received by the @link CEPEngine.
     *
     * */
    public Enveloper getEnveloper();
    /**
     * Provide the Publisher used by the handler. If non is given an Default implementation should be used.
     *
     * @return an Publisher used to propagate the already packed and serialized compound-event received by the @link CEPEngine.
     *
     * */
    public Publisher getPublisher();


}
