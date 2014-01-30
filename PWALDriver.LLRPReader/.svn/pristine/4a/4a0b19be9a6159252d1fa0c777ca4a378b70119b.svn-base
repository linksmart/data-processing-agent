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

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import eu.ebbits.pwal.impl.driver.llrpreader.org.fosstrak.llrp.adaptor.config.AdaptorConfiguration;
import eu.ebbits.pwal.impl.driver.llrpreader.org.fosstrak.llrp.adaptor.config.ConfigurationLoader;
import eu.ebbits.pwal.impl.driver.llrpreader.org.fosstrak.llrp.adaptor.config.ReaderConfiguration;
import eu.ebbits.pwal.impl.driver.llrpreader.org.fosstrak.llrp.adaptor.exception.LLRPDuplicateNameException;
import eu.ebbits.pwal.impl.driver.llrpreader.org.fosstrak.llrp.adaptor.exception.LLRPRuntimeException;
import eu.ebbits.pwal.impl.driver.llrpreader.org.fosstrak.llrp.adaptor.queue.QueueEntry;
import eu.ebbits.pwal.impl.driver.llrpreader.org.fosstrak.llrp.client.LLRPExceptionHandler;
import eu.ebbits.pwal.impl.driver.llrpreader.org.fosstrak.llrp.client.LLRPExceptionHandlerTypeMap;
import eu.ebbits.pwal.impl.driver.llrpreader.org.fosstrak.llrp.client.MessageHandler;

import org.apache.log4j.Logger;
import org.llrp.ltk.types.LLRPMessage;

/**
 * The AdaptorManagement handles your adaptors, enqueues LLRPMessages, handles 
 * errors from the reader site and notifies you about incoming LLRPMessages.<br/>
 * <br/>
 * There are some common pitfalls when using the AdaptorManagement:
 * <ul>
 * <li>you must specify the repository where the messages shall be logged to (see example)</li>
 * <li>you must register an exception handler (see example)</li>
 * <li>you must shutdown the AdaptorManagement through the provided 
 * shutdown method. Otherwise the reader connections don't get shutdown properly (see example)</li>
 * </ul>
 * <br/>
 * Below there is some sample-code, how you can use the AdaptorManagement:
 * <p>
 * <code>// create a message handler</code><br/>
 * <code>MessageHandler msgHandler = new MessageHandler();</code><br/>
 * <br/>
 * <code>// create an exception handler</code><br/>
 * <code>ExceptionHandler handler = new ExceptionHandler();</code><br/>
 * <br/>
 * <code>// run the initializer method</code><br/>
 * <code>String readConfig = Utility.findWithFullPath("/readerDefaultConfig.properties");</code><br/>
 * <code>String writeConfig = readConfig;</code><br/>
 * <code>boolean commitChanges = true;</code><br/>
 * <code>AdaptorManagement.getInstance().initialize(</code><br/>
 * <code>&nbsp;&nbsp;&nbsp;&nbsp;readConfig, storeConfig, commitChanges, handler, msgHandler);</code><br/>
 * <br/>
 * <code>// now the management should be initialized and ready to be used</code><br/>
 * <br/>
 * <code>// create an adaptor</code><br/>
 * <code>String adaptorName = "myAdaptor";</code><br/>
 * <code>AdaptorManagement.getInstance().define(adaptorName, "localhost");</code><br/>
 * <br/>
 * <code>// create a reader</code><br/>
 * <code>String readerName = "myReader";</code><br/>
 * <code>Adaptor adaptor = AdaptorManagement.getAdaptor(adaptorName);</code><br/>
 * <code>adaptor.define(readerName, "192.168.1.23", 5084, true, true);</code><br/>
 * <br/>
 * <code>//Enqueue some LLRPMessage on the adaptor</code><br/>
 * <code>AdaptorManagement.enqueueLLRPMessage(adaptorName, readerName, message);</code><br/>
 * <br/>
 * <code>// when you shutdown your application call the shutdown method</code><br/>
 * <code>AdaptorManagement.getInstance().shutdown();</code><br/>
 * </p>
 * @author sawielan
 *
 */
public final class AdaptorManagement {
    
    /** the name for the default local adaptor. */
    public static final String DEFAULT_ADAPTOR_NAME = "DEFAULT";
        
    /** the logger. */
    private static Logger log = Logger.getLogger(AdaptorManagement.class);

    /** the exception handler. */
    private static LLRPExceptionHandler exceptionHandler = null;
    
    /** 
     * if storeConfig is set and commitChanges is true then all 
     * the changes to the AdaptorManagement are committed to storeConfig.
     */
    private boolean commitChanges = true;
    
    /** where the configuration shall be read from. */
    private String readConfig = null;
    
    /** where the configuration shall be written to (if changes happen). */
    private String storeConfig = null;
    
    /** Strings to be used to print adaptor name **/
    private static final String ADAPTOR_STRING_PREFIX = "Adaptor '";
    private static final String ADAPTOR_STRING_POSTFIX = "' does not exist!";
    
