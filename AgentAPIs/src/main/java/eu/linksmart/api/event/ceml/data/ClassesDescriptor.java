package eu.linksmart.api.event.ceml.data;



import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UnknownUntraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;

import java.util.List;
import java.util.function.Function;

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
 * This interfaces represent a description of a classes of data that is part of a set of data that a model will get for learning/training, evaluating or predicting.
 * A DataDescriptor can be either a description of part of the Input or part of the Target.
 * A nominal data is typically for defining the possible classification classes available.
 *
 *
 *
 * @author Jose Angel Carvajal Soto
 * @since       1.1.1
 * @see eu.linksmart.api.event.ceml.data.DataDescriptor
 * @see eu.linksmart.api.event.types.JsonSerializable
 *
 * */
@Deprecated
public interface ClassesDescriptor extends DataDescriptor {
    /**
     * Creates an DataDescriptor of the reference implementation of a ClassesDescriptorInstance
     *
     * @param name name of the data as String.
     * @param classes if the descriptor describe a set of classes (taxonomy), the classes must be given as a List of Strings
     * @param isTarget if <code>true</code> set the description as Target, Input otherwise.
     *
     * @return a instance of a DataDescriptor implemented by ClassesDescriptorInstance.
     *
     * @throws UnknownUntraceableException  if any error occurs
     *
     * @see eu.linksmart.api.event.ceml.data.DataDescriptorInstance
     * @see eu.linksmart.api.event.ceml.data.DataDescriptor.DescriptorTypes
     *
     * */
    static ClassesDescriptor factory(String name, List<String> classes, boolean isTarget) throws UnknownUntraceableException {

        try {
            return new ClassesDescriptorInstance(name,classes,isTarget);
        } catch (Exception e) {
            throw new UnknownUntraceableException(e);
        }
    }
    /**
     * Creates an DataDescriptor of the reference implementation of a ClassesDescriptorInstance
     *
     * @param name name of the data as String.
     * @param classes if the descriptor describe a set of classes (taxonomy), the classes must be given as a List of Strings
     * @param functionInputType determinate the input type of the selection function
     * @param selectionFunction In case of set of classes, a selection function can be provided.
     * @param isTarget if <code>true</code> set the description as Target, Input otherwise.
     * @param <F> is the native type of the label.
     *
     * @return a instance of a DataDescriptor implemented by ClassesDescriptorInstance.
     *
     * @throws UnknownUntraceableException  if any error occurs
     *
     * @see eu.linksmart.api.event.ceml.data.DataDescriptorInstance
     * @see eu.linksmart.api.event.ceml.data.DataDescriptor.DescriptorTypes
     *
     * */
    static <F> ClassesDescriptor factory( String name, List< String> classes,Class<F> functionInputType, Function<F, Integer> selectionFunction, boolean isTarget) throws UnknownUntraceableException {

        try {
            ClassesDescriptor classesDescriptor = new ClassesDescriptorInstance(name,classes,isTarget);

            classesDescriptor.setSelectionFunction(selectionFunction,functionInputType);

            return classesDescriptor;
        } catch (Exception e) {
            throw new UnknownUntraceableException(e);
        }
    }
    /**
     * Determinate the class as string using the selection class.
     * @param selectionParameter is the parameter used by the selection function to select a class of the possible defined classes.
     * @param <F> is the native type of the label.
     *
     * @throws TraceableException probably this may never be triggered.
     * @throws UntraceableException
     *      either {@link UnknownUntraceableException}, in the case an Uncontrolled exception happen;
     *      or either {@link UntraceableException}, in the case the selection function is not set or it cannot be cast to a number.
     *
     * @return the class as string
     * */
    <F >String  getClass(F selectionParameter) throws TraceableException, UntraceableException;
    /**
     * Determinate the class as string using the selection class.
     * @param selectionParameter is the parameter used by the selection function to select a class of the possible defined classes.
     * @param <F> is the native type of the label.
     *
     * @throws TraceableException in the case the selection function is not set or it cannot be cast to a number.
     * @throws UntraceableException a {@link UnknownUntraceableException}, in the case an Uncontrolled exception happen;
     *
     * @return the class as string
     * */
    <F> Integer getIndexClass(F selectionParameter) throws TraceableException, UntraceableException;
    /**
     * setts the selection class used to select a class to a string.
     * @param function is the selection function
     * @param type type of the class (Date, Int, etc.)
     * @param <F> is the native type of the label.
     *
     * */
    <F> void setSelectionFunction(Function<F, Integer> function,Class<F> type);
    /**
     * gets the i-esm class of the classes.
     * @param i is the value to be selected
     *
     * @return  the class as string
     * */
    String getClass(int i);
    /**
     * setts the i-esm class of the classes.
     * @param i is the value to be set
     * @param clazz the new value of the i-esm class
     *
     * */
    void setClass(int i,String clazz);
    /**
     * @return all defined classes
     *
     * */
    List<String> getClasses();
    /**
     * setts all defined classes.
     *
     * @param  classes is the new set of classes
     *
     * */
    void setClasses(List<String> classes);
    /**
     * @return if the data is a description of the classes.
     * */
    default boolean isClassesDescription() { return true;}
}
