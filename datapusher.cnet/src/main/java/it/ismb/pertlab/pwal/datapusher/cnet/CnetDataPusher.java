package it.ismb.pertlab.pwal.datapusher.cnet;

import it.ismb.pertlab.pwal.api.data.pusher.DataPusher;
import it.ismb.pertlab.pwal.api.devices.events.PWALDeviceListener;
import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceNetworkType;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.api.internal.Pwal;
import it.ismb.pertlab.pwal.datapusher.cnet.datamodel.ArrayOfIoTEntity;
import it.ismb.pertlab.pwal.datapusher.cnet.datamodel.IoTEntity;
import it.ismb.pertlab.pwal.datapusher.cnet.datamodel.IoTProperty;
import it.ismb.pertlab.pwal.datapusher.cnet.datamodel.Meta;
import it.ismb.pertlab.pwal.datapusher.cnet.datamodel.ObjectFactory;
import it.ismb.pertlab.pwal.datapusher.cnet.datamodel.TypedStringType;
import it.ismb.pertlab.pwal.datapusher.cnet.restclient.CnetDataPusherRestClient;

public class CnetDataPusher extends DataPusher implements PWALDeviceListener 
{
	private Pwal pwal;
	private ObjectFactory objectFactory;
	private CnetDataPusherRestClient cnetRestClient;
	
	public CnetDataPusher(int seconds, Pwal pwal) {
		super(seconds, pwal);
		this.objectFactory = new ObjectFactory();
		this.pwal = pwal;
		this.pwal.addPwalDeviceListener(this);
		this.cnetRestClient = new CnetDataPusherRestClient("http://energyportal.cnet.se/StorageManagerMdb20140604/REST/IoTEntities", log);
	}	
	
	public void start()
	{
		log.debug("Start method.");
	}

	@Override
	public void run() {
		log.info("Timer scattato dopo " + this.seconds + " secondi.");
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
					depthIotProperty.getTypeof().add("almanac:FillLevelSensor"+"GetDepth");
					depthIotProperty.setName("Depth values");
					depthIotProperty.setDatatype("integer");
					TypedStringType dum = this.objectFactory.createTypedStringType();
					//			um.setValue(newDevice.getUnit().getValue());
					depthIotProperty.setUnitOfMeasurement(dum);
					iotEntity.getIoTProperty().add(depthIotProperty);
					IoTProperty levelIotProperty = this.objectFactory.createIoTProperty();
					levelIotProperty.getTypeof().add("almanac:FillLevelSensor"+"GetLevel");
					levelIotProperty.setName("Level values");
					levelIotProperty.setDatatype("integer");
					TypedStringType lum = this.objectFactory.createTypedStringType();
					//			um.setValue(newDevice.getUnit().getValue());
					levelIotProperty.setUnitOfMeasurement(lum);
					iotEntity.getIoTProperty().add(levelIotProperty);
					break;
				case DeviceType.FLOW_METER_SENSOR:
					IoTProperty flowIoTProperty = this.objectFactory.createIoTProperty();
					flowIoTProperty.getTypeof().add("almanac:FlowMeterSensor"+"GetFlow");
					flowIoTProperty.setName("Flow values");
					flowIoTProperty.setDatatype("integer");
					TypedStringType fum = this.objectFactory.createTypedStringType();
					//			um.setValue(newDevice.getUnit().getValue());
					flowIoTProperty.setUnitOfMeasurement(fum);
					iotEntity.getIoTProperty().add(flowIoTProperty);
					break;
					//smartsantander non deve finire sullo storage cnet
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
				String deviceAbout = this.cnetRestClient.pushNewIoTEntity(iotEntity);
				this.pwal.getDevice(newDevice.getPwalId()).setPwalId(deviceAbout);
			}
			else
				log.info("SmartSantander devices values are not going to be stored");
		}
		else
		{
			log.info("Device {} already exist. Saving the about id..", newDevice);
			this.pwal.getDevice(newDevice.getPwalId()).setPwalId(arrayOfIoTEntity.getIoTEntity().get(0).getAbout());
		}
	}

	@Override
	public void notifyPWALDeviceRemoved(Device removedDevice) {
		//TO BE DONE
	}  
}
