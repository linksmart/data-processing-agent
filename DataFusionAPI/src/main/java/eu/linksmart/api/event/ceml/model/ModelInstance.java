package eu.linksmart.api.event.ceml.model;

import eu.linksmart.api.event.ceml.data.DataDescriptors;

import java.util.List;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */

// TODO TBD
public class ModelInstance<Input,Return> implements Model<Input,Return>{
    private final DataDescriptors descriptors;

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

}
