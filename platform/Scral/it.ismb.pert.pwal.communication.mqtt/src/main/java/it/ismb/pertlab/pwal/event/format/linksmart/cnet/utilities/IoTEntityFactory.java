package it.ismb.pertlab.pwal.event.format.linksmart.cnet.utilities;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.model.AirQualirySensor;
import it.ismb.pertlab.pwal.api.devices.model.DewPointSensor;
import it.ismb.pertlab.pwal.api.devices.model.DistanceSensor;
import it.ismb.pertlab.pwal.api.devices.model.FillLevel;
import it.ismb.pertlab.pwal.api.devices.model.FlowMeter;
import it.ismb.pertlab.pwal.api.devices.model.HumiditySensor;
import it.ismb.pertlab.pwal.api.devices.model.LightSensor;
import it.ismb.pertlab.pwal.api.devices.model.PhMeter;
import it.ismb.pertlab.pwal.api.devices.model.PressureSensor;
import it.ismb.pertlab.pwal.api.devices.model.SimpleFillLevelSensor;
import it.ismb.pertlab.pwal.api.devices.model.SittingsCounter;
import it.ismb.pertlab.pwal.api.devices.model.Thermometer;
import it.ismb.pertlab.pwal.api.devices.model.TransitsCounter;
import it.ismb.pertlab.pwal.api.devices.model.VehicleCounter;
import it.ismb.pertlab.pwal.api.devices.model.VehicleSpeed;
import it.ismb.pertlab.pwal.api.devices.model.WasteBin;
import it.ismb.pertlab.pwal.api.devices.model.WaterPump;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.event.format.linksmart.cnet.jaxb.IoTEntity;
import it.ismb.pertlab.pwal.event.format.linksmart.cnet.jaxb.IoTProperty;
import it.ismb.pertlab.pwal.event.format.linksmart.cnet.jaxb.MetaType;
import it.ismb.pertlab.pwal.event.format.linksmart.cnet.jaxb.ObjectFactory;
import it.ismb.pertlab.pwal.event.format.linksmart.cnet.jaxb.TypedStringType;

import java.util.HashMap;

public class IoTEntityFactory
{
    private ObjectFactory objectFactory;
    private static HashMap<String, IoTEntity> iotEntityStore;

    public IoTEntityFactory()
    {
        this.objectFactory = new ObjectFactory();
        iotEntityStore = new HashMap<>();
    }

