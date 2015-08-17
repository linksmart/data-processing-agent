package org.fit.fraunhofer.almanac;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Thing;

import java.util.ArrayList;

/**
 * Created by Werner-Kytölä on 10.07.2015.
 */
public class Things {
    @JsonProperty("Thing")
    private ArrayList<Thing> thing;

    public Things(){
        thing = new ArrayList<>();
    }

    public ArrayList<Thing> ThingsArray(){ return thing;}
    @JsonProperty("Thing")
    public ArrayList<Thing> getThing() {
        return thing;
    }
    @JsonProperty("Thing")
    public void setThing(ArrayList<Thing> thing) {
        this.thing = thing;
    }
}
