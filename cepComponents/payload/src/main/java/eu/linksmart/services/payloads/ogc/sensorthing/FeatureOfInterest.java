package eu.linksmart.services.payloads.ogc.sensorthing;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eu.linksmart.services.payloads.ogc.sensorthing.linked.FeatureOfInterestImpl;
import eu.linksmart.services.payloads.ogc.sensorthing.linked.ObservationImpl;

import java.util.List;

/**
 * Created by José Ángel Carvajal on 25.07.2017 a researcher of Fraunhofer FIT.
 */

@JsonDeserialize(as = FeatureOfInterestImpl.class)
@JsonSerialize(as = FeatureOfInterestImpl.class)
public interface FeatureOfInterest extends CCIEncoding{
    /**
     * Provides the list of events about this {@link FeatureOfInterestImpl}
     * instance. The returned set is Live reference to the internal data
     * structure which is not Thread-safe. Synchronization and concurrent
     * modification issues might arise in multi-threaded environments.
     *
     * @return the events
     */
    @JsonPropertyDescription("An Observation observes on one-and-only-one FeatureOfInterest. One FeatureOfInterest could be observed by zero-to-many Observations.")
    @JsonGetter("observations")
    List<Observation> getObservations();

    /**
     * Sets the list of Observations about this {@link FeatureOfInterestImpl}.
     * Removes any list previously existing.
     *
     * @param observations the events to set
     */
    @JsonPropertyDescription("An Observation observes on one-and-only-one FeatureOfInterest. One FeatureOfInterest could be observed by zero-to-many Observations.")
    @JsonSetter("observations")
    void setObservations(List<Observation> observations);

    /**
     * adds to the list of Observations about this {@link FeatureOfInterestImpl}.
     * It ignore if the same observation already existed.
     *
     * @param observation the events to set
     */
    void addObservations(Observation observation);

    /**
     * navigationLink is the relative or absolute URL that retrieves content of the Observations.
     *
     * @return  a string that represents the relative or absolute URL that retrieves content of the Observations
     */
    @JsonPropertyDescription("navigationLink is the relative or absolute URL that retrieves content of the Observations.")
    @JsonProperty(value = "Observations@iot.navigationLink")
    String getObservationsNavigationLink();

    @JsonGetter(value = "feature")
    @JsonPropertyDescription("Gets the feature. The feature is the detailed description of the feature. The data type is defined by encodingType.")
    Object getFeature();

    @JsonSetter(value = "feature")
    @JsonPropertyDescription("Setts the feature. The feature is the detailed description of the feature. The data type is defined by encodingType.")
    void setFeature(Object name);
}
