package eu.linksmart.services.utils.serialization;


import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
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
 * Interface hides the underlying deserialization or parsing technology used in the IoT Agents.
 * E.g. Jackson, Gson, Java serialization, etc.
 *
 *
 * @author Jose Angel Carvajal Soto
 * @since  1.1.1
 *
 * */
public interface Deserializer {
    /**
     * The class takes a text representation of an object, probably json, and parse and construct an java object from it.
     *
     * @param <T> type of the object to be parsed
     * @param objectString is the string representation of an object
     * @param tClass is the java class that will be constructing using the object string representation
     *
     * @return the instantiation of the object represented by te objectString
     *
     * @exception java.io.IOException when the parsing didn't work
     * @exception sun.reflect.generics.reflectiveObjects.NotImplementedException when this method had not implemented.
     * This may happens if the Deserializer is used just for byte[] to object and not string to object, as deserializer and not as parser.
     * In this case, the function <code>deserialize</code> must be implemented.
     *
     *
     * */
    <T> T parse(String objectString, Class<T> tClass) throws IOException, NotImplementedException;
    /**
     * The class takes a array of bytes which may be a serialized object or the serialization of the string representation of an object,
     * and instantiate an object out of it.
     *
     * @param <T> type of the object to be deserialized
     * @param bytes is array of bytes that either is an serialized object or the serialized string representation of an object.
     * @param tClass is the java class that will be constructing using the object string representation
     *
     * @return the instantiation of the object obtained from the array of bytes.
     *
     * @exception java.io.IOException when the serialization didn't work
     * @exception sun.reflect.generics.reflectiveObjects.NotImplementedException when this method had not implemented.
     * This may happens if the Deserializer is used just for string to object and not byte[] to object, as parser and not as deserializer.
     * In this case, the function <code>parse</code> must be implemented.
     *
     *
     * */
    <T> T deserialize(byte[] bytes, Class<T> tClass) throws IOException, NotImplementedException;

    <I,C extends I> boolean defineClassToInterface(Class<I> tInterface,Class<C> tClass );
    /**
     * Endorse the Deserializer to release resources if is needed.
     * */
    void close();
}
