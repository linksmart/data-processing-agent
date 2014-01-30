package eu.ebbits.pwal.api.driver;

import java.util.Collection;

import eu.ebbits.pwal.api.exceptions.PWALReadNotPossibleException;
import eu.ebbits.pwal.api.exceptions.PWALUpdateNotPossibleException;
import eu.ebbits.pwal.api.exceptions.PWALWriteNotPossibleException;
import eu.ebbits.pwal.api.model.PWALValue;
import eu.ebbits.pwal.api.model.PWALVariable;

/**
 * Interface for controlling <code>PWALDriver</code>'s 
 * <code>VariablesDelegate</code>.
 * <p> 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author     ISMB
 * @version    %I%, %G%
 * @see         PWALDelegate
 * @see         eu.ebbits.pwal.impl.driver.framework.VariablesDelegate
 * @since      PWAL 0.1.0
 */
public interface PWALVariablesDelegate extends PWALDelegate {

    /** 
    * Retrieves the <code>PWALVariable</code>, if available, given its name.
    *
    * @param name - a <code>String</code> with the variable's name to get
    * 
    * @return        the <code>PWALVariable</code> if available,
    *                 <code>null</code> otherwise
    *  
    * @see            eu.ebbits.pwal.api.model.PWALVariable
    * @since        PWAL 0.1.0
    */
    PWALVariable getPWALVariable(String name);
    
    /** 
    * Retrieves a collection of <code>PWALVariable</code>s, if available, 
    * given their names.
    *
    * @param names - a <code>String</code> collection with the vars names to get
    * 
    * @return        the collection of available <code>PWALVariable</code>s or
    *                 <code>null</code> values where the given name does not exist
    *  
    * @see            eu.ebbits.pwal.api.model.PWALVariable
    * @since        PWAL 0.1.0
    */
    Collection<PWALVariable> getPWALVariables(Collection<String> names);
    
    /** 
    * Retrieves the size of the current <code>PWALVariable</code>s collection.
    *
    * @return        the number of available <code>PWALVariable</code>s
    *  
    * @since        PWAL 0.1.0
    */
    int getPWALVariablesCollectionSize();
    
    /** 
    * Retrieves the all available <code>PWALVariable</code>s.
    *
    * @return        the collection of available <code>PWALVariable</code>s or
    *                 <code>null</code> if the collection is empty
    *                 <!-- TODO check if the latter is true --> 
    *  
    * @see            eu.ebbits.pwal.api.model.PWALVariable
    * @since        PWAL 0.1.0
    */
    Collection<PWALVariable> getPWALVariablesCollection();
    
    /** 
    * Retrieves the names of all available <code>PWALVariable</code>s.
    *
    * @return        the collection of all available variables' names 
    *                 <code>String</code> or <code>null</code> if the collection 
    *                 is empty
    *                 <!-- TODO check if the latter is true --> 
    *  
    * @see            eu.ebbits.pwal.api.model.PWALVariable
    * @since        PWAL 0.1.0
    */
    Collection<String> getPWALVariablesNames();
    
    /** 
    * Retrieves the size of the currently monitored <code>PWALVariable</code>s.
    *
    * @return        the number of monitored <code>PWALVariable</code>s
    *  
    * @since        PWAL 0.1.0
    */
    int getMonitoredPWALVariablesCollectionSize();
    
    /** 
    * Retrieves the names of all monitored <code>PWALVariable</code>s.
    *
    * @return        the collection of all monitored variables' names 
    *                 <code>String</code> or <code>null</code> if the collection 
    *                 is empty
    *                 <!-- TODO check if the latter is true --> 
    *  
    * @see            eu.ebbits.pwal.api.model.PWALVariable
    * @since        PWAL 0.1.0
    */
    Collection<String> getMonitoredPWALVariablesNames();
    
    /** 
    * Checks whether a variable is available given its name.
    *
    * @param name - a <code>String</code> with the variable's name to check
    * 
    * @return        <code>true</code> if the variable is thread safe,
    *                 <code>false</code> otherwise
    *
    * @since        PWAL 0.1.0
    */
    boolean isPWALVariableAvailable(String name);
    
    /** 
    * Checks whether a variable is being monitored given its name.
    *
    * @param name - a <code>String</code> with the variable's name to check
    * 
    * @return        <code>true</code> if the variable is monitored,
    *                 <code>false</code> otherwise
    *
    * @since        PWAL 0.1.0
    */
    boolean isPWALVariableMonitored(String name);
    
    /** 
    * Sends a <code>PWALVariable</code> start monitor signal and sets the 
    * polling policy given the variable's name, sampling period and priority.
    *
    * @param name -     a <code>String</code> with the variable name to monitor
    * @param periodms - a <code>long</code> with the sampling period in 
    *                     milliseconds
    * @param priority - an <code>int</code> with the monitoring thread priority
    *                     (see {@link java.lang.Thread#setPriority})
    * 
    * @return        <code>true</code> if the monitor thread has started 
    *                 successfully, <code>false</code> otherwise
    *
    * @since        PWAL 0.1.0
    */
    boolean startMonitorPWALVariable(String name, long periodms, int priority);
    
    /** 
    * Sends a <code>PWALVariable</code> start monitor signal and sets the 
    * polling policy to several variables, given their names, and the same 
    * sampling period and priority for all of them.
    *
    * @param names    -    a collection of <code>String</code> with the variables' 
    *                     names to monitor
    * @param periodms - a <code>long</code> with the sampling period in 
    *                     milliseconds
    * @param priority - an <code>int</code> with the monitoring thread priority
    *                     (see {@link java.lang.Thread#setPriority})
    * 
    * @return        an array with <code>true</code> if the respective monitor 
    *                 thread has started successfully, <code>false</code> 
    *                 otherwise
    *
    * @since        PWAL 0.1.0
    */
    boolean[] startMonitorPWALVariables(Collection<String> names, 
                                                long periodms, int priority);
    
