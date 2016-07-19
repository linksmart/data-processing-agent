package eu.linksmart.api.event.ceml.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.linksmart.api.event.datafusion.JsonSerializable;
import eu.linksmart.api.event.ceml.data.DataDescriptors;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */

// TODO TBD
public class ModelInstance<Input,Return> implements Model<Input,Return>{
    @JsonIgnore
    private DataDescriptors descriptors;

    protected ModelInstance(DataDescriptors descriptors){
        // Todo
        this.descriptors=descriptors;
    }


    @Override
    public DataDescriptors getDataDescriptors() {
        return null;
    }

    @Override
    public boolean learn(Input input) throws Exception {

        // Todo
        return false;
    }

    @Override
    public Return predict(Input input) throws Exception {

        // Todo
        return null;
    }

    @Override
    public void setDescriptors(DataDescriptors descriptors) {
        this.descriptors =descriptors;
    }

    @Override
    public DataDescriptors getDescriptors() {
        return descriptors;
    }

    @Override
    public JsonSerializable build() throws Exception {
        if(descriptors== null || descriptors.getDescriptors().isEmpty())
            throw new Exception("The descriptors are a mandatory field!");

        return this;
    }
}
