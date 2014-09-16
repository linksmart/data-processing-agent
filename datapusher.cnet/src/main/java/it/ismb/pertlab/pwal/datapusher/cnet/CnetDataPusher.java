package it.ismb.pertlab.pwal.datapusher.cnet;

import it.ismb.pertlab.pwal.api.data.pusher.DataPusher;
import it.ismb.pertlab.pwal.api.devices.events.PWALDeviceListener;
import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.model.FillLevel;
import it.ismb.pertlab.pwal.api.devices.model.FlowMeterFit;
import it.ismb.pertlab.pwal.api.devices.model.VehicleCounter;
import it.ismb.pertlab.pwal.api.devices.model.VehicleSpeed;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.api.internal.Pwal;
import it.ismb.pertlab.pwal.datapusher.cnet.datamodel.ArrayOfIoTEntity;
import it.ismb.pertlab.pwal.datapusher.cnet.datamodel.IoTEntity;
import it.ismb.pertlab.pwal.datapusher.cnet.datamodel.IoTProperty;
import it.ismb.pertlab.pwal.datapusher.cnet.datamodel.IoTStateObservation;
import it.ismb.pertlab.pwal.datapusher.cnet.datamodel.Meta;
import it.ismb.pertlab.pwal.datapusher.cnet.datamodel.ObjectFactory;
import it.ismb.pertlab.pwal.datapusher.cnet.datamodel.TypedStringType;
import it.ismb.pertlab.pwal.datapusher.cnet.restclient.CnetDataPusherRestClient;

import java.util.HashMap;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;

public class CnetDataPusher extends DataPusher implements PWALDeviceListener
{
    private Pwal pwal;
    private ObjectFactory objectFactory;
    private CnetDataPusherRestClient cnetRestClient;
    private HashMap<String, IoTEntity> iotEntities;
    private String serviceEndpoint;

    public CnetDataPusher(int seconds, Pwal pwal, String serviceEndpoint)
    {
        super(seconds, pwal);
        this.serviceEndpoint = serviceEndpoint;
        log.debug("Service endpoint: {} ", this.serviceEndpoint);
        this.objectFactory = new ObjectFactory();
        this.pwal = pwal;
        this.pwal.addPwalDeviceListener(this);
        this.cnetRestClient = new CnetDataPusherRestClient(
                this.serviceEndpoint, log);
        this.iotEntities = new HashMap<>();
    }