    /** 
     * flags whether the AdaptorManagement has been initialized or not. 
     * you cannot initialize it twice!.
     */
    private boolean initialized = false;
    
    
    /** internal state keeper. if set to true, the first local adaptor gets exported by rmi. */
    private boolean export = false;
    
    /** if there is a severe error in the adaptorManagement this is set to true */
    private static boolean error = false;
    
    /** the exception that describes the error condition. */
    private static LLRPRuntimeException errorException = null;
    
    /** the error code for the exception handler. */
    private static LLRPExceptionHandlerTypeMap errorType = null;
    
    
    
    /** if the configuration is load from file we can use this loader to read/write it */
    private ConfigurationLoader configLoader = new ConfigurationLoader();

    
    // we need to distinguish between local and remote adaptors as 
    // for local adaptors we want to be able to store the configuration
    // at all the time.
    
    /** all the worker threads running an adaptor held by the management (local and remote adaptors). */
    private static Map<String, AdaptorWorker> workers = new ConcurrentHashMap<String, AdaptorWorker> ();
    
    /** all the worker threads running an adaptor held by the management (local adaptors). */
    private Map<String, AdaptorWorker> localWorkers = new ConcurrentHashMap<String, AdaptorWorker> ();
    
    /** all the worker threads running an adaptor held by the management (remote adaptors). */
    private Map<String, AdaptorWorker> remoteWorkers = new ConcurrentHashMap<String, AdaptorWorker> ();
    
    /** a list of handlers that like to receive all the LLRP messages. */
    private List<MessageHandler> fullHandlers = new LinkedList<MessageHandler> ();
    
    /** these handlers would like to receive only certain LLRP Messages. */
    private Map<Class, LinkedList<MessageHandler> > partialHandlers = new HashMap<Class, LinkedList<MessageHandler> > ();
    
    
    
    // ------------------------------- initialization -------------------------------
    
    /**
     * initializes the AdaptorManagement.
     * @param readConfig where the configuration shall be read from.
     * @param storeConfig where the configuration shall be written to (if changes happen).
     * @param commitChanges if storeConfig is set and commitChanges is true then all 
     * the changes to the AdaptorManagement are committed to storeConfig.
     * @param exceptionHandler the exception handler from the GUI.
     * @param handler a handler to dispatch the LLRP messages (can be set to null).
     * @throws LLRPRuntimeException whenever the AdaptorManagement could not be loaded.
     * @return returns 
     * <ul>
     * <li>true if initialization has been performed</li>
     * <li>false if initialization has already been performed and therefore the 
     * process was aborted</li>
     * </ul>.
     */
    public boolean initialize(
            String readConfig, 
            String storeConfig,
            boolean commitChanges,
            LLRPExceptionHandler exceptionHandler,
            MessageHandler handler) 
        throws LLRPRuntimeException {
        
        return initialize(readConfig, 
                storeConfig, 
                commitChanges, exceptionHandler, handler, false);
    }
    
    /**
     * ATTENTION: initializes the AdaptorManagement.DO NOT USE THIS METHOD as long as you know 
     * what you are doing (this method instructs with export=true to export the 
     * first local adaptor as a server adaptor. 
     * @param readConfig where the configuration shall be read from.
     * @param storeConfig where the configuration shall be written to (if changes happen).
     * @param commitChanges if storeConfig is set and commitChanges is true then all 
     * the changes to the AdaptorManagement are committed to storeConfig.
     * @param exceptionHandler the exception handler from the GUI.
     * @param handler a handler to dispatch the LLRP messages (can be set to null).
     * @param export if the first local adaptor is to be exported by RMI or not.
     * @throws LLRPRuntimeException whenever the AdaptorManagement could not be loaded.
     * @return returns 
     * <ul>
     * <li>true if initialization has been performed</li>
     * <li>false if initialization has already been performed and therefore the 
     * process was aborted</li>
     * </ul>.
     */
    public boolean initialize(
            String readConfig, 
            String storeConfig,
            boolean commitChanges,
            LLRPExceptionHandler exceptionHandler,
            MessageHandler handler,
            boolean export) 
        throws LLRPRuntimeException {
        if (initialized) {
            log.error("You cannot initialize the AdaptorManagement twice!\n" +
                    "use the getters/setters to perform the requested changes!\n" +
                    "we will abort now!!!");
            return false;
        }
        
        this.export = export;
        
        this.readConfig = readConfig;
        this.storeConfig = storeConfig;
        this.commitChanges = commitChanges;
        this.exceptionHandler = exceptionHandler;
        
        if (null != handler) {
            registerFullHandler(handler);
        }
        
        load();
        initialized = true;
        return true;
    }
    
    /**
     * flags whether the AdaptorManagement has already been initialized.
     * @return whether the AdaptorManagement has already been initialized.
     */
    public boolean isInitialized() {
        return initialized;
    }
    
    /**
     * resets the management to initial state. 
     * @throws LLRPRuntimeException if an error occurs during reset.
     */
    public synchronized void reset() throws LLRPRuntimeException {
        if (!initialized) {
            throw new LLRPRuntimeException("AdaptorManagement is not initialized");
        }
        clearWorkers();
        
        // drop all the handlers
        synchronized (fullHandlers) {
            fullHandlers.clear();
        }
        
        synchronized (partialHandlers) {
            partialHandlers.clear();
        }
        
        load();
        log.debug("finished reset");
    }
    
