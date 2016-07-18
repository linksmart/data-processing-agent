package eu.linksmart.api.event.ceml.data;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
 class ClassesDescriptorInstance<T> extends DataDescriptorInstance implements ClassesDataDescriptor<T> {


    private Map<T,String> classes = new HashMap<>();
    private Function<T,T> selectionFunction=null;


    ClassesDescriptorInstance(String name, Map<T, String> classes) throws Exception {
        super(name,ClassesDescriptorInstance.class.getComponentType());
        this.classes =classes;

    }
    ClassesDescriptorInstance(String name, Map<T, String> classes, Function<T, T> selectionFunction) throws Exception {
        super(name,ClassesDescriptorInstance.class.getComponentType());
        this.classes =classes;
        this.selectionFunction = selectionFunction;

    }
    public String getClass(T selectionParameter){
        if(selectionFunction!=null){
            return classes.get(selectionFunction.apply(selectionParameter));
        }
        return classes.get(selectionParameter);

    }
    @Override
    public boolean isClassesDescription() {
        return true;
    }

    @Override
    public ClassesDescriptorInstance getClassesDescription() {
        return this;
    }
}
