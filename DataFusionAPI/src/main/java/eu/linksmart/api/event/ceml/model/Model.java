package eu.linksmart.api.event.ceml.model;

import eu.linksmart.api.event.ceml.data.DataDescriptors;

import java.util.List;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public interface Model<T> {

    public static <T> Model<T> factorty(DataDescriptors descriptor){
       return new ModelInstance<>(descriptor);
    }
    public boolean learn(List<T> input) throws Exception;
    public List<T> predict(List<T> input) throws Exception;
}
