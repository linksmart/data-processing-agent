package eu.linksmart.api.event.ceml.data;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
 interface DataNominalDescriptor {

    public static DataNominalDescriptor factory(DescriptorTypes type, String name) throws Exception {
        DataNominalDescriptor result;
        switch (type){
            case NOMINAL_CLASSES:
                throw new Exception("The definition of classes needs the classes as parameter");

            case DATE:
                result = new DataDescriptorInstance(name,Date.class.getComponentType());
                break;
            case INTEGER:
                result= new DataDescriptorInstance(name,Integer.class.getComponentType());
                break;
            case NUMBER:
            case DOUBLE:
            default:
                result= new DataDescriptorInstance(name,Double.class.getComponentType());

        }
        return result;
    }
    public static <T> DataNominalDescriptor factory(DescriptorTypes type, String name, Map<T, String> classes, Function<T, T> selectionFunction) throws Exception {
        if(type == DescriptorTypes.NOMINAL_CLASSES)
            return ClassesDataDescriptor.factory(name,classes,selectionFunction);
        return factory(type,name);
    }
    public static <T> DataNominalDescriptor factory(DescriptorTypes type, String name, Map<T, String> classes) throws Exception {
        if(type == DescriptorTypes.NOMINAL_CLASSES)
            return ClassesDataDescriptor.factory(name,classes);
        return factory(type,name);
    }
    Class getNativeType();
    String getName();
    boolean isClassesDescription();
    ClassesDescriptorInstance getClassesDescription();

    public enum DescriptorTypes{
        NOMINAL_CLASSES,INTEGER,DOUBLE,NUMBER, DATE

    }
}
