package eu.linksmart.api.event.ceml.data;

import eu.linksmart.api.event.types.JsonSerializable;

import java.util.List;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
//@JsonDeserialize(as = DoubleTumbleWindowEvaluator.class)
public interface DataDescriptors extends List<DataDescriptor>,DataDescriptor, JsonSerializable{

    public static DataDescriptors factory(String name,int inputSize,int targetSize, DescriptorTypes type){
        return new DataDefinition(name,inputSize,targetSize, type);
    }
    public static DataDescriptors factory(DataDescriptor... definitions){
        return new DataDefinition(definitions);
    }

    //public List<DataDescriptor> getDescriptors();
    public List<DataDescriptor> getTargetDescriptors();
    public List<DataDescriptor> getInputDescriptors();

    //public DataDescriptor getDescriptor(int i) throws Exception;
    public DataDescriptor getTargetDescriptor(int i) throws Exception;
    public DataDescriptor getInputDescriptor(int i) throws Exception;

    public List<DataDescriptor> getTargets();
    public int getTotalInputSize();
    public int getInputSize();
    public int getTargetSize();
    public boolean isLambdaTypeDefinition();

}
