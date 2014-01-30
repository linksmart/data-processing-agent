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

import org.llrp.ltk.types.LLRPMessage;

/**
 * All processes that want to receive messages from the {@link AdaptorManagement} 
 * need to implement this interface. Upon arrival of a new LLRP message, the 
 * {@link AdaptorManagement} invokes the handle method on the registered 
 * handlers.
 * @author sawielan
 *
 */
public interface MessageHandler {
    
    /**
     * This method is invoked from the adapter management whenever a new LLRP 
     * message arrives on an attached reader.
     * @param adaptorName the name of the adapter where the reader belongs to. 
     * @param readerName the name of the receiving reader.
     * @param message the LLRP message.
     */
    void handle(String adaptorName, String readerName, LLRPMessage message);
}
