package de.fraunhofer.fit.event.ceml.type;

import weka.core.Attribute;
import weka.core.Instances;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by angel on 19/11/15.
 */
public class DataStructure {



    private ArrayList<AttributeStructure> attributesStructures = null;

    private String[] usedBy = null;
    private Instances instances = null;
    protected Map<String, Attribute> attributes = new Hashtable<>();
    private Map<String, String> attributesById=null;

    private String name;

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



    public Instances getInstances() {
        if(instances==null)
            buildInstances();
        return instances;
    }

    public Attribute getAttributeByID(String attributeID){
        return attributes.get(attributesById.get(attributeID));
    }
    public Instances buildInstances(){
        ArrayList<AttributeStructure> temp= new ArrayList<>();
        for (AttributeStructure structures:attributesStructures ) {

            structures.fixParentReference(this);


            for (int i = 0; i<structures.cardinality; i++) {
                String postFix = (structures.cardinality ==1)?"":String.valueOf(i);

                AttributeStructure  aux = new AttributeStructure();
                aux.attributeName = structures.attributeName+postFix;
                aux.id= structures.id+postFix;
                aux.attributesClasses = structures.attributesClasses;
                aux.cardinality =1;
                temp.add(aux);
            }

        }
        attributesStructures = temp;

        for (AttributeStructure structures:attributesStructures ) {

            structures.buildAttributes();

        }
        instances =new Instances("test", new ArrayList<Attribute>(attributes.values()),attributes.size()*10);

        instances.setClassIndex(instances.numAttributes()-1);

        return instances;
    }

    public class AttributeStructure {
        private ArrayList<String> attributesClasses=null;
        private Attribute attribute=null;

        public int getCardinality() {
            return cardinality;
        }

        private int cardinality = 0;

        private String id=null;
        private String attributeName=null;
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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
            if( attributesById ==null)
                attributesById= new Hashtable<>();
            attributes.put(attribute.name(),attribute);
            attributesById.put(id,attributeName);

        }
        private void fixParentReference(DataStructure dataStructure){
            try {
                Field field  = AttributeStructure.class.getDeclaredField("this$0");
                field.setAccessible(true);
                field.set(this, dataStructure);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
