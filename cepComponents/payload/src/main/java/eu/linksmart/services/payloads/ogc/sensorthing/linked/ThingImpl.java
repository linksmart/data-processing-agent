
package eu.linksmart.services.payloads.ogc.sensorthing.linked;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eu.linksmart.services.payloads.ogc.sensorthing.*;
import eu.linksmart.services.payloads.ogc.sensorthing.base.CommonControlInfoDescriptionImpl;
import jdk.nashorn.internal.objects.annotations.Setter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/*
 *  Copyright [2013] [Fraunhofer-Gesellschaft]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */
/**
 * Implementation of {@link Thing} interface
 * @author Jose Angel Carvajal Soto
 * @since  1.5.0
 *
 * Created by José Ángel Carvajal on 04.04.2016 a researcher of Fraunhofer FIT.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "@iot.id" , scope = Thing.class)
public class ThingImpl extends CommonControlInfoDescriptionImpl implements Thing {



	/**
	 * The empty class constructor, implements the bean instantiation pattern.
	 */
	public ThingImpl()
	{
	}

	/**
	 * The class constructor, takes a Thing textual description and a metadat
	 * description.
	 *
	 * @param description
	 *            This is the description of the thing entity. The content is
	 *            open to accommodate changes to SensorML and to support other
	 *            description languages.
	 * @param metadata
	 *            Meant to host the Thing metadata, e.g. in JSON
	 */
	public ThingImpl(String description, String metadata)
	{

		super(description);

	}

	@JsonIgnore
	protected List<HistoricalLocation> historicalLocations;

	@JsonIgnore
	protected List<Location> locations;
	@JsonIgnore
	protected List<Datastream> datastreams = null;
	@JsonIgnore
	protected ConcurrentMap<Object,Datastream> datastreamsByKey = new ConcurrentHashMap<>();
	@JsonIgnore
	protected Map<String,Object> properties;

	@Override
	public Map<String,Object>  getProperties() {
		return properties;
	}
	@Override

	public void setProperties(Map<String, Object> properties) { this.properties = properties; }

	@Override
	public void addProperty(String key, Object property) {
		if(this.properties==null)
			this.properties= new HashMap<>();
		this.properties.put(key,property);
	}


	@Override
	public List<Datastream> getDatastreams() {
		return datastreams;
	}
	@Override
	public void setDatastreams(List<Datastream> datastreams) {
		if(datastreams!=null) {
			datastreams.forEach(d->d.setThing(this));
			this.datastreams = datastreams;
			datastreams.forEach(d-> datastreamsByKey.put(d.getId(),d));
		}

	}
	@Override
	public void addDatastreams(Datastream datastream) {
		datastream.setThing(this);
		if(!datastreams.contains(datastream) && !datastreamsByKey.containsKey(datastream.getId())) {
			datastreams.add(datastream);
			datastreamsByKey.put(datastream.getId(),datastream);
		}
	}
	@Override
	public Datastream getDatastream(Object id) {
		return datastreamsByKey.getOrDefault(id,null);
	}

	@Override
	public boolean containsDatastreams(Object id) {
		return datastreamsByKey.containsKey(id);
	}

	@Override
	public List<HistoricalLocation> getHistoricalLocations() {
		return historicalLocations;
	}

	@Override
	public void setHistoricalLocations(List<HistoricalLocation> historicalLocations) {
		if(historicalLocations!=null) {
			historicalLocations.forEach(d->d.addThing(this));
			/*
			if I want to add locations in the historical location automatically I have to de-comment this part
			if(locations!= null){
				historicalLocations.forEach(h -> {
					if(h.locations==null)
						h.locations = new ArrayList<>();
					h.setLocations(locations);

				});
			}*/
			this.historicalLocations = historicalLocations;
		}

	}
	@Override
	public void addHistoricalLocation(HistoricalLocation historicalLocation) {

		if(historicalLocation.getThings() == null)
			historicalLocation.setThings(new ArrayList<>());

		if(!historicalLocation.getThings().contains(this))
			historicalLocation.getThings().add(this);

		if(this.historicalLocations==null)
			this.historicalLocations= new ArrayList<>();
		if(!this.historicalLocations.contains(historicalLocation))
			this.historicalLocations.add(historicalLocation);
	}
	@Override
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

	@Override
	public List<Location> getLocations() {
		return locations;
	}

	@Override
	public void setLocations(List<Location> locations) {
		if(locations!=null) {
			locations.forEach(d->d.addThing(this));
			this.locations = locations;
		}

	}
	@Override
	public void addLocation(Location locations) {
		if(locations.getThings()==null)
			locations.setThings(new ArrayList<>());
		if(!locations.getThings().contains(this))
			locations.getThings().add(this);
		if(this.locations==null)
			this.locations= new ArrayList<>();
		if(!this.locations.contains(locations))
			this.locations.add(locations);
	}

	@Override
	public void removeDatastream(Object id) {
		if(datastreamsByKey.containsKey(id))
			return;

		Datastream datastream = datastreamsByKey.get(id);
		datastreams.remove(datastream);
		datastreamsByKey.remove(id);

	}

	public ConcurrentMap<Object, Datastream> getDatastreamsByKey() {
		return datastreamsByKey;
	}

	public void setDatastreamsByKey(ConcurrentMap<Object, Datastream> datastreamsByKey) {
		this.datastreamsByKey = datastreamsByKey;
	}
}