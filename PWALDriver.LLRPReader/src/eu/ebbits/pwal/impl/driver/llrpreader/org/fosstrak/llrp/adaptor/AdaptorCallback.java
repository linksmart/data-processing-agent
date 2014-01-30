/*
 *  
 *  Fosstrak LLRP Commander (www.fosstrak.org)
 * 
 *  Copyright (C) 2008 ETH Zurich
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/> 
 *
 */

package eu.ebbits.pwal.impl.driver.llrpreader.org.fosstrak.llrp.adaptor;

import java.rmi.RemoteException;
import java.rmi.StubNotFoundException;
import java.rmi.server.UnicastRemoteObject;

import eu.ebbits.pwal.impl.driver.llrpreader.org.fosstrak.llrp.adaptor.exception.LLRPRuntimeException;
import eu.ebbits.pwal.impl.driver.llrpreader.org.fosstrak.llrp.client.LLRPExceptionHandlerTypeMap;

import org.apache.log4j.Logger;
import org.llrp.ltk.exceptions.InvalidLLRPMessageException;
import org.llrp.ltk.generated.LLRPMessageFactory;
import org.llrp.ltk.types.LLRPMessage;

/**
 * creates a callback instance that retrieves asynchronous messages.
 * @author sawielan
 *
 */
public class AdaptorCallback extends UnicastRemoteObject implements AsynchronousNotifiable {
    
    /**
     * serial id.
     */
    private static final long serialVersionUID = 1L;
    
    /** the worker that holds this callback. */
    private AdaptorWorker worker = null;
    
    /** the logger. */
    private static Logger log = Logger.getLogger(AdaptorCallback.class.getName());
    
    /**
     * creates a callback instance that retrieves asynchronous messages.
     * @param remote true if run remotely.
     * @throws RemoteException when there is an rmi exception.
     */
    public AdaptorCallback(boolean remote) throws RemoteException {
        if (remote) {
            try {
                UnicastRemoteObject.exportObject(this);
            } catch (StubNotFoundException e) {
                // this exception is normal as exportObject is backwards compatible to 
                // java 1.4. since java 1.5 the stub gets auto-generated and so 
                // there is no stub available -> exception. we can safely 
                // ignore this exception.
                log.info("ignoring exception as with java > 5 ok.");
            } catch (RemoteException e) {
                log.error(e.getStackTrace(),e);
            }
        }
    }
    

    /* 
     * @see org.fosstrak.llrp.adaptor.AsynchronousNotifiable#notify(byte[], java.lang.String)
     */
    @Override
    public void notify(byte[] message, String readerName) throws RemoteException {
        try {
            // create the llrp message 
            LLRPMessage llrpMessage = LLRPMessageFactory.createLLRPMessage(message);
            
            // dispatch the message to the simplified handlers
            AdaptorManagement.getInstance().dispatchHandlers(
                    worker.getAdaptor().getAdaptorName(), readerName, llrpMessage);
            
        } catch (InvalidLLRPMessageException e) {
            AdaptorManagement.getInstance().postException(new LLRPRuntimeException(e.getMessage()), 
                    LLRPExceptionHandlerTypeMap.EXCEPTION_MSG_SENDING_ERROR, 
                    worker.getAdaptor().getAdaptorName(), readerName);
        }
    }
    
    /**
     * sets the worker that holds this callback.
     * @param worker the worker that holds this callback.
     */
    public void setWorker(AdaptorWorker worker) {
        this.worker = worker;
    }
    
    @Override
    public void notifyError(LLRPRuntimeException e, String readerName) throws RemoteException {
        AdaptorManagement.getInstance().postException(e, e.getExceptionType(),
                worker.getAdaptor().getAdaptorName(),
                readerName);
    }
}