    @Override
    public void run()
    {
        log.info("Pushing data to data manager");
        DateTime dateTime = new DateTime(org.joda.time.DateTimeZone.UTC);
        try
        {
            XMLGregorianCalendar gc = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateTime.toGregorianCalendar());
            log.info("Date: {}", gc);
            Thread.sleep(2000);
        }
        catch (DatatypeConfigurationException | InterruptedException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        log.debug("Date time: {}", dateTime.toString());
        for (Device d : this.pwalDevicesList)
        {
            log.debug("Creating IoTEntity for {}", d.getPwalId());
            IoTEntity existingIoTEntity = this.iotEntities.get(d.getPwalId());
            if (existingIoTEntity != null)
            {
                IoTEntity tosend = this.objectFactory.createIoTEntity();
                tosend.setAbout(existingIoTEntity.getAbout());
                for (IoTProperty p : existingIoTEntity.getIoTProperty())
                {
                    // Boolean isToSend = true;
                    IoTProperty iotPropertyToAdd = this.objectFactory
                            .createIoTProperty();
                    IoTStateObservation observation = this.objectFactory
                            .createIoTStateObservation();
                    iotPropertyToAdd.setAbout(p.getAbout());
                    switch (d.getType())
                    {
                    case DeviceType.FILL_LEVEL_SENSOR:
                        FillLevel fl = (FillLevel) d;
                        switch (p.getName())
                        {
                        case "Depth values":
                            String depth = String.valueOf(fl.getDepth());
                            if (depth != null && !depth.isEmpty())
                            {
                                observation.setValue(depth);
                            }
                            else
                                observation.setValue("0");
                            break;
                        case "Level values":
                            String level = String.valueOf(fl.getLevel());
                            if (level != null && !level.isEmpty())
                                observation.setValue(level);
                            else
                                observation.setValue("0");
                            break;
                        default:
                            break;
                        }
                        break;
                    case DeviceType.FLOW_METER_SENSOR:
                        FlowMeterFit fm = (FlowMeterFit) d;
                        String flow = String.valueOf(fm.getFlow());
                        if (flow != null && !flow.isEmpty())
                        {
                            Double doubleflow = Double.parseDouble(flow);
                            doubleflow = doubleflow / 10000;
                            observation.setValue(String.valueOf(doubleflow));
                        }
                        else
                            observation.setValue("0");
                        break;
                    case DeviceType.VEHICLE_COUNTER:
                        VehicleCounter counter = (VehicleCounter) d;
                        switch (p.getName())
                        {
                        case "Occupancy":
                            String occupancy = String.valueOf(counter
                                    .getOccupancy());
                            log.debug("Occupancy case: {}", occupancy);
                            if (!occupancy.equals("-1.0"))
                            {
                                observation.setValue(occupancy);
                            }
                            else
                                observation.setValue("0");
                            break;
                        case "Number of vehicle":
                            String number = String.valueOf(counter.getCount());
                            log.debug("Number of vehicle case: {}", number);
                            if (!number.equals("-1.0"))
                                observation.setValue(number);
                            else
                                observation.setValue("0");
                            break;
                        default:
                            break;
                        }
                        break;
                    case DeviceType.VEHICLE_SPEED:
                        log.debug("After:" + d.getType());
                        VehicleSpeed speed = (VehicleSpeed) d;
                        switch (p.getName())
                        {
                        case "Average speed":
                            String average = String.valueOf(speed
                                    .getAverageSpeed());
                            log.debug("Average speed case: {}", average);
                            if (!average.equals("-1.0"))
                            {
                                observation.setValue(average);
                            }
                            else
                                observation.setValue("0");
                            break;
                        case "Median speed":
                            String median = String.valueOf(speed
                                    .getMedianSpeed());
                            log.debug("Median speed case: {}", median);
                            if (!median.equals("-1.0"))
                            {
                                observation.setValue(median);
                            }
                            else
                                observation.setValue("0");
                            break;
                        case "Occupancy":
                            String occupancy = String.valueOf(speed
                                    .getOccupancy());
                            log.debug("Occupancy case: {}", occupancy);
                            if (!occupancy.equals("-1.0"))
                            {
                                observation.setValue(occupancy);
                            }
                            else
                                observation.setValue("0");
                            break;
                        default:
                            break;
                        }
                        break;
                    }
                    XMLGregorianCalendar xmlCal;
                    try
                    {
                        xmlCal = DatatypeFactory.newInstance()
                                .newXMLGregorianCalendar(dateTime.toString());
                        observation.setPhenomenonTime(xmlCal);
                        observation.setResultTime(xmlCal);
                        iotPropertyToAdd.getIoTStateObservation().add(
                                observation);
                        tosend.getIoTProperty().add(iotPropertyToAdd);
                    }
                    catch (DatatypeConfigurationException e)
                    {
                        log.error("DatatypeConfigurationException: ", e);
                    }
                }
                tosend.toXml();
                log.info("Sending data to the cloud...");
                this.cnetRestClient.pushNewValues(tosend);
                log.info("Data sent to the cloud.");
            }
        }
    }

