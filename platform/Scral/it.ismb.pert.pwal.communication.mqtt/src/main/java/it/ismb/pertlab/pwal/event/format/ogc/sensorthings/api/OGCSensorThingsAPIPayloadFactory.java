/*
 * OGCSensorThingsAPIPayloadFactory
 * 
 * Copyright (c) 2015 Dario Bonino
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

/*
 * Disclaimer: Please notice that this implementation is neither neat
 * nor optimal due to the really intertwined and "obscure" structure of
 * the PWAL. Solutions and designs adopted here are mainly to tackle and
 * solve immediate problems and do not reflect nor imply any
 * "contribution" to the existing design of the PWAL itself, for which
 * the author denies any participation nor agreement.
 */
package it.ismb.pertlab.pwal.event.format.ogc.sensorthings.api;

import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Datastream;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Location;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.ObservedProperty;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Sensor;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Thing;
import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.model.Unit;
import it.ismb.pertlab.pwal.api.events.base.PWALBaseEvent;
import it.ismb.pertlab.pwal.api.events.base.PWALNewDataAvailableEvent;
import it.ismb.pertlab.pwal.api.events.base.PWALNewDeviceAddedEvent;
import it.ismb.pertlab.pwal.api.utils.SemanticModel;
import it.ismb.pertlab.pwal.api.utils.UIDGenerator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.measure.DecimalMeasure;
import javax.measure.quantity.Quantity;

import org.geojson.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Factory class offering means to translate PWAL events into OGC SensorThings
 * API - compliant payloads.
 * 
 * @author <a href="mailto:bonino@ismb.it">Dario Bonino</a> *
 */
public class OGCSensorThingsAPIPayloadFactory
{
	// the only instance
	private static OGCSensorThingsAPIPayloadFactory theFactory;
	
	// the class logger
	private Logger logger;
	
	/**
	 * Private constructor used for implementing the singleton pattern
	 */
	private OGCSensorThingsAPIPayloadFactory()
	{
		// create instance logger
		this.logger = LoggerFactory.getLogger(OGCSensorThingsAPIPayloadFactory.class);
	}
	
	/**
	 * Returns an instance of OGCSensorThingsAPIPayloadFcatory to be used for
	 * translating PWAL events into MQTT topics.
	 * 
	 * @return an {@link OGCSensorThingsAPIPayloadFactory} instance.
	 */
	public static OGCSensorThingsAPIPayloadFactory getInstance()
	{
		// singleton pattern implementation
		if (OGCSensorThingsAPIPayloadFactory.theFactory == null)
		{
			OGCSensorThingsAPIPayloadFactory.theFactory = new OGCSensorThingsAPIPayloadFactory();
		}
		
		return OGCSensorThingsAPIPayloadFactory.theFactory;
	}
	
	/**
	 * Translates the given event into an OGC SensorThings API compliant payload
	 * to be exploited in data transmission, e.g., over mqtt.
	 * 
	 * @param event
	 *            The PWAL event ({@link PWALNewDataAvailableEvent} to
	 *            translate.
	 * @return The OGC SensorThings API compliant payload (as a JSON-ifiable
	 *         object)
	 */
	public Set<Observation> getObservationDataPayload(PWALNewDataAvailableEvent event, String platformFQDN)
	{
		// create the UID generator
		UIDGenerator uidGen = UIDGenerator.getInstance();
		
		// create the object set
		HashSet<Observation> observations = new HashSet<Observation>();
		
		// the device id
		String deviceId = event.getSender().getId();//event.getSenderId();
		
		// TODO: check whether this can be made safer and more general
		Map<String, Object> values = event.getValues();
		
		// iterate over value keys and map each-of-them to the corresponding
		// data stream
		for (String rawStreamName : values.keySet())
		{
			// prepare the Sensor data needed to represent the data source
			Sensor sensor = new Sensor();
			
			// Set the sensor id, in the SCRAL case this will most likely be
			// equal
			// to the device, which in turn corresponds to the OGCThing entity.
			// This however might induce some misunderstandings, especially for
			// complex devices carrying more than one sensor. Therefore a second
			// step would involve identification of "single" sensors belonging
			// to a
			// given device, i.e., Thing.
			sensor.setId(uidGen.uidFromStringArray(
					new String[] { deviceId, this.getSensorName(event.getSender(), rawStreamName), platformFQDN }, "."));
			
			// get the thing class if specified
			String ontologyClass = this.generateThingClass(event.getSender());
			
			if ((ontologyClass != null) && (!ontologyClass.isEmpty()))
				sensor.setMetadata(ontologyClass);
			
			// create the corresponding observation object
			Observation observation = new Observation();
			
			// add the sensor to the current observation
			observation.setSensor(sensor);
			
			// remove leading and trailing spaces
			String dataStreamName = rawStreamName.trim();
			
			// check if the datastream name is in the form getXXXX
			if (dataStreamName.startsWith("get"))
				dataStreamName = dataStreamName.substring(3);
			
			// build the datastream-id as a concatenation of the device id and
			// the actual stream name (TODO: update to the last id
			// generation algorithm)
			String datastreamId = deviceId + "-" + dataStreamName;
			
			// create and set the datastream object
			Datastream dataStream = new Datastream();
			dataStream.setId(uidGen.uidFromStringArray(new String[] { datastreamId, platformFQDN }, "."));
			
			// add the datastream to the observation
			observation.setDatastream(dataStream);
			
			// set the result type
			// TODO: version 1 all events shall contain unitOfMeasures, to be
			// upated
			observation.setResultType("Measure");
			
			// set the result value
			// TODO: version 1 all events shall contain unitOfMeasures, to be
			// upated
			observation.setResultValue(values.get(rawStreamName));
			
			// store the phenomenonTime
			// TODO: remove JodaTime!!!
			observation.setPhenomenonTime(event.getTimeStamp().toDate());
			
			// add the observation
			observations.add(observation);
		}
		
		return observations;
	}
	
