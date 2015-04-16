package eu.linksmart.gc.network.backbone.protocol.wrapper;

import eu.linksmart.gc.api.network.NMResponse;
import eu.linksmart.gc.api.network.VirtualAddress;
import eu.linksmart.gc.api.network.backbone.Backbone;
import eu.linksmart.gc.api.security.communication.SecurityProperty;
import org.apache.felix.scr.annotations.*;
import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import java.util.List;

/**
 * Created by Caravajal on 02.04.2015.
 */

@Component(name="BackboneMQTT", immediate=true)
@Service({Backbone.class})
public class WrapperProtocolImpl implements Backbone {
    private Logger LOG = Logger
            .getLogger(WrapperProtocolImpl.class.getName());
    protected ComponentContext context;
    private Backbone AsyncBackbone;
    private Backbone SyncBackbone;
    //WrapperProtocolConfiguratorImpl configurator;
    /*@Reference(name="ConfigurationAdmin",
            cardinality = ReferenceCardinality.MANDATORY_UNARY,
            bind="bindConfigAdmin",
            unbind="unbindConfigAdmin",
            policy= ReferencePolicy.STATIC)
    private ConfigurationAdmin mConfigAdmin = null;

    protected void bindConfigAdmin(ConfigurationAdmin configAdmin) {
        LOG.debug("BackboneRouter::binding configadmin");
        this.mConfigAdmin = configAdmin;
    }

    protected void unbindConfigAdmin(ConfigurationAdmin configAdmin) {
        LOG.debug("BackboneRouter::un-binding configadmin");
        this.mConfigAdmin = null;
    }*/
    @Reference(name="Backbone",
            cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE,
            bind="bindBackbone",
            unbind="unbindBackbone",
            policy= ReferencePolicy.DYNAMIC)
    private Backbone mBackbone;


    protected void bindBackbone(Backbone backbone) {
        LOG.debug("BackboneRouter::binding backbone");
        if (backbone.getName().equals("eu.linksmart.gc.network.backbone.protocol.mqtt.MqttBackboneProtocolImpl"))
            AsyncBackbone = backbone;
        if (backbone.getName().equals("eu.linksmart.gc.network.backbone.protocol.http.HttpImpl"))
            SyncBackbone = backbone;
    }

    protected void unbindBackbone(Backbone backbone) {
        LOG.debug("BackboneRouter::un-binding backbone");
        if (backbone.getName().equals("eu.linksmart.gc.network.backbone.protocol.mqtt.MqttBackboneProtocolImpl"))
            AsyncBackbone = null;
        if (backbone.getName().equals("eu.linksmart.gc.network.backbone.protocol.http.HttpImpl"))
            SyncBackbone = null;
    }
    @Activate
    protected void activate(ComponentContext context) throws Exception {
        LOG.info("[activating Backbone WrapperProtocol]");
        //this.conf = new WrapperProtocolConfiguratorImpl(this, context.getBundleContext(), mConfigAdmin);
        //this.conf.registerConfiguration();



    }
    @Deactivate
    public void deactivate(ComponentContext context) {

        LOG.info("[de-activating Backbone WrapperProtocol]");


    }
    @Override
    public NMResponse sendDataSynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte[] data) {
        if(SyncBackbone != null)
            return SyncBackbone.sendDataSynch(senderVirtualAddress,receiverVirtualAddress,data);

        LOG.error("There is no Sync backbone to send the data");
        return null;
    }

    @Override
    public NMResponse sendDataAsynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte[] data) {
        if(AsyncBackbone != null)
            return AsyncBackbone.sendDataAsynch(senderVirtualAddress,receiverVirtualAddress,data);

        LOG.error("There is no Async backbone to send the data");
        return null;
    }

    @Override
    public NMResponse receiveDataSynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte[] data) {
        if(SyncBackbone != null)
            return SyncBackbone.receiveDataSynch(senderVirtualAddress, receiverVirtualAddress, data);

        LOG.error("There is no Sync backbone to send the data");
        return null;
    }

    @Override
    public NMResponse receiveDataAsynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte[] data) {
        if(AsyncBackbone != null)
            return AsyncBackbone.receiveDataAsynch(senderVirtualAddress, receiverVirtualAddress, data);

        LOG.error("There is no Async backbone to send the data");
        return null;
    }

    @Override
    public NMResponse broadcastData(VirtualAddress senderVirtualAddress, byte[] data) {
        if(AsyncBackbone != null)
            return AsyncBackbone.broadcastData(senderVirtualAddress,data);

        LOG.error("There is no Async backbone to send the data");
        return null;
    }

    @Override
    public String getEndpoint(VirtualAddress virtualAddress) {
        String sync = null, async = null;
        if(SyncBackbone != null)
            sync =SyncBackbone.getEndpoint(virtualAddress);

        if(AsyncBackbone != null)
            async =AsyncBackbone.getEndpoint(virtualAddress);

        if (sync  != null && async != null)
            if(sync.equals(async))
                return sync;
            else {
                LOG.error("The endpoints of sync and async backbone protocol do not match");
                return sync;
            }
        else if (sync != null)
            return sync;
        else if (async != null)
            return async;

        return null;
    }

    @Override
    public boolean addEndpoint(VirtualAddress virtualAddress, String endpoint) {
        boolean sync = false, async = false;
        if(SyncBackbone != null)
            sync =SyncBackbone.addEndpoint(virtualAddress,endpoint);

        if(AsyncBackbone != null)
            async =AsyncBackbone.addEndpoint(virtualAddress, endpoint);

        return async || sync;
    }

    @Override
    public boolean removeEndpoint(VirtualAddress virtualAddress) {
        boolean sync = false, async = false;
        if(SyncBackbone != null)
            sync =SyncBackbone.removeEndpoint(virtualAddress);

        if(AsyncBackbone != null)
            async =AsyncBackbone.removeEndpoint(virtualAddress);

        return async || sync;
    }

    @Override
    public String getName() {
        return WrapperProtocolImpl.class.getName();
    }

    @Override
    public List<SecurityProperty> getSecurityTypesRequired() {
        List<SecurityProperty> sync = null, async = null;
        if(SyncBackbone != null)
            sync =SyncBackbone.getSecurityTypesRequired();

        if(AsyncBackbone != null)
            async =AsyncBackbone.getSecurityTypesRequired();

        if (sync  != null && async != null)
            if(sync.equals(async))
                return sync;
            else {
                LOG.error("The SecurityProperty of sync and async backbone protocol do not match");
                return sync;
            }
        else if (sync != null)
            return sync;
        else if (async != null)
            return async;

        return null;
    }

    @Override
    public void addEndpointForRemoteService(VirtualAddress senderVirtualAddress, VirtualAddress remoteVirtualAddress) {
        if(SyncBackbone != null)
            SyncBackbone.addEndpointForRemoteService(senderVirtualAddress, remoteVirtualAddress);

        if(AsyncBackbone != null)
            AsyncBackbone.addEndpointForRemoteService(senderVirtualAddress, remoteVirtualAddress);
    }
}
