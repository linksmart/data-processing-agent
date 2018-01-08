package eu.linksmart.api.event.types;

import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;

import java.io.Serializable;
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
 * This interface indicates that the class that implements it can be serialized/deserialized in a JSON document.
 *
 *
 * @author Jose Angel Carvajal Soto
 * @since  1.0.0
 * */
public interface JsonSerializable extends Serializable{

    /**
     * when this is generated out of an JSON document, this function must be call if additionally object construction process must be started.
     * @return a reference of this
     * @exception eu.linksmart.api.event.exceptions.StatementException if the given JSON document that generated the object, it does not reached the needed attributes to be build.
     * @exception eu.linksmart.api.event.exceptions.InternalException if while the postprocessing some known logic failed.
     * @exception eu.linksmart.api.event.exceptions.UnknownException if while the postprocessing some unknown logic failed.
     * @exception eu.linksmart.api.event.exceptions.UntraceableException if while the postprocessing some known logic failed and the JSON document or structure of the object do not possess any referenceable id.
     * @exception eu.linksmart.api.event.exceptions.UnknownUntraceableException  if while the postprocessing some unknown logic failed and the JSON document or structure of the object do not possess any referenceable id.
     * */
    JsonSerializable build() throws TraceableException, UntraceableException;
 //   void rebuild(T me) throws Exception;
    /**
     * If the parsed object needs to released some resources generated in the build process.
     *
     * @exception java.lang.Exception if some resource cannot released.
     * */
    void destroy()throws Exception;
}
