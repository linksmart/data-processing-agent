package eu.ebbits.pwal.impl.driver.llrpreader;

import org.osgi.service.component.ComponentContext;

import eu.ebbits.pwal.api.annotations.PWALServiceAnnotation;
import eu.ebbits.pwal.impl.driver.PWALServicesDelegateImpl;

/**
 * <code>LLRPReaderDriver</code> services delegate.
 * <p> 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author    ISMB
 * @version    %I%, %G%
 * @since    PWAL 0.2.0
 */
public class LLRPReadererServicesDelegate extends PWALServicesDelegateImpl {
    
    /**
     * Constructor of the service delegate of the LLRP driver
     * 
     * @param driver - driver that uses the delegate as <codeLLRPReaderDriverImpl<code>
     */
    public LLRPReadererServicesDelegate(LLRPReaderDriverImpl driver) {
        super(driver);
    }

    @Override
    public void init(ComponentContext context) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updatePWALServicesCollection() {
        // TODO Auto-generated method stub
        
    }
    
    /** 
     * Writes values on tag memory.
     * 
     * @param tagID     ID of the tag to be write
     * @param tagMask    The reader will take each tag EPC and bitwise AND it with this parameter
     * @param bits         Memory bank
     *          0: Reserved
     *            1: EPC
     *            2: TID
     *            3: User
     * @param base        base to start in the memory bank
     * @param values    values to be written (it's null if it's used to read)
     * 
     * @return boolean result: true all ok, false errors
     * 
     * 
     * @since       PWAL 0.2.0
     */
    @PWALServiceAnnotation (exposed = true)
    public synchronized boolean writeTagMemory(String tagID, String tagMask, int [] bits, int base, int [] values) {
        return ((LLRPReaderDriverImpl) this.getDriver()).getReader().writeTagMemory(tagID, tagMask, bits, base, values);
    }
    
    
    /**
     * Reads values from the memory of a tag
     * 
     * @param tagID          ID of the tag to be read
     * @param tagMask     The reader will take each tag EPC and bitwise AND it with this parameter
     * @param bits          Memory bank
     *          0: Reserved
     *            1: EPC
     *            2: TID
     *            3: User
     * @param base         base to start in the memory bank
     * @param nOfWords     number of words to be read (it's null if it's used to write)
     * 
     * @return int[]: value read
     * 
     * @since       PWAL 0.2.0
     */
    @PWALServiceAnnotation (exposed = true)
    public int [] readTagMemory(String tagID, String tagMask, int[] bits, int base, int nOfWords) {
        return ((LLRPReaderDriverImpl) this.getDriver()).getReader().readTagMemory(tagID, tagMask, bits, base, nOfWords);
    }
}
