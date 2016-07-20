package eu.linksmart.api.event.ceml.data;

import eu.linksmart.api.event.datafusion.JsonSerializable;

import java.util.Date;
import java.util.List;
import java.util.function.Function;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public interface DataDescriptor extends JsonSerializable {

    public static DataDescriptor factory(DescriptorTypes type, String name, boolean isTarget) throws Exception {
      return factory(type,name,null,null,null,isTarget);
    }
    public static <T> DataDescriptor factory(DescriptorTypes type, String name, List<String> classes, Class<T> inputClassType, Function<T, Integer> selectionFunction, boolean isTarget) throws Exception {
        DataDescriptor result;
        switch (type){
            case NOMINAL_CLASSES:
                return ClassesDescriptor.factory(name, classes,inputClassType,selectionFunction,isTarget);
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
    public static DataDescriptor factory(DescriptorTypes type, String name,List<String> classes, boolean isTarget) throws Exception {
        return factory(type,name,classes,null,null,isTarget);
    }
    public Class getNativeType();
    public String getName();
    boolean isTarget();
    boolean isClassesDescription();
    DataDescriptor getClassesDescription();
    public DescriptorTypes getType();
    public enum DescriptorTypes{
        NOMINAL_CLASSES,INTEGER,DOUBLE,NUMBER, DATE;

        static public Class getNativeType(DescriptorTypes type){
            switch (type){
                case NOMINAL_CLASSES:
                    return ClassesDescriptor.class;
                case DATE:
                    return Date.class;
                case INTEGER:
                    return Integer.class;
                case NUMBER:
                case DOUBLE:
                default:
                    return Double.class;

            }
        }

    }
}
