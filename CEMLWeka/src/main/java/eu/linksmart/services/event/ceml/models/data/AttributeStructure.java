package eu.linksmart.services.event.ceml.models.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import eu.linksmart.api.event.ceml.data.ClassesDescriptor;
import eu.linksmart.api.event.ceml.data.ClassesDescriptorInstance;
import eu.linksmart.api.event.ceml.data.DataDescriptor;
import eu.linksmart.api.event.datafusion.exceptions.StatementException;
import eu.linksmart.api.event.datafusion.exceptions.UntraceableException;
import weka.core.Attribute;

public class AttributeStructure extends ClassesDescriptorInstance implements DataDescriptor {

        @JsonPropertyDescription("Define the amount of repetitions of the same attribute (if is a vector)")
        @JsonProperty(value = "Cardinality")
        protected int cardinality = 0;
        protected Attribute attribute=null;




        @JsonCreator
        public AttributeStructure(String name, Class clazz,boolean isTarget) throws Exception {
            super(name,null,isTarget);
            this.javaType =clazz;


        }
        public AttributeStructure(DataDescriptor descriptor) throws Exception {
            this(descriptor.getName(),descriptor.getNativeType(),descriptor.isTarget());

        }

        protected AttributeStructure(ClassesDescriptor descriptor) throws Exception {
            super(descriptor.getName(),descriptor.getClasses(),descriptor.isTarget());


        }


        public int getCardinality() {
            return cardinality;
        }


        public Attribute getAttribute() {
            return attribute;
        }

        public void setAttribute(Attribute attribute)  {

            this.attribute = attribute;
            name = attribute.name();


        }




        public void setName(String attributeName) throws Exception {

         //   if (attribute!=null)
                this.name = attributeName;
         //   else
//                throw new Exception("Change the name of the attribute after the attribute is not possible");


        }
        public ClassesDescriptor build() throws StatementException, UntraceableException {
            super.build();

            attribute = new Attribute(name);


            //attributes.put(attribute.name(),attribute);
            return this;

        }
        public boolean isClassAttribute(){
            return this instanceof ClassesAttributeStructure;
        }

        public ClassesAttributeStructure getClassAttribute(){
            if(isClassAttribute())
                return (ClassesAttributeStructure)this;

            return null;
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