package eu.ebbits.pwal.impl.driver;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import eu.ebbits.pwal.api.driver.PWALVariablesDelegate;
import eu.ebbits.pwal.api.exceptions.PWALReadNotPossibleException;
import eu.ebbits.pwal.api.exceptions.PWALUpdateNotPossibleException;
import eu.ebbits.pwal.api.exceptions.PWALWriteNotPossibleException;
import eu.ebbits.pwal.api.model.PWALValue;
import eu.ebbits.pwal.api.model.PWALVariable;


/**
 * This class is imagined as part of the new ebbits DDK: it will thus undergo significant modifications in the ebbits iterative process. 
 * it will be thus documented during the final release after all needed adapters are available.
 * 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author        ISMB
 * @version        %I%, %G%
 * @see            eu.ebbits.pwal.impl.driver.PWALDriverImpl
 * @since        PWAL 0.1.0
 */
public abstract class PWALVariablesDelegateImpl extends PWALDelegateImpl implements PWALVariablesDelegate {
    
    private Map<String, PWALVariable> availableVariables;

    private Map<String, PWALVariableMonitorImpl> monitoredVariables;

    /**
     * Delegate for the variables
     * 
     * @param driver - Driver that uses the delegate
     * 
     */
    public PWALVariablesDelegateImpl(PWALDriverImpl driver) {
        super(driver);
        this.availableVariables = new HashMap<String, PWALVariable>();
        this.monitoredVariables = new HashMap<String, PWALVariableMonitorImpl>();
    }
    
    @Override
    public PWALVariable getPWALVariable(String variableName) {
        return this.availableVariables.get(variableName);
    }
    
    @Override
    public Collection<PWALVariable> getPWALVariables(Collection<String> variablesName) {
        if (!variablesName.isEmpty()) {
            Collection<PWALVariable> values = new HashSet<PWALVariable>();
            for(String variableName : variablesName) {
                values.add(getPWALVariable(variableName));
            }
            return values;
        } else {
            return null;
        }
    }
    
    protected final Map<String, PWALVariable> getAvailableVariables() {
        return availableVariables;
    }
    
    @Override
    public int getPWALVariablesCollectionSize() {
        return this.availableVariables.size();
    }
    
    @Override
    public Collection<PWALVariable> getPWALVariablesCollection() {
        return this.availableVariables.values();
    }

    @Override
    public Collection<String> getPWALVariablesNames() {
        return this.availableVariables.keySet();
    }
    
    @Override
    public int getMonitoredPWALVariablesCollectionSize() {
        return this.monitoredVariables.size();
    }
    
    @Override    
    public Collection<String> getMonitoredPWALVariablesNames() {
        return this.monitoredVariables.keySet();
    }
    
    @Override
    public boolean isPWALVariableAvailable(String variableName) {
        return this.availableVariables.containsKey(variableName);
    }

