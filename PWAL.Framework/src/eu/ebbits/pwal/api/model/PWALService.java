package eu.ebbits.pwal.api.model;

import java.lang.reflect.Method;

/**
 * Class to represent generic Services/functions provided by the PWAL. 
 * For instance this object could be used to model a "PushButton" or 
 * <code>TurnOn()</code> functionality.
 * <p>
 * <code>PWALServices</code> can be extended to support driver-specific 
 * services.
 * <p>
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author     ISMB
 * @version    %I%, %G%
 * @since      PWAL 0.1.0
 */
public class PWALService extends PWALObject {
    
    /** Internal method. */    // TODO Should we change method into PWALMethod?
    private Method method;

    /**
     * Default constructor.
     * 
     * @param m - method to be modeled (by reflection)
    *
    * @since        PWAL 0.1.0
     */
    public PWALService(final Method m) {
        super(m.getName());
        this.method = m;
    }
    
    /**
     * Retrieves the service method.
     * 
     * @return method - by reflection, the service represented by this method.
    *
    * @since        PWAL 0.1.0
     */
    public final Method getMethod() {
        return this.method;
    }

}
