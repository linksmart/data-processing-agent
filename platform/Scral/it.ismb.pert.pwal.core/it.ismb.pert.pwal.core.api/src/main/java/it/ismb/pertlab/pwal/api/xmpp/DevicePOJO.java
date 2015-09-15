package it.ismb.pertlab.pwal.api.xmpp;




/**
 * 
 * POJO objects for the devices 
 *
 */
public class DevicePOJO {
	private String dal_device_uid = null;
	private String [] dal_device_reference_uids = null;
	private String dal_device_driver = null;
	private String dal_device_name = null;
	private String dal_device_status = null;
	private String dal_device_hardware_vendor = null;
	private String dal_device_hardware_version = null;
	private String dal_device_firmware_vendor = null;
	private String dal_device_firmware_version = null;
	private String [] dal_device_types = null;
	private String dal_device_serial_number = null;
	private String dal_device_description = null;
	
	/**
	 * Returns the device uid
	 * 
	 * @return the device uid
	 */
	public String getDal_device_uid() {
		return dal_device_uid;
	}
	
	/**
	 * Sets the device uid
	 * 
	 * @param dal_device_uid 
	 * 		the device uid to set
	 */
	public void setDal_device_uid(String dal_device_uid) {
		this.dal_device_uid = dal_device_uid;
	}
	
	/**
	 * Returns the reference uids of the devices related to this
	 * 
	 * @return the list of the uids of the devices related to this
	 * 
	 */
	public String[] getDal_device_reference_uids() {
		return dal_device_reference_uids;
	}
	
	/**
	 * Sets the list of the uids of the devices related to this 
	 *  
	 * @param dal_device_reference_uids 
	 * 			the list of the uids of the devices related to this to set
	 * 
	 */
	public void setDal_device_reference_uids(String[] dal_device_reference_uids) {
		this.dal_device_reference_uids = dal_device_reference_uids;
	}
	
	/**
	 * Returns the device driver
	 * 
	 * @return the device driver
	 */
	public String getDal_device_driver() {
		return dal_device_driver;
	}
	
	
	/**
	 * Sets the device driver
	 * 
	 * @param dal_device_driver 
	 * 			the device driver to set
	 */
	public void setDal_device_driver(String dal_device_driver) {
		this.dal_device_driver = dal_device_driver;
	}
	
	/**
	 * Returns the device name
	 * 
	 * @return the device name
	 */
	public String getDal_device_name() {
		return dal_device_name;
	}
	
	/**
	 * Sets the device name
	 * 
	 * @param dal_device_name 
	 * 			the device name to set
	 */
	public void setDal_device_name(String dal_device_name) {
		this.dal_device_name = dal_device_name;
	}
	
	/**
	 * Returns the device status
	 * 
	 * @return the device status
	 */
	public String getDal_device_status() {
		return dal_device_status;
	}
	
	/**
	 * Sets the device status
	 * 
	 * @param dal_device_status
	 * 			device status to set
	 */
	public void setDal_device_status(String dal_device_status) {
		this.dal_device_status = dal_device_status;
	}
	
	/**
	 * Returns the device hardware vendor
	 * 
	 * @return the device hardware vendor
	 */
	public String getDal_device_hardware_vendor() {
		return dal_device_hardware_vendor;
	}
	
	/**
	 * Sets the device hardware vendor
	 * 
	 * @param dal_device_hardware_vendor 
	 * 			device hardware vendor to set
	 * 
	 */
	public void setDal_device_hardware_vendor(String dal_device_hardware_vendor) {
		this.dal_device_hardware_vendor = dal_device_hardware_vendor;
	}
	
	/**
	 * Returns the device hardware version
	 * 
	 * @return the device hardware version
	 */
	public String getDal_device_hardware_version() {
		return dal_device_hardware_version;
	}
	
	/**
	 * Sets the device hadrware version
	 * 
	 * @param dal_device_hardware_version
	 * 			device hardware version to set
	 */
	public void setDal_device_hardware_version(String dal_device_hardware_version) {
		this.dal_device_hardware_version = dal_device_hardware_version;
	}
	
	/**
	 * Returns the device firmware vendor
	 * 
	 * @return the device firmware vendor
	 */
	public String getDal_device_firmware_vendor() {
		return dal_device_firmware_vendor;
	}
	
	/**
	 * Sets the device firmware vendor
	 * 
	 * @param dal_device_firmware_vendor
	 * 			device firmware vendor to set
	 */
	public void setDal_device_firmware_vendor(String dal_device_firmware_vendor) {
		this.dal_device_firmware_vendor = dal_device_firmware_vendor;
	}
	
	
	/**
	 * Returns the device firmware version
	 * 
	 * @return the device fimware version
	 */
	public String getDal_device_firmware_version() {
		return dal_device_firmware_version;
	}
	
	/**
	 * Sets the device firmware version
	 * 
	 * @param dal_device_firmware_version 
	 * 			device firmware version to set
	 * 	
	 */
	public void setDal_device_firmware_version(String dal_device_firmware_version) {
		this.dal_device_firmware_version = dal_device_firmware_version;
	}
	
	/**
	 * Returns the device types
	 * 
	 * @return the device types
	 */
	public String[] getDal_device_types() {
		return dal_device_types;
	}
	
	/**
	 * Sets the device types
	 * 
	 * @param dal_device_types 
	 * 			device types to set
	 */
	public void setDal_device_types(String[] dal_device_types) {
		this.dal_device_types = dal_device_types;
	}
	
	/**
	 * Returns the device serial number
	 * 
	 * @return the device serial number
	 * 	
	 */
	public String getDal_device_serial_number() {
		return dal_device_serial_number;
	}
	
	/**
	 * Sets the device serial number
	 * 
	 * @param dal_device_serial_number 
	 * 			device serial number to set
	 */
	public void setDal_device_serial_number(String dal_device_serial_number) {
		this.dal_device_serial_number = dal_device_serial_number;
	}
	
	/**
	 * Returns the device description
	 * 
	 * @return the device description
	 */
	public String getDal_device_description() {
		return dal_device_description;
	}
	
	/**
	 * Set the device description
	 * 
	 * @param dal_device_description 
	 * 			device description to set
	 */
	public void setDal_device_description(String dal_device_description) {
		this.dal_device_description = dal_device_description;
	}
}
