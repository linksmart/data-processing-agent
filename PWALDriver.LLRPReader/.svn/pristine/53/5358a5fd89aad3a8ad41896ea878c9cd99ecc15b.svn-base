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

import eu.ebbits.pwal.impl.driver.llrpreader.org.fosstrak.llrp.adaptor.exception.LLRPRuntimeException;

/**
 * All exception handlers registered at the AdaptorManagement 
 * need to implement this interface in order to be delivered 
 * with exceptions triggered on the reader level.
 * @author sawielan
 *
 */
public interface LLRPExceptionHandler {
    
    /**
     * This method will be called asynchronously whenever 
     * an Exception is triggered.
     * @param aExceptionType a type-map describing the exception.
     * @param e the exception itself.
     * @param aAdapter the adapter that triggered the exception.
     * @param aReader the reader that triggered the exception.
     */
    void postExceptionToGUI(LLRPExceptionHandlerTypeMap aExceptionType, LLRPRuntimeException e, String aAdapter, String aReader);
}
