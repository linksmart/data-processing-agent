package eu.linksmart.api.event.ceml.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eu.linksmart.api.event.exceptions.StatementException;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;

import java.util.ArrayList;

import java.util.List;
import java.util.function.Function;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public class ClassesDescriptorInstance extends DataDescriptorInstance implements ClassesDescriptor {

    @JsonProperty("Classes")
    @JsonDeserialize(as = ArrayList.class)
    protected List<String> classes = new ArrayList<>();


    @JsonIgnore
    protected Function<Object,Integer> selectionFunction=null;
    @JsonIgnore
    protected Class functionInputType = null;


    protected ClassesDescriptorInstance(String name, List<String> classes, boolean isTarget) throws Exception {
        super(name,ClassesDescriptorInstance.class,isTarget);
        this.classes =classes;

    }
    /*protected <T extends Object> ClassesDescriptorInstance(String name, List<String> classes, , Function<T, Integer> selectionFunction, boolean isTarget) throws Exception {
        super(name,ClassesDescriptorInstance.class.getComponentType(), isTarget);
        this.classes =classes;

        this.selectionFunction = (Function<Object, Integer>)selectionFunction;

    }*/
    public <F> String getClass(F selectionParameter) throws Exception {
        if(selectionFunction!=null && selectionParameter.getClass().isAssignableFrom(functionInputType)){
            return classes.get(selectionFunction.apply(selectionParameter));
        }

        if(selectionParameter.getClass().isAssignableFrom(Integer.class))
            return classes.get((Integer)selectionParameter);

        throw new Exception("Selection not possible the selection function wasn't set and the selection parameter cannot be casted to Integer");

    }
    public <F> Integer getIndexClass(F selectionParameter) throws TraceableException {
        if(selectionFunction!=null && selectionParameter.getClass().isAssignableFrom(functionInputType)){
            return (selectionFunction.apply(selectionParameter));
        }

        if(selectionParameter.getClass().isAssignableFrom(Integer.class))
            return ((Integer)selectionParameter);

        if(selectionParameter.getClass().isAssignableFrom(String.class)) {
            for (int i = 0; i < classes.size(); i++)
                if (classes.get(i).equals(selectionParameter))
                    return i;
            return -1;
        }


        throw new StatementException(name,ClassesDescriptor.class.getCanonicalName(),"Selection not possible the selection function wasn't set or the selection parameter cannot be casted");

    }

    @Override
    public <F > void setSelectionFunction(Function<F, Integer> function, Class<F> type) {
        functionInputType = type;

       if(type!=null&&type.isAssignableFrom(Object.class))
            this.selectionFunction = (Function<Object, Integer>)function;

    }

    @Override
    public String getClass(int i) {
        return classes.get(i);
    }

    @Override
    public void setClass(int i, String clazz) {
        classes.set(i,clazz);
    }

    @Override
    public List<String> getClasses() {
        return classes;
    }

    @Override
    public void setClasses(List<String> classes) {
        this.classes =classes;
    }


    @Override
    public boolean isAssignable(Class type) {

        return super.isAssignable(type) || String.class.isAssignableFrom(type);
    }

    @Override
    public ClassesDescriptor build() throws StatementException, UntraceableException {
        if(classes==null|| classes.isEmpty())
            throw new StatementException(this.getClass().getName(),this.getClass().getCanonicalName(), "The classes is a mandatory field for the clasess descriptor!");
        super.build();

        return this;

    }

    @Override
    public void destroy() throws Exception {
        // nothing
    }
}
