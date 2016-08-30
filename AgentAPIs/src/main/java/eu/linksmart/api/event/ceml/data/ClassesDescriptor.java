package eu.linksmart.api.event.ceml.data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */

public interface ClassesDescriptor extends DataDescriptor {
    public static ClassesDescriptor factory( String name, List< String> classes, boolean isTarget) throws Exception {

        return new ClassesDescriptorInstance(name,classes,isTarget);
    }
    public static <F> ClassesDescriptor factory( String name, List< String> classes,Class<F> functionInputType, Function<F, Integer> selectionFunction, boolean isTarget) throws Exception {
        ClassesDescriptor classesDescriptor = new ClassesDescriptorInstance(name,classes,isTarget);
        classesDescriptor.setSelectionFunction(selectionFunction,functionInputType);

        return classesDescriptor;
    }
    public <F >String  getClass(F selectionParameter) throws Exception;
    public<F> void setSelectionFunction(Function<F, Integer> function,Class<F> type);
    public String getClass(int i);
    public void setClass(int i,String clazz);
    public List<String> getClasses();
    public void setClasses(List<String> classes);
}
