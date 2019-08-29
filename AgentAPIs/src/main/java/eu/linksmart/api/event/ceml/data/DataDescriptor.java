package eu.linksmart.api.event.ceml.data;

import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UnknownUntraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.JsonSerializable;

import java.util.Date;
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
 * This interfaces represent a description of a simple data that is part of a set of data that a model will get for learning/training, evaluating or predicting.
 * A DataDescriptor can be either a description of part of the Input or part of the Target.
 * A non-nominal DataDescriptor must be either DATE, INTEGER, DOUBLE or NUMBER.
 * In case of a nominal description (e.g. a taxonomy) then the ClassesDescriptor interface should be used.
 *
 *
 *
 * @author Jose Angel Carvajal Soto
 * @since       1.1.1
 * @see eu.linksmart.api.event.ceml.data.DataDescriptors
 * @see eu.linksmart.api.event.types.JsonSerializable
 * @see eu.linksmart.api.event.ceml.data.ClassesDescriptor
 *
 * */
@Deprecated
public interface DataDescriptor extends JsonSerializable {
    /**
     * Creates an lambda DataDescriptor of the reference implementation of a DataDescriptorInstance
     *
     * @param type of the data bing described as DescriptorTypes
     * @param name name of the data as String.
     * @param isTarget if <code>true</code> set the description as Target, Input otherwise.
     *
     * @return a instance of a lambda DataDescriptor implemented by DataDescriptorInstance.
     *
     * @throws UnknownUntraceableException  if any unknown error occurs {@link UnknownUntraceableException}
     *
     * @see eu.linksmart.api.event.ceml.data.DataDescriptorInstance
     * @see eu.linksmart.api.event.ceml.data.DataDescriptor.DescriptorTypes
     *
     * */
    static DataDescriptor factory(DataDescriptors.DescriptorTypes type, String name, boolean isTarget) throws UnknownUntraceableException {
        try {
            return factory(type,name,null,null,null,isTarget);
        } catch (Exception e) {
            throw new UnknownUntraceableException(e);
        }
    }
    /**
     * Creates an DataDescriptor of the reference implementation of a DataDescriptorInstance
     *
     * @param type of the data bing described as DescriptorTypes
     * @param name name of the data as String.
     * @param classes if the descriptor describe a set of classes (taxonomy), the classes must be given as a List of Strings
     * @param inputClassType Native Java class that the model will be provided.
     * @param selectionFunction In case of set of classes, a selection function can be provided.
     * @param isTarget if <code>true</code> set the description as Target, Input otherwise.
     * @param <T> is the native type of the label.
     *
     * @return a instance of a DataDescriptor implemented by DataDescriptorInstance.
     *
     * @throws UntraceableException  if any error that cannot be trace occurs {@link UntraceableException}
     * @throws TraceableException  if any error that can be trace occurs {@link TraceableException}
     *
     * @see eu.linksmart.api.event.ceml.data.DataDescriptorInstance
     * @see eu.linksmart.api.event.ceml.data.DataDescriptor.DescriptorTypes
     *
     * */
    static <T> DataDescriptor factory(DataDescriptors.DescriptorTypes type, String name, List<String> classes, Class<T> inputClassType, Function<T, Integer> selectionFunction, boolean isTarget) throws TraceableException, UntraceableException {
        DataDescriptor result;
        switch (type){
            case NOMINAL_CLASSES:
                return ClassesDescriptor.factory(name, classes,inputClassType,selectionFunction,isTarget);
            case DATE:
                result = new DataDescriptorInstance(name,Date.class,isTarget);
                break;
            case INTEGER:
                result= new DataDescriptorInstance(name,Integer.class,isTarget);
                break;
            case BOOLEAN:
                result= new DataDescriptorInstance(name,Boolean.class,isTarget);
                break;
            case OBJECT:
                result= new DataDescriptorInstance(name,Object.class,isTarget);
                break;
            case NUMBER:
            case DOUBLE:
            default:
                result= new DataDescriptorInstance(name,Double.class,isTarget);
        }

        return result;
    }
    /**
     * Creates an DataDescriptor of the reference implementation of a DataDescriptorInstance
     *
     * @param type of the data bing described as DescriptorTypes
     * @param name name of the data as String.
     * @param classes if the descriptor describe a set of classes (taxonomy), the classes must be given as a List of Strings
     * @param isTarget if <code>true</code> set the description as Target, Input otherwise.
     *
     * @return a instance of a DataDescriptor implemented by DataDescriptorInstance.
     *
     * @throws UntraceableException  if any error that cannot be trace occurs {@link UntraceableException}
     * @throws TraceableException  if any error that can be trace occurs {@link TraceableException}
     *
     * @see eu.linksmart.api.event.ceml.data.DataDescriptorInstance
     * @see eu.linksmart.api.event.ceml.data.DataDescriptor.DescriptorTypes
     *
     * */
    static DataDescriptor factory(DataDescriptors.DescriptorTypes type, String name,List<String> classes, boolean isTarget) throws TraceableException, UntraceableException  {
        return factory(type,name,classes,null,null,isTarget);
    }
    /**
     * @return native value of the described data
     * */
    Class getNativeType();
    /**
     * @return name of the described data
     * */
    String getName();
    /**
     * @return if the data is part of the Target, otherwise is Input
     * */
    boolean isTarget();
    /**
     * @return if the data is a description of the classes.
     * */
    default boolean isClassesDescription() { return false;}
    /**
     * @return declarative type of the described data
     * */
    DataDescriptors.DescriptorTypes getType();
    /**
     * set the isTarget property as: isTarget = not isTarget
     * */
    void toggleTarget();
    boolean isAssignable(Class type);
    /**
     *
     * Enumeration of declarative types possible in for a DataDescriptor
     *
     * */
    enum DescriptorTypes{
        /**
         * If the descriptor is a description of classes, java native translation is ClassesDescriptor
         * @see eu.linksmart.api.event.ceml.data.ClassesDescriptor
         * */
        NOMINAL_CLASSES,
        /**
         * If the descriptor is an Integer, java native translation is Integer
         * @see java.lang.Integer
         * */
        INTEGER,
        /**
         * If the descriptor is an Double, java native translation is Double
         * @see java.lang.Double
         * */
        DOUBLE,
        /**
         * If the descriptor is an Number, java native translation is Double
         * @see java.lang.Double
         * */
        NUMBER,
        /**
         * If the descriptor is an BOOLEAN, java native translation is Boolean
         * @see java.lang.Double
         * */
        BOOLEAN,
        /**
         * If the descriptor is an OBJECT, java native translation is Object
         * @see java.lang.Double
         * */
        OBJECT,
        /**
         * If the descriptor is an DATE, java native translation is Date
         * @see java.util.Date
         * */
        DATE;

        /**
         * Translates from the declarative type to the java native type
         * @param type as DescriptorTypes
         *
         * @return the type as a Java Class class
         * */
        static public Class getNativeType(DescriptorTypes type){
            switch (type){
                case NOMINAL_CLASSES:
                    return ClassesDescriptor.class;
                case DATE:
                    return Date.class;
                case INTEGER:
                    return Integer.class;
                case BOOLEAN:
                    return Boolean.class;
                case OBJECT:
                    return Object.class;
                case NUMBER:
                case DOUBLE:
                default:
                    return Double.class;

            }
        }

    }
}
