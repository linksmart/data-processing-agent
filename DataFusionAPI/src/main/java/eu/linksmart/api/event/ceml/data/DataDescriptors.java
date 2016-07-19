package eu.linksmart.api.event.ceml.data;

import java.util.List;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public interface DataDescriptors {

    public static DataDescriptors factory(int inputTotalSize,int targetSize){
        return new DataDefinition(inputTotalSize,targetSize);
    }
    public static DataDescriptors factory(DataDescriptor... definitions){
        return new DataDefinition(definitions);
    }

    public List<DataDescriptor> getDescriptors() ;
    public DataDescriptor getDescriptor(int i) throws Exception;
    public List<DataDescriptor> getTargets();
    public int getTotalInputSize();
    public int getInputSize();
    public int getTargetSize();
}
