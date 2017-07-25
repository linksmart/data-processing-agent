package eu.linksmart.services.payloads.ogc.sensorthing.linked;

import com.fasterxml.jackson.annotation.*;
import eu.linksmart.services.payloads.ogc.sensorthing.FeatureOfInterest;
import eu.linksmart.services.payloads.ogc.sensorthing.Observation;
import eu.linksmart.services.payloads.ogc.sensorthing.base.CCIEncodingImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by José Ángel Carvajal on 04.04.2016 a researcher of Fraunhofer FIT.
 * An Observation results in a value being assigned to a phenomenon.
 * The phenomenon is a property of a feature, the latter being the FeatureOfInterest of the Observation [OGC and ISO 19156:2011].
 * In the context of the Internet of Things, many Observations’ FeatureOfInterest can be the Location of the Thing.
 * For example, the FeatureOfInterest of a wifi-connect thermostat can be the Location of the thermostat (i.e., the living room where the thermostat is located in).
 * In the case of remote sensing, the FeatureOfInterest can be the geographical area or volume that is being sensed.
 * @link http://docs.opengeospatial.org/is/15-078r6/15-078r6.html#32
 */

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "@iot.id", scope = FeatureOfInterest.class)
public class FeatureOfInterestImpl extends CCIEncodingImpl implements FeatureOfInterest {

    /// An Observation observes on one-and-only-one FeatureOfInterest. One FeatureOfInterest could be observed by zero-to-many Observations.
    @JsonIgnore
    protected List<Observation> observations;

    @Override
    public List<Observation> getObservations() {
        return observations;
    }


    @Override
    public void setObservations(List<Observation> observations) {
        if(observations!= null)
            observations.forEach(o->o.setFeatureOfInterest(this));
        if (this.observations == null ) {
            this.observations=observations;
            return;
        }
        this.observations.addAll(observations);
    }

    @Override
    public void addObservations(Observation observation) {
        if(observations==null)
            observations= new ArrayList<>();
        this.observations.add(observation);
    }


    @Override
    public String getObservationsNavigationLink() {
        return "FeatureOfInterest("+id+")/Observations";
    }


    @JsonIgnore
    protected Object feature;



    @Override
    public Object getFeature() {
        return feature;
    }
    @Override
    public void setFeature(Object name) {
        this.feature = name;
    }


}
