package eu.linksmart.api.event.ceml.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.linksmart.api.event.datafusion.JsonSerializable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public class DataDefinition extends ArrayList<DataDescriptor>  implements DataDescriptors{
    @JsonProperty("InputSize")
    protected   int inputSize=-1;
    @JsonProperty("TargetSize")
    protected   int targetSize=-1;
    @JsonProperty("TotalInputSize")
    protected   int totalInputSize=-1;
    protected  List<DataDescriptor> input= new ArrayList<>(), targets= new ArrayList<>();
    protected  boolean lambdaTypes = false;
    @JsonProperty("Name")
    protected String name = "noSet";
    @JsonProperty("NativeType")
    protected Class javaType=DataDefinition.class;

    @JsonProperty("isTarget")
    protected boolean target =false;

    @JsonProperty("Type")
    protected DescriptorTypes type= DescriptorTypes.NUMBER;

    public DataDefinition(){


    }
    protected DataDefinition(String name, int inputSize, int targetSize, DescriptorTypes type){

        this.inputSize=inputSize;
        this.targetSize=targetSize;
        totalInputSize = inputSize + targetSize;
      //  totalInput = null;
        input = null;
        targets = null;
        lambdaTypes = true;
        this.type =type;
        this.name = name;

    }

    protected DataDefinition(DataDescriptor... definitions){

      // totalInput= new ArrayList<>();
       input= new ArrayList<>();
       targets= new ArrayList<>();
        for(DataDescriptor descriptor : definitions){
            add(descriptor);
            if (descriptor.isTarget())
                targets.add(descriptor);
            else
                targets.add(descriptor);


        }
        this.inputSize=targets.size();
        this.targetSize=targets.size();

        totalInputSize = inputSize + targetSize;

        lambdaTypes = false;

    }


   /* @Override
    public List<DataDescriptor> getDescriptors() {
        return totalInput;
    }*/

    @Override
    public List<DataDescriptor> getTargetDescriptors() {
        return targets;
    }

    @Override
    public List<DataDescriptor> getInputDescriptors() {
        return input;
    }

   /* @Override
    public DataDescriptor getDescriptor(int i) throws Exception {
        if(totalInput!=null)
            return totalInput.get(i);

        return DataDescriptor.factory(DataDescriptor.DescriptorTypes.NUMBER,String.valueOf(i),i<inputSize);
    }*/

    @Override
    public DataDescriptor getTargetDescriptor(int i) throws Exception {
        if(targets!=null)
            return targets.get(i);

        return DataDescriptor.factory(DataDescriptor.DescriptorTypes.NUMBER,String.valueOf(i),i<inputSize);
    }

    @Override
    public DataDescriptor getInputDescriptor(int i) throws Exception {
        if(input!=null)
            return input.get(i);

        return DataDescriptor.factory(DataDescriptor.DescriptorTypes.NUMBER,String.valueOf(i),i<inputSize);
    }

    @Override
    public List<DataDescriptor> getTargets() {
        return targets;
    }

    @Override
    public int getTotalInputSize() {
        if(lambdaTypes)
            return totalInputSize;
        return size();
    }
    @Override
    public int size(){
            return getTotalInputSize();
    }
    @Override
    public boolean isEmpty(){
        return size()>0;
    }
    @Override
    public int getInputSize() {
        if(lambdaTypes)
            return inputSize;
        return input.size();
    }

    @Override
    public int getTargetSize() {
        if(lambdaTypes)
            return targetSize;
        return targets.size();
    }

    @Override
    public boolean isLambdaTypeDefinition() {
        return lambdaTypes;
    }

    @Override
    public JsonSerializable build() throws Exception {
        if((inputSize==-1)&&(totalInputSize==-1)&&(targetSize==-1)) {

            for (DataDescriptor descriptor : this)
                descriptor.build();

            this.inputSize=targets.size();
            this.targetSize=targets.size();

            totalInputSize = inputSize + targetSize;
        } else
            lambdaTypes=true;


        return this;
    }

    @Override
    public void destroy() throws Exception {
        // nothing
    }

    @Override
    public Class getNativeType() {
        return javaType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isTarget() {
        return target;
    }
    @Override
    public void asTarget() {
        target =true;
    }
    @Override
    public boolean isClassesDescription() {
        return false;
    }

    @Override
    public DataDescriptor getClassesDescription() {
        return null;
    }

    @Override
    public DescriptorTypes getType() {
        return type;
    }
}
