package eu.linksmart.api.event.ceml.data;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public interface ClassesDescriptor<F> extends DataDescriptor {
    public static <F> ClassesDescriptor<F> factory( String name, List< String> classes, boolean isTarget) throws Exception {

        return new ClassesDescriptorInstance<>(name,classes,isTarget);
    }
    public static <F> ClassesDescriptor<F> factory( String name, List< String> classes, Function<F, Integer> selectionFunction, boolean isTarget) throws Exception {
        return new ClassesDescriptorInstance<>(name,classes, selectionFunction,isTarget);
    }
    public String getClass(F selectionParameter) throws Exception;
}
