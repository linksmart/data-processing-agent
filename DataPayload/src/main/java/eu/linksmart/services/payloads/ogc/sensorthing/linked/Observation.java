package eu.linksmart.services.payloads.ogc.sensorthing.linked;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eu.linksmart.services.payloads.ogc.sensorthing.base.CommonControlInfo;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.serialize.DateDeserializer;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.serialize.DateSerializer;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.time.Period;
import java.util.Date;


/**
 * Created by José Ángel Carvajal on 04.04.2016 a researcher of Fraunhofer FIT.
 */

//@JsonIgnoreProperties({"@iot.id, @iot.selfLink"})
//@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@iot.id", scope = Observation.class )
public class Observation extends eu.linksmart.services.payloads.ogc.sensorthing.Observation {


    //@JsonProperty("@iot.id")
    //protected Object id;

    public FeatureOfInterest getFeatureOfInterest() {
        return featureOfInterest;
    }

    public void setFeatureOfInterest(FeatureOfInterest featureOfInterest) {
        this.featureOfInterest = featureOfInterest;
    }

    /**TBD. */
    //@JsonManagedReference(value = "featureOfInterest")

    //@ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    //@JoinColumn(name="objectID", nullable=false)
    @JsonManagedReference
    protected FeatureOfInterest featureOfInterest;



    /**TBD.*/
    @JsonBackReference(value = "datastream")
    protected Datastream datastream = null;
    @JsonBackReference(value = "datastream")
    public Datastream getDatastream() {
        return datastream;
    }
    @JsonBackReference(value = "datastream")
    public void setDatastream(Datastream datastream) {
        this.datastream = datastream;
    }


    /*@JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Datastream@iot.navigationLink")
    public String getDatastreamNavigationLink() {
        return "Observation("+id+")/Datastream";
    }
    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Datastream@iot.navigationLink")
    public void setDatastreamNavigationLink(String value) {   }

    @JsonProperty(value = "FeatureOfInterest@iot.navigationLink")
    protected long featureOfInterestNavigationLink;*/

    /*@JsonPropertyDescription("TBD.")
    @JsonProperty(value = "FeatureOfInterest@iot.navigationLink")
    public String getFeatureOfInterestNavigationLink() {
        return "Observation("+id+")/FeatureOfInterest";
    }
    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "FeatureOfInterest@iot.navigationLink")
    public void setFeatureOfInterestNavigationLink(String value) {   }*/

    /*
       /** TBD. * /
    @JsonManagedReference(value = "")
    @JsonBackReference(value = "")
    protected TBD TBD ;
    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "TBD@iot.navigationLink")
    protected String historicalLocationsNavigationLink;
     */



}
