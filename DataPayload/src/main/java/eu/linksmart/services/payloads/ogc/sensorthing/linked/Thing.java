
package eu.linksmart.services.payloads.ogc.sensorthing.linked;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eu.linksmart.services.payloads.ogc.sensorthing.base.CommonControlInfoDescription;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Thing class.
 * <p>
 * <strong>Definition:</strong> We use the ITU-T definition, i.e., with regard
 * to the Internet of Things, a thing is an object of the physical world
 * (physical things) or the information world (virtual things) which is capable
 * of being identified and integrated into communication networks. (ITU-T
 * Y.2060)
 * </p>
 * 
 * @author <a href="mailto:carvajal@fit.fhg.de">Angel Carvajal</a>
 *
 */
public class Thing extends eu.linksmart.services.payloads.ogc.sensorthing.Thing
{


    @JsonProperty(value = "historicalLocations")
    public Set<HistoricalLocation> getHistoricalLocations() {
        return historicalLocations;
    }

    @JsonProperty(value = "historicalLocations")
    public void setHistoricalLocations(Set<HistoricalLocation> historicalLocations) {
        this.historicalLocations = historicalLocations;
    }

    /**
	 * The Location entity locates the Thing. Multiple Things MAY be
     * located at the same Location. A Thing MAY not have a
     * Location. A Thing SHOULD have only one Location.
     * However, in some complex use cases, a Thing MAY have more
     * than one Location representations. In such case, the Thing MAY
     * have more than one Locations. These Locations SHALL have
     * different encodingTypes and the encodingTypes SHOULD be in
     * different spaces (e.g., one encodingType in Geometrical space and
     * one encodingType in Topological space).
     **/
    @JsonProperty(value = "historicalLocations")
    protected Set<HistoricalLocation> historicalLocations;
    /**navigationLink is the relative URL that retrieves content of related entities. */

    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "HistoricalLocations@iot.navigationLink")
    public String getHistoricalLocationsNavigationLink() {
        return "Location("+id+")/HistoricalLocations";
    }
    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "HistoricalLocations@iot.navigationLink")
    public void setHistoricalLocationsNavigationLink(String value) {   }
	/**
	 * A thing can have zero-to-many datastreams. A datastream entity can only
	 * link to a thing as a collection of events or properties.
	 */
    /**
     * A thing can have zero-to-many datastreams. A datastream entity can only
     * link to a thing as a collection of events or properties.
     */

    @JsonProperty(value = "datastreams")
    protected Set<Datastream> datastreams;

    /**navigationLink is the relative URL that retrieves content of related entities. */
    @JsonPropertyDescription("navigationLink is the relative Datastreams that retrieves content of related entities.")
    @JsonProperty(value = "Datastreams@iot.navigationLink")
    public String getDatastreamsNavigationLink() {
        return "Thing("+id+")/Datastreams";
    }

    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Datastreams@iot.navigationLink")
    public void setDatastreamsNavigationLink(String value) {   }

    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Locations@iot.navigationLink")
    public String getLocationsNavigationLink() {
        return "Thing("+id+")/Locations";
    }
    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Locations@iot.navigationLink")
    public void setLocationsNavigationLink(String value) {}

    @JsonBackReference(value = "locations")
    protected Set<Location> locations;





	/**
	 * Provides the list of locations in which this Thing has been registered.
	 * The returned set is Live reference to the internal data structure which
	 * is not Thread-safe. Synchronization and concurrent modification issues
	 * might arise in multi-threaded environments.
	 *
	 * @return the locations
	 */
	@JsonProperty(value = "locations")
	public Set<Location> getLocations()
	{
		return locations;
	}

	/**
	 * Sets the list of locations in which this Thing has been registered.
	 * Replaces any existing list.
	 *
	 * @param historicalLocations
	 *            the locations to set
	 */
	@JsonProperty(value = "locations")
	public void setLocations(Set<Location> historicalLocations)
	{
		this.locations = historicalLocations;
	}

	/**
	 * Adds one location to the list of Locations in which this Thing has been
	 * registered.
	 *
	 * @param historicalLocations
	 */
	public void addHistoricalLocations(HistoricalLocation historicalLocations)
	{
		// check not null
		if ((this.historicalLocations != null)&&(historicalLocations!=null))
			// add the location
			this.historicalLocations.add(historicalLocations);
	}

	/**
	 * Removes one location from the set of locations in which this
	 * {@link eu.linksmart.services.payloads.ogc.sensorthing.linked.Thing} instance was positioned.
	 *
	 * @param historicalLocations
	 *            The location to remove.
	 * @return true if removal is successful, false otherwise.
	 */
	public boolean removeHistoricalLocations(HistoricalLocation historicalLocations)
	{
		// the removal flag
		boolean removed = false;

		// check if the locations set is not null
		if (this.historicalLocations != null)
			// remove the location
			removed = this.historicalLocations.remove(historicalLocations);

		// return the removal result
		return removed;
	}

	/**
	 * Provides the list of datastreams generated by this Thing. The returned
	 * set is Live reference to the internal data structure which is not
	 * Thread-safe. Synchronization and concurrent modification issues might
	 * arise in multi-threaded environments.
	 *
	 * @return the {@link java.util.Set}:{@link eu.almanac.ogc.sensorthing.api.datamodel.Datastream}  of datastreams generated by
	 *         this {@link eu.linksmart.services.payloads.ogc.sensorthing.linked.Thing} instance.
	 */
	@JsonProperty(value = "datastreams")
	public Set<Datastream> getDatastreams()
	{
		return datastreams;
	}

	/**
	 * Sets the list of datastreams generated by this thing. Removes any list
	 * previously existing.
	 *
	 * @param datastreams
	 *            the datastreams to set.
	 */
	@JsonProperty(value = "datastreams")
	public void setDatastreams(Set<Datastream> datastreams)
	{
		this.datastreams = datastreams;
	}

	/**
	 * Add a single datastream to the list of datastreams generated by this
	 * {@link eu.linksmart.services.payloads.ogc.sensorthing.linked.Thing} instance.
	 *
	 * @param datastream
	 *            The {@link eu.almanac.ogc.sensorthing.api.datamodel.Datastream} instance to add.
	 */
	public void addDatastream(Datastream datastream)
	{
		// check not null
		if ((this.datastreams != null)&&(datastream!=null))
			// add the datastream to the existing set of datastreams
			this.datastreams.add(datastream);
	}

	/**
	 * Removes the given datastream from the list of datastreams generated by
	 * this {@link eu.linksmart.services.payloads.ogc.sensorthing.linked.Thing} instance.
	 *
	 * @param datastream
	 *            The {@link eu.almanac.ogc.sensorthing.api.datamodel.Datastream} instance to remove.
	 * @return true if removal was successful, false otherwise.
	 */
	public boolean removeDataStream(Datastream datastream)
	{
		// the removal flag
		boolean removed = false;
		
		// check if the locations set is not null
		if (this.datastreams != null)
			// remove the location
			removed = this.datastreams.remove(datastream);
		
		// return the removal result
		return removed;
	}



}