	/**
	 * Translates the given event into an OGC SensorThings API compliant payload
	 * to be exploited in data transmission, e.g., over mqtt.
	 * 
	 * @param event
	 *            The PWAL event ({@link PWALNewDeviceAddedEvent} to translate.
	 * @return The OGC SensorThings API compliant payload (as a JSON-ifiable
	 *         object)
	 */
	public Thing getSensorMetadataPayload(PWALBaseEvent event, String platformFQDN)
	{
		// create the UID generator
		UIDGenerator uidGen = UIDGenerator.getInstance();
		
		// the sender device
		Device device = event.getSender();
		
		// the device id
		String deviceId = device.getId();//event.getSenderId();
		
		// create the object set
		Thing thing = new Thing();
		
		// create the thing description
		thing.setDescription("The " + device.getType() + " connected to the " + device.getNetworkType() + " network.");
		
		// get the thing class if specified
		String ontologyClass = this.generateThingClass(device);
		
		if ((ontologyClass != null) && (!ontologyClass.isEmpty()))
			thing.setMetadata(ontologyClass);
		
		// prepare the thing location
		Location thingLocation = new Location();
		
		// the location timestamp
		thingLocation.setTime(new Date());
		
		// create the geometry point
		Point locationPoint = new Point(device.getLocation().getLon(), device.getLocation().getLat());
		
		// add the point
		thingLocation.setGeometry(locationPoint);
		
		// add the location to the thing metadata
		thing.addLocation(thingLocation);
		
		// build the datastreams descriptions
		thing.setDatastreams(this.generateDataStreamsFromDevice(device, deviceId, platformFQDN));
		
		// set the thing id
		thing.setId(uidGen.uidFromStringArray(new String[] { deviceId, platformFQDN }, "."));
		
		return thing;
	}
	
	/**
	 * Translates the given device into an OGC SensorThings API compliant
	 * description to be exploited by ccalling modules
	 * 
	 * TODO: check if the sender id is exactly the same as the device id and in
	 * such a case compact the method logic here with the device as a parameter
	 * 
	 * @param device
	 *            The PWAL event ({@link PWALNewDeviceAddedEvent} to translate.
	 * @return The OGC SensorThings API compliant payload (as a JSON-ifiable
	 *         object)
	 */
	public Thing getSensorMetadataPayload(Device device, String platformFQDN)
	{
		// create the UID generator
		UIDGenerator uidGen = UIDGenerator.getInstance();
		
		// the device id
		String deviceId = device.getId();
		
		// create the object set
		Thing thing = new Thing();
		
		// create the thing description
		thing.setDescription("The " + device.getType() + " connected to the " + device.getNetworkType() + " network.");
		
		// get the thing class if specified
		String ontologyClass = this.generateThingClass(device);
		
		if ((ontologyClass != null) && (!ontologyClass.isEmpty()))
			thing.setMetadata(ontologyClass);
		
		// prepare the thing location
		Location thingLocation = new Location();
		
		// the location timestamp
		thingLocation.setTime(new Date());
		
		// create the geometry point
		Point locationPoint = new Point(device.getLocation().getLon(), device.getLocation().getLat());
		
		// add the point
		thingLocation.setGeometry(locationPoint);
		
		// add the location to the thing metadata
		thing.addLocation(thingLocation);
		
		// build the datastreams descriptions
		thing.setDatastreams(this.generateDataStreamsFromDevice(device, deviceId, platformFQDN));
		
		// set the thing id
		thing.setId(uidGen.uidFromStringArray(new String[] { deviceId, platformFQDN }, "."));
		
		return thing;
	}
	