    /**
     * loads the whole AdaptorManagement.
     * @throws LLRPRuntimeException when the configuration could not be loaded from file.
     */
    private void load() throws LLRPRuntimeException {
        try {
            loadFromFile();
        } catch (LLRPRuntimeException e) {
            setStatus(true, 
                    e,
                    LLRPExceptionHandlerTypeMap.EXCEPTION_ADAPTOR_MANAGEMENT_NOT_INITIALIZED);
            throw e;
        }
    }
    
    /**
     * commits the configuration to the properties file.
     */
    public void commit() {
        if (isCommitChanges()) {
            try {
                storeToFile();
            } catch (LLRPRuntimeException e) {
                log.debug("could not commit the changes to the configuration file");
                setStatus(true, 
                        e,
                        LLRPExceptionHandlerTypeMap.EXCEPTION_ADAPTOR_MANAGEMENT_NOT_INITIALIZED);
            }
        }
    }
    
    /**
     * check whether the AdaptorManagement is ok or not. 
     * if not, an exception is thrown and reported to the exception handler.
     */
    public static void checkStatus() throws LLRPRuntimeException {
        if (error) {
            postException(errorException, errorType, "", "");
            throw errorException;
        }
    }
    
    /**
     * sets the status of the adaptorManagement.
     */
    private void setStatus(
            boolean error,
            LLRPRuntimeException errorException, 
            LLRPExceptionHandlerTypeMap errorType) 
    {
        this.error = error;
        this.errorException = errorException;
        this.errorType = errorType;
    }
    
    /**
     * the client leaves the adaptor management. the management makes the 
     * cleanup.
     */
    public synchronized void shutdown() {
        log.debug("shutting AdaptorManagement down.");
        
        // first disconnect the local readers.
        disconnectReaders();
        
        // remove the remote readers.
        synchronized (AdaptorManagement.class) {
            synchronized (workers) {
                synchronized (localWorkers) {
                    synchronized (remoteWorkers) {
                        for (AdaptorWorker worker : workers.values()) {
                            // stop all the workers.
                            worker.tearDown();
                        }
                        // deregister the asynchronous callbacks
                        for (AdaptorWorker worker : remoteWorkers.values()) {
                            // stop all the workers.
                            try {
                                worker.getAdaptor().deregisterFromAsynchronous(worker.getCallback());
                            } catch (RemoteException e) {
                                log.error("an error occured when deregistering from remote adaptor: " 
                                        + e.getMessage());
                            }
                        }
                    } // synchronized remoteWorkers
                } // synchronized localWorkers
            } // synchronized workers
        } // synchronized adaptorManagement
    }
    
    // --------------------------- adaptor handling ---------------------------
        
    /**
     * remove all the adaptors before loading the new adaptors from configuration file.
     * @throws LLRPRuntimeException
     */
    private synchronized void clearWorkers() throws LLRPRuntimeException {
        synchronized (workers) {
            synchronized (localWorkers) {
                synchronized (remoteWorkers) {
                    // remove the workers.
                    for (AdaptorWorker worker : workers.values()) {
                        try {
                            if (worker.getAdaptorIpAddress() == null) {
                                // if it is a local adaptor undefine the readers 
                                worker.getAdaptor().undefineAll();
                            }
                            undefine(worker.getAdaptor().getAdaptorName());
                        } catch (RemoteException e) {
                            log.error(e.getStackTrace(),e);
                        }
                    }        
                    
                    // erase all existing adaptors
                    remoteWorkers.clear();
                    localWorkers.clear();
                    workers.clear();
                }
            }
        }
    }
    
    /**
     * disconnectReaders shuts down all local readers. 
     */
    public void disconnectReaders() {
        synchronized (workers) {
            synchronized (localWorkers) {
                for (AdaptorWorker worker : localWorkers.values()) {
                    try {
                        worker.getAdaptor().disconnectAll();
                    } catch (RemoteException e) {
                        log.error(e.getStackTrace(),e);
                    } catch (LLRPRuntimeException e) {
                        log.error(e.getStackTrace(),e);
                    }
                }
            }
        }
    }
    
    /**
     * tells whether an adaptorName already exists.
     * @param adaptorName the name of the adaptor to check.
     * @throws LLRPRuntimeException whever something goes wrong ...
     * @return true if adaptor exists else false.
     */
    public static boolean containsAdaptor(String adaptorName) throws LLRPRuntimeException {
        checkStatus();
    
        return workers.containsKey(adaptorName);
    }
    
    /**
     * checks, whether a given adapter is a local adapter or not.
     * @param adapterName the name of the adapter to check.
     * @return true if the adapter is local, false otherwise.
     * @throws LLRPRuntimeException whenever something goes wrong...
     */
    public boolean isLocalAdapter(String adapterName) throws LLRPRuntimeException {
        checkStatus();
        
        return localWorkers.containsKey(adapterName);
    }
    
