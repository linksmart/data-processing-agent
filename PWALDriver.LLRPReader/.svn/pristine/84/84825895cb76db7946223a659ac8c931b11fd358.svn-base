package eu.ebbits.pwal.impl.driver.llrpreader.adaptor;

import org.apache.log4j.Logger;

import eu.ebbits.pwal.impl.driver.llrpreader.org.fosstrak.llrp.adaptor.exception.LLRPRuntimeException;
import eu.ebbits.pwal.impl.driver.llrpreader.org.fosstrak.llrp.client.LLRPExceptionHandler;
import eu.ebbits.pwal.impl.driver.llrpreader.org.fosstrak.llrp.client.LLRPExceptionHandlerTypeMap;

/**
 * Handler for exceptions
 * <p> 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author    ISMB
 * @version    %I%, %G%
 * @since   PWAL 0.2.0
 */
public class ExceptionHandler implements LLRPExceptionHandler {
    private static Logger log = Logger.getLogger(ExceptionHandler.class.getName());
    
    @Override
    public void postExceptionToGUI(LLRPExceptionHandlerTypeMap aExceptionType,
            LLRPRuntimeException e, String aAdapter, String aReader) {
        log.error(String.format("Thrown Exception from adapter: %s, reader: %s",aAdapter,aReader));
        log.error(aExceptionType.toString());
        log.error(e.getStackTrace(),e);
    }

}
