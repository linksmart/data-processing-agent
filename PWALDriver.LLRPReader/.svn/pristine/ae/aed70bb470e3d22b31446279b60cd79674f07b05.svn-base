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

package eu.ebbits.pwal.impl.driver.llrpreader.org.fosstrak.llrp.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import eu.ebbits.pwal.impl.driver.llrpreader.org.fosstrak.llrp.adaptor.exception.LLRPRuntimeException;

/**
 * helper class to instantiate the repository.
 * @author sawielan
 *
 */
public final class RepositoryFactory {
    
    private RepositoryFactory () {
    }
    
    /** user-name in the arguments table. */
    public static final String ARG_USERNAME = "username";
    
    /** password in the arguments table. */
    public static final String ARG_PASSWRD = "password";
    
    /** JDBC string in the arguments table. */
    public static final String ARG_JDBC_STRING = "jdbcString";
    
    /** parameter whether to wipe DB in the arguments table. */
    public static final String ARG_WIPE_DB = "wipeDB";
    
    /** parameter whether to log RO_ACCESS_REPORT. */
    public static final String ARG_LOG_RO_ACCESS_REPORT = "logROAccess";
    
    /** parameter whether to wipe RO_ACCESS_REPORTS DB in the arguments table.*/
    public static final String ARG_WIPE_RO_ACCESS_REPORTS_DB = "wipeROAccessDB";
    
    /** class name in the arguments table. */
    public static final String ARG_DB_CLASSNAME = "dbClassName";
    
    // the log4j logger.
    private static Logger log = Logger.getLogger(RepositoryFactory.class);
    
    /**
     * helper to create a hash-map with key-value pairs. just provide a 2D 
     * array, with pairs of (key, value).
     * <h3>Example:</h3>
     * <code>Map&lt;String,String&gt; m = createMap(new String[][] {</code><br/>
     * <code>&nbsp;&nbsp;&nbsp;&nbsp;{key1, value1},</code><br/>
     * <code>&nbsp;&nbsp;&nbsp;&nbsp;{key2, value2},</code><br/>
     * <code>&nbsp;&nbsp;&nbsp;&nbsp;{key3, value3}</code><br/>
     * <code>&nbsp;&nbsp;}</code>
     * @param keyValue the key values 2D array.
     * @return a hash-map mapping the 2D array in a hash-table.
     */
    public static Map<String, String> createMap(String [][] keyValue) {
        Map<String, String> map = new HashMap<String, String> ();
        if (null == keyValue) { 
            return map; 
        }
        
        final int len = keyValue.length;
        for (int i=0; i<len; i++) {
            map.put(keyValue[i][0], keyValue[i][1]);
        }
        return map;
    }
    
    /**
     * create a new repository and read the configuration from a Properties 
     * data structure.
     * @param properties the properties where to obtain the configuration from.
     * @return an instance of a {@link Repository}.
     * @throws InstantiationException when no instantiation was possible.
     * @throws IllegalAccessException access to class was denied.
     * @throws ClassNotFoundException when the class is not existing.
     * @throws LLRPRuntimeException when something other went wrong.
     */
    public static Repository create(Properties properties) 
        throws InstantiationException, LLRPRuntimeException, 
            IllegalAccessException, ClassNotFoundException {
        
        // extract the settings from the properties.
        Map<String, String> args = new HashMap<String, String>();
        
        args.put(ARG_USERNAME, properties.getProperty(ARG_USERNAME));
        args.put(ARG_PASSWRD, properties.getProperty(ARG_PASSWRD));
        args.put(ARG_JDBC_STRING, properties.getProperty(ARG_JDBC_STRING));
        args.put(ARG_WIPE_DB, properties.getProperty(ARG_WIPE_DB));
        args.put(ARG_LOG_RO_ACCESS_REPORT, 
                properties.getProperty(ARG_LOG_RO_ACCESS_REPORT));
        args.put(ARG_WIPE_RO_ACCESS_REPORTS_DB, 
                properties.getProperty(ARG_WIPE_RO_ACCESS_REPORTS_DB));
        args.put(ARG_DB_CLASSNAME, properties.getProperty(ARG_DB_CLASSNAME));
        
        return create(args);
    }
    
    /**
     * create a new repository with the configuration parameters provided 
     * via the parameters hash map.
     * @param args a hash-map providing the parameters.
     * @return an instance of a {@link Repository}.
     * @throws InstantiationException when no instantiation was possible.
     * @throws IllegalAccessException access to class was denied.
     * @throws ClassNotFoundException when the class is not existing.
     * @throws LLRPRuntimeException when something other went wrong.
     */
    public static Repository create(Map<String, String> args) 
    
        throws InstantiationException, LLRPRuntimeException,
            IllegalAccessException, ClassNotFoundException {

        if (null == args) { 
            throw new InstantiationException ("Args map is null!!! - aborting");
        }
        Object db = Class.forName(args.get(ARG_DB_CLASSNAME)).newInstance();
        Repository repository = null;
        if (db instanceof Repository) {
            repository = (Repository) db;
            try {
            repository.initialize(args);
            String logRO = args.get(ARG_LOG_RO_ACCESS_REPORT);
            if ((null != logRO) && (Boolean.parseBoolean(logRO))) {
                repository.getROAccessRepository().initialize(repository);
            } else {
                log.debug("NOT enabling RO_ACCESS_REPORT logging.");
            }
            } catch (LLRPRuntimeException llrpe) {
                log.error(String.format("could not initialize: '%s'", 
                        llrpe.getMessage()));
                throw new LLRPRuntimeException(llrpe);
            }
        } else {
            // throw an Exception
            log.error(String.format(
                    "Implementing class is not of type Repository: 's'",
                    args.get(RepositoryFactory.ARG_DB_CLASSNAME)));
            throw new InstantiationException(String.format(
                    "Illegal implementing class: '%s'", 
                    args.get(RepositoryFactory.ARG_DB_CLASSNAME)));
        }
        return repository;
    }
}
