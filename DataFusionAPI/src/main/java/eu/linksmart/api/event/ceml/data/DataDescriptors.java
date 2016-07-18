package eu.linksmart.api.event.ceml.data;

import java.util.Arrays;
import java.util.List;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public interface DataDescriptors {

    public static DataDescriptors factory(int n){
        return new DataDefinition(n);
    }
    public static DataDescriptors factory(DataDefinition... definitions){
        return new DataDefinition(definitions);
    }
    public int getSize() ;

    public List<DataDefinition> getNominalDescriptors() ;
    public DataDefinition getNominalDescriptor(int i) ;
}