    /**
     * The method convert a Device object into an IoTEntity automatically
     * 
     * @param device the device object that has to be converted
     * @return an IoTEntity object following CNET schemas
     */
    public IoTEntity device2IoTEntity(Device device)
    {
        IoTEntity iotEntity = this.objectFactory.createIoTEntity();
        iotEntity.getTypeof().add(device.getType());
//        iotEntity
//                .getPrefix()
//                .add("geo: http://www.georss.org/georss/ almanac:http://ns.inertia.eu/ontologies xs:XMLSchema");
        iotEntity
                .setName(device.getId());
        iotEntity.setAbout(String.format("%s%s","_", device.getPwalId()).replace('-', '_'));

        MetaType networkTypeMeta = this.objectFactory.createMetaType();
        networkTypeMeta.setProperty("almanac:networkType");
        networkTypeMeta.setValue(device.getNetworkType());
        iotEntity.getMeta().add(networkTypeMeta);

        MetaType geoPointMeta = this.objectFactory.createMetaType();
        geoPointMeta.setProperty("geo:point");
        // what if they are not available?
        if (device.getLocation() != null)
        {
            geoPointMeta.setValue(device.getLocation().getLon() + " "
                    + device.getLocation().getLat());
            iotEntity.getMeta().add(geoPointMeta);
        }

        // what if sensor can return two values (different types)
        // an iotproperty has to be created for each capabilities
        switch (device.getType())
        {
        case DeviceType.FILL_LEVEL_SENSOR:
            FillLevel fl = (FillLevel) device;
//            setIoTProperty(fl, iotEntity, "Depth value", "getDepth", fl
//                    .getDepth().getClass().getSimpleName(), "cm");
            setIoTProperty(fl, iotEntity, "Level value", "getLevel", fl
                    .getLevel().getClass().getName(), "%");
            break;
        case DeviceType.SIMPLE_FILL_LEVEL_SENSOR:
            SimpleFillLevelSensor sfl = (SimpleFillLevelSensor) device;
            setIoTProperty(sfl, iotEntity, "Level value", "getLevel", sfl
                    .getLevel().getClass().getName(), "%");
            break;
        case DeviceType.FLOW_METER_SENSOR:
            FlowMeter fm = (FlowMeter) device;
            setIoTProperty(fm, iotEntity, "Flow value", "getFlow", fm.getFlow()
                    .getClass().getName(), "m^3/s");
            break;
        case DeviceType.WATER_PUMP:
            WaterPump wp = (WaterPump) device;
            setIoTProperty(wp, iotEntity, "Water pump speed", "getSpeed", wp.getSpeed()
                    .getClass().getName(), "m^3/s");
            break;
        case DeviceType.PH_METER:
            PhMeter pm = (PhMeter) device;
            setIoTProperty(pm, iotEntity, "Ph value", "getPh", pm.getPh()
                    .getClass().getSimpleName(), "ph");
            break;
        case DeviceType.VEHICLE_COUNTER:
            VehicleCounter vc = (VehicleCounter) device;
            setIoTProperty(vc, iotEntity, "Car count value", "getCount", vc
                    .getCount().getClass().getSimpleName(), "#");
            setIoTProperty(device, iotEntity, "Car occupancy value",
                    "getOccupancy", vc.getOccupancy().getClass()
                            .getSimpleName(), "%");
            break;
        case DeviceType.VEHICLE_SPEED:
            VehicleSpeed vs = (VehicleSpeed) device;
            setIoTProperty(vs, iotEntity, "Car average speed value",
                    "getSpeed",
                    vs.getAverageSpeed().getClass().getSimpleName(), "Km/h");
            setIoTProperty(vs, iotEntity, "Car median speed value",
                    "getMedianSpeed", vs.getMedianSpeed().getClass()
                            .getSimpleName(), "Km/h");
            setIoTProperty(vs, iotEntity, "Car count value", "getCount", vs
                    .getCount().getClass().getSimpleName(), "#");
            setIoTProperty(vs, iotEntity, "Car occupancy value",
                    "getOccupancy", vs.getOccupancy().getClass()
                            .getSimpleName(), "%");
            break;
        case DeviceType.AIR_QUALITY_SENSOR:
            AirQualirySensor aq = (AirQualirySensor) device;
            setIoTProperty(aq, iotEntity, "CO2 level", "getCO2Level", aq
                    .getCO2Level().getClass().getSimpleName(), "Ppm");
            break;
        case DeviceType.DEW_POINT_SENSOR:
            DewPointSensor dp = (DewPointSensor) device;
            setIoTProperty(dp, iotEntity, "Dew point temperature",
                    "getDewPoint", dp.getDewPointTemperature().getClass()
                            .getSimpleName(), "C");
            break;
        case DeviceType.DISTANCE_SENSOR:
            DistanceSensor ds = (DistanceSensor) device;
            setIoTProperty(ds, iotEntity, "Distance value (cm)",
                    "getDistanceCm", ds.getDistanceCm().getClass()
                            .getSimpleName(), "cm");
            setIoTProperty(ds, iotEntity, "Distance value (inch)",
                    "getDistanceInch", ds.getDistanceInch().getClass()
                            .getSimpleName(), "inch");
            break;
        case DeviceType.HUMIDITY_SENSOR:
            HumiditySensor hs = (HumiditySensor) device;
            setIoTProperty(hs, iotEntity, "Humidity level", "getHumidity", hs
                    .getHumidity().getClass().getSimpleName(), "%");
            break;
        case DeviceType.LIGHT_SENSOR:
            LightSensor ls = (LightSensor) device;
            setIoTProperty(ls, iotEntity, "Lumen value", "getLumen", ls
                    .getLight().getClass().getSimpleName(), "lm");
            break;
        case DeviceType.PRESSURE_SENSOR:
            PressureSensor ps = (PressureSensor) device;
            setIoTProperty(ps, iotEntity, "Pressure value", "getBar", ps
                    .getPressure().getClass().getSimpleName(), "bar");
            break;
        case DeviceType.SITTINGS_COUNTER:
            SittingsCounter sc = (SittingsCounter) device;
            setIoTProperty(sc, iotEntity, "Sitting counter", "getSittingCount",
                    sc.getSittingsCount().getClass().getSimpleName(), "#");
            break;
        case DeviceType.THERMOMETER:
            Thermometer t = (Thermometer) device;
            setIoTProperty(t, iotEntity, "Temperature value", "getTemperature",
                    t.getTemperature().getClass().getSimpleName(), "C");
            break;
        case DeviceType.TRANSITS_COUNTER:
            TransitsCounter tc = (TransitsCounter) device;
            setIoTProperty(tc, iotEntity, "Transit count value",
                    "getTransitCount", tc.getTransitCount().getClass()
                            .getSimpleName(), "#");
            break;
        case DeviceType.WASTE_BIN:
            WasteBin wb = (WasteBin) device;
            setIoTProperty(wb, iotEntity, "Fill level value", "getFillLevel",
                    wb.getLevel().getClass().getSimpleName(), "%");
            setIoTProperty(wb, iotEntity, "Temperature value",
                    "getTemperature", wb.getTemperature().getClass()
                            .getSimpleName(), "C");
            break;
        default:
            break;
        }
        if (iotEntity != null)
        {
            synchronized (iotEntityStore)
            {
                iotEntityStore.put(device.getPwalId(), iotEntity);
            }
        }
        return iotEntity;
    }

    /**
     * This method is useful to create a new IoTProperty and set it into an
     * existing IoTEntity. For each device this method must be called for each
     * capability exposed by the device.
     * 
     * @param device is the reference to the device
     * @param iotEntity is the existing IoTEntity
     * @param propertyName is the choosen name for the property
     * @param propertyTypeOf is the name of the method called to obtain the
     *            value to be inserted into the IoTProperty
     * @param dataType is the type of the data to be insert into the IoTProperty
     */
    private void setIoTProperty(Device device, IoTEntity iotEntity,
            String propertyName, String propertyTypeOf, String dataType,
            String unitSymbol)
    {
        IoTProperty occupancyProperty = this.objectFactory.createIoTProperty();
        occupancyProperty.getTypeof().add(
                String.format("%s:%s:%s", "almanac", device.getType(),
                        propertyTypeOf));
        occupancyProperty.setName(propertyName);
        occupancyProperty.setDatatype(dataType);
        occupancyProperty.setAbout(String.format("%s:%s:%s", device.getId(),
                device.getType(), propertyTypeOf));
        TypedStringType oum = this.objectFactory.createTypedStringType();
//        if (device.getUnit() != null && !device.getUnit().getSymbol().isEmpty())
//            oum.setValue(device.getUnit().getSymbol());
//        else
        oum.setValue(unitSymbol);
        oum.getTypeof().add(propertyName);
        occupancyProperty.setUnitOfMeasurement(oum);
        iotEntity.getIoTProperty().add(occupancyProperty);
    }

    public static HashMap<String, IoTEntity> getIotEntityStore()
    {
        synchronized (iotEntityStore)
        {
            return iotEntityStore;
        }
    }
}
