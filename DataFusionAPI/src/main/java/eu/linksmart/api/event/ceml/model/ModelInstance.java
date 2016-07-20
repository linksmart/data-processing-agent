package eu.linksmart.api.event.ceml.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.linksmart.api.event.datafusion.JsonSerializable;
import eu.linksmart.api.event.ceml.data.DataDescriptors;


import java.util.*;

/**
 * Created by José �?ngel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 * Class implementing Autoregressive neural networks.
 */

// TODO TBD
public abstract class ModelInstance<Input,Return> implements Model<Input,Return>{

    @JsonIgnore
    private DataDescriptors descriptors;
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
            throw new Exception("The descriptors are a mandatory field!");

        return this;
    }
}