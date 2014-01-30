package eu.ebbits.pwal.impl.driver.robotcontroller;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.osgi.service.component.ComponentContext;

import eu.ebbits.pwal.api.annotations.PWALServiceAnnotation;
import eu.ebbits.pwal.api.model.PWALValue;
import eu.ebbits.pwal.impl.driver.PWALServicesDelegateImpl;


/**
 * <code>RobotControllerDriver</code> services delegate.
 * <p> 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author    ISMB
 * @version    %I%, %G%
 * @since    M36demo 1.0
 */
public class RobotControllerServicesDelegate extends PWALServicesDelegateImpl {
    
    private static final int AXIS_INDEX = 3;

    private boolean[] robotAvailableAxis;
    private String robotName;
    private String robotType;

    /**
     * Constructor of the delegate
     * 
     *    @param driver - <code>RobotControllerDriverImpl</code> that uses the delegate
     */
    public RobotControllerServicesDelegate(RobotControllerDriverImpl driver) {
        super(driver);
    }

    @Override
    public void init(ComponentContext context) {        
    }

    @Override
    public void run() {
    }

    @Override
    public void updatePWALServicesCollection() {        
    }

    /**
     * Retrieves an axis parameter
     * 
     * @param axisId - ID of the axis as <code>int</code>
     * @param paramId - ID of te parameter as <code>int</code>
     * 
     * @return   a <codePWALValue</code> containing the parameter, null in case of exception
     */
    @PWALServiceAnnotation (exposed = true)
    public synchronized PWALValue getRobotAxisParameter(int axisId, int paramId) {
        try {
            String response = ((RobotControllerDriverImpl) this.getDriver()).getClient().
                    sendRobotControllerCommand("Ax" + axisId + "_" + paramId);
            return new PWALValue(Double.valueOf(response.split("=")[AXIS_INDEX].trim()));
        } catch (SocketException e) {
            return null;
        } catch (UnknownHostException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }
    
    /**
     * Retrieves the list of available axis
     * 
     * @return    a <code>boolean[]</code>, each value is true if the axis corresponding to the array index is available,
     *            otherwise is false
     */
    @PWALServiceAnnotation (exposed = true)
    public synchronized boolean[] getRobotAvailableAxis() {
        // FIXME: Implement this
        return this.robotAvailableAxis;
    }

    /**
     * Retrieves the robot name     
     *     
     * @return    the robot name as <code>String</code>
     */
    @PWALServiceAnnotation (exposed = true)
    public synchronized String getRobotName() {
        // FIXME: Implement this
        return this.robotName;
    }
    
    /**
     * Retrieves the robot type     
     *     
     * @return    the robot type as <code>String</code>
     */    
    @PWALServiceAnnotation (exposed = true)
    public synchronized String getRobotType() {
        // FIXME: Implement this
        return this.robotType;
    }
    

    /**
     * Checks if the robot axis is available
     * 
     * @param axisId - Id of the axis to check as <code>int</code>
     * 
     * @return  a <code>boolean</code>, true if the axis is available, false otherwise
     */
    @PWALServiceAnnotation (exposed = true)
    public synchronized boolean isRobotAxisAvailable(int axisId) {
        // FIXME: Implement this
        if(axisId<RobotControllerDriverImpl.N_OF_AXIS) {
            return this.robotAvailableAxis[axisId];
        } else {
            return false;
        }
    }
}