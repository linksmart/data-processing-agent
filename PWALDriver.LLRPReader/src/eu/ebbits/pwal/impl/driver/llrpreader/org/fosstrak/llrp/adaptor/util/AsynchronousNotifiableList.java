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

package eu.ebbits.pwal.impl.driver.llrpreader.org.fosstrak.llrp.adaptor.util;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import eu.ebbits.pwal.impl.driver.llrpreader.org.fosstrak.llrp.adaptor.AsynchronousNotifiable;
import eu.ebbits.pwal.impl.driver.llrpreader.org.fosstrak.llrp.adaptor.exception.LLRPRuntimeException;

/**
 * helper class to maintain a list of asynchronous message receivers. the 
 * helper checks whether there are transmission exception, and if so then the 
 * respective receivers get dropped after a certain number of erroneous 
 * transmissions.
 * @author swieland
 *
 */
public class AsynchronousNotifiableList implements AsynchronousNotifiable {
    
    /** a list with all the receivers of asynchronous messages. */
    private List<Receiver> receivers = new LinkedList<Receiver>();
    
    /** remove the receiver after this number of unsuccessful connection attempts. */
    public static final int NUM_NON_RECHABLE_ALLOWED = 3;
    
    /** erroneous receivers that will be removed during cleanup. */
    private ConcurrentLinkedQueue<Receiver> erroneous = null;
    
    /**
     * internal wrapper class that helps counting the errors...
     * @author sawielan
     *
     */
    private class Receiver {
        // the number of errors occurred.
        private int errors = 0;
        
        // the receiver.
        private AsynchronousNotifiable receiver = null;
        
        /**
         * creates a wrapper class.
         * @param receiver the receiver.
         */
        public Receiver(AsynchronousNotifiable receiver) {
            this.receiver = receiver;
        }
        
        /**
         * sets the error-count to zero.
         */
        public void clean() {
            errors = 0;
        }
        
        /**
         * increases the error-count by one.
         */
        public void error() {
            errors++;
            
            if ((NUM_NON_RECHABLE_ALLOWED < errors) && (null == erroneous)) {
                erroneous = new ConcurrentLinkedQueue<Receiver> ();
                erroneous.add(this);
            }
        }
        
        /**
         * @return the number of errors occurred on this entry.
         */
        public int numErrors() {
            return errors;
        }
        
        /**
         * @return the receiver of this helper.
         */
        public AsynchronousNotifiable getReceiver() {
            return receiver;
        }
    }
    
    /**
     * add a new receiver to the list.
     * @param entry the new receiver to be stored in the list.
     */
    public void add(AsynchronousNotifiable entry) {
        synchronized (receivers) {
            receivers.add(new Receiver(entry));
        }
    }
    
    /**
     * removes a receiver from the list.
     * @param entry the receiver to be removed.
     */
    public void remove(AsynchronousNotifiable entry) {
        synchronized (receivers) {
            Receiver toBeRemoved = null;
            for (Receiver r : receivers) {
                if (r.getReceiver().equals(entry)) {
                    toBeRemoved = r;
                    break;
                }
            }
            if (null != toBeRemoved) {
                receivers.remove(toBeRemoved);
            }
        }
    }
    
    /**
     * @return if true then the list contains erroneous receivers.
     */
    private boolean isDirty() {
        return (null == erroneous);
    }
    
    /**
     * removes all the erroneous receivers from the list.
     */
    private synchronized void cleanup() {    
        if (null != erroneous) {
            synchronized (erroneous) {    
                synchronized(receivers) {
                    for (Receiver e : erroneous) {
                        receivers.remove(e);
                    }
                    erroneous = null;
                }
            }            
        }
    }

    /**
     * notify all the receivers with a new message.
     * @param message the LLRP message.
     * @param readerName the reader that delivered the message.
     * @throws RemoteException when there is an RMI exception.
     */
    public void notify(byte[] message, String readerName)
            throws RemoteException {
        
        for (Receiver receiver : receivers) {
            try {
                
                receiver.getReceiver().notify(message, readerName);
            } catch (RemoteException e) {
                receiver.error();
            }
            receiver.clean();
        }
        // run the cleanup routine.
        if (isDirty()) {
            cleanup();
        }
    }

    /**
     * notify all the receivers about an exception in the reader module.
     * @param e the exception.
     * @param readerName the reader that delivered the exception.
     * @throws RemoteException when there is an RMI exception.
     */
    public void notifyError(LLRPRuntimeException e, String readerName)
            throws RemoteException {
    
        for (Receiver receiver : receivers) {
            try {
                receiver.getReceiver().notifyError(e, readerName);
            } catch (RemoteException ex) {
                receiver.error();
            }
            receiver.clean();
        }
        // run the cleanup routine.
        if (isDirty()) {
            cleanup();
        }
    }

    /**
     * @return a list holding all the registered receivers.
     */
    public List<Receiver> getAll() {
        return receivers;
    }
}