    /**
     * adds a new adaptor to the adaptor list.
     * @param adaptorName the name of the new adaptor.
     * @param address if you are using a client adaptor you have to provide the address of the server stub.
     * @throws LLRPRuntimeException when either name already exists or when there occurs an error in adaptor creation.
     * @throws RemoteException when there is an error during transmition.
     * @throws NotBoundException when there is no registry available.
     */
    public synchronized String define(String adaptorName, String address) 
        throws LLRPRuntimeException, RemoteException, NotBoundException {
        checkStatus();
        String adaptorNameToUse = adaptorName;
        
        synchronized (workers) {
            synchronized (localWorkers) {
                synchronized (remoteWorkers) {
                    
                    Adaptor adaptor = null;
                    if (address != null) {
                        // try to get the instance from the remote 
                        // adaptor.
                        Registry registry = LocateRegistry.getRegistry(address, Constants.REGISTRY_PORT);
                        adaptor = (Adaptor) registry.lookup(Constants.ADAPTOR_NAME_IN_REGISTRY);
                        
                        // server adaptor always keeps its name. therefore 
                        // we rename the adaptor
                        log.debug(String.format("adaptor is remote. therefore renaming %s to %s.",
                                adaptorName, adaptor.getAdaptorName()));
                        
                        adaptorNameToUse = adaptor.getAdaptorName();
                    }
                    
                    // tests whether there exists already a adaptor of this name
                    if (containsAdaptor(adaptorNameToUse)) {
                        log.error(ADAPTOR_STRING_PREFIX + adaptorNameToUse + "' already exists!");
                        LLRPDuplicateNameException e = new LLRPDuplicateNameException(adaptorNameToUse, 
                                ADAPTOR_STRING_PREFIX + adaptorNameToUse + "' already exists!");
                        
                        postException(
                                e, LLRPExceptionHandlerTypeMap.EXCEPTION_ADAPTOR_ALREADY_EXISTS,
                                adaptorNameToUse, "");
                        throw e;
                    }
                    
                    AdaptorCallback cb = null;
                    AdaptorWorker worker = null;
                    if (address == null) {
                        // determine the special hopefully unique server adaptor name
                        if (export) {
                            // change the name of the adaptor to the ip of the current machine.
                            String hostNamePrefix = "server adaptor - ";
                            String hostAddress = String.format("unknown ip %d", System.currentTimeMillis());
                            try {
                                java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
                                hostAddress = localMachine.getHostAddress();
                            }
                            catch (java.net.UnknownHostException uhe) {
                                log.debug("hmmm, what happened? " +
                                        "This should not occur here :-).");
                            }
                            adaptorNameToUse = hostNamePrefix + hostAddress;
                        }
                        
                        // local case
                        adaptor = new AdaptorImpl(adaptorNameToUse);
                        ((AdaptorImpl)adaptor).setAdaptorManagement(this);
                        cb = new AdaptorCallback(false);
                        worker = new AdaptorWorker(cb, adaptor);
                        worker.setAdaptorIpAddress(null);
                        localWorkers.put(adaptorNameToUse, worker);
                        
                        log.debug("created a new local adaptor '" + 
                                adaptorNameToUse + "'.");
                    } else {
                        // remote case
                        cb = new AdaptorCallback(true);
                        worker = new AdaptorWorker(cb, adaptor);
                        // store the ip address of the remote adaptor.
                        worker.setAdaptorIpAddress(address);
                        remoteWorkers.put(adaptorNameToUse, worker);
                        
                        log.debug("created a new client adaptor '" + 
                                adaptorNameToUse + "' with url '" + address + "'.");
                    }
                    
                    // register the callback.
                    try {
                        adaptor.registerForAsynchronous(cb);
                    } catch (RemoteException e) {
                        log.error(e.getStackTrace(),e);
                    }
                    
                    // register the thread.
                    workers.put(adaptorNameToUse, worker);
                    
                    // start the thread
                    new Thread(worker).start();
                    
                    // if the user requests an export of the adaptor we do this...
                    // the adaptor HAS to be local!
                    if ((export) && (address == null)) {
                        // now export the adaptor
                    
                        // create the new registry
                        log.debug("create a registry for the export of the local adaptor.");
                        LocateRegistry.createRegistry(Constants.REGISTRY_PORT);
                        Registry registry = LocateRegistry.getRegistry(Constants.REGISTRY_PORT);
                        
                        log.debug("bind the adaptor to the registry");
                        try {
                            registry.bind(Constants.ADAPTOR_NAME_IN_REGISTRY, adaptor);
                        } catch (AlreadyBoundException e) {

                            // this exception should NEVER occur as we destroy the 
                            // registry when we register the new adaptor.
                            log.error("THERE WAS A SEVERE ERROR THAT SHOULD NEVER OCCUR!!!");
                            log.error(e.getStackTrace(),e);
                        }
                    }
                }
            }
            
            commit();
        }
        
        return adaptorNameToUse;
    }
    
