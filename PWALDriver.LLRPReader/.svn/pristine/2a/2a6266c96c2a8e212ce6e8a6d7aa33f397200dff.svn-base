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

import java.rmi.Remote;
import java.rmi.RemoteException;

import eu.ebbits.pwal.impl.driver.llrpreader.org.fosstrak.llrp.adaptor.exception.LLRPRuntimeException;

/**
 * if you want to receive asynchronous message notifications 
 * you can implement this interface.
 * @author sawielan
 *
 */
public interface AsynchronousNotifiable extends Remote {
    /**
     * when an asynchronous message arrived, this method will be invoked.
     * @param message the LLRPMessage arrived asynchronously.
     * @param readerName the nam eof the reader that read the message.
     * @throws RemoteException when there has been an error in the communication.
     */
    void notify(byte[] message, String readerName) throws RemoteException;
    
    /**
     * this method can be called asynchronously when 
     * there occurs an asynchronous error or exception.
     * @param e the exception that was triggered.
     * @param readerName the name of the reader that triggered the error.
     * @throws RemoteException when there has been an error in the communication.
     */
    void notifyError(LLRPRuntimeException e, String readerName) throws RemoteException;
}
