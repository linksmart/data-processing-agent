package eu.linksmart.services.event.ceml.models.data;

import com.fasterxml.jackson.annotation.*;
import eu.linksmart.api.event.ceml.data.DataDefinition;
import eu.linksmart.api.event.ceml.data.DataDescriptors;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.services.utils.function.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import weka.core.Attribute;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by angel on 19/11/15.
 */
@Deprecated() // this class is a candidate to refactor or archived. It has not been updated and is incompatible with the last Agent version (14.04.19)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataStructure extends DataDefinition {


    @JsonPropertyDescription("Define the raw structure of the attributes.")
    @JsonProperty(value = "AttributesStructures")
    private ArrayList<AttributeStructure> attributesStructures = null;
    @JsonIgnore
    private Logger loggerService = LogManager.getLogger(DataStructure.class);
    @JsonIgnore
    private String[] usedBy = null;
    @JsonIgnore
    private Instances instances = null;
    @JsonIgnore
    protected Map<String, AttributeStructure> attributes = new Hashtable<>();
    private String attributeTargetName;

    @JsonCreator
    public DataStructure(){

    }
    public DataStructure(DataDescriptors descriptors){
        super(descriptors);

    }

    public DataStructure(AttributeStructure... descriptors){
        super(descriptors);


    }
    public String[] getUsedBy() {
        return usedBy;
    }

    public void setUsedBy(String[] usedBy) {
        this.usedBy = usedBy;
    }


    public String getName() {
        return name;
    }

    public ArrayList<AttributeStructure> getAttributesStructures() {
        return attributesStructures;
    }

    public Map<String, AttributeStructure> getAttributes() {
        return attributes;
    }


    public void setName(String name) {
        this.name = name;
    }

    public AttributeStructure getLearningTarget(){
        return attributes.get(attributeTargetName);
    }


    public Instances getInstances()  {
        if(instances==null)
            try {
                build();
            } catch (Exception e) {
                loggerService.error(e.getMessage(),e);
            }
        return instances;
    }

    public DataStructure build() throws TraceableException, UntraceableException {
        super.build();
        ArrayList<AttributeStructure> temp= new ArrayList<>();
        boolean isTarget = false;
        //attributesStructures.get(attributesStructures.size()-1).attributeName = name;
        for (AttributeStructure structures:attributesStructures ) {


            if(structures.cardinality==0){
                structures.cardinality =1;
            }

            for (int i = 0; i<structures.cardinality; i++) {
                String postFix = (structures.cardinality ==1)?"":String.valueOf(i);

                AttributeStructure  aux = null;
                try {
                    if (structures.isClassAttribute())
                        aux = new ClassesAttributeStructure(structures);
                    else
                        aux = new AttributeStructure(structures);


                aux.cardinality =1;

                if(structures.isTarget() && !isTarget) {
                    isTarget = true;
                    aux.toggleTarget();
                }else if(structures.isTarget() )
                    loggerService.error("Unexpected second target!");

                temp.add(aux);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        attributesStructures = temp;
        if(!isTarget)
            attributesStructures.get(attributesStructures.size()-1).toggleTarget();
        ArrayList<Attribute> orderAttributes= new ArrayList<>();

        for (AttributeStructure structures:attributesStructures ) {
            structures.build();
            attributes.put(structures.getName(),structures);
            orderAttributes.add(structures.getAttribute());
        }
        attributeTargetName = orderAttributes.get(orderAttributes.size()-1).name();
        loggerService.info("attribute target name: "+attributeTargetName);
        instances =new Instances(name, orderAttributes,attributes.size()*10);

        instances.setClassIndex(instances.numAttributes()-1);

        return this;
    }


}