    /** 
    * Sends a <code>PWALVariable</code> stop monitor signal.
    *
    * @param name - a <code>String</code> with the variable name to stop 
    *                 monitoring
    * 
    * @return        <code>true</code> if the monitor thread has being stopped 
    *                 successfully, <code>false</code> otherwise
    *
    * @since        PWAL 0.1.0
    */
    boolean stopMonitorPWALVariable(String name);
    
    /** 
    * Sends a <code>PWALVariable</code> stop monitor signal to several 
    * variables.
    *
    * @param names    - a collection of <code>String</code> with the variables 
    *                 names to stop monitoring
    * 
    * @return        <code>true</code> if the monitor thread has being stopped 
    *                 successfully, <code>false</code> otherwise
    *
    * @since        PWAL 0.1.0
    */
    boolean[] stopMonitorPWALVariables(Collection<String> names);
    
    /** 
     * Retrieves the latest <code>PWALValue</code> read by a 
     * <code>PWALVariable</code> given its name.
     *
     * @param name - a <code>String</code> with the variable name to read
     * 
     * @return        <code>PWALValue</code> if the variable exist and has a valid
     *                 not-null value, <code>null</code> otherwise
     *
     * @see            eu.ebbits.pwal.api.model.PWALValue
     * @since        PWAL 0.1.0
     */
    PWALValue read(String name);
    
    /** 
     * Retrieves the latest <code>PWALValue</code>s read by a set of
     * <code>PWALVariable</code>s given their names.
     *
     * @param names    - a collection of <code>String</code>s with the variable 
     *                 names to read
     * 
     * @return        a collection of <code>PWALValue</code>s where the variable 
     *                 exist and has a valid not-null value, <code>null</code> 
     *                 otherwise
     *
     * @see            eu.ebbits.pwal.api.model.PWALValue
     * @since        PWAL 0.1.0
     */
    Collection<PWALValue> read(Collection<String> names);
    
    /** 
     * Sends a <code>PWALValue</code> to be written into a 
     * <code>PWALVariable</code> given its name.
     *
     * @param name - a <code>String</code> with the variable name to write
     * @param val - the <code>PWALValue</code> to be written
     * 
     * @return        <code>true</code> if the variable exist and the value was 
     *                 written successfully, <code>false</code> otherwise
     *
     * @throws PWALWriteNotPossibleException - if something went wrong at the
     *                                             physical layer
     *
     * @see            eu.ebbits.pwal.api.model.PWALValue
     * @since        PWAL 0.1.0
     */
    boolean write(String name, PWALValue val) 
            throws PWALWriteNotPossibleException;
    
    /** 
     * Sends an <code>Object</code> as a value to be written into a 
     * <code>PWALVariable</code> given its name.
     *
     * @param name - a <code>String</code> with the variable name to write
     * @param val - a generic <code>Object</code> value to be written
     * 
     * @return        <code>true</code> if the variable exist and the value was 
     *                 written successfully, <code>false</code> otherwise
     *
     * @throws PWALWriteNotPossibleException - if something went wrong at the 
     *                                             physical layer
     *
     * @see            eu.ebbits.pwal.api.model.PWALValue
     * @since        PWAL 0.1.0
     */
    boolean write(String name, Object val) 
            throws PWALWriteNotPossibleException;
    
    /** 
     * Retrieves asynchronously the <code>PWALValue</code> of a
     * <code>PWALVariable</code> from its physical world endpoint, bypassing 
     * its polling policy and cached value.
     *
     * @param vnam - the <code>String</code> variable name to read
     * 
     * @return        <code>PWALValue</code> if the variable exist and its 
     *                 physical world endpoint has been read, <code>null</code> 
     *                 otherwise
     * 
     * @throws PWALReadNotPossibleException 
     *
     * @see            eu.ebbits.pwal.api.model.PWALVariable
     * @see            eu.ebbits.pwal.api.model.PWALValue
     * @since        PWAL 0.1.0
     */
    PWALValue readNow(String vnam) throws PWALReadNotPossibleException;
    
    /** 
     * Sends asynchronously a <code>PWALValue</code> to be written into a 
     * <code>PWALVariable</code>'s physical world endpoint.
     *
     * @param var -    the <code>PWALVariable</code> to write
     * @param val -    the <code>PWALValue</code> to be written
     * 
     * @return        <code>true</code> if the variable exist and the value has 
     *                 been written into the physical world endpoint successfully, 
     *                 <code>false</code> otherwise
     *
     * @throws PWALWriteNotPossibleException - if something went wrong at 
     *                                             physical layer
     *
     * @see            eu.ebbits.pwal.api.model.PWALVariable
     * @see            eu.ebbits.pwal.api.model.PWALValue
     * @since        PWAL 0.1.0
     */
    boolean writeNow(PWALVariable var, PWALValue val) 
            throws PWALWriteNotPossibleException;
    
    /** 
     * Browses the physical world and updates the current list of available 
     * <code>PWALVariable</code>s, adding new discovered physical world
     * endpoints and removing those not reachable any longer.
     *
     * @throws PWALUpdateNotPossibleException - if something went wrong at 
     *                                             physical layer
     *
     * @since        PWAL 0.1.0
     */
    boolean updatePWALVariablesCollection() throws PWALUpdateNotPossibleException;

}
