package eu.linksmart.api.event.ceml.model;

import eu.linksmart.api.event.ceml.data.DataDescriptors;

import java.util.List;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public interface Model<T> {

    public static <T> Model<T> factory(DataDescriptors descriptor){
       return new ModelInstance<>(descriptor);
    }
    public DataDescriptors getDataDescriptors();
    public boolean learn(List<T> input) throws Exception;
    public List<T> predict(List<T> input) throws Exception;

    public boolean learn(Map<String, T> input) throws Exception;
    public List<T> predict(Map<String, T> input) throws Exception;


    public Map<String, T> predictInMap(List<T> input) throws Exception;
    public Map<String, T> predictInMap(Map<String, T> input) throws Exception;
}