    /**
     * removes an adaptor from the adaptor list.
     * @param adaptorName the name of the adaptor to remove.
     * @throws LLRPRuntimeException when either the name does not exist or when an internal runtime error occurs.
     */
    public synchronized void undefine(String adaptorName) throws LLRPRuntimeException {
        checkStatus();
        
        synchronized (workers) {
            synchronized (localWorkers) {
                synchronized (remoteWorkers) {
                    if (!containsAdaptor(adaptorName)) {
                        log.error(ADAPTOR_STRING_PREFIX + adaptorName + ADAPTOR_STRING_POSTFIX);
                        LLRPRuntimeException e = new LLRPRuntimeException(ADAPTOR_STRING_PREFIX + adaptorName + ADAPTOR_STRING_POSTFIX);
                        
                        postException(
                                e, LLRPExceptionHandlerTypeMap.EXCEPTION_ADAPTER_NOT_EXIST,
                                adaptorName, "");
                        throw e;
                    }
                    
                    localWorkers.remove(adaptorName);
                    remoteWorkers.remove(adaptorName);
                    
                    // remove the adaptor
                    AdaptorWorker worker = workers.remove(adaptorName);
                    try {
                        worker.getAdaptor().deregisterFromAsynchronous(worker.getCallback());
                        // stop the worker.
                        worker.tearDown();
                        
                    } catch (RemoteException e) {
                        log.error(e.getStackTrace(),e);
                    }
                }
            }
        }
        commit();
    }
    
    /**
     * returns a list of all the available adaptor names.
     * @return a list of all the available adaptor names.
     */
    public List<String> getAdaptorNames() throws LLRPRuntimeException {
        checkStatus();
        
        ArrayList<String> adaptorNames = new ArrayList<String> ();
        
        // make a deep copy (no leakage)
        for (AdaptorWorker worker : workers.values()) {
            try {
                adaptorNames.add(worker.getAdaptor().getAdaptorName());
                worker.cleanConnFailure();
            } catch (RemoteException e) {
                worker.reportConnFailure();
                log.error("could not connect to remote adaptor: " + e.getMessage());
            }
            
        }
        checkWorkers();

        return adaptorNames;
    }
    
    /**
     * returns an adaptor to a given adaptorName.
     * @param adaptorName the name of the requested adaptor.
     * @return an adaptor to a given adaptorName.
     * @throws LLRPRuntimeException when the adaptor does not exist.
     */
    public static Adaptor getAdaptor(String adaptorName) throws LLRPRuntimeException {
        checkStatus();
        
        if (!containsAdaptor(adaptorName)) {            
            log.error(ADAPTOR_STRING_PREFIX + adaptorName + ADAPTOR_STRING_POSTFIX);
            LLRPRuntimeException e = new LLRPRuntimeException(ADAPTOR_STRING_PREFIX + adaptorName + ADAPTOR_STRING_POSTFIX);
            
            postException(
                    e, LLRPExceptionHandlerTypeMap.EXCEPTION_ADAPTER_NOT_EXIST,
                    adaptorName, "");
            throw e;
        }
        
        return workers.get(adaptorName).getAdaptor();
    }
    
    /**
     * helper to access the default local adaptor more convenient.
     * @return the default local adaptor.
     * @throws LLRPRuntimeException this should never occur!
     */
    public AdaptorImpl getDefaultAdaptor() throws LLRPRuntimeException {
        checkStatus();
                        
        if (!workers.containsKey(DEFAULT_ADAPTOR_NAME)) {
            // create the default adaptor
            try {
                define(DEFAULT_ADAPTOR_NAME, null);
            } catch (Exception e) {
                // these two exceptions only occur in remote adaptors. 
                // therefore we can safely ignore them
                log.debug("hmmm, what happened? This should not occur here :-).");
            }
        }
        return (AdaptorImpl)getAdaptor(DEFAULT_ADAPTOR_NAME);
    }
    
    /**
     * you can check whether an adaptor is ready to accept messages.
     * @param adaptorName the name of the adaptor to check.
     * @return true when ok, else false.
     */
    public boolean isReady(String adaptorName) throws LLRPRuntimeException {
        checkStatus();
        
        if (!containsAdaptor(adaptorName)) {            
            log.error(ADAPTOR_STRING_PREFIX + adaptorName + ADAPTOR_STRING_POSTFIX);
            LLRPRuntimeException e = new LLRPRuntimeException(ADAPTOR_STRING_PREFIX + adaptorName + ADAPTOR_STRING_POSTFIX);
            
            postException(
                    e, LLRPExceptionHandlerTypeMap.EXCEPTION_ADAPTER_NOT_EXIST,
                    adaptorName, "");
            throw e;
        }
        
        return workers.get(adaptorName).isReady();
    }
    
