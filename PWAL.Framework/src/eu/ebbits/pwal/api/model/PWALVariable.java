package eu.ebbits.pwal.api.model;

import eu.ebbits.pwal.api.exceptions.PWALReadNotPossibleException;
import eu.ebbits.pwal.api.exceptions.PWALWriteNotPossibleException;
import eu.ebbits.pwal.api.driver.PWALVariablesDelegate;

/**
 * Class to represent physical world variables provided by the PWAL. 
 * A device can be associated with a arbitrary number of PWAL variables. 
 * <p>
 * <code>PWALVariables</code> can be extended to support driver-specific 
 * variables.
 * <p>
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author     ISMB
 * @version    %I%, %G%
 * @since      PWAL 0.1.0
 */
public class PWALVariable extends PWALObject {

    /**
     * Link with <code>PWALDriver</code>'s variables delegate, 
     * for accessing the physical world. 
     */
    private PWALVariablesDelegate father;
    
    // TODO Should we put here some structure needed for each specific driver to
    // recognize/access the variable (i.e., symbol path in a PLC)?
    
    /** The variable's value. */
    private PWALValue value;
    
    /**
     * Generic constructor.
     * 
     * @param nam -    the variable name <code>String</code>
     * @param val -    the variable <code>PWALValue</code>
     * 
     * @since      PWAL 0.1.0
     */
    public PWALVariable(final String nam, final PWALValue val) {
        super(nam);
        this.value = val;
    }
    
    /**
     * Generic constructor.
     * 
     * @param nam -    the variable name <code>String</code>
     * @param val -    the variable value <code>Object</code>
     * 
     * @since      PWAL 0.1.0
     */
    public PWALVariable(final String nam, final Object val) {
        super(nam);
        this.value = new PWALValue(val);
    }
    
    /**
     * Sets or replaces the linked variable's delegate.
     * 
     * @param del -    the <code>VariablesDelegate</code> to link
     * 
     * @since      PWAL 0.1.0
     */
    public final void setFather(final PWALVariablesDelegate del) {
        this.father = del;
    }
    
    /**
     * Generates a string representation of the variable.
     *
     * @return        the <code>String</code> representation
     *
    * @since        PWAL 0.1.0
     */
    public final String toString() {
        return this.getName() + ":" + this.value.toString();
    }
    
    /**
     * Retrieves the read PWALValue in cache.
     * 
     * @return        the variable's <code>PWALValue</code>
     * 
     * @since      PWAL 0.1.0
     */
    public final PWALValue read() {
        return value;
    }
    
    /**
     * Sends a write command to the physical layer under the 
     * <code>PWALVariable</code>.
     * 
     * @param val -    the <code>PWALValue</code> to write
     * 
     * @return        <code>true</code> if the physical layer write command was
     *                 successful, <code>false</code> otherwise.
     * 
     * @throws PWALWriteNotPossibleException - if the physical layer write 
     *                                             operation was not possible
     * 
     * @since      PWAL 0.1.0
     */
    public final boolean write(final PWALValue val) 
            throws PWALWriteNotPossibleException {
        this.value = val;
        return this.writeNow();
    }
    
    /**
     * Sends a write command to the physical layer under the 
     * <code>PWALVariable</code>.
     * 
     * @param val -    the generic <code>Object</code> to write
     * 
     * @return        <code>true</code> if the physical layer write command was
     *                 successful, <code>false</code> otherwise.
     * 
     * @throws PWALWriteNotPossibleException - if the physical layer write 
     *                                             operation was not possible
     * 
     * @since      PWAL 0.1.0
     */
    public final boolean write(final Object val) 
            throws PWALWriteNotPossibleException {
        this.value = new PWALValue(val);
        return this.writeNow();
    }
    
    /**
     * Sends an asynchronous read command to the physical layer under the 
     * <code>PWALVariable</code>. The read value is stored internally in the
     * object.
     * 
     * @return        <code>true</code> if the physical layer read command was
     *                 successful, <code>false</code> otherwise.
     * 
     * @throws PWALReadNotPossibleException 
     * 
     * @since      PWAL 0.1.0
     */
    public final boolean readNow() throws PWALReadNotPossibleException {
        if (this.father != null) {
            this.value = this.father.readNow(this.getName());
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sends an asynchronous write command to the physical layer under the 
     * <code>PWALVariable</code>. The value to write is taken from the internal
     * value.
     * 
     * @return        <code>true</code> if the physical layer write command was
     *                 successful, <code>false</code> otherwise.
     * 
     * @throws PWALWriteNotPossibleException - if the physical layer write 
     *                                             operation was not possible
     * 
     * @since      PWAL 0.1.0
     */
    public final boolean writeNow() throws PWALWriteNotPossibleException {
        if (this.father != null) {
            return this.father.writeNow(this, this.value);
        } else {
            return false;
        }
    }

    /**
     * Sets or replaces the value parsing a string.
     * 
     * @param val -    the <code>String</code> to parse
     * 
     * @since      PWAL 0.1.0
     */
    public final void setValueFromString(final String val) {
        if (this.value == null) {
            this.value = new PWALValue(null);
        }
        this.value.setValueFromString(val);
    }

}
