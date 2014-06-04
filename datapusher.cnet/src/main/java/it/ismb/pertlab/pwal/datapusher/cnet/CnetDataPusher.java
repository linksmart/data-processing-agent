package it.ismb.pertlab.pwal.datapusher.cnet;

import it.ismb.pertlab.pwal.api.data.pusher.DataPusher;
import it.ismb.pertlab.pwal.api.devices.events.PWALDeviceListener;
import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.model.FillLevel;
import it.ismb.pertlab.pwal.api.devices.model.FlowMeter;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceNetworkType;
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
	
	public CnetDataPusher(int seconds, Pwal pwal) {
		super(seconds, pwal);
		this.objectFactory = new ObjectFactory();
		this.pwal = pwal;
		this.pwal.addPwalDeviceListener(this);
		this.cnetRestClient = new CnetDataPusherRestClient("http://energyportal.cnet.se/StorageManagerMdb20140604/REST/IoTEntities", log);
		this.iotEntities = new HashMap<>();
	}	
	
	public void start()
	{
		log.debug("Start method.");
	}

	@Override
	public void run() {
		log.info("Pushing data to data manager");
		DateTime dateTime = new DateTime(org.joda.time.DateTimeZone.UTC);
		log.debug("Date time: {}",dateTime.toString());
		for (Device d : pwal.getDevicesList()) {
			log.debug("Creating IoTEntity for {}",d.getPwalId());
			IoTEntity iotEntity = this.iotEntities.get(d.getPwalId());
			IoTEntity tosend = this.objectFactory.createIoTEntity();
			tosend.setAbout(iotEntity.getAbout());
			for (IoTProperty p : iotEntity.getIoTProperty()) {
				IoTProperty toadd = this.objectFactory.createIoTProperty();
				IoTStateObservation o = this.objectFactory.createIoTStateObservation();
				toadd.setAbout(p.getAbout());
				switch (d.getType()) {
					case DeviceType.FILL_LEVEL_SENSOR:
						FillLevel fl = (FillLevel)d;
						switch (p.getName()) {
						case "Depth values":
							o.setValue(String.valueOf(fl.getDepth()));
							break;
						case "Level values":
							o.setValue(String.valueOf(fl.getLevel()));
							break;
						default:
							break;
						}
						break;
					case DeviceType.FLOW_METER_SENSOR:
						FlowMeter fm = (FlowMeter)d;
						o.setValue(String.valueOf(fm.getFlow()));
				}
				XMLGregorianCalendar xmlCal;
				try {
					xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateTime.toString());
					o.setPhenomenonTime(xmlCal);
					o.setResultTime(xmlCal);
					toadd.getIoTStateObservation().add(o);
					iotEntity.getIoTProperty().add(toadd);
				} catch (DatatypeConfigurationException e) {
					log.error("DatatypeConfigurationException: ",e);
				}
			}
			tosend.toXml();
		}
	}

	@Override
	public void notifyPWALDeviceAdded(Device newDevice) {
		log.info("Checking if the device already exists.");
		ArrayOfIoTEntity arrayOfIoTEntity = cnetRestClient.alreadyExists(newDevice.getId());
		if(arrayOfIoTEntity.getIoTEntity().size() == 0)
		{
			if(!newDevice.getNetworkType().equals(DeviceNetworkType.SMARTSANTANDER))
			{
				log.info("Device {} does not exist into the database. Going to push...",newDevice);
				log.info("Pushing new device {} into CNET cloud database.", newDevice.getPwalId());
				
				IoTEntity iotEntity = this.objectFactory.createIoTEntity();
				iotEntity.getTypeof().add(newDevice.getType());
				iotEntity.getPrefix().add("geo: http://www.georss.org/georss/ almanac:http://ns.inertia.eu/ontologies xs:XMLSchema");
				iotEntity.setName(newDevice.getId());
				
				Meta networkTypeMeta = this.objectFactory.createMeta();
				networkTypeMeta.getProperty().add("almanac:networkType");
				networkTypeMeta.setValue(newDevice.getNetworkType());
				iotEntity.getMeta().add(networkTypeMeta);
				
				Meta geoPointMeta = this.objectFactory.createMeta();
				geoPointMeta.getProperty().add("geo:point");
				//what if they are not available?
				if(newDevice.getLocation() != null)
					geoPointMeta.setValue(newDevice.getLocation().getLat() + " " + newDevice.getLocation().getLon());
				iotEntity.getMeta().add(geoPointMeta);
				
				//what if sensor can return two values (different types)
				//an iotproperty has to be created for each capabilties
				switch (newDevice.getType()) {
				case DeviceType.FILL_LEVEL_SENSOR:
					IoTProperty depthIotProperty = this.objectFactory.createIoTProperty();
					depthIotProperty.getTypeof().add("almanac:FillLevelSensor:"+"GetDepth");
					depthIotProperty.setName("Depth values");
					depthIotProperty.setDatatype("integer");
					TypedStringType dum = this.objectFactory.createTypedStringType();
					dum.setValue("cm");
					depthIotProperty.setUnitOfMeasurement(dum);
					iotEntity.getIoTProperty().add(depthIotProperty);
					IoTProperty levelIotProperty = this.objectFactory.createIoTProperty();
					levelIotProperty.getTypeof().add("almanac:FillLevelSensor:"+"GetLevel");
					levelIotProperty.setName("Level values");
					levelIotProperty.setDatatype("integer");
					TypedStringType lum = this.objectFactory.createTypedStringType();
					lum.setValue("%");
					levelIotProperty.setUnitOfMeasurement(lum);
					iotEntity.getIoTProperty().add(levelIotProperty);
					break;
				case DeviceType.FLOW_METER_SENSOR:
					IoTProperty flowIoTProperty = this.objectFactory.createIoTProperty();
					flowIoTProperty.getTypeof().add("almanac:FlowMeterSensor:"+"GetFlow");
					flowIoTProperty.setName("Flow values");
					flowIoTProperty.setDatatype("integer");
					TypedStringType fum = this.objectFactory.createTypedStringType();
					fum.setValue("m^3/s");
					flowIoTProperty.setUnitOfMeasurement(fum);
					iotEntity.getIoTProperty().add(flowIoTProperty);
					break;
				case DeviceType.WATER_PUMP:
					IoTProperty velocityIoTProperty = this.objectFactory.createIoTProperty();
					velocityIoTProperty.getTypeof().add("almanac:WaterPump:"+"SetVelocity");
					velocityIoTProperty.setName("Water pump velocity values");
					velocityIoTProperty.setDatatype("integer");
					TypedStringType svum = this.objectFactory.createTypedStringType();
					svum.setValue("");
					velocityIoTProperty.setUnitOfMeasurement(svum);
					iotEntity.getIoTProperty().add(velocityIoTProperty);
					break;

				//smartsantander non deve finire sullo storage cnet...ora si...
	//			case DeviceType.VEHICLE_COUNTER:
	//				iotProperty.setDatatype("double");
	//				break;
	//			case DeviceType.VEHICLE_SPEED:
	//				iotProperty.setDatatype("double");
	//				break;
					//to be continued...
				default:
					break;
				}
								
				iotEntity.toXml();
				IoTEntity deviceAbout = this.cnetRestClient.pushNewIoTEntity(iotEntity);
				this.iotEntities.put(deviceAbout.getAbout(), deviceAbout);
				this.pwal.getDevice(newDevice.getPwalId()).setPwalId(deviceAbout.getAbout());
			}
			else
				log.info("SmartSantander devices values are not going to be stored");
		}
		else
		{
			log.info("Device {} already exist. Saving the about id..", newDevice);
			IoTEntity iot = arrayOfIoTEntity.getIoTEntity().get(0);
			this.iotEntities.put(iot.getAbout(), iot);
			this.pwal.getDevice(newDevice.getPwalId()).setPwalId(iot.getAbout());
		}
	}

	@Override
	public void notifyPWALDeviceRemoved(Device removedDevice) {
		//TO BE DONE
	}  
}
