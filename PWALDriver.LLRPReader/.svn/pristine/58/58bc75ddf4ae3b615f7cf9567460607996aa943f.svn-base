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

import java.sql.SQLException;
import java.util.List;

import eu.ebbits.pwal.impl.driver.llrpreader.org.fosstrak.llrp.adaptor.exception.LLRPRuntimeException;
import eu.ebbits.pwal.impl.driver.llrpreader.org.fosstrak.llrp.client.repository.sql.roaccess.ROAccessItem;

/**
 * Common interface for all the implementations providing access to the 
 * RO_ACCESS_REPORTS data-base. The actual implementation of the interface 
 * is chosen at runtime via the strategy pattern from the respective Context 
 * (in this case the implementation of the {@link Repository} interface). The 
 * interface extends the {@link MessageHandler} interface, in order to be able 
 * to receive LLRP RO_ACCESS_REPORTS messages. 
 * <h3>NOTICE:</h3> The registration at the {@link AdaptorManagement} is done 
 * automatically for the implementing class. So do this ONLY, if you know 
 * exactly what you are planing to do (otherwise messages might get logged 
 * twice!!!).
 * @author sawielan
 *
 */
public interface ROAccessReportsRepository extends MessageHandler {
    
    /**
     * set the repository that "owns" this RO_ACCESS_REPORTS repository.
     * @param repository the repository that "owns" this RO_ACCESS_REPORTS repository.
     */
    void setRepository(Repository repository);
    
    /**
     * Initializer for the RO_ACCESS_REPORTS repository. 
     * <strong>NOTICE</strong>: if you create an instance of a subclass of this 
     * interface, you <strong>MUST</strong> call this method directly after 
     * instantiation.
     * @param repository the repository belonging to this RO_ACCESS_REPORTS DB.
     * @throws when there is a problem with initialization (eg. missing param).
     */
    void initialize(Repository repository) throws LLRPRuntimeException;
    
    /**
     * retrieves all the RO_ACCESS_REPORTS elements in the database.
     * @return a list of all the entries in the database stored into 
     * {@link ROAccessItem}.
     * @throws SQLException when there is an error of any kind.
     */
    List<ROAccessItem> getAll() throws SQLException;
    
    /**
     * drop all the messages in the repository.
     * @throws SQLException when there is an error of any kind.
     */
    void clear() throws SQLException;
}