    @Override
    public boolean isPWALVariableMonitored(String variableName) {
        return this.monitoredVariables.containsKey(variableName);
    }
    
    
    /**
     * Registers a new PWALVariable
     * 
     * @param variable    -    the PWALVariable to be registerd
     * @param value        -    associated value (<code>PWALVariable</code>)
     * 
     * @return <code>true</code> if the variable is registered, <code>false</code> otherwise
     * 
     * @since PWAL 0.2.0
     * 
     */
    public boolean registerPWALVariable(PWALVariable variable) {
        try {
            if (!this.availableVariables.containsKey(variable.getName())) {
                this.availableVariables.put(variable.getName(), variable);
                return true;
            } else {
                return false; // Variable variableName already exists
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Registers a new PWALVariable passing its name
     * 
     * @param variableName    -    name of the variable to register
     * @param value            -    associated value (<code>Object</code>)
     * 
     * @return <code>true</code> if the variable is registered, <code>false</code> otherwise
     * 
     * @since PWAL 0.2.0
     */
    public boolean registerPWALVariable(String variableName, PWALValue value) {
        PWALVariable variable = new PWALVariable(variableName, value);
        return registerPWALVariable(variable);
    }

    
    /**
     * Registers a new PWALVariable
     * 
     * @param variableName    -    name of the variable to register
     * @param value            -    associated value
     * 
     * @return <code>true</code> if the variable is registered, <code>false</code> otherwise
     * 
     * @since PWAL 0.2.0
     */
    public boolean registerPWALVariable(String variableName, Object value) {
        PWALVariable variable = new PWALVariable(variableName, value);
        return registerPWALVariable(variable);
    }

    /**
     * Registers a set new PWALVariables
     * 
     * @param variables        -    collection of PWALVariables
     * 
     * @return a list of boolean, one for each variable
     *             <code>true</code> if the variable is registered, <code>false</code> otherwise
     * 
     * @since PWAL 0.2.0
     */
    public boolean[] registerPWALVariables(Collection<PWALVariable> variables) {
        if (!variables.isEmpty()) {
            boolean[] success = new boolean[variables.size()];
            int i = 0;
            for (PWALVariable variable : variables) {
                success[i] = registerPWALVariable(variable);
                i++;
            }
            return success;
        } else {
            return null;
        }
    }

    /**
     * Unegisters a PWALVariable
     * 
     * @param variable    -    PWALVariable to unregister
     * 
     * @return <code>true</code> if the variable is unregistered, <code>false</code> otherwise
     * 
     * @since PWAL 0.2.0
     */
    public boolean unregisterPWALVariable(PWALVariable variable) {
        return unregisterPWALVariable(variable.getName());
    }
    
    /**
     * UnRegisters a PWALVariable by name
     * 
     * @param variableName    -    name of the variable to unregister
     * 
     * @return <code>true</code> if the variable is unregistered, <code>false</code> otherwise
     * 
     * @since PWAL 0.2.0
     */
    public boolean unregisterPWALVariable(String variableName) {
        try {
            if (this.monitoredVariables.containsKey(variableName)) {
                this.monitoredVariables.get(variableName).stop();
                this.monitoredVariables.remove(variableName);
            }
            this.availableVariables.remove(variableName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Unregisters a set of PWALVariable by names
     * 
     * @param variablesName    -    <code>Collection</code> of variables' names to be unregistered
     * 
     * @return a list of boolean, one for each variable
     *             <code>true</code> if the variable is unregistered, <code>false</code> otherwise
     * 
     * @since PWAL 0.2.0
     */
    public boolean[] unregisterPWALVariables(Collection<String> variablesName) {
        if (!variablesName.isEmpty()) {
            boolean[] success = new boolean[variablesName.size()];
            int i = 0;
            for(String variableName : variablesName) {
                success[i] = unregisterPWALVariable(variableName);
                i++;
            }
            return success;
        } else {
            return null;
        }
    }

    /**
     * Starts the monitoring of a PWALVariable
     * 
     * @param variable            -    PWALVariable to be monitored
     * @param samplingPeriod    -    sampling period for the PWALVariableMonitorImpl in milliseconds
     * @param priority            -    priority for the PWALVariableMonitorImpl
     * 
     * @return <code>true</code> if the monitoring is started, <code>false</code> otherwise
     * 
     * @since PWAL 0.2.0
     */
    public boolean startMonitorPWALVariable(PWALVariable variable, long samplingPeriod, int priority) {
        return startMonitorPWALVariable(variable.getName(), samplingPeriod, priority);
    }
    
    
    @Override
    public boolean startMonitorPWALVariable(String variableName, long samplingPeriod, int priority) {
        try {
            if (this.availableVariables.containsKey(variableName)) {
                PWALVariable variable = this.availableVariables.get(variableName);
                PWALVariableMonitorImpl monitor = new PWALVariableMonitorImpl(this, variable);
                this.monitoredVariables.put(variableName, monitor);
                monitor.start(samplingPeriod, priority);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    
    @Override
    public boolean[] startMonitorPWALVariables(Collection<String> variablesName, long samplingPeriod, int priority) {
        if (!variablesName.isEmpty()) {
            boolean[] success = new boolean[variablesName.size()];
            int i = 0;
            for(String variableName : variablesName) {
                success[i] = startMonitorPWALVariable(variableName, samplingPeriod, priority);
                i++;
            }
            return success;
        } else {
            return null;
        }
    }

    /**
     * Stops the monitoring of a PWALVariable
     * 
     * @param variable            -    PWALVariable that should no longer be monitored
     * 
     * @return <code>true</code> if the variable is unregistered, <code>false</code> otherwise
     * 
     * @since PWAL 0.2.0
     */
    public boolean stopMonitorPWALVariable(PWALVariable variable) {
        return stopMonitorPWALVariable(variable.getName());
    }
    
    @Override
    public boolean stopMonitorPWALVariable(String variableName) {
        try {
            if (this.availableVariables.containsKey(variableName)) {
                this.monitoredVariables.get(variableName).stop();
                this.monitoredVariables.remove(variableName);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override    
    public boolean[] stopMonitorPWALVariables(Collection<String> variablesName) {
        if (!variablesName.isEmpty()) {
            boolean[] success = new boolean[variablesName.size()];
            int i = 0;
            for(String variableName : variablesName) {
                success[i] = stopMonitorPWALVariable(variableName);
                i++;
            }
            return success;
        } else {
            return null;
        }
    }
    
    
    @Override
    public PWALValue read(String variableName) {
        if(this.availableVariables.get(variableName) == null) {
            return null;
        }
        
        return getPWALVariable(variableName).read();
    }

    
    @Override
    public PWALValue readNow(String variableName) throws PWALReadNotPossibleException {
        PWALVariable var = getPWALVariable(variableName);
        if (var != null) {
            return readNow(var);
        } else {
            return null;
        }
    }

    @Override
    public Collection<PWALValue> read(Collection<String> variablesName) {
        if (!variablesName.isEmpty()) {
            Collection<PWALValue> values = new HashSet<PWALValue>();
            for(String variableName : variablesName) {
                values.add(read(variableName));
            }
            return values;
        } else {
            return null;
        }
    }
    
    @Override
    public boolean write(String variableName, PWALValue value) throws PWALWriteNotPossibleException {
        PWALVariable variable = this.availableVariables.get(variableName);
        return writeNow(variable, value);
    }
    
    @Override
    public boolean write(String variableName, Object value) throws PWALWriteNotPossibleException {
        PWALVariable variable = this.availableVariables.get(variableName);
        return writeNow(variable, new PWALValue(value));
    }
    
    /**
     * Reads a variable value
     * 
     * @param variableName -    name of the variable to read
     * 
     * @return current PWALValue of the PWALVariable, <code>null</code> if the variable name is not valid
     * 
     * @throws PWALReadNotPossibleException
     * 
     */
    public abstract PWALValue readNow(PWALVariable variable) throws PWALReadNotPossibleException;
    
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
    public abstract boolean writeNow(PWALVariable variable, PWALValue value) throws PWALWriteNotPossibleException;
    
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
    public abstract boolean updatePWALVariablesCollection() throws PWALUpdateNotPossibleException;
}
