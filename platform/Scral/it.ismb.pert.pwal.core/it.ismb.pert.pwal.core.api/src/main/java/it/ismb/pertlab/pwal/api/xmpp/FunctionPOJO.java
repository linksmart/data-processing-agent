package it.ismb.pertlab.pwal.api.xmpp;

/**
 * 
 * POJO Object for the functions
 *
 */
public class FunctionPOJO {
	private String dal_function_uid = null;
	private String dal_function_type = null;
	private String dal_function_version = null;
	private String dal_function_device_uid = null;
	private String []dal_function_reference_uids = null;
	private String dal_function_description = null;
	private String [] dal_function_operation_names = null;
	private String dal_function_property_names = null;
	
	/**
	 * Return the uid of the function
	 * 
	 * @return the uid of the function
	 */
	public String getDal_function_uid() {
		return dal_function_uid;
	}
	/**
	 * Sets the uid of the fynction
	 * 
	 * @param dal_function_uid 
	 * 		uid to set
	 */
	public void setDal_function_uid(String dal_function_uid) {
		this.dal_function_uid = dal_function_uid;
	}
	
	/**
	 * Returns the type of the function
	 * 
	 * @return the type of the function
	 * 		
	 */
	public String getDal_function_type() {
		return dal_function_type;
	}
	
	/**
	 * Sets the type of the function
	 * 
	 * @param dal_function_type the 
	 * 			type of the function to set
	 */
	public void setDal_function_type(String dal_function_type) {
		this.dal_function_type = dal_function_type;
	}
	
	/**
	 * Returns the version of the function
	 * 
	 * @return the version of the function
	 */
	public String getDal_function_version() {
		return dal_function_version;
	}
	
	
	/**
	 * Sets the version of the function
	 * 
	 * @param dal_function_version 
	 * 			version of the function to set
	 */
	public void setDal_function_version(String dal_function_version) {
		this.dal_function_version = dal_function_version;
	}
	
	/**
	 * Returns the uid of the device associated to this function
	 * 
	 * @return the uid of the device
	 * 			
	 */
	public String getDal_function_device_uid() {
		return dal_function_device_uid;
	}
	
	
	/**
	 * Sets the uid of the device associated with this function
	 * 
	 * @param dal_function_device_uid 
	 * 			uid of the device to set
	 */
	public void setDal_function_device_uid(String dal_function_device_uid) {
		this.dal_function_device_uid = dal_function_device_uid;
	}
	
	/**
	 * Returns the list of the uids of the devices related with this
	 * 
	 * @return the list of the uids of the devices related with this
	 * 
	 */
	public String[] getDal_function_reference_uids() {
		return dal_function_reference_uids;
	}
	
	/**
	 * Sets the list of the uids of the devices related with this
	 *   
	 * @param dal_function_reference_uids 
	 * 			list of hte devices to set
	 */
	public void setDal_function_reference_uids(String[] dal_function_reference_uids) {
		this.dal_function_reference_uids = dal_function_reference_uids;
	}
	
	/**
	 * Returns the description of the function 
	 * 
	 * @return the description of the function
	 */
	public String getDal_function_description() {
		return dal_function_description;
	}
	
	/**
	 * Sets the description of the function
	 * 
	 * @param dal_function_description 
	 * 			description of the function to set
	 */
	public void setDal_function_description(String dal_function_description) {
		this.dal_function_description = dal_function_description;
	}
	
	/**
	 * Returns the names of the operations allowed on the function
	 * 
	 * @return the names of the operations allowed on the devices
	 * 		
	 */
	public String[] getDal_function_operation_names() {
		return dal_function_operation_names;
	}
	
	/**
	 * Sets the names of the operations allowed on the devices
	 * 
	 * @param dal_function_operation_names 
	 * 			list of the operations names to set
	 */
	public void setDal_function_operation_names(
			String[] dal_function_operation_names) {
		this.dal_function_operation_names = dal_function_operation_names;
	}
	
	/**
	 * Returns the list of the names of the function's properties
	 * 
	 * @return the list of the function's properties
	 */
	public String getDal_function_property_names() {
		return dal_function_property_names;
	}
	
	
	/**
	 * Sets the list of the function's properties
	 * 
	 * @param dal_function_property_names 
	 * 			list of the function's properties to set
	 */
	public void setDal_function_property_names(String dal_function_property_names) {
		this.dal_function_property_names = dal_function_property_names;
	}
	
	
}
