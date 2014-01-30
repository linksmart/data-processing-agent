package eu.ebbits.pwal.api.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Class to encapsulate reflected <code>Method</code>s 
 * including the <code>Object</code> where the method is called if not static and its 
 * arguments.
 * <p>
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author     ISMB
 * @version    %I%, %G%
 * @see        java.lang.reflect.Method
 * @since      PWAL 0.1.0
 */
public class PWALMethod {
    
    /** Internal method. */
    private Method method;
    
    /** Method's class instance. */
    private Object object;
    
    /** Method's arguments. */
    private Object[] arguments;
    
    /**
     * Constructor.
     * 
     * @param met -    the reflected <code>Method</code>
     * @param obj -    the method's <code>Object</code> if not static
     * @param args - the method's argument array
     *
     * @since       PWAL 0.1.0
     */
    public PWALMethod(final Method met, final Object obj, final Object[] args) {
        this.method = met;
        this.object = obj;
        this.arguments = args.clone();
    }

    /**
     * Retrieve the reflected method.
     * 
     * @return        the internal <code>Method</code>
     *
     * @since        PWAL 0.1.0
     */
    public final Method getMethod() {
        return method;
    }

    /**
     * Sets or replaces the reflected method.
     * 
     * @param met -    the internal <code>Method</code> to set
     *
     * @since       PWAL 0.1.0
     */
    public final void setMethod(final Method met) {
        this.method = met;
    }

    /**
     * Retrieves the method's class instance.
     * 
     * @return        the method's <code>Object</code>
     *
     * @since        PWAL 0.1.0
     */
    public final Object getObject() {
        return object;
    }

    /**
     * Sets or replaces the method's class instance.
     * 
     * @param obj -    the method's <code>Object</code> to set
     *
     * @since        PWAL 0.1.0
     */
    public final void setObject(final Object obj) {
        this.object = obj;
    }

    /**
     * Retrieves the method's arguments.
     * 
     * @return        the <code>Object</code> array of method's arguments
     *
     * @since        PWAL 0.1.0
     */
    public final Object[] getArgs() {
        return arguments;
    }

    /**
     * Sets or replaces the method's class instance.
     * 
     * @param args - the arguments to set
     *
     * @since        PWAL 0.1.0
     */
    public final void setArgs(final Object[] args) {
        this.arguments = args.clone();
    }
    
    /**
     * Calls the method.
     * 
     * @return        the returned <code>Object</code> from the method if any,
     *                 <code>null</code> otherwise
     *
     * @throws IllegalAccessException -    if the <code>Method</code> cannot be 
     *                                     accessed
     * @throws InvocationTargetException - if the underlying <code>Method</code> 
     *                                     throws an exception
     *
     * @since        PWAL 0.1.0
     */
    public final Object invoke() 
            throws IllegalAccessException, InvocationTargetException {
        return this.method.invoke(this.object, this.arguments);
    }
    
}
