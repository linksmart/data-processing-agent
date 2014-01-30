package eu.ebbits.pwal.api.driver.llrpreader;

import java.util.ArrayList;

import eu.ebbits.pwal.api.driver.PWALDriver;
import eu.ebbits.pwal.api.exceptions.PWALConfigurationNotPossibleException;

/**
 * <code>LLRPReaderDriver</code> services interface.
 * <p> 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author    ISMB
 * @version    %I%, %G%
 * @since    PWAL 0.2.0
 */
public interface LLRPReaderDriver extends PWALDriver {

    /**
     * Template method used to configure the whole driver
     * 
     * @param values <code>Vector</code> of <code>Object</code> that has to be configured
     * 
     * @throws PWALConfigurationNotPossibleException - if something goes wrong configuring the driver 
     * 
     * @sincePWAL 0.2.0
     */
    void configureLLRPDriver(ArrayList<Object>values) throws PWALConfigurationNotPossibleException;
    
    /**
     * Retrieves the path of the file containing the read configurations of the reader
     * 
     * @return a string containing the path to the read configurations' file
     * 
     * @sincePWAL 0.2.0 
     */
    String getReadConfig(); 

    /**
     * Sets the path of the file containing the read configurations of the reader
     * 
     * @param readConfig path to the read configurations' file
     * 
     * @throws PWALConfigurationNotPossibleException - if something goes wrong configuring the value
     * 
     * @sincePWAL 0.2.0  
     */
    void setReadConfig(String readConfig) throws PWALConfigurationNotPossibleException; 

    
    /**
     * Retrieves the path of the file containing the write configurations of the reader
     * 
     * @return a string containing the path to the write configurations' file
     * 
     * @sincePWAL 0.2.0 
     */
    String getWriteConfig();


    /**
     * Sets the path of the file containing the write configurations of the reader
     * 
     * @param writeConfig path to the write configurations' file
     * 
     * @throws PWALConfigurationNotPossibleException - if something goes wrong configuring the value
     * 
     * @sincePWAL 0.2.0  
     */
    void setWriteConfig(String writeConfig) throws PWALConfigurationNotPossibleException;


    /**
     * Indicates if the changes to the configuration are committed to the configuration file or not
     * 
     * @return true if the changes are committed, false otherwise
     * 
     * @sincePWAL 0.2.0 
     */
    boolean isCommitChanges();

    /**
     * Sets if the changes to the configuration are committed to the configuration file or not
     * 
     * @param commitChanges true if the changes must be committed, false otherwise
     * 
     * @sincePWAL 0.2.0 
     */
    void setCommitChanges(boolean commitChanges);

    
    /**
     * Retrieves the LLRP Adapter name
     * 
     * @return a String containing the LLRP Adapter name
     * 
     * @sincePWAL 0.2.0 
     */
    String getAdapterName();


    /**
     * Sets the LLRP Adapter name
     * 
     * @param adapterName the name used for the LLRP Adapter
     * 
     * @throws PWALConfigurationNotPossibleException - if something goes wrong configuring the value
     * 
     * @sincePWAL 0.2.0
     */
    void setAdapterName(String adapterName) throws PWALConfigurationNotPossibleException;
    
    /**
     * Retrieves the reader name
     * 
     * @return a String containing the name used to identify the LLRP reader
     *
     * @sincePWAL 0.2.0 
     */
    String getReaderName();


    /**
     * Sets the reader name
     * 
     * @param readerName the name used to identify the LLRP reader
     * 
     * @throws PWALConfigurationNotPossibleException - if something goes wrong configuring the value
     *
     * @sincePWAL 0.2.0 
     */
    void setReaderName(String readerName) throws PWALConfigurationNotPossibleException;


    /**
     * Retrieves the IP address of the reader
     * 
     * @return a String containing the IP address of the reader
     * 
     * @sincePWAL 0.2.0 
     */
    String getReaderAddress();


    /**
     * Sets the IP address of the reader
     * 
     * @param readerAddress IP address of the reader
     *
     * @throws PWALConfigurationNotPossibleException - if something goes wrong configuring the value
     * 
     * @sincePWAL 0.2.0 
     */
    void setReaderAddress(String readerAddress) throws PWALConfigurationNotPossibleException;


    /**
     * Retrieves the TCP port used by the LLRP reader 
     * 
     * @return an int representing the IP port used by the LLRP reader
     * 
     * @sincePWAL 0.2.0 
     */
    int getReaderPort();


    /**
     * Sets the TCP port used by the LLRP reader
     * 
     * @param readerPort    TCP port number
     * 
     * @throws PWALConfigurationNotPossibleException - if something goes wrong configuring the value
     * 
     * @sincePWAL 0.2.0 
     */
    void setReaderPort(int readerPort) throws PWALConfigurationNotPossibleException;


    /**
     * Retrieves the path to the rospec file of the LLRP reader 
     * 
     * @return a String containing the path to the rospec file
     * 
     * @sincePWAL 0.2.0 
     */
    String getRospecFile();


    /**
     * Sets the path to the rospec file of the LLRP reader
     * 
     * @param rospecFile path of the rospec file of the Reader
     * 
     * @throws PWALConfigurationNotPossibleException - if something goes wrong configuring the value
     * 
     * @sincePWAL 0.2.0 
     */
    void setRospecFile(String rospecFile) throws PWALConfigurationNotPossibleException;
}
