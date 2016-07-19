package eu.linksmart.api.event.ceml.data;

import eu.linksmart.api.event.ceml.JsonSerializable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public interface DataDescriptor extends JsonSerializable {

    public static DataDescriptor factory(DescriptorTypes type, String name, boolean isTarget) throws Exception {
        DataDescriptor result;
        switch (type){
            case NOMINAL_CLASSES:
                throw new Exception("The definition of classes needs the classes as parameter");

            case DATE:
                result = new DataDescriptorInstance(name,Date.class.getComponentType(),isTarget);
                break;
            case INTEGER:
                result= new DataDescriptorInstance(name,Integer.class.getComponentType(),isTarget);
                break;
            case NUMBER:
            case DOUBLE:
            default:
                result= new DataDescriptorInstance(name,Double.class.getComponentType(),isTarget);

        }

        return result;
    }
    public static <T> DataDescriptor factory(DescriptorTypes type, String name, List<String> classes, Function<T, Integer> selectionFunction, boolean isTarget) throws Exception {
        if(type == DescriptorTypes.NOMINAL_CLASSES)
            return ClassesDescriptor.factory(name, classes, selectionFunction,isTarget);
        return factory(type,name,isTarget);
    }
    public static <T> DataDescriptor factory(DescriptorTypes type, String name,List<String> classes, boolean isTarget) throws Exception {
        if(type == DescriptorTypes.NOMINAL_CLASSES)
            return ClassesDescriptor.factory(name, classes,isTarget);
        return factory(type,name,isTarget);
    }
    Class getNativeType();
    String getName();
    boolean isTarget();
    boolean isClassesDescription();
    ClassesDescriptorInstance getClassesDescription();
    public Class getType();
    public enum DescriptorTypes{
        NOMINAL_CLASSES,INTEGER,DOUBLE,NUMBER, DATE

    }
}
