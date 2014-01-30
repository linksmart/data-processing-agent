package eu.ebbits.pwal.impl.driver.robotcontroller;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.ComponentContext;

import eu.ebbits.pwal.api.exceptions.PWALWriteNotPossibleException;
import eu.ebbits.pwal.api.model.PWALValue;
import eu.ebbits.pwal.api.model.PWALVariable;
import eu.ebbits.pwal.impl.driver.PWALVariablesDelegateImpl;


/**
 * <code>RobotControllerDriver</code> variables delegate.
 * <p> 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author    ISMB
 * @version    %I%, %G%
 * @since    M36demo 1.0
 */
public class RobotControllerVariablesDelegate extends PWALVariablesDelegateImpl {

    private Map<String, String> readCommands;
    
    //==================numeric constants===========================/
    
    /* index of the value to read */
    private static final int VALUE_TO_READ_INDEX = 3;
    
    /**
     * Constructor of the delegate
     * 
     *    @param driver - <code>RobotControllerDriverImpl</code> that uses the delegate
     */
    public RobotControllerVariablesDelegate(RobotControllerDriverImpl driver) {
        super(driver);
        this.readCommands = new HashMap<String, String>();
    }

    @Override
    public void init(ComponentContext context) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void run() {

    }

    @Override
    public PWALValue readNow(PWALVariable variable) {
        String response;
        try {
            response = ((RobotControllerDriverImpl) this.getDriver()).getClient().
                    sendRobotControllerCommand(readCommands.get(variable.getName()));
            return new PWALValue(Double.valueOf(response.split("=")[VALUE_TO_READ_INDEX].trim()));
        } catch (SocketException e) {
            // TODO Auto-generated catch block
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        return null;
    }

    @Override
    public boolean writeNow(PWALVariable variable, PWALValue value)
            throws PWALWriteNotPossibleException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean updatePWALVariablesCollection() {

        for (short axis = 1; axis <= RobotControllerDriverImpl.N_OF_AXIS; axis++) {
            this.registerPWALVariable("Directcurrent" + axis, null);
            this.readCommands.put("Directcurrent" + axis, "Ax" + axis + "_219");

            this.registerPWALVariable("Loadcurrent" + axis, null);
            this.readCommands.put("Loadcurrent" + axis, "Ax" + axis + "_379");

            this.registerPWALVariable("Motorpower" + axis, null);
            this.readCommands.put("Motorpower" + axis, "Ax" + axis + "_844");

            this.registerPWALVariable("Motortemp" + axis, null);
            this.readCommands.put("Motortemp" + axis, "Ax" + axis + "_381");
        }
        return true;
    }

}