	/**
	 * Get the sensor name corresponding to the given value key for the given
	 * device
	 * 
	 * @param device
	 * @param valueKey
	 * @return
	 */
	private String getSensorName(Device device, String valueKey)
	{
		// reflection black magic. TODO: remove direct references to the device
		// object and create a representative event payload instead.
		Class<?> implementedInterfaces[] = device.getClass().getInterfaces();
		
		// return the sensor name
		return this.checkInterface(implementedInterfaces, valueKey);
	}
	
	/**
	 * Get the sensor name corresponding to the given value key for the given
	 * device sub interface (Recursive, stops as soon as an interface with
	 * declared methods is found).
	 * 
	 * @param interfacesToCheck
	 * @param valueKey
	 * @return
	 */
	private String checkInterface(Class<?> interfacesToCheck[], String valueKey)
	{
		// reflection black magic. TODO: remove direct references to the device
		// object and create a representative event payload instead.
		
		// the sensor name to return, empty by default
		String sensorName = "";
		// iterate over all implemented interfaces
		for (int i = 0; i < interfacesToCheck.length; i++)
		{
			// debug
			this.logger.debug("Implemented interface:" + interfacesToCheck[i].getSimpleName());
			
			// check if the current interface extends
			if (Device.class.isAssignableFrom(interfacesToCheck[i]))
			{
				this.logger.debug("Assignable from Device");
				
				// get all declared methods
				Method methods[] = interfacesToCheck[i].getDeclaredMethods();
				if (methods.length > 0)
				{
					// search for valueKey match
					for (Method m : methods)
					{
						// search for matches with the declared value key
						if (m.getName().equals(valueKey))
						{
							// the interface identifies the sensor name
							sensorName = interfacesToCheck[i].getSimpleName().toLowerCase();
						}
					}
				}
				else
				{
					if (interfacesToCheck[i].isInterface())
						sensorName = this.checkInterface(interfacesToCheck[i].getInterfaces(), valueKey);
				}
			}
		}
		
		// debug
		this.logger.debug("SensorName: " + sensorName);
		
		// return the sensor name
		return sensorName;
	}
	
	/**
	 * 
	 * @return
	 */
	private Set<Datastream> generateDataStreamsFromDevice(Device device, String deviceId, String platformFQDN)
	{
		// WARNIG!!!! this part is utmost fragile and likely to stop working at
		// the smallest breeze due to the hard hardcoding approach used in event
		// payload generation. If any problem arises involving alignment of
		// datastream ids, probably the cause will be this code which generates
		// something little different from the hardcoded values put in the data
		// event map.
		
		// TODO: refactor completely the event-management part and remove
		// "magic" strings and hardcoded data...
		
		// reflection black magic. TODO: remove direct references to the device
		// object and create a representative event payload instead.
		Class<?> implementedInterfaces[] = device.getClass().getInterfaces();
		
		// return the sensor name
		return this.generateDataStreamsFromImplementedInterfaces(implementedInterfaces, device, deviceId, platformFQDN);
	}
	
