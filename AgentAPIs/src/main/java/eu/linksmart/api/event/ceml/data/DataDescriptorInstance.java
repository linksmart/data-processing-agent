package eu.linksmart.api.event.ceml.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.linksmart.api.event.exceptions.StatementException;
import eu.linksmart.api.event.exceptions.UntraceableException;

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

        //if (!(Number.class.isAssignableFrom(clazz) || Date.class.isAssignableFrom(clazz) || ClassesDescriptorInstance.class.isAssignableFrom(clazz)))
        //    throw new Exception("The Data description accepts only three kinds of types: Number, Date or ClassesDescription");

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
    public DescriptorTypes getType() {
        return type;
    }

    @Override
    public void toggleTarget() {
        target = true;
    }

    @Override
    public boolean isAssignable(Class type) {
        return javaType.isAssignableFrom(type);
    }

    @Override
    public DataDescriptor build() throws UntraceableException, StatementException {
        if(name==null)
            throw new StatementException(this.getClass().getName(), this.getClass().getCanonicalName(),"The name is a mandatory field for the data descriptor!");

        if(javaType==null)
            javaType =DescriptorTypes.getNativeType(type);

        return this;

    }
/*
    @Override
    public void rebuild(DataDescriptor me) throws Exception {

        if(!DescriptorTypes.getNativeType(me.getType()).equals(me.getNativeType()))
            throw new Exception("Error while rebuilding"+name+" The given native type and type descriptor don't match");
        name = me.getName();
        target = me.isTarget();
        type = me.getType();
        build();
    }
*/
    @Override
    public void destroy() throws Exception {
        // nothing
    }
}