    // --------------------------- message and error handling ---------------------------
    /**
     * enqueue an LLRPMessage to be sent to a llrp reader. the adaptor will
     * process the message when ready.
     * @param adaptorName the name of the adaptor holding the llrp reader.
     * @param readerName the name of the llrp reader.
     * @param message the LLRPMessage.
     * @throws LLRPRuntimeException when the queue of the adaptor is full.
     */
    public void enqueueLLRPMessage(String adaptorName, String readerName, LLRPMessage message) throws LLRPRuntimeException {
        checkStatus();
        
        synchronized (workers) {
            AdaptorWorker theWorker = null;
            if (!workers.containsKey(adaptorName)) {
                postException(new LLRPRuntimeException("Adaptor does not exist"), 
                        LLRPExceptionHandlerTypeMap.EXCEPTION_ADAPTER_NOT_EXIST, adaptorName, readerName);
                
            } else {
                theWorker = workers.get(adaptorName);
            }
            
            if (!theWorker.isReady()) {
                LLRPRuntimeException e = new LLRPRuntimeException("Queue is full");
                postException(e, LLRPExceptionHandlerTypeMap.EXCEPTION_READER_LOST, "AdaptorManagement", readerName);
                throw e;
            }
            log.debug("enqueueLLRPMessage(" + adaptorName + ", " + readerName + ")");
            theWorker.enqueue(new QueueEntry(message, readerName, adaptorName));
        }
    }
    
    /**
     * register a handler that will receive all the incoming messages.
     * @param handler the handler.
     */
    public void registerFullHandler(MessageHandler handler) {
        synchronized (fullHandlers) {
            fullHandlers.add(handler);
        }
    }
    
    /**
     * remove a handler from the full handler list.
     * @param handler the handler to be removed.
     */
    public void deregisterFullHandler(MessageHandler handler) {
        synchronized (fullHandlers) {
            fullHandlers.remove(handler);
        }
    }
    
    /**
     * tests whether a given handler is already registered or not.
     * @param handler the handler to check for.
     * @return true if the handler is present, false otherwise.
     */
    public boolean hasFullHandler(MessageHandler handler) {
        synchronized (fullHandlers) {
            return fullHandlers.contains(handler);
        }
    }
    
    /**
     * register a handler that will receive only a restricted set of messages.
     * @param handler the handler.
     * @param clzz the type of messages that the handler likes to receive (example KEEPALIVE.class).
     */
    public void registerPartialHandler(MessageHandler handler, Class clzz) {
        synchronized (partialHandlers) {
            LinkedList<MessageHandler> handlers = partialHandlers.get(clzz);
            if (null == handlers) {
                handlers = new LinkedList<MessageHandler> ();
                partialHandlers.put(clzz, handlers);
            }
            handlers.add(handler);
        }
    }
    
    /**
     * remove a handler from the handlers list.
     * @param handler the handler to remove.
     * @param clzz the class where the handler is registered.
     */
    public void deregisterPartialHandler(MessageHandler handler, Class clzz) {
        synchronized (partialHandlers) {
            LinkedList<MessageHandler> handlers = partialHandlers.get(clzz);
            if (null != handlers) {
                synchronized (handlers) {                    
                    handlers.remove(handler);
                }
            }
        }
    }
    
    /**
     * checks whether a given handler is registered at a given selector class.
     * @param handler the handler to check.
     * @param clzz the class where to search for the handler.
     * @return true if the handler is present, false otherwise.
     */
    public boolean hasPartialHandler(MessageHandler handler, Class clzz) {
        synchronized (partialHandlers) {
            LinkedList<MessageHandler> handlers = partialHandlers.get(clzz);
            if (null != handlers) {
                synchronized (handlers) {
                    return handlers.contains(handler);
                }
            }
        }
        return false;
    }
    
    /**
     * dispatches an LLRP message to all the registered full handlers. All the 
     * handlers that have interest into the class of the message will be 
     * informed as well.
     * @param adaptorName the name of the adapter that received the message.
     * @param readerName the reader that received the message. 
     * @param message the LLRP message itself.
     */
    public void dispatchHandlers(String adaptorName, String readerName, 
            LLRPMessage message) {
        
        // handle full handlers...
        synchronized (fullHandlers) {
            for (MessageHandler handler : fullHandlers) {
                handler.handle(adaptorName, readerName, message);
            }
        }
        
        // handle partial handlers
        synchronized (partialHandlers) {
            LinkedList<MessageHandler> handlers = partialHandlers.get(message.getClass());
            if (null != handlers) {
                synchronized (handlers) {
                    for (MessageHandler handler : handlers) {
                        handler.handle(adaptorName, readerName, message);
                    }
                }
            }
        }
        
    }
    
    /**
     * posts an exception the the exception handler.
     * @param exceptionType the type of the exception. see {@link LLRPExceptionHandler} for more details.
     * @param adapterName the name of the adaptor that caused the exception.
     * @param readerName the name of the reader that caused the exception.
     * @param e the exception itself.
     */
    public static void postException(
            LLRPRuntimeException e, 
            LLRPExceptionHandlerTypeMap 
            exceptionType, 
            String adapterName, 
            String readerName) 
    {
        
        if (exceptionHandler == null) {
            log.error("ExceptionHandler not set!!!");
            log.error(e.getStackTrace(),e);
            return;
        }

        log.debug(String.format("Received error call on callback from '%s'.\nException:\n%s", readerName, e.getMessage()));
        
        exceptionHandler.postExceptionToGUI(exceptionType, e, adapterName, readerName);
    }
    
    
    // ------------------------------- singleton handling -------------------------------
    
