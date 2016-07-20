package eu.linksmart.api.event.ceml.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.Date;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public class DataDescriptorInstance implements DataDescriptor {
    @JsonProperty("Name")
     protected String name = null;
    @JsonProperty("NativeType")
    protected Class javaType=null;

    @JsonProperty("isTarget")
    protected boolean target =false ;

    @JsonProperty("Type")
    protected DescriptorTypes type= DescriptorTypes.NUMBER;

    protected DataDescriptorInstance(String name, Class clazz,boolean isTarget) throws Exception {

        if (!(Number.class.isAssignableFrom(clazz) || Date.class.isAssignableFrom(clazz) || ClassesDescriptorInstance.class.isAssignableFrom(clazz)))
            throw new Exception("The Data description accepts only three kinds of types: Number, Date or ClassesDescription");

        this.name = name;
        this.javaType = clazz;
        target =isTarget;

    }

    @Override
    public Class getNativeType() {
        return javaType;
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
    public DataDescriptor getClassesDescription() {
        return null;
    }

    @Override
    public DescriptorTypes getType() {
        return type;
    }

    @Override
    public DataDescriptor build() throws Exception {
        if(name==null)
            throw new Exception("The name is a mandatory field!");

        javaType =DescriptorTypes.getNativeType(type);

        return this;

    }
}
