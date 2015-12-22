package de.fraunhofer.fit.event.ceml.type.requests;

import com.fasterxml.jackson.annotation.*;
import eu.linksmart.gc.utils.function.Utils;
import eu.linksmart.gc.utils.logging.LoggerService;
import weka.core.Attribute;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by angel on 19/11/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataStructure {


    @JsonPropertyDescription("Define the raw structure of the attributes.")
    @JsonProperty(value = "AttributesStructures")
    private ArrayList<AttributeStructure> attributesStructures = null;
    @JsonIgnore
    private LoggerService loggerService = Utils.initDefaultLoggerService(DataStructure.class);
    @JsonIgnore
    private String[] usedBy = null;
    @JsonIgnore
    private Instances instances = null;
    @JsonIgnore
    protected Map<String, Attribute> attributes = new Hashtable<>();
    @JsonIgnore
    private String attributeTargetName = null;
    @JsonIgnore
    private String name;
    @JsonCreator
    public DataStructure(){

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

    public void setAttributesStructures(ArrayList<AttributeStructure> attributesStructures) {
        this.attributesStructures = attributesStructures;
    }

    public Map<String, Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Attribute> attributes) {
        this.attributes = attributes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Attribute getLearningTarget(){
        return attributes.get(attributeTargetName);
    }


    public Instances getInstances() {
        if(instances==null)
            buildInstances();
        return instances;
    }

    public Instances buildInstances(){
        ArrayList<AttributeStructure> temp= new ArrayList<>();
        boolean isTarget = false;
        for (AttributeStructure structures:attributesStructures ) {


            if(structures.cardinality==0){
                structures.cardinality =1;
            }

            for (int i = 0; i<structures.cardinality; i++) {
                String postFix = (structures.cardinality ==1)?"":String.valueOf(i);

                AttributeStructure  aux = new AttributeStructure();
                aux.attributeName = structures.attributeName+postFix;
                aux.attributesClasses = structures.attributesClasses;
                aux.cardinality =1;

                if(structures.isTarget && !isTarget) {
                    isTarget = true;
                    aux.isTarget=true;
                }else if(structures.isTarget )
                    loggerService.error("Unexpected second target!");

                temp.add(aux);
            }



        }
        attributesStructures = temp;
        if(!isTarget)
            attributesStructures.get(attributesStructures.size()-1).isTarget =true;
        ArrayList<Attribute> orderAttributes= new ArrayList<>();

        for (AttributeStructure structures:attributesStructures ) {

            structures.buildAttributes(attributes);
            orderAttributes.add(structures.getAttribute());

        }
        attributeTargetName = orderAttributes.get(orderAttributes.size()-1).name();
        instances =new Instances(name, orderAttributes,attributes.size()*10);

        instances.setClassIndex(instances.numAttributes()-1);

        return instances;
    }
    public Object getPositiveClassValue(){
        return getLearningTarget().enumerateValues().nextElement();
    }

   /*  @Override
    public Gson getGsonSerializer() {
        return GsonSerializableParent.GsonSerializer();
    }
    public static Gson GsonSerializer() throws NotImplementedException {
        return GsonSerializable.GsonSerializer();
    }
    public static GsonBuilder GsonBuilder() throws NotImplementedException{
        return GsonSerializable.GsonBuilder();
    }

    public static GsonBuilder GsonBuilder(GsonBuilder gsonBuilder) throws NotImplementedException{
        return GsonSerializable.GsonBuilder(gsonBuilder);
    }
    public static String Serializer(Object object) throws NotImplementedException{
        return GsonSerializable.GsonSerializer().toJson(object);
    }
    public static Object Dserializer(String object) throws NotImplementedException{
        return GsonSerializable.GsonSerializer().fromJson(object,object.getClass());
    }
   public class AttributeStructure extends GsonSerializableChild<AttributeStructure> {
        @JsonPropertyDescription("Define the name of the attribute or of the attribute vector name (base name of all members of the vector")
        @JsonProperty(value = "AttributesClasses")
        private ArrayList<String> attributesClasses=null;

        @JsonPropertyDescription("Define the amount of repetitions of the same attribute (if is a vector)")
        @JsonProperty(value = "Cardinality")
        private int cardinality = 0;


        @JsonPropertyDescription("Define the possible values of the attribute, in case is a nominal attribute")
        @JsonProperty(value = "Name")
        private String attributeName=null;


        @JsonPropertyDescription("Define if this attribute is the learning target")
        @JsonProperty(value = "IsTarget")
        private boolean isTarget = false;
        @JsonCreator
        public AttributeStructure() {
        }

        public boolean isTarget() {
            return isTarget;
        }
        private Attribute attribute=null;
        public int getCardinality() {
            return cardinality;
        }
        public ArrayList<String> getAttributesClasses() {
            return attributesClasses;
        }

        public void setAttributesClasses(ArrayList<String> attributesClasses) {
            this.attributesClasses = attributesClasses;
        }

        public Attribute getAttribute() {
            return attribute;
        }

        public void setAttribute(Attribute attribute)  {

            this.attribute = attribute;
            attributeName = attribute.name();


        }

        public String getAttributeName() {
            return attributeName;
        }

        public void setAttributeName(String attributeName) throws Exception {

            if (attribute!=null)
                this.attributeName = attributeName;
            else
                throw new Exception("Change the name of the attribute after the attribute is not possible");


        }
        protected void buildAttributes(){
            attribute =null;
            if(attributesClasses!=null)
                if(!attributesClasses.isEmpty())
                    attribute = new Attribute(attributeName, attributesClasses);

            if(attribute==null)
                attribute = new Attribute(attributeName);

            if( attributes ==null)
                attributes = new Hashtable<String, Attribute>();

            attributes.put(attribute.name(),attribute);

        }
       /* private void fixParentReference(DataStructure dataStructure){
            try {
                Field field  = AttributeStructure.class.getDeclaredField("this$0");
                field.setAccessible(true);
                field.set(this, dataStructure);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
/*
        @Override
        public Gson getGsonSerializer() {
            return GsonSerializableChild.GsonSerializer();
        }

    }*/
}
