package eu.ebbits.pwal.impl.driver.llrpreader.adaptor;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import eu.ebbits.pwal.api.exceptions.PWALConfigurationNotPossibleException;
import eu.ebbits.pwal.impl.driver.llrpreader.LLRPReaderDriverImpl;


/**
 * Main Thread of the bundle
 * <p> 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author    ISMB
 * @version    %I%, %G%
 * @since   PWAL 0.2.0
 */
public class LLRPReaderBundleManager extends Thread {
    private static Logger log = Logger.getLogger(LLRPReaderBundleManager.class.getName());

    private boolean running = false;
    private AdaptorManagerThread adaptorManagerThread;
    private String readConfig = "";
    private String writeConfig = "";
    private boolean commitChanges = true;
    private String adapterName = "";
    private String readerName = "";
    private String readerAddress = "";
    private int readerPort = 0;
    private String rospecFile = "";
    private boolean isConfigured = false;
    
    // For example 255:255:255:255 || 1:1:1:1 || 13:567:56:77:8080
    private static final String IP_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                                            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                                            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                                            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])" +
                                            "(:(([6][5][5][3][0-5])|" +
                                            "([6][5][5][0-2][0-9])|" +
                                            "([6][5][0-4][0-9][0-9])|" +
                                            "([6][0-4][0-9][0-9][0-9])|" +
                                            "([1-5][0-9][0-9][0-9][0-9])|" +
                                            "([1-9][0-9][0-9][0-9])|" +
                                            "([1-9][0-9][0-9])|([1-9][0-9])|" +
                                            "([1-9])))?$";
    
    // For example c:\path\subpath || /usr/path || relative_path
    private static final String FILE_PATH_PATTERN = "^(([a-zA-Z][:]([\\\\]|[/]))?(.+([\\\\]|[/]))+)?(.+)([.](.+))?$";

    private LLRPReaderDriverImpl driver;

    private static final int MAX_PORT_NUMBER = 65535;
    
    private static final int WAIT_TIME = 2000;

    
    //==================numeric constants======================
    private static final int READ_CONFIG_INDEX = 0;
    private static final int WRITE_CONFIG_INDEX = 1;
    private static final int COMMIT_CHANGES_INDEX = 2;
    private static final int ADAPTER_NAME_INDEX = 3;
    private static final int READER_NAME_INDEX = 4;
    private static final int READER_ADDRESS_INDEX = 5;
    private static final int READER_PORT_INDEX = 6;
    private static final int RO_SPEC_FILE_INDEX = 7; 
    
    
    /**
     * Constructor of the Main thread of the LLRP driver
     * 
     * @param llrpReaderDriverImpl - implementation of the driver as <code>LLRPReaderDriverImpl</code>
     */
    public LLRPReaderBundleManager(LLRPReaderDriverImpl llrpReaderDriverImpl) {
        this.driver = llrpReaderDriverImpl;
    }

    /**
     * Template method used to configure the whole driver
     *
     * @param <code>List</code> of <code>Object</code>s to be configured
     * 
     *  values needed as parameters:
     *      String readConfig
     *         String writeConfig
     *        boolean commitChanges
     *        String adapterName
     *        String readerName
     *        String readerAddress
     *        int readerPort
     *        String rospecFile
     *
     * @throws PWALConfigurationNotPossibleException - if something goes wrong configuring the driver
     *  
     */
    public void configureLLRPDriver(List<Object>values) throws PWALConfigurationNotPossibleException {
        try {
            this.setReadConfig((String) values.get(READ_CONFIG_INDEX));
            this.setWriteConfig((String) values.get(WRITE_CONFIG_INDEX));
            this.setCommitChanges((boolean) values.get(COMMIT_CHANGES_INDEX));
            this.setAdapterName((String) values.get(ADAPTER_NAME_INDEX));
            this.setReaderName((String) values.get(READER_NAME_INDEX));
            this.setReaderAddress((String) values.get(READER_ADDRESS_INDEX));
            this.setReaderPort((int) values.get(READER_PORT_INDEX));
            this.setRospecFile((String) values.get(RO_SPEC_FILE_INDEX));
        } catch(Exception ex){
            throw new PWALConfigurationNotPossibleException("invalid parameters for configureLLRPDriver",ex);
        }
        isConfigured=true;
    }
    
    /**
     * 
     * Retrieves the path to the configuration file for the read parameters
     * 
     * @return a String containing the path to the read configurations' file
     * 
     */
    public String getReadConfig() {
        return readConfig;
    }

    /**
     * Sets the path to the configuration file for the read parameters of the reader
     * 
     * @param readConfig path to the file to set 
     * 
     * @throws PWALConfigurationNotPossibleException - if something goes wrong configuring the value 
     */
    public void setReadConfig(String readConfig) throws PWALConfigurationNotPossibleException {
        if(readConfig==null || readConfig.isEmpty() || !Pattern.matches(FILE_PATH_PATTERN,readConfig)) {
            throw new PWALConfigurationNotPossibleException("invalid value for readConfig, it has to be a file location");
        }
        this.readConfig = readConfig;
    }

    
    /**
     * 
     * Retrieves the path to the configuration file for the write parameters
     * 
     * @return a String containing the path to the write configurations' file
     *
     */
    public String getWriteConfig() {
        return writeConfig;
    }


    /**
     * Sets the path to the configuration file for the write parameters of the reader
     * 
     * @param writeConfig path to the file to set 
     * 
     * @throws PWALConfigurationNotPossibleException - if something goes wrong configuring the value
     *  
     */

    public void setWriteConfig(String writeConfig) throws PWALConfigurationNotPossibleException {
        if(writeConfig==null || writeConfig.isEmpty() || !Pattern.matches(FILE_PATH_PATTERN,writeConfig)) {
            throw new PWALConfigurationNotPossibleException("invalid value for writeConfig, it has to be a file location");
        }
        this.writeConfig = writeConfig;
    }

    /**
     * Indicates if the changes are committed to the configuration file or not
     * 
     * @return change committed (true), change not committed (false)
     */
    public boolean isCommitChanges() {
        return commitChanges;
    }

    /**
     * Sets if the changes are committed to the configuration file or not
     * 
     * @param commitChanges: change committed (true), change not committed (false)
     * 
     */
    public void setCommitChanges(boolean commitChanges) {
        this.commitChanges = commitChanges;
    }

    
    /**
     * Retrieves the adapter name
     * 
     * @return a String containing the LLRP Adapter name
     */    
    public String getAdapterName() {
        return adapterName;
    }


    /**
     * Sets the adapter name
     * 
     * @param adapterName adapter name to set
     * 
     * @throws PWALConfigurationNotPossibleException - if something goes wrong configuring the value 
     */

    public void setAdapterName(String adapterName) throws PWALConfigurationNotPossibleException {
        if(adapterName==null || adapterName.isEmpty()) {
            throw new PWALConfigurationNotPossibleException("invalid value for adapterName, it can't be null");
        }
        this.adapterName = adapterName;
    }

    
    /**
     * Retrieves the reader name
     * 
     * @return a String containing the name used to identify the LLRP reader
     */        

    public String getReaderName() {
        return readerName;
    }


    /**
     * Sets the reader name
     * 
     * @param readerName: reader name to set
     * 
     * @throws PWALConfigurationNotPossibleException - if something goes wrong configuring the value 
     */
    public void setReaderName(String readerName) throws PWALConfigurationNotPossibleException {
        if(readerName==null || readerName.isEmpty()) {
            throw new PWALConfigurationNotPossibleException("invalid value for readerName, it can't be null");
        }
        this.readerName = readerName;
    }


    /**
     * Retrieves the reader IP address
     * 
     * @return a String containing the IP address of the reader
     */
    public String getReaderAddress() {
        return readerAddress;
    }


    /**
     * Sets the reader IP address
     *  
     * @param readerAddress: the reader address to set
     * 
     * @throws PWALConfigurationNotPossibleException - if something goes wrong configuring the value 
     */
    public void setReaderAddress(String readerAddress) throws PWALConfigurationNotPossibleException {
        if(readerAddress==null || readerAddress.isEmpty() || !Pattern.matches(IP_PATTERN,readerAddress)) {
            throw new PWALConfigurationNotPossibleException("invalid value for readerAddress, it has to be a valid ip address");
        }
        this.readerAddress = readerAddress;
    }


    /**
     * Retrieves the TCP port of the reader
     * 
     * @return an int representing the IP port used by the LLRP reader
     */
    public int getReaderPort() {
        return readerPort;
    }


    /**
     * 
     * Sets the TCP port of the reader
     * 
     * @param readerPort: the reader port to set
     * 
     * @throws PWALConfigurationNotPossibleException - if something goes wrong configuring the value 
     */
    public void setReaderPort(int readerPort) throws PWALConfigurationNotPossibleException {
        // Valori da 1 a 65535
        if(readerPort<1 || readerPort>MAX_PORT_NUMBER) {
            throw new PWALConfigurationNotPossibleException("invalid value for readerPort, it has to be a valid port (1 <= readerPort <= 65535");
        }
        this.readerPort = readerPort;
    }


    /**
     * 
     * Retrieves the path to rospec file of the reader
     * 
     * @return a String containing the path to the ROSpec file
     */
    public String getRospecFile() {
        return rospecFile;
    }


    /**
     * Sets the path to the rospec file
     * 
     * @param rospecFile: path to the ROSpec file to set
     * 
     * @throws PWALConfigurationNotPossibleException - if something goes wrong configuring the value 
     */
    public void setRospecFile(String rospecFile) throws PWALConfigurationNotPossibleException {
        if(rospecFile==null || rospecFile.isEmpty() || !Pattern.matches(FILE_PATH_PATTERN,rospecFile)) {
            throw new PWALConfigurationNotPossibleException("invalid value for rospecFile, it has to be a valid file location");
        }
        this.rospecFile = rospecFile;
    }

    /**
     * Retrieves the read interval in milliseconds
     *  
     * @return readCycle read interval of the reader in milliseconds
     */
    public int getReadCycle() {
        return adaptorManagerThread.getReadCycle();
    }

    /**
     * Sets the read interval of the reader
     * 
     * @param readCycle: read cycle of the reader
     */
    public void setReadCycle(int readCycle) {
        adaptorManagerThread.setReadCycle(readCycle);
    }
    
    /**
     * Indicates if the inventory is on or off
     * 
     * @return true if the inventory is on, false otherwise
     */
    public boolean isInventoryOn() {
        return adaptorManagerThread.isInventoryOn();
    }
    
    /**
     * Starts and stops the inventory
     * 
     * @param inventoryOn true, starts the inventory, false, stops it
     */
    public void setInventoryOn(boolean inventoryOn) {
        adaptorManagerThread.setInventoryOn(inventoryOn);
    }
    
    @Override
    public void run (){
        running=true;    
        boolean startThread = true;
        do {
            // Lancia il thread gestore del reader            
            if(startThread && isConfigured) {
                adaptorManagerThread = new AdaptorManagerThread(readConfig, writeConfig, commitChanges, adapterName, readerName, readerAddress, readerPort, rospecFile, driver);
                adaptorManagerThread.start();
                // Se cade la connessione e poi si ripristrina non deve riavviare i thread
                startThread = false;    
            } else {
                try {
                    sleep(WAIT_TIME);
                } catch (InterruptedException e) {
                    log.warn(e.getStackTrace(),e);
                }
            }
        } while(running);
    }
 
    
    /**
     * Method used to write an RFID tag
     * 
     * @param accessSpecID ID for the accessSpec
     * @param tagMask The reader will take each tag EPC and bitwise AND it with this parameter
     * @param bits Memory bank
     *          0: Reserved
     *            1: EPC
     *            2: TID
     *            3: User
     * @param base base to start in the memory bank
     * @param values values to be written (it's null if it's used to read)
     * 
     * @return boolean result: true all ok, false errors
     */
    public boolean writeTagMemory(String tagID, String tagMask, int [] bits, int base, int[] values) {
        return adaptorManagerThread.writeTagMemory(tagID, tagMask, bits, base, values);
    }

    /**
     * @param accessSpecID ID for the accessSpec
     * @param tagMask The reader will take each tag EPC and bitwise AND it with this parameter
     * @param bits: Memory bank
     *          0: Reserved
     *            1: EPC
     *            2: TID
     *            3: User
     * @param base base to start in the memory bank
     * @param nOfWords number of words to be read (it's null if it's used to write)
     * 
     * @return int[]: value read
     * 
     */
    public int[] readTagMemory(String tagID, String tagMask, int[] bits, int base, int nOfWords) {
        return adaptorManagerThread.readTagMemory(tagID, tagMask, bits, base, nOfWords);
    }
    
     
    /**
     * Stops the thread
     * 
     */
    public void stopThread () {
        if(running) {
            running = false;
        } else {
            return;
        }
        // Stops the thread
        this.adaptorManagerThread.stopThread();    
    }
}