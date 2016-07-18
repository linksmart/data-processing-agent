package eu.linksmart.api.event.ceml.model;

import eu.linksmart.api.event.ceml.data.DataDescriptors;

import java.util.List;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */

// TODO TBD
 class ModelInstance<T> implements Model<T>{
    private final DataDescriptors descriptors;

    public ModelInstance(DataDescriptors descriptors){
        // Todo
        this.descriptors=descriptors;
    }


    @Override
    public DataDescriptors getDataDescriptors() {
        return null;
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

    @Override
    public boolean learn(Map<String, T> input) throws Exception {
        return false;
    }

    @Override
    public List<T> predict(Map<String, T> input) throws Exception {
        return null;
    }

    @Override
    public Map<String, T> predictInMap(List<T> input) throws Exception {
        return null;
    }

    @Override
    public Map<String, T> predictInMap(Map<String, T> input) throws Exception {
        return null;
    }
}
