package eu.linksmart.api.event.ceml.data;

import java.util.Map;
import java.util.function.Function;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public interface ClassesDataDescriptor<T> extends DataNominalDescriptor {
    public static <T> ClassesDataDescriptor<T> factory( String name, Map<T, String> classes) throws Exception {

        return new ClassesDescriptorInstance<>(name,classes);
    }
    public static <T> ClassesDataDescriptor<T> factory( String name, Map<T, String> classes, Function<T, T> selectionFunction) throws Exception {
        return new ClassesDescriptorInstance<>(name,classes, selectionFunction);
    }
    public String getClass(T selectionParameter);
}
