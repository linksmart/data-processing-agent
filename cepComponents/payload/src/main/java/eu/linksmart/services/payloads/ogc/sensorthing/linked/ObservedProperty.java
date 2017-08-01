package eu.linksmart.services.payloads.ogc.sensorthing.linked;

import com.fasterxml.jackson.annotation.*;

import javax.persistence.OneToMany;
import java.util.List;
import java.util.Set;

/**
 * Created by José Ángel Carvajal on 04.04.2016 a researcher of Fraunhofer FIT.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "@iot.id", scope = ObservedProperty.class)
public class ObservedProperty extends eu.linksmart.services.payloads.ogc.sensorthing.ObservedProperty {

    /**
     * A thing can have zero-to-many datastreams. A datastream entity can only
     * link to a thing as a collection of events or properties.
     */


    @JsonIgnore
    private List<Datastream> datastreams;

    @JsonGetter("datastreams")
    public List<Datastream> getDatastreams() {
        return datastreams;
    }
    @JsonSetter("datastreams")
    public void setDatastreams(List<Datastream> datastreams) {
        if(datastreams!=null) {
            datastreams.forEach(d->d.setObservedProperty(this));
            this.datastreams = datastreams;
        }

    }

    @JsonPropertyDescription("navigationLink is the relative Datastreams that retrieves content of related entities.")
    @JsonProperty(value = "Datastreams@iot.navigationLink")
    public String getDatastreamsNavigationLink() {
        return "ObservedProperty("+id+")/Datastreams";
    }

    // @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Datastreams@iot.navigationLink")
    public void setDatastreamsNavigationLink(String value) {   }
}
