package de.fraunhofer.fit.event.ceml.type;

import weka.core.Attribute;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by angel on 18/11/15.
 */
public class InstancesStructure {
    private String name =null;
    private String id=null;

    private Map<String,Object> usedInLearningObjects=null;
    private Map<Object,Attribute> attributes=null;


    private Instances instances=null;
    public InstancesStructure(){

    }
    public InstancesStructure(DataSetStructure dataSetStructure){

        name =dataSetStructure.getName();
        id =dataSetStructure.getId();
        attributes = new Hashtable<>();
        for (Object key:dataSetStructure.getAttributes().keySet() ) {
            attributes.put(key,new Attribute(dataSetStructure.getAttributes().get(key).toString()));

        }

        ArrayList<Attribute> att= new ArrayList<>(attributes.values());
        instances = new Instances(getName(),att,att.size()*10);
        instances.setClass(attributes.values().iterator().next());

    }

    public Instances getInstances() {
        return instances;
    }

    public void setInstances(Instances instances) {
        this.instances = instances;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public Map<String, Object> getUsedInLearningObjects() {
        return usedInLearningObjects;
    }

    public void setUsedInLearningObjects(Map<String, Object> usedInLearningObjects) {
        this.usedInLearningObjects = usedInLearningObjects;
    }

    public void setAttributes(Map<Object, Attribute> attributes) {
        this.attributes = attributes;
    }

    public Map<Object, Attribute> getAttributes() {
        return attributes;
    }
}
