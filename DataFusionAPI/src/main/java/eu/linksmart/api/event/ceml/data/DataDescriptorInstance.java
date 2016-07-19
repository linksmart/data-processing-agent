package eu.linksmart.api.event.ceml.data;

import java.util.Date;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public class DataDescriptorInstance implements DataDescriptor {
     private String name;
     private Class type;


     private boolean target ;
    protected DataDescriptorInstance(String name, Class clazz,boolean isTarget) throws Exception {

        if (!(Number.class.isAssignableFrom(clazz) && Date.class.isAssignableFrom(clazz) && ClassesDescriptorInstance.class.isAssignableFrom(clazz)))
            throw new Exception("The Data description accepts only three kinds of types: Number, Date or ClassesDescription");

        this.name = name;
        this.type = clazz;
        target =isTarget;

    }

    @Override
    public Class getNativeType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isTarget() {
        return target;
    }

    @Override
    public boolean isClassesDescription() {
        return false;
    }

    @Override
    public ClassesDescriptorInstance getClassesDescription() {
        return null;
    }

    @Override
    public Class getType() {
        return type;
    }

    @Override
    public void build() {

    }
}
