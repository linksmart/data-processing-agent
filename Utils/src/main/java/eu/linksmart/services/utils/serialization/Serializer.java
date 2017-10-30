package eu.linksmart.services.utils.serialization;




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
 * Interface hides the underlying serialization technology used in the IoT Agents.
 * E.g. Jackson, Gson, etc.
 *
 *
 * @author Jose Angel Carvajal Soto
 * @since  1.1.1
 *
 * */
public interface Serializer {
    /**
     * Serialize in bytes a given object.
     *
     * @param <T> type of the object to be serialized
     * @param object to be serialized
     *
     * @return the serialized object
     * @exception java.io.IOException if the object cannot be serialized
     * */
    public<T> byte[] serialize(T object) throws IOException;

    /**
     * Serialize in a string a given object.
     *
     * @param <T> type of the object to be serialize
     * @param object to be serialized
     *
     * @return the serialized object as string
     * @exception java.io.IOException if the object cannot be serialized
     * */
    public<T> String toString(T object) throws IOException;

    <T> void addModule(String name, Class<T> tClass, SerializerMode<T> serializerMode);

    <I,C extends I> void addModule(String name, Class<I> tInterface, Class<C> tClass);

    /**
     * Endorse the Deserializer to release resources if is needed.
     * */
    void close();
}
