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
import java.util.List;

import eu.ebbits.pwal.impl.driver.llrpreader.org.fosstrak.llrp.adaptor.exception.LLRPRuntimeException;

/**
 * The interface Adaptor provides a general interface how to 
 * access a set of LLRP readers.
 * @author sawielan
 *
 */
public interface Adaptor extends Remote {
    /**
     * defines a new LLRP reader on this adaptor.
     * @param readerName the name of the LLRP reader.
     * @param readerAddress the address where to contact the LLRP reader.
     * @param clientInitiatedConnection LLRP allows two different ways how to create a connection to 
     * a reader. <br/>
     * <ol>
     * <li>client initiated connection: in this model the client tries to establish the connection to the reader</li>
     * <li>reader initiated connection: in this model the client waits for a reader to establish the connection</li>
     * </ol>
     * when you specify true a client initiated connection is established. otherwise a reader initiated connection.
     * @param connectImmediately tells whether the reader shall establish connection immediately or not<br/>
     * <ol>
     * <li>true: the reader tries to build up the connection immediately</li>
     * <li>false: the reader is just created, but the connection to the physical reader will not be established yet.
     * you need to run the connect command on the reader before you can use it!</li>
     * </ol> 
     * @throws LLRPRuntimeException if a runtime exception occurs (like duplicate reader name etc. ...).
     * @throws RemoteException when there was an rmi exception.
     */
    void define(String readerName, 
            String readerAddress, 
            boolean clientInitiatedConnection,
            boolean connectImmediately) throws RemoteException, LLRPRuntimeException;
    
    /**
     * defines a new LLRP reader on this adaptor.
     * @param readerName the name of the LLRP reader.
     * @param readerAddress the address where to contact the LLRP reader.
      * @param port the port where to connect to.
     * @param clientInitiatedConnection LLRP allows two different ways how to create a connection to
     * a reader. <br/>
     * <ol>
     * <li>client initiated connection: in this model the client tries to establish the connection to the reader</li>
     * <li>reader initiated connection: in this model the client waits for a reader to establish the connection</li>
     * </ol>
     * when you specify true a client initiated connection is established. otherwise a reader initiated connection.  
     * @param connectImmediately tells whether the reader shall establish connection immediately or not<br/>
     * <ol>
     * <li>true: the reader tries to build up the connection immediately</li>
     * <li>false: the reader is just created, but the connection to the physical reader will not be established yet.
     * you need to run the connect command on the reader before you can use it!</li>
     * </ol> 
     * @throws LLRPRuntimeException if a runtime exception occurs (like duplicate reader name etc. ...).
     * @throws RemoteException when there was an rmi exception.
     */
    void define(String readerName, 
            String readerAddress, 
            int port, 
            boolean clientInitiatedConnection,
            boolean connectImmediately) throws RemoteException, LLRPRuntimeException;
    
    /**
     * removes a LLRP reader from this adaptor.
     * @param readerName the name of the LLRP reader to remove.
     * @throws LLRPRuntimeException if a runtime exception occurs (eg reader does not exist etc. ...).
     * @throws RemoteException when there was an rmi exception.
     */
    void undefine(String readerName) throws RemoteException, LLRPRuntimeException;
    
    /**
     * removes all the LLRP readers from this adaptor.
     * @throws RemoteException when there was an rmi exception.
     * @throws LLRPRuntimeException if a runtime exception occurs (eg reader does not exist etc. ...).
     */
    void undefineAll() throws RemoteException, LLRPRuntimeException;
    
    /**
     * disconnects all the LLRP readers from this adaptor.
     * @throws RemoteException when there was an rmi exception.
     * @throws LLRPRuntimeException if a runtime exception occurs (eg reader does not exist etc. ...).
     */
    void disconnectAll() throws RemoteException, LLRPRuntimeException;
    
    /**
     * checks whether a readerName already exists.
     * @param readerName the name of the reader.
     * @return true if the reader exists else false.
     * @throws RemoteException when there was an rmi exception.
     */
    boolean containsReader(String readerName) throws RemoteException;
    
    /**
     * returns a list of all currently registered LLRP readers.
     * @return a list of all currently registered LLRP readers.
     * @throws RemoteException when there was an rmi exception.
     */
    List<String> getReaderNames() throws RemoteException;
    
    /**
     * returns a requested reader.
     * @param readerName the name of the requested reader.
     * @return the reader. 
     * @throws RemoteException when there was an rmi exception.
     */
    Reader getReader(String readerName) throws RemoteException;
    
    /**
     * returns the name of this adaptor.
     * @return the name of this adaptor.
     * @throws RemoteException when there was an rmi exception.
     */
    String getAdaptorName() throws RemoteException;
    
    /**
     * sets the name of the adaptor.
     * @param adaptorName the name of the adaptor to set.
     * @throws RemoteException
     */
    void setAdaptorName(String adaptorName) throws RemoteException;
    
    
    /**
     * sends a llrp message to the specified reader.
     * @param readerName the name of the reader where to send the message.
     * @param message the llrp message.
     * @throws LLRPRuntimeException whever a runtime error occurs.
     * @throws RemoteException when there was an rmi exception.
     */
    void sendLLRPMessage(String readerName, byte[] message) throws RemoteException, LLRPRuntimeException;
    
    /**
     * sends a llrp message to all the readers.
     * @param message the llrp message.
     * @throws LLRPRuntimeException whever a runtime error occurs.
     * @throws RemoteException when there was an rmi exception.
     */
    void sendLLRPMessageToAllReaders(byte[] message) throws RemoteException, LLRPRuntimeException;
    
    /**
     * register for asynchronous messages from the reader.
     * @param receiver the receiver that shall be notified with the message.
     * @throws RemoteException when there was an rmi exception.
     */
    void registerForAsynchronous(AsynchronousNotifiable receiver) throws RemoteException;
    
    /**
     * deregister from the asynchronous messages. the receiver will no more 
     * receive asynchronous llrp messages.
     * @param receiver the receiver to deregister.
     * @throws RemoteException when there was an rmi exception.
     */
    void deregisterFromAsynchronous(AsynchronousNotifiable receiver) throws RemoteException;
    
    /**
     * when a asynchronous message arrives from the reader this method 
     * will be invoked. the message then gets dispatched to the 
     * registered receivers.
     * @param message the llrp message.
     * @param readerName the name of the reader that triggered the event.
     * @throws RemoteException when there was an rmi exception.
     */
    void messageReceivedCallback(byte[] message, String readerName) throws RemoteException;
    
    /**
     * callback interface for asynchronous error messages from the reader.
     * @param e the exception that has been reported.
     * @param readerName the name of the reader where the error occured.
     * @throws RemoteException whenver there is an error on transport level (rmi).
     */
    void errorCallback(LLRPRuntimeException e, String readerName) throws RemoteException;

}
