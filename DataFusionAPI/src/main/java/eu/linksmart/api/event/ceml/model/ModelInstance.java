package eu.linksmart.api.event.ceml.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import eu.linksmart.api.event.datafusion.JsonSerializable;
import eu.linksmart.api.event.ceml.data.DataDescriptors;


import java.util.*;

/**
 * Created by José �?ngel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 * Class implementing Autoregressive neural networks.
 */

// TODO TBD
public abstract class ModelInstance<Input,Return,LearningObject> implements Model<Input,Return,LearningObject>{

    @JsonIgnore
    protected DataDescriptors descriptors;

    protected String name;
    protected Class nativeType;
    @JsonPropertyDescription("Algorithm use to build the model")
    @JsonProperty(value = "Type")
    protected String type;



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public LearningObject getLerner() {
        return lerner;
    }

    public void setLerner(LearningObject lerner) {
        this.lerner = lerner;
    }

    protected LearningObject lerner;
   // @JsonIgnore
   // protected Class<? extends Model>type;

    //final protected String modelName;

    public ModelInstance(){

    }



    @Override
    public void setDescriptors(DataDescriptors descriptors) {
        this.descriptors = descriptors;

    }

    @Override
    public DataDescriptors getDescriptors() {
        return descriptors;
    }


    @Override
    public JsonSerializable build() throws Exception {
        if(descriptors== null || descriptors.isEmpty())
            throw new Exception("The descriptors and learner are mandatory fields!");

        nativeType =lerner.getClass();
        return this;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class getNativeType() {
        return nativeType;
    }

    public void setNativeType(Class nativeType) {
        this.nativeType = nativeType;
    }
}