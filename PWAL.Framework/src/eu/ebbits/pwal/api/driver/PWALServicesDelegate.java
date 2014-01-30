package eu.ebbits.pwal.api.driver;

import java.util.Collection;

import eu.ebbits.pwal.api.model.PWALService;

/**
 * Interface for controlling <code>PWALDriver</code>'s 
 * <code>ServicessDelegate</code>.
 * <p> 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author     ISMB
 * @version    %I%, %G%
 * @see         PWALDelegate
 * @see         eu.ebbits.pwal.impl.driver.framework.ServicesDelegate
 * @since      PWAL 0.1.0
 */
public interface PWALServicesDelegate extends PWALDelegate {
    
    /** 
    * Retrieves the <code>PWALService</code>, if available, given its name.
    *
    * @param name - a <code>String</code> with the service's name to get
    * 
    * @return        the <code>PWALService</code> if available,
    *                 <code>null</code> otherwise
    *  
    * @see            eu.ebbits.pwal.api.model.PWALService
    * @since        PWAL 0.1.0
    */
    PWALService getPWALService(String name);
    
    /** 
     * Retrieves a collection of <code>PWALServices</code>s, if available, 
     * given their names.
     *
     * @param names    - a <code>String</code> collection with the services names to
     *                 get
     * 
     * @return        the collection of available <code>PWALService</code>s or
     *                 <code>null</code> values where the given name does not exist
     *  
     * @see            eu.ebbits.pwal.api.model.PWALService
     * @since        PWAL 0.1.0
     */
    Collection<PWALService> getPWALServices(Collection<String> names);
    
    /** 
     * Retrieves the size of the current <code>PWALService</code>s collection.
     *
     * @return        the number of available <code>PWALService</code>s
     *  
     * @since        PWAL 0.1.0
     */
    int getPWALServicesCollectionSize();
    
    /** 
     * Retrieves the all available <code>PWALService</code>s.
     *
     * @return        the collection of available <code>PWALService</code>s or
     *                 <code>null</code> if the collection is empty
     *                 <!-- TODO check if the latter is true --> 
     *  
     * @see            eu.ebbits.pwal.api.model.PWALService
     * @since        PWAL 0.1.0
     */
    Collection<PWALService> getPWALServicesCollection();
    
    /** 
    * Browses the physical world and updates the current list of available 
    * <code>PWALService</code>s, adding new discovered physical world services
    * and removing those not reachable any longer.
    *
    * @since        PWAL 0.1.0
    */
    void updatePWALServicesCollection();
    
}
