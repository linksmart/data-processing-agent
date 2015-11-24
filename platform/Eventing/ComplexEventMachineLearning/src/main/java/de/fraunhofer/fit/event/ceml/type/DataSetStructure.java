package de.fraunhofer.fit.event.ceml.type;

import java.util.Map;

/**
 * Created by angel on 18/11/15.
 */
public class DataSetStructure {
    private String name;
    private String id;
    private Map<Object,Object> attributes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public Map<Object,Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<Object,Object> attributes) {
        this.attributes = attributes;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
