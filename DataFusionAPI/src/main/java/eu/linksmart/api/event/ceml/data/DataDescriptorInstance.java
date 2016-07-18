package eu.linksmart.api.event.ceml.data;

import java.util.Date;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
 class DataDescriptorInstance implements DataNominalDescriptor {
    private String name;
    private Class type;
    DataDescriptorInstance(String name, Class clazz) throws Exception {

        if (!(Number.class.isAssignableFrom(clazz) && Date.class.isAssignableFrom(clazz) && ClassesDescriptorInstance.class.isAssignableFrom(clazz)))
            throw new Exception("The Data description accepts only three kinds of types: Number, Date or ClassesDescription");

        this.name = name;
        this.type = clazz;

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
    public boolean isClassesDescription() {
        return false;
    }

    @Override
    public ClassesDescriptorInstance getClassesDescription() {
        return null;
    }

}