	@SuppressWarnings("unchecked")
	private Set<Datastream> generateDataStreamsFromImplementedInterfaces(Class<?> interfaces[], Device device,
			String deviceId, String platformFQDN)
	{
		HashSet<Datastream> datastreams = new HashSet<Datastream>();
		
		// create an instance of UID generator
		UIDGenerator uidGen = UIDGenerator.getInstance();
		
		// iterate over all implemented interfaces
		for (int i = 0; i < interfaces.length; i++)
		{
			// debug
			this.logger.debug("Implemented interface:" + interfaces[i].getSimpleName());
			
			// check if the current interface extends
			if (Device.class.isAssignableFrom(interfaces[i]))
			{
				this.logger.debug("Assignable from Device");
				
				// get all declared methods
				Method methods[] = interfaces[i].getDeclaredMethods();
				if (methods.length > 0)
				{
					// search for valueKey match
					for (Method m : methods)
					{
						// remove leading and trailing spaces
						String dataStreamName = m.getName();
						
						// check if the datastream name is in the form getXXXX
						if (dataStreamName.startsWith("get"))
							dataStreamName = dataStreamName.substring(3);
						
						// build the datastream-id as a concatenation of the
						// device id and
						// the actual stream name (TODO: update to the last id
						// generation algorithm)
						String datastreamId = deviceId + "-" + dataStreamName;
						
						// create and set the datastream object
						Datastream dataStream = new Datastream();
						dataStream.setId(uidGen.uidFromStringArray(new String[] { datastreamId, platformFQDN }, "."));
						
						// generate the corresponding observer property
						ObservedProperty oProperty = new ObservedProperty();
						
						// build the observer property id
						String observedPropertyId = deviceId + "-observedPropery-" + dataStreamName;
						oProperty.setId(uidGen.uidFromStringArray(new String[] { observedPropertyId, platformFQDN },
								"."));
						
						// try to get the measurement value
						Method measureGetter;
						try
						{
							measureGetter = device.getClass().getMethod(m.getName() + "AsMeasure");
							
							// get the annotation if exists
							SemanticModel model = m.getAnnotation(SemanticModel.class);
							
							// check not null
							if (model != null)
							{
								// the annotated class uri
								String classURI = model.value();
								
								if ((classURI != null) && (!classURI.isEmpty()))
									oProperty.setUrn(classURI);
							}
							if (DecimalMeasure.class.isAssignableFrom(measureGetter.getReturnType()))
							{
								// get the value (ignored)
								DecimalMeasure<Quantity> value;
								try
								{
									value = (DecimalMeasure<Quantity>) measureGetter.invoke(device);
									
									// check not null
									if (value != null)
									{
										oProperty.setUnitOfMeasurement(value.getUnit().toString());
									}
								}
								catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
								{
									this.logger.warn("Unable to call getter for determining unit of measure", e);
								}
							}
						}
						catch (NoSuchMethodException | SecurityException e1)
						{
							// handle cases in which the unit might be null
							Unit unit = device.getUnit();
							if (unit != null)
								oProperty.setUnitOfMeasurement(unit.getValue());
							
						}
						
						// add the datastream reference to this ObservedProperty
						// instance
						// oProperty.addDatastream(dataStream);
						
						// add the observed property to the datastream
						dataStream.setObservedProperty(oProperty);
						
						if (dataStream != null)
							datastreams.add(dataStream);
						
					}
				}
				else
				{
					// recurse over super interfaces
					if (interfaces[i].isInterface())
					{
						// get data streams from inherited interfaces
						Set<Datastream> inheritedDatastreams = generateDataStreamsFromImplementedInterfaces(
								interfaces[i].getInterfaces(), device, deviceId, platformFQDN);
						
						// if not null, add to the device data streams
						if ((inheritedDatastreams != null) && (!inheritedDatastreams.isEmpty()))
						{
							// add all datastreams
							datastreams.addAll(inheritedDatastreams);
						}
					}
				}
			}
		}
		
		return datastreams;
	}
	
	/**
	 * Provides the corresponding ontology class representing the given device,
	 * if a suitable annotation is present.
	 * 
	 * @param device
	 *            The device for which the ontology class shall be retrieved.
	 * @return The ontology class as a {@link String} representing the class
	 *         URI.
	 */
	private String generateThingClass(Device device)
	{
		String classURI = "";
		
		// get directly implemented interfaces
		Class<?>[] implementedInterfaces = device.getClass().getInterfaces();
		
		// iterate over interfaces
		for (int i = 0; i < implementedInterfaces.length; i++)
		{
			// check if the current interface extends
			if (Device.class.isAssignableFrom(implementedInterfaces[i]))
			{
				// get the annotation if exists
				SemanticModel model = implementedInterfaces[i].getAnnotation(SemanticModel.class);
				
				// check not null
				if (model != null)
					// the annotated class uri
					classURI = model.value();
			}
		}
		
		return classURI;
	}
}
