package de.fraunhofer.fit.event.ceml.type.requests.builded;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import weka.core.Attribute;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class AttributeStructure  {
        @JsonPropertyDescription("Define the name of the attribute or of the attribute vector name (base name of all members of the vector")
        @JsonProperty(value = "AttributesClasses")
        protected ArrayList<String> attributesClasses=null;

        @JsonPropertyDescription("Define the amount of repetitions of the same attribute (if is a vector)")
        @JsonProperty(value = "Cardinality")
        protected int cardinality = 0;


        @JsonPropertyDescription("Define the possible values of the attribute, in case is a nominal attribute")
        @JsonProperty(value = "Name")
        protected String attributeName=null;


        @JsonPropertyDescription("Define if this attribute is the learning target")
        @JsonProperty(value = "IsTarget")
        protected boolean isTarget = false;
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

         //   if (attribute!=null)
                this.attributeName = attributeName;
         //   else
//                throw new Exception("Change the name of the attribute after the attribute is not possible");


        }
        protected void buildAttributes(Map<String, Attribute> attributes){
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


    }