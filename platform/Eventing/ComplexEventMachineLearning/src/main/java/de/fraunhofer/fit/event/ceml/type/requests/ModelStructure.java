package de.fraunhofer.fit.event.ceml.type.requests;

import java.io.Serializable;

/**
 * Created by angel on 26/11/15.
 */
public class ModelStructure implements Serializable {
    protected String type;
   public ModelStructure(){
        type =null;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
