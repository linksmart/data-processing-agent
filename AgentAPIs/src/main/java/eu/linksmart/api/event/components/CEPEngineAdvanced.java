package eu.linksmart.api.event.components;

import eu.linksmart.api.event.exceptions.InternalException;

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
 *
 * The interface provide access to advance features of a Complex-Event Processing (CEP) engine.
 * This interface is the advanced functionality offered by the agent but not needed use an CEP engine.
 * The implementation of this interface should be a service.
 * Any engine that provide the CEPEngine interface but this one will still be usable in the agent.
 *
 * @author Jose Angel Carvajal Soto
 * @since       1.0.1
 * @see  AnalyzerComponent
 *
 * */
public interface CEPEngineAdvanced extends CEPEngine {
    /**
     * Insert an object into the CEP engine
     * @param name (or alias) of the object will be referenced inside the engine.
     * @param variable the object to be inserted.
     * @param <T> native type of the object to insert in the Engine
     *
     *
     * @exception UnsupportedOperationException is thrown if this operation is not supported by the engine.
     *
     * */
    <T>void insertObject(String name,T variable) throws UnsupportedOperationException;
    /**
     * Add additional package used in the engine
     * @param canonicalNameClassOrPkg String representing a class or a package path containing classes (e.g. my.package.*).
     *
     * @return <code>true</code> if the Class or Package Path was loaded into the CEP engine. <code>false</code> otherwise.
     *
     * @exception eu.linksmart.api.event.exceptions.InternalException is thrown if an known exception happens, but the source of
     * the exception is not related to the input itself. In other words, exceptions which are known and expected by the developer.
     * */
    boolean loadAdditionalPackages( String canonicalNameClassOrPkg) throws InternalException;
    /**
     * Set the internal clock of the engine
     *
     * @param date the new given time of the engine.
     *
     * @return <code>true</code> if the new time was set successful. <code>false</code> otherwise.
     *
     * @exception eu.linksmart.api.event.exceptions.InternalException is thrown if an known exception happens, but the source of
     * the exception is not related to the input itself. In other words, exceptions which are known and expected by the developer.
     * */
    boolean setEngineTimeTo( Date date)throws InternalException;
    /**
     * Provides the  internal time of the engine. The time of the engine and the real time may differ.
     *
     * @return <code>Date</code> with the internal time of the engine.
     *
     * */
    Date getEngineCurrentDate();

    /**
     * Remove, destroy, drops an inserted object by insertObject function.
     *
     * @param name name of the object to be remove in the CEP engine
     *
     * @return <code>true</code> if the object existed and was removed. <code>false</code> otherwise.
     *
     * */
    boolean dropObject(String name);
}
