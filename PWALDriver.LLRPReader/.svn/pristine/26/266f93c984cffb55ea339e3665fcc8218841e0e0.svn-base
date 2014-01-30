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
 * This class models a logical entity of a physical reader. it maintains 
 * the connectivity to the physical reader. 
 * @author sawielan
 *
 */
public interface Reader extends Remote  {

    /**
     * connects this reader to the real physical llrp reader.
     * @param clientInitiatedConnection if the connection is initiated by the client then 
     * you should pass true. if the physical reader initiates the connection then provide false.
     * @throws LLRPRuntimeException whenever an error occurs.
     * @throws RemoteException whenever there is an rmi error.
     */
    void connect(boolean clientInitiatedConnection)
            throws LLRPRuntimeException, RemoteException;

    /**
     * disconnect the reader stub from the physical reader.
     * @throws RemoteException whenever there is an rmi error.
     */
    void disconnect() throws RemoteException;

    /**
     * try to reconnect the reader.
     * @throws RemoteException whenever there is an rmi error.
     * @throws LLRPRuntimeException whenever there is a exception during connection setup.
     */
    void reconnect() throws LLRPRuntimeException, RemoteException;

    /**
     * send a message to the llrp reader.
     * @param message the message to be sent.
     * @throws RemoteException whenever there is an rmi error.
     */
    void send(byte[] message) throws RemoteException;

    /**
     * tells if the reader is connected or not.
     * @return true if the reader is connected.
     * @throws RemoteException whenever there is an rmi error.
     */
    boolean isConnected() throws RemoteException;

    /**
     * return the ip address of this reader.
     * @return the ip address of this reader.
     * @throws RemoteException whenever there is an rmi error.
     */
    String getReaderAddress() throws RemoteException;

    /**
     * return the name of this reader.
     * @return the name of this reader.
     * @throws RemoteException whenever there is an rmi error.
     */
    String getReaderName() throws RemoteException;
    
    /**
     * return the port of this reader.
     * @return the port of this reader.
     * @throws RemoteException whenever there is an rmi error.
     */
    int getPort() throws RemoteException;

    /**
     * tell if this reader maintains a client initiated connection or if the 
     * reader accepts a connection from a llrp reader.
     * @return <ul><li>true if client initiated connection</li><li>false if llrp reader initiated connection</li></ul>
     * @throws RemoteException whenever there is an rmi error.
     */
    boolean isClientInitiated() throws RemoteException;

    /**
     * sets the connect behavior to the specified value.
     * @param clientInitiated if true then the client issues the connect.
     * @throws RemoteException whever there is an RMI error.
     */
    void setClientInitiated(boolean clientInitiated) throws RemoteException;
    
    /** 
     * tells whether this reader connects immediately after creation.
     * @return whether this reader connects immediately after creation.
     * @throws RemoteException whenever there is an RMI error.
     */
    boolean isConnectImmediate() throws RemoteException;
    
    /**
     * 
     * tells whether this reader connects immediately after creation.
     * @param value whether this reader connects immediately after creation.
     * @throws RemoteException whenever there is an RMI error.     
     */
    void setConnectImmediate(boolean value) throws RemoteException;
    
    /**
     * register for asynchronous messages from the physical reader.
     * @param receiver the receiver that shall be notified with the message.
     * @throws RemoteException whenever there is an RMI error.
     */
    void registerForAsynchronous(AsynchronousNotifiable receiver) throws RemoteException;

    /**
     * deregister from the asynchronous messages. the receiver will no more 
     * receive asynchronous llrp messages.
     * @param receiver the receiver to deregister.
     * @throws RemoteException whenever there is an RMI error.
     */
    void deregisterFromAsynchronous(
            AsynchronousNotifiable receiver) throws RemoteException;
    
    /**
     * sets the connection timeout period for the reader. if the times * keepAlivePeriod has 
     * passed by without a notification from the reader the reader gets disconnected.
     * @param keepAlivePeriod the reader must send in this period a keepalive message. time in ms.
     * @param times how many missed keepalive messages are ok.
     * @param report whether to report the keepalive messages to the repo or not.
     * @param throwException whether to throw an exception upon disconnection.
     * @throws RemoteException whenever there is an RMI error. 
     */
    void setKeepAlivePeriod(int keepAlivePeriod, int times, boolean report, boolean throwException) throws RemoteException;
    
    /**
     * returns the keepalive period set for this reader.
     * @return the keepalive period set for this reader.
     * @throws RemoteException whenever there is an RMI error. 
     */
    int getKeepAlivePeriod() throws RemoteException;
    
    /**
     * if set to true, the reader will report all the keep-alive messages exchanged between the 
     * reader and the driver stub.
     * @param report if true report the status messages. if false not.
     * @throws RemoteException whenever there is an RMI error.
     */
    void setReportKeepAlive(boolean report) throws RemoteException;
    
    /**
     * @return whether the reader stub delivers the keep-alive messages to the repo or not.
     * @throws RemoteException whenever there is an RMI error.
     */
    boolean isReportKeepAlive() throws RemoteException;
    
    /**
     * the reader meta-data contains information about the reader, the settings, etc.
     * @return a meta-data structure.
     * @throws RemoteException whenever there is an RMI error.
     */
    ReaderMetaData getMetaData() throws RemoteException;
}
