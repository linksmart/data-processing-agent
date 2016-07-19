package eu.linksmart.api.event.ceml.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.ArrayList;

import java.util.List;
import java.util.function.Function;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public class ClassesDescriptorInstance<F> extends DataDescriptorInstance implements ClassesDescriptor<F> {

    @JsonProperty("Classes")
    @JsonDeserialize(as = ArrayList.class)
    protected List<String> classes = new ArrayList<>();


    @JsonIgnore
    protected Function<F,Integer> selectionFunction=null;


    protected ClassesDescriptorInstance(String name, List<String> classes, boolean isTarget) throws Exception {
        super(name,ClassesDescriptorInstance.class.getComponentType(),isTarget);
        this.classes =classes;

    }
    protected ClassesDescriptorInstance(String name, List<String> classes, Function<F, Integer> selectionFunction, boolean isTarget) throws Exception {
        super(name,ClassesDescriptorInstance.class.getComponentType(), isTarget);
        this.classes =classes;
        this.selectionFunction = selectionFunction;

    }
    public String getClass(F selectionParameter) throws Exception {
        if(selectionFunction!=null){
            return classes.get(selectionFunction.apply(selectionParameter));
        }

        if(selectionParameter.getClass().isAssignableFrom(Integer.class))
            return classes.get((Integer)selectionParameter);

        throw new Exception("Selection not possible the selection function wasn't set and the selection parameter cannot be casted to Integer");

    }
    @Override
    public boolean isClassesDescription() {
        return true;
    }

    @Override
    public ClassesDescriptorInstance getClassesDescription() {
        return this;
    }

    @Override
    public ClassesDescriptor build() throws Exception {
        if(classes==null|| classes.isEmpty())
            throw new Exception("The classes is a mandatory field!");
        super.build();

        return this;



    }
}
