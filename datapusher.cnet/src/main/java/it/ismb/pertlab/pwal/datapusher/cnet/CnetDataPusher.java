package it.ismb.pertlab.pwal.datapusher.cnet;

import it.ismb.pertlab.pwal.api.data.pusher.DataPusher;
import it.ismb.pertlab.pwal.api.devices.events.PWALDeviceListener;
import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.internal.Pwal;
import it.ismb.pertlab.pwal.datapusher.cnet.datamodel.IoTEntity;
import it.ismb.pertlab.pwal.datapusher.cnet.datamodel.IoTProperty;
import it.ismb.pertlab.pwal.datapusher.cnet.datamodel.Meta;
import it.ismb.pertlab.pwal.datapusher.cnet.datamodel.ObjectFactory;
import it.ismb.pertlab.pwal.datapusher.cnet.datamodel.TypedStringType;

public class CnetDataPusher extends DataPusher implements PWALDeviceListener 
{
	private Pwal pwal;
	
	public Pwal getPwal() {
		return pwal;
	}

	public void setPwal(Pwal pwal) {
		this.pwal = pwal;
	}

	private ObjectFactory objectFactory;
	
	public CnetDataPusher(int seconds) {
		super(seconds);
		this.objectFactory = new ObjectFactory();
	}
	
	public void start()
	{
		this.pwal.addPwalDeviceListener(this);
	}

	@Override
	public void run() {
		log.info("Timer scattato dopo " + this.seconds + " secondi.");
	}

	@Override
	public void notifyPWALDeviceAdded(Device newDevice) {
		log.info("Pushing new device {} into CNET cloud database.", newDevice.getPwalId());
																																		
		IoTEntity iotEntity = this.objectFactory.createIoTEntity();
		iotEntity.getTypeof().add(newDevice.getType());
		iotEntity.getPrefix().add("geo: http://www.georss.org/georss/ almanac:http://ns.inertia.eu/ontologies xs:XMLSchema");
		iotEntity.setName(newDevice.getPwalId());
		
		Meta networkTypeMeta = this.objectFactory.createMeta();
		networkTypeMeta.getProperty().add("almanac:networkType");
		networkTypeMeta.setValue(newDevice.getNetworkType());
		iotEntity.getMeta().add(networkTypeMeta);
		
		Meta geoPointMeta = this.objectFactory.createMeta();
		geoPointMeta.getProperty().add("geo:point");
		geoPointMeta.setValue(newDevice.getLocation().getLat() + " " + newDevice.getLocation().getLon());
		iotEntity.getMeta().add(geoPointMeta);
		
		IoTProperty iotProperty = this.objectFactory.createIoTProperty();
		iotProperty.getTypeof().add(newDevice.getType());
		iotProperty.setName(newDevice.getPwalId());
		iotProperty.setDatatype("double");
		TypedStringType um = this.objectFactory.createTypedStringType();
//		um.setValue(newDevice.getUnit().getValue());
		iotProperty.setUnitOfMeasurement(um);
		
		iotEntity.getIoTProperty().add(iotProperty);
		
		iotEntity.toXml();
	}

	@Override
	public void notifyPWALDeviceRemoved(Device removedDevice) {
		// TODO write code to manage when a device is removed
	}  
}