    /** private constructor for singleton. */
    private AdaptorManagement() {}
    
    /** the instance of the singleton. */
    private static AdaptorManagement instance = new AdaptorManagement();
    
    /**
     * returns the singleton of the AdaptorManagement.
     * @return the singleton of the AdaptorManagement.
     */
    public static AdaptorManagement getInstance() {
        return instance;
    }

    
    
    // ------------------------------- default config -------------------------------
    private synchronized void createDefaultConfiguration() throws LLRPRuntimeException {
            
        // do not store this configuration
        storeConfig = null;
        
        // no config -> no changes to commit
        setCommitChanges(false);
        
        // clear the workers 
        clearWorkers();
        
        // create a default adaptor
        try {
            define(DEFAULT_ADAPTOR_NAME, null);
        } catch (RemoteException e) {
            log.error(e.getStackTrace(),e);
        } catch (NotBoundException e) {
            log.error(e.getStackTrace(),e);
        }
    }
    // ------------------------------- load and store -------------------------------
    
    /**
     * loads the adaptorManagement configuration from file (holds the adaptors and the readers for the local adaptor).
     * all the adaptors defined currently get removed!!! the action is atomic, this means that depending on your 
     * setting, the client might get blocked for a short moment!
     * @throws LLRPRuntimeException whenever there is an exception during restoring.
     */
    public synchronized void loadFromFile() throws LLRPRuntimeException {
    
        if (readConfig == null) {

            // if the config cannot be read, then we inform the user about that
            // issue but then just use a default configuration
            
            log.info("config not specified -> create a default configuration");    
            createDefaultConfiguration();
            return;
        }
        
        // store the commit mode.
        boolean commitMode = isCommitChanges();
        setCommitChanges(false);
        
        boolean isExported = false;
        synchronized (AdaptorManagement.class) {
            synchronized (workers) {
                synchronized (localWorkers) {
                    synchronized (remoteWorkers) {
                        
                        // clear out all available adaptors
                        clearWorkers();
                        
                        List<AdaptorConfiguration> configurations = null;
                        try {
                            configurations = configLoader.getConfiguration(readConfig);
                        } catch (LLRPRuntimeException e) {
                            log.info("could not read the config -> create a default configuration");
                            
                            createDefaultConfiguration();
                            return;
                        }
                        
                        for (AdaptorConfiguration adaptorConfiguration : configurations) {
                            
                            String adaptorName = adaptorConfiguration.getAdaptorName();
                            String adaptorIP = adaptorConfiguration.getIp();
                                            
                            if (adaptorConfiguration.isLocal()) {
                                log.debug("Load local Adaptor");
                                adaptorIP = null;
                            } else {
                                log.debug(String.format("Load Remote Adaptor: '%s' on '%s'",
                                        adaptorName, 
                                        adaptorConfiguration.getIp()));
                            }
                            
                            boolean adaptorCreated = false;
                            try {
                                if ((export) && (isExported)) {
                                    // only export the first adaptor
                                    isExported = true;
                                    define(adaptorName, adaptorIP);
                                }
                                adaptorName = define(adaptorName, adaptorIP);
    
                                adaptorCreated = true;
                                log.debug(String.format("adaptor '%s' successfully created", adaptorName));
                            } catch (Exception e) {
                                log.error(String.format("could not create adaptor '%s': %s", adaptorName,
                                        e.getMessage()));
                            }
                            
                            // only create the readers when the adaptor has been created successfully
                            // and if the adaptor is remote, we just retrieve the readers. 
                            if ((adaptorCreated) && (adaptorConfiguration.isLocal())) {
                                // get a handle of the adaptor and register all the readers.
                                Adaptor adaptor = getAdaptor(adaptorName);
                                
                                if (adaptorConfiguration.getReaderPrototypes() != null) {
                                    for (ReaderConfiguration readerConfiguration : adaptorConfiguration.getReaderPrototypes()) {
                                        
                                        String readerName = readerConfiguration.getReaderName();
                                        String readerIp = readerConfiguration.getReaderIp();
                                        int readerPort = readerConfiguration.getReaderPort();
                                        boolean readerClientInitiated = readerConfiguration.isReaderClientInitiated();
                                        boolean connectImmediately = readerConfiguration.isConnectImmediately();
                                        
                                        log.debug(String.format("Load llrp reader: '%s' on '%s:%d', clientInitiatedConnection: %b, connectImmediately: %b", 
                                                readerName, readerIp, readerPort, readerClientInitiated, connectImmediately));
                                        
                                        // create the reader
                                        try {
                                            // try to establish the connection immediately
                                            adaptor.define(readerName, readerIp, readerPort, readerClientInitiated, connectImmediately);
                                            log.debug(String.format("reader '%s' successfully created", readerName));
                                        } catch (RemoteException e) {
                                            log.error(String.format("could not create reader '%s'", readerName));
                                            log.error(e.getStackTrace(),e);
                                        }
                                    }
                                }
                            }
                        }
                        
                    } // synchronized remoteWorkers
                } // synchronized localWorkers
            } // synchronized workers
        } // synchronized adaptorManagement
        
        // restore the commit mode.
        setCommitChanges(commitMode);
    }
    
