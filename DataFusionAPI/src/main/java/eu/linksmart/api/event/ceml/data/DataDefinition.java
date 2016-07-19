package eu.linksmart.api.event.ceml.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public class DataDefinition  implements DataDescriptors{

    private  int inputSize=-1,targetSize=-1, totalInputSize=-1;
    private  List<DataDescriptor> totalInput= new ArrayList<>();
    private  List<DataDescriptor> input= new ArrayList<>();
    private  List<DataDescriptor> targets= new ArrayList<>();

    protected DataDefinition(int inputSize, int targetSize){

        this.inputSize=inputSize;
        this.targetSize=targetSize;
        totalInputSize = inputSize + targetSize;
        totalInput = null;
        input = null;
        targets = null;
    }

    protected DataDefinition(DataDescriptor... definitions){


        for(DataDescriptor descriptor : definitions){
            totalInput.add(descriptor);
            if (descriptor.isTarget())
                targets.add(descriptor);
            else
                targets.add(descriptor);


        }
        this.inputSize=targets.size();
        this.targetSize=targets.size();

        totalInputSize = inputSize + targetSize;

    }


    @Override
    public List<DataDescriptor> getDescriptors() {
        return totalInput;
    }

    @Override
    public DataDescriptor getDescriptor(int i) throws Exception {
        if(totalInput!=null)
            return totalInput.get(i);

        return DataDescriptor.factory(DataDescriptor.DescriptorTypes.NUMBER,String.valueOf(i),i<inputSize);
    }

    @Override
    public List<DataDescriptor> getTargets() {
        return targets;
    }

    @Override
    public int getTotalInputSize() {
        return totalInputSize;
    }

    @Override
    public int getInputSize() {
        return inputSize;
    }

    @Override
    public int getTargetSize() {
        return targetSize;
    }

}
