package eu.linksmart.api.event.ceml.model;

import eu.linksmart.api.event.ceml.data.DataDescriptors;

import java.util.List;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */

// TODO TBD
 class ModelInstance<T> implements Model<T>{
    public ModelInstance(DataDescriptors descriptors){
        // Todo
    }


    @Override
    public boolean learn(List<T> input) throws Exception {

        // Todo
        return false;
    }

    @Override
    public List<T> predict(List<T> input) throws Exception {

        // Todo
        return null;
    }
}
