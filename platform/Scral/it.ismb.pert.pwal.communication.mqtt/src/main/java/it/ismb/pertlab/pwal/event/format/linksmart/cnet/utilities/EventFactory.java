package it.ismb.pertlab.pwal.event.format.linksmart.cnet.utilities;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.api.events.base.PWALNewDataAvailableEvent;
import it.ismb.pertlab.pwal.event.format.linksmart.cnet.jaxb.EventModified;
import it.ismb.pertlab.pwal.event.format.linksmart.cnet.jaxb.IoTEntity;
import it.ismb.pertlab.pwal.event.format.linksmart.cnet.jaxb.IoTProperty;
import it.ismb.pertlab.pwal.event.format.linksmart.cnet.jaxb.IoTStateObservation;
import it.ismb.pertlab.pwal.event.format.linksmart.cnet.jaxb.ObjectFactory;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventFactory
{
    private Logger log = LoggerFactory.getLogger(EventFactory.class);
    private ObjectFactory objectFactory = new ObjectFactory();

    public EventModified createEvent(PWALNewDataAvailableEvent event,
            String eventType) throws DatatypeConfigurationException,
            JAXBException
    {
        // create CNET Event object
        EventModified toReturn = new EventModified();
        // set Event about as random UUID
        // toReturn.setAbout(UUID.randomUUID().toString());
        //
        // // create the EventMeta
        // EventMeta meta = this.objectFactory.createEventMeta();
        // // set the EventMeta about -> same Event object about
        // meta.setEventID(toReturn.getAbout());

        // create an event topic
        // TypedStringType topic = this.objectFactory.createTypedStringType();
        // topic.getTypeof().add(eventType);
        // // assign the topic to the EventMeta object
        // meta.setTopic(topic);
        //
        // //set the event type
        // TypedStringType eventTypeTypedString =
        // this.objectFactory.createTypedStringType();
        // eventTypeTypedString.getTypeof().add(String.format("%s:%s",
        // "almanac",event.getSender().getType()));
        // meta.setEventType(eventTypeTypedString);
        // set a timestamp for the Event converting a JodaTime object into a
        // XMLGregorianCalendar
        // DateTime timestamp = new DateTime(event.getTimeStamp(),
        // DateTimeZone.UTC);
        // GregorianCalendar gregorianCalendar = (GregorianCalendar)
        // GregorianCalendar
        // .getInstance();
        // gregorianCalendar.setTime(timestamp.toDate());
        // XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory
        // .newInstance().newXMLGregorianCalendar(gregorianCalendar);
        // meta.setTimestamp(xmlGregorianCalendar);
        // toReturn.setEventMeta(meta);

        // creating the source IoTEntity representing the device generating the
        // event
        // check if exists an existing IoTEntity for the current device
        IoTEntity existingIoTEntity = IoTEntityFactory.getIotEntityStore().get(
                event.getSenderId());
        if (existingIoTEntity != null)
        {
            toReturn.setAbout(existingIoTEntity.getAbout());
            // creating an other IoTEntity object to add a IoTObservation to
            // each device IoTProperty
            IoTEntity tosend = this.objectFactory.createIoTEntity();
            tosend.setAbout(existingIoTEntity.getAbout());
            // loop all devices iot properties
            for (IoTProperty p : existingIoTEntity.getIoTProperty())
            {
                // Boolean isToSend = true;
                IoTProperty iotPropertyToAdd = this.objectFactory
                        .createIoTProperty();
                IoTStateObservation observation = this.objectFactory
                        .createIoTStateObservation();
                iotPropertyToAdd.setAbout(p.getAbout());
                // iotPropertyToAdd.setDatatype(p.getDatatype());
                // iotPropertyToAdd.setDescription(p.getDescription());
                // iotPropertyToAdd.setName(p.getName());
                // iotPropertyToAdd.setUnitOfMeasurement(p.getUnitOfMeasurement());
                Device d = event.getSender();
                switch (d.getType())
                {
                case DeviceType.FILL_LEVEL_SENSOR: // Serial manager devices
                                                   // must be updated
                    switch (p.getName())
                    {
                    case "Level value":
                        observation.setValue(String.valueOf(event.getValues()
                                .get("getLevel")));
                        break;
//                    case "Depth value":
//                        observation.setValue(String.valueOf(event.getValues()
//                                .get("getDepth")));
//                        break;
                    default:
                        break;
                    }
                    break;
                case DeviceType.SIMPLE_FILL_LEVEL_SENSOR: // Serial manager devices
                    // must be updated
                    switch (p.getName())
                    {
                    case "Level value":
                        observation.setValue(String.valueOf(event.getValues()
                                .get("getLevel")));
                        break;
                    default:
                        break;
                    }
                    break;
                case DeviceType.FLOW_METER_SENSOR:
                    observation.setValue(String.valueOf(event.getValues().get(
                            "getFlow")));
                    break;
                case DeviceType.WATER_PUMP:
                    observation.setValue(String.valueOf(event.getValues().get(
                            "getSpeed")));
                    break;
                case DeviceType.PH_METER:
                    observation.setValue(String.valueOf(event.getValues().get(
                            "getPh")));
                    break;
                case DeviceType.VEHICLE_COUNTER:
                    switch (p.getName())
                    {
                    case "Car occupancy value":
                        observation.setValue(String.valueOf(event.getValues()
                                .get("getOccupancy")));
                        break;
                    case "Car count value":
                        observation.setValue(String.valueOf(event.getValues()
                                .get("getCount")));
                        break;
                    default:
                        break;
                    }
                    break;
                case DeviceType.VEHICLE_SPEED:
                    switch (p.getName())
                    {
                    case "Car average speed value":
                        observation.setValue(String.valueOf(event.getValues()
                                .get("getAverageSpeed")));
                        break;
                    case "Car median speed value":
                        observation.setValue(String.valueOf(event.getValues()
                                .get("getMedianSpeed")));
                        break;
                    case "Car occupancy value":
                        observation.setValue(String.valueOf(event.getValues()
                                .get("getOccupancy")));
                        break;
                    case "Car count value":
                        observation.setValue(String.valueOf(event.getValues()
                                .get("getCount")));
                        break;
                    default:
                        log.error("Invalid IoTProperty name: {}", p.getName());
                        break;
                    }
                    break;
                case DeviceType.AIR_QUALITY_SENSOR:
                    observation.setValue(String.valueOf(event.getValues().get(
                            "getCO2Level")));
                    break;
                case DeviceType.DEW_POINT_SENSOR:
                    observation.setValue(String.valueOf(String.valueOf(event
                            .getValues().get("getDewPointTemperature"))));
                    break;
                case DeviceType.DISTANCE_SENSOR:
                    switch (p.getName())
                    {
                    case "Distance value (cm)":
                        observation
                                .setValue(String.valueOf(String.valueOf(event
                                        .getValues().get("getDistanceCm"))));
                        break;
                    case "Distance value (inch)":
                        observation.setValue(String.valueOf(event.getValues()
                                .get("getDistanceInch")));
                        break;
                    default:
                        log.error("Invalid iotproperty name: {}", p.getName());
                        break;
                    }
                    break;
                case DeviceType.HUMIDITY_SENSOR:
                    observation.setValue(String.valueOf(event.getValues().get(
                            "getHumidity")));
                    break;
                case DeviceType.LIGHT_SENSOR:
                    observation.setValue(String.valueOf(event.getValues().get(
                            "getLight")));
                    break;
                case DeviceType.PRESSURE_SENSOR:
                    observation.setValue(String.valueOf(event.getValues().get(
                            "getPressure")));
                    break;
                case DeviceType.SITTINGS_COUNTER:
                    observation.setValue(String.valueOf(event.getValues().get(
                            "getSittingsCount")));
                    break;
                case DeviceType.THERMOMETER:
                    observation.setValue(String.valueOf(event.getValues().get(
                            "getTemperature")));
                    break;
                case DeviceType.TRANSITS_COUNTER:
                    observation.setValue(String.valueOf(event.getValues().get(
                            "getTransitCount")));
                    break;
                case DeviceType.WASTE_BIN:
                    switch (p.getName())
                    {
                    case "Fill level value":
                        observation.setValue(String.valueOf(event.getValues()
                                .get("getFillLevel")));
                        break;
                    case "Temperature value":
                        observation.setValue(String.valueOf(event.getValues()
                                .get("getTemperature")));
                        break;
                    default:
                        break;
                    }
                    break;
                default:
                    log.error("Unknown device type: {}", d.getType());
                    break;
                }
                XMLGregorianCalendar timeStamp;
                XMLGregorianCalendar expiration;
                try
                {
                    timeStamp = DatatypeFactory.newInstance()
                            .newXMLGregorianCalendar(
                                    event.getTimeStamp().toString());
                    observation.setPhenomenonTime(timeStamp);
                    // TODO: is there an event type list????
                    switch (eventType)
                    {
                    case "OBSERVATION":
                        expiration = DatatypeFactory
                                .newInstance()
                                .newXMLGregorianCalendar(
                                        ((PWALNewDataAvailableEvent) event)
                                                .getExpirationTime().toString());

                        break;
                    default:
                        expiration = timeStamp;
                        break;
                    }
                    observation.setResultTime(expiration);
                    iotPropertyToAdd.getIoTStateObservation().add(observation);
                    toReturn.getProperty().add(iotPropertyToAdd);
                    // tosend.getIoTProperty().add(iotPropertyToAdd);
                }
                catch (DatatypeConfigurationException e)
                {
                    log.error("DatatypeConfigurationException: ", e);
                }
            }
            // create the Source object to be added (really don't get the
            // meaning)
            // Source source = this.objectFactory.createSource();
            // source.setAbout(UUID.nameUUIDFromBytes(new
            // String("ALMANAC").getBytes()).toString());
            // source.setProject("ALMANAC");
            // toReturn.setSource(source);

            // set the IoTEntity as Event Content Type
            //

        }
        else
            return null;
        return toReturn;
    }
}