    /**
     * stores the configuration of the adaptor management to file. the remote adaptors get stored and for 
     * the local adaptor all readers get stored as well.
     * @throws LLRPRuntimeException whenever there occurs an error during storage.
     */
    public synchronized void storeToFile() throws LLRPRuntimeException {
        if (storeConfig == null) {
            log.info("Store config not specified, not storing the configuration.");
            return;
        }
        
        synchronized (AdaptorManagement.class) {
            synchronized (workers) {
                synchronized (localWorkers) {
                    synchronized (remoteWorkers) {
                        
                        List<AdaptorConfiguration> configurations = new LinkedList<AdaptorConfiguration>();
                        
                         for (String adaptorName : workers.keySet()) {
                             String ip = workers.get(adaptorName).getAdaptorIpAddress();
                             boolean isLocal = false;
                             if (ip == null) {
                                 isLocal = true;
                             }
                            configurations.add(
                                    new AdaptorConfiguration(
                                            adaptorName, 
                                            ip,
                                            isLocal,
                                            null));
                        }
                        
                         for (AdaptorConfiguration configuration : configurations) {
                             if (configuration.isLocal()) {
                                 List<ReaderConfiguration> readerConfigurations = new LinkedList<ReaderConfiguration> ();
                                 configuration.setReaderConfigurations(readerConfigurations);
                                 // get a handle on the adaptor
                                 Adaptor adaptor = getAdaptor(configuration.getAdaptorName());
                                 try {
                                    for (String readerName : adaptor.getReaderNames()) {
                                        Reader reader = adaptor.getReader(readerName);
                                        boolean connectImmed = false;    // somehow this causes bugs with MINA, if we start the reader at startup.
                                        boolean clientInit = reader.isClientInitiated();
                                        String ip = reader.getReaderAddress();
                                        int port = reader.getPort();
                                        
                                        readerConfigurations.add(new ReaderConfiguration(
                                                    readerName,
                                                    ip,
                                                    port,
                                                    clientInit,
                                                    connectImmed
                                                )
                                        );
                                    }
                                } catch (RemoteException e) {
                                    // local configuration therefore we can ignore the remote exception.
                                    log.error(e.getStackTrace(),e);
                                }
                             }
                         }
                         
                         try {
                             configLoader.writeConfiguration(configurations, storeConfig);
                         } catch (LLRPRuntimeException e) {
                             postException(e, 
                                     LLRPExceptionHandlerTypeMap.EXCEPTION_ADAPTOR_MANAGEMENT_CONFIG_NOT_STORABLE, 
                                     "", "");
                         }
                         
                    } // synchronized remoteWorkers
                } // synchronized localWorkers
            } // synchronized workers
        } // synchronized adaptorManagement
    }
    
    // ------------------------------- getter and setter -------------------------------
    /**
     * returns the exception handler.
     * @return the exception handler.
     */
    public LLRPExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    /**
     * sets the exception handler.
     * @param exceptionHandler the exception handler.
     */
    public void setExceptionHandler(LLRPExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * flags whether all changes to the AdaptorManagement get reflected to the 
     * configuration file.
     * @return true if yes, false otherwise.
     */
    public boolean isCommitChanges() {
        return commitChanges;
    }

    /**
     * sets whether all changes to the AdaptorManagement get reflected to the 
     * configuration file.
     * @param commitChanges 
     * <ul>
     * <li>true then the changes get stored back to the configuration immediately</li>
     * <li>false the changes are not stored back</li>
     * </ul>
     */
    public void setCommitChanges(boolean commitChanges) {
        this.commitChanges = commitChanges;
    }

    /**
     * returns the configuration file where to read the settings.
     * @return the configuration file where to read the settings.
     */
    public String getReadConfig() {
        return readConfig;
    }

    /**
     * sets the configuration file.
     * @param readConfig the configuration file.
     */
    public void setReadConfig(String readConfig) {
        this.readConfig = readConfig;
    }

    /**
     * returns the configuration file where to store changes.
     * @return the configuration file where to store changes.
     */
    public String getStoreConfig() {
        return storeConfig;
    }

    /**
     * sets the configuration file where to store changes.
     * @param storeConfig the configuration file where to store changes.
     */
    public void setStoreConfig(String storeConfig) {
        this.storeConfig = storeConfig;
    }
    
    private synchronized void checkWorkers() {
        LinkedList<AdaptorWorker> errors = new LinkedList<AdaptorWorker> ();
        synchronized (workers) {
            synchronized (localWorkers) {
                synchronized (remoteWorkers) {
                    for (AdaptorWorker worker : workers.values()) {
                        if (!worker.ok()) {
                            errors.add(worker);
                        }
                    }
                    
                    // remove the erroneous
                    for (AdaptorWorker worker : errors) {
                        // remove from all the workers.
                        workers.remove(worker);
                        remoteWorkers.remove(worker);
                        localWorkers.remove(worker);
                    }
                }
            }
        }
        commit();
    }
}
