package eu.ebbits.pwal.impl.driver.opcxmlda;

import java.rmi.RemoteException;

import org.osgi.service.component.ComponentContext;

import eu.ebbits.pwal.api.exceptions.PWALUpdateNotPossibleException;
import eu.ebbits.pwal.api.exceptions.PWALWriteNotPossibleException;
import eu.ebbits.pwal.api.model.PWALValue;
import eu.ebbits.pwal.api.model.PWALVariable;
import eu.ebbits.pwal.impl.driver.PWALVariablesDelegateImpl;

/**
 * VariablesDelegate of the PLC PWAL driver implementation, employing OPC.
 *  
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author        ISMB
 * @version        %I%, %G%
 * @see            eu.ebbits.pwal.api.driver.PWALDriver
 * @since        PWAL 0.1.0
 */
public class OPCVariablesDelegate extends PWALVariablesDelegateImpl {

    /**
     * Constructor of the delegate
     * 
     *    @param driver - <code>OPCDriverImpl</code> that uses the delegate
     */
    public OPCVariablesDelegate(OPCDriverImpl driver) {
        super(driver);
    }

    @Override
    public void init(ComponentContext context) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public PWALValue readNow(PWALVariable variable) {
        if (variable==null) {
            return null;
        }
        String varName = variable.getName();
        String myret = ((OPCDriverImpl) this.getDriver()).getClient().read(varName);
         
        PWALVariable var = this.getAvailableVariables().get(varName);
        //FIXME we should fix this by modifying correctly the PWALValue/PWALVariable stuff, exploiting the xsitype in the browse PLC function... 
        if(var==null) {
            return null;
        }
        var.setValueFromString(myret);
        return var.read();
    }

    @Override
    public boolean writeNow(PWALVariable variable, PWALValue value) throws PWALWriteNotPossibleException {
        // TODO Auto-generated method stub

        return false;
    }

    @Override
    public boolean updatePWALVariablesCollection() throws PWALUpdateNotPossibleException {
        try {
            String[] allvars = ((OPCDriverImpl) this.getDriver()).getClient().browseAll();
        
            for(String s : allvars) {
                this.getAvailableVariables().put(s, new PWALVariable(s,null));
            }
        } catch (RemoteException e) {
            throw new PWALUpdateNotPossibleException(e.getMessage(), e);
        }
        return true;
    }

}
