package eu.ebbits.pwal.impl.driver.wsn;

import org.osgi.service.component.ComponentContext;

import eu.ebbits.pwal.api.annotations.PWALServiceAnnotation;
import eu.ebbits.pwal.impl.driver.PWALServicesDelegateImpl;
import eu.ebbits.pwal.impl.driver.wsn.client.ProxyDecoder;

/**
 * <code>WSNDriver</code> services delegate.
 * <p> 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author    ISMB
 * @version    %I%, %G%
 * @since    M36demo 1.0
 */
public class WSNServicesDelegate extends PWALServicesDelegateImpl {
    private ProxyDecoder proxyDecoder = null;
    
    /**
     * Constructor of the delegate
     * 
     *    @param driver - <code>WSNDriverImpl</code> that uses the delegate
     */
    public WSNServicesDelegate(WSNDriverImpl driver) {
        super(driver);
    }

    @Override
    public void init(ComponentContext context) {
        proxyDecoder = new ProxyDecoder();
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
     * Service exposed to set the sampling rate of the driver
     * 
     * @param payloadData - <code>byte[]</code> containing the data useful to set the sampling rate
     */
    @PWALServiceAnnotation (exposed = true)
    public void setSamplingRate(byte[] payloadData){
        proxyDecoder.decode(payloadData);
    }

    /**
     * Service exposed to start the sampling
     * 
     * @param payloadData - <code>byte[]</code> containing the data useful to start the sampling
     * 
     */
    @PWALServiceAnnotation (exposed = true)
    public void startSampling(byte[] payloadData){
        proxyDecoder.decode(payloadData);
    }
    
    /**
     * Service exposed to stop the sampling
     * 
     * @param payloadData - <code>byte[]</code> containing the data useful to stop the sampling
     * 
     */    
    @PWALServiceAnnotation (exposed = true)
    public void stopSampling(byte[] payloadData){
        proxyDecoder.decode(payloadData);
    }
}