    @Override
    public void notifyPWALDeviceAdded(Device newDevice)
    {
        this.pwalDevicesList.add(newDevice);
        log.info("Checking if the device already exists.");
        ArrayOfIoTEntity arrayOfIoTEntity = cnetRestClient
                .alreadyExists(newDevice.getPwalId());
        if (arrayOfIoTEntity != null
                && arrayOfIoTEntity.getIoTEntity().size() == 0)
        {
            log.info(
                    "Device {} does not exist into the database. Going to push...",
                    newDevice);
            log.info("Pushing new device {} into CNET cloud database.",
                    newDevice.getPwalId());

            IoTEntity iotEntity = this.objectFactory.createIoTEntity();
            iotEntity.getTypeof().add(newDevice.getType());
            iotEntity
                    .getPrefix()
                    .add("geo: http://www.georss.org/georss/ almanac:http://ns.inertia.eu/ontologies xs:XMLSchema");
            iotEntity.setName(newDevice.getId() + " ("
                    + newDevice.getNetworkType() + ")");
            iotEntity.setAbout(newDevice.getPwalId());

            Meta networkTypeMeta = this.objectFactory.createMeta();
            networkTypeMeta.getProperty().add("almanac:networkType");
            networkTypeMeta.setValue(newDevice.getNetworkType());
            iotEntity.getMeta().add(networkTypeMeta);

            Meta geoPointMeta = this.objectFactory.createMeta();
            geoPointMeta.getProperty().add("geo:point");
            // what if they are not available?
            if (newDevice.getLocation() != null)
                geoPointMeta.setValue(newDevice.getLocation().getLat() + " "
                        + newDevice.getLocation().getLon());
            iotEntity.getMeta().add(geoPointMeta);

            // what if sensor can return two values (different types)
            // an iotproperty has to be created for each capabilties
            switch (newDevice.getType())
            {
            case DeviceType.FILL_LEVEL_SENSOR:
                IoTProperty depthIotProperty = this.objectFactory
                        .createIoTProperty();
                depthIotProperty.getTypeof().add(
                        "almanac:FillLevelSensor:" + "GetDepth");
                depthIotProperty.setName("Depth values");
                depthIotProperty.setDatatype("integer");
                depthIotProperty.setAbout(newDevice.getPwalId()
                        + depthIotProperty.getName());
                TypedStringType dum = this.objectFactory
                        .createTypedStringType();
                dum.setValue("cm");
                depthIotProperty.setUnitOfMeasurement(dum);
                iotEntity.getIoTProperty().add(depthIotProperty);
                IoTProperty levelIotProperty = this.objectFactory
                        .createIoTProperty();
                levelIotProperty.getTypeof().add(
                        "almanac:FillLevelSensor:" + "GetLevel");
                levelIotProperty.setName("Level values");
                levelIotProperty.setDatatype("integer");
                levelIotProperty.setAbout(newDevice.getPwalId()
                        + levelIotProperty.getName());
                TypedStringType lum = this.objectFactory
                        .createTypedStringType();
                lum.setValue("%");
                levelIotProperty.setUnitOfMeasurement(lum);
                iotEntity.getIoTProperty().add(levelIotProperty);
                break;
            case DeviceType.FLOW_METER_SENSOR:
                IoTProperty flowIoTProperty = this.objectFactory
                        .createIoTProperty();
                flowIoTProperty.getTypeof().add(
                        "almanac:FlowMeterSensor:" + "GetFlow");
                flowIoTProperty.setName("Flow values");
                flowIoTProperty.setDatatype("integer");
                flowIoTProperty.setAbout(newDevice.getPwalId()
                        + flowIoTProperty.getName());
                TypedStringType fum = this.objectFactory
                        .createTypedStringType();
                fum.setValue("m^3/s");
                flowIoTProperty.setUnitOfMeasurement(fum);
                iotEntity.getIoTProperty().add(flowIoTProperty);
                break;
            case DeviceType.VEHICLE_COUNTER:
                iotEntity.setAbout("SmartSantander-" + newDevice.getId());
                IoTProperty occupancyProperty = this.objectFactory
                        .createIoTProperty();
                occupancyProperty.getTypeof().add(
                        "SmartSantander:VehicleCounterSensor:"
                                + "GetCarOccupancy");
                occupancyProperty.setName("Occupancy");
                occupancyProperty.setDatatype("double");
                occupancyProperty.setAbout(newDevice.getId()
                        + occupancyProperty.getName());
                TypedStringType oum = this.objectFactory
                        .createTypedStringType();
                oum.setValue("%");
                occupancyProperty.setUnitOfMeasurement(oum);
                iotEntity.getIoTProperty().add(occupancyProperty);

                IoTProperty carCountProperty = this.objectFactory
                        .createIoTProperty();
                carCountProperty.getTypeof().add(
                        "SmartSantander:VehicleCounterSensor:" + "GetCarCount");
                carCountProperty.setName("Number of vehicle");
                carCountProperty.setDatatype("double");
                carCountProperty.setAbout(newDevice.getId()
                        + carCountProperty.getName());
                TypedStringType cum = this.objectFactory
                        .createTypedStringType();
                cum.setValue("");
                carCountProperty.setUnitOfMeasurement(cum);
                iotEntity.getIoTProperty().add(carCountProperty);

                break;
            case DeviceType.VEHICLE_SPEED:
                iotEntity.setAbout("SmartSantander-" + newDevice.getId());
                IoTProperty averageSpeedProperty = this.objectFactory
                        .createIoTProperty();
                averageSpeedProperty.getTypeof().add(
                        "SmartSantander:VehicleSpeedSensor:"
                                + "GetAverageSpeed");
                averageSpeedProperty.setName("Average speed");
                averageSpeedProperty.setDatatype("double");
                averageSpeedProperty.setAbout(newDevice.getId()
                        + averageSpeedProperty.getName());
                TypedStringType asum = this.objectFactory
                        .createTypedStringType();
                asum.setValue("Km/h");
                averageSpeedProperty.setUnitOfMeasurement(asum);
                iotEntity.getIoTProperty().add(averageSpeedProperty);
                IoTProperty medianSpeedProperty = this.objectFactory
                        .createIoTProperty();
                medianSpeedProperty.getTypeof()
                        .add("SmartSantander:VehicleSpeedSensor:"
                                + "GetMedianSpeed");
                medianSpeedProperty.setName("Median speed");
                medianSpeedProperty.setDatatype("double");
                medianSpeedProperty.setAbout(newDevice.getId()
                        + medianSpeedProperty.getName());
                TypedStringType msum = this.objectFactory
                        .createTypedStringType();
                msum.setValue("Km/h");
                medianSpeedProperty.setUnitOfMeasurement(msum);
                iotEntity.getIoTProperty().add(medianSpeedProperty);
                IoTProperty occupancyProperty1 = this.objectFactory
                        .createIoTProperty();
                occupancyProperty1.getTypeof().add(
                        "SmartSantander:VehicleSpeedSensor:"
                                + "GetCarOccupancy");
                occupancyProperty1.setName("Occupancy");
                occupancyProperty1.setDatatype("double");
                occupancyProperty1.setAbout(newDevice.getId()
                        + occupancyProperty1.getName());
                TypedStringType oum1 = this.objectFactory
                        .createTypedStringType();
                oum1.setValue("%");
                occupancyProperty1.setUnitOfMeasurement(oum1);
                iotEntity.getIoTProperty().add(occupancyProperty1);
                break;
            case DeviceType.PH_METER:
                IoTProperty phIoTProperty = this.objectFactory
                        .createIoTProperty();
                phIoTProperty.getTypeof().add(
                        "almanac:FlowMeterSensor:" + "GetFlow");
                phIoTProperty.setName("Flow values");
                phIoTProperty.setDatatype("integer");
                phIoTProperty.setAbout(newDevice.getPwalId()
                        + phIoTProperty.getName());
                TypedStringType phum = this.objectFactory
                        .createTypedStringType();
                phum.setValue("ph");
                phIoTProperty.setUnitOfMeasurement(phum);
                iotEntity.getIoTProperty().add(phIoTProperty);
                break;
            default:
                break;
            }

            iotEntity.toXml();
            log.info("Sending data to the cloud...");
            // IoTEntity deviceAbout =
            // this.cnetRestClient.pushNewIoTEntity(iotEntity);
            log.info("Data sent to the cloud.");
            // IoTEntity deviceAbout = new IoTEntity();
            // this.iotEntities.put(newDevice.getPwalId(), deviceAbout);

            // deviceAbout.toXml();
        }
        else
        {
            log.info("Device {} already exist. Saving the about id..",
                    newDevice);
            IoTEntity iot = arrayOfIoTEntity.getIoTEntity().get(0);
            this.iotEntities.put(newDevice.getPwalId(), iot);
            // this.pwal.getDevice(newDevice.getPwalId()).setPwalId(iot.getAbout());
        }
    }

    @Override
    public void notifyPWALDeviceRemoved(Device removedDevice)
    {
        this.pwalDevicesList.remove(removedDevice);
    }
}
