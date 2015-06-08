package eu.linksmart.gc.network.routing.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.felix.scr.annotations.*;
import org.apache.log4j.Logger;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.gc.api.network.VirtualAddress;
import eu.linksmart.gc.api.network.NMResponse;
import eu.linksmart.gc.api.network.backbone.Backbone;
import eu.linksmart.gc.api.network.networkmanager.core.NetworkManagerCore;
import eu.linksmart.gc.api.network.routing.BackboneRouter;
import eu.linksmart.gc.api.network.routing.RouteEntry;
import eu.linksmart.gc.api.security.communication.SecurityProperty;

@Component(name="BackboneRouter", immediate=true)
@Service
public class BackboneRouterImpl implements BackboneRouter {

    private Logger logger = Logger
            .getLogger(BackboneRouterImpl.class.getName());
    protected ComponentContext context;

    @Reference(name="ConfigurationAdmin",
            cardinality = ReferenceCardinality.MANDATORY_UNARY,
            bind="bindConfigAdmin",
            unbind="unbindConfigAdmin",
            policy=ReferencePolicy.STATIC)
    private ConfigurationAdmin mConfigAdmin = null;

    @Reference(name="Backbone",
            cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE,
            bind="bindBackbone",
            unbind="unbindBackbone",
            policy= ReferencePolicy.DYNAMIC)
    private Backbone mBackbone;
    
    @Reference(name="NetworkManagerCore",
            cardinality = ReferenceCardinality.OPTIONAL_UNARY,
            bind="bindNMCore",
            unbind="unbindNMCore",
            policy= ReferencePolicy.DYNAMIC)
    private NetworkManagerCore nmCore;
    
    private Map<VirtualAddress, Backbone> activeRouteMap = new HashMap<VirtualAddress, Backbone>();

    /**
     * <VirtualAddress, <backboneName, endpoint> >
     */
    private Map<VirtualAddress, List<RouteEntry>> potentialRouteMap = new ConcurrentHashMap<VirtualAddress, List<RouteEntry>>();

    private Map<String, Backbone> availableBackbones = new ConcurrentHashMap<String, Backbone>();
   
    private static String BACKBONE_ROUTER = BackboneRouterImpl.class
            .getSimpleName();
    // private static String ROUTING_JXTA = "JXTA";
    BackboneRouterConfigurator configurator;
    Object backboneAddingLock = new Object();

    protected void bindConfigAdmin(ConfigurationAdmin configAdmin) {
    	logger.debug("BackboneRouter::binding configadmin");
        this.mConfigAdmin = configAdmin;
    }

    protected void unbindConfigAdmin(ConfigurationAdmin configAdmin) {
    	logger.debug("BackboneRouter::un-binding configadmin");
        this.mConfigAdmin = null;
    }

    protected void bindBackbone(Backbone backbone) {
    	logger.debug("BackboneRouter::binding backbone");
        mBackbone = backbone;

        if (addBackbone(mBackbone)) {
            movePotentialToActiveRoutes(mBackbone);
        }
    }
    
    protected void unbindBackbone(Backbone backbone) {
    	logger.debug("BackboneRouter::un-binding backbone");
        mBackbone = backbone;
        removeActiveRoutes(mBackbone);
        availableBackbones.values().remove(mBackbone);
    }
    
    protected void bindNMCore(NetworkManagerCore core) {
    	logger.debug("BackboneRouter::binding networkmanager-core");
        nmCore = core;
    }

    protected void unbindNMCore(NetworkManagerCore core) {
    	logger.debug("BackboneRouter::un-binding networkmanager-core");
        nmCore = null;
    }
    
    @Activate
    protected void activate(ComponentContext context) {
        logger.info("[activating " + BACKBONE_ROUTER + "]");

        this.context = context;

        //bindNMCore((NetworkManagerCore) context.locateService(NetworkManagerCore.class.getSimpleName()));

        configurator = new BackboneRouterConfigurator(this, context.getBundleContext(),mConfigAdmin);
//        configurator = new BackboneRouterConfigurator(this,
//                context.getBundleContext());
        configurator.registerConfiguration();
        logger.info(BACKBONE_ROUTER + " started");
    }
    
    @Deactivate
    protected void deactivate(ComponentContext context) {
        logger.info("de-activating " + BACKBONE_ROUTER);
    }

    private void movePotentialToActiveRoutes(Backbone backbone) {

        synchronized (backboneAddingLock) {
            List<VirtualAddress> movedVirtualAddressList = new ArrayList<VirtualAddress>();

            logger.debug("Moving potential to active routes for backbone "
                    + backbone.getName());

            if (!potentialRouteMap.isEmpty()) {
                Iterator<Entry<VirtualAddress, List<RouteEntry>>> iter = potentialRouteMap
                        .entrySet().iterator();

                while (iter.hasNext()) {
                    Map.Entry<VirtualAddress, List<RouteEntry>> entry = (Map.Entry<VirtualAddress, List<RouteEntry>>) iter
                            .next();

                    List<RouteEntry> routeEntryList = entry.getValue();

                    for (RouteEntry routeEntry : routeEntryList) {
                        String backboneName = routeEntry.getBackboneName();

                        if ((backbone.getName() != null)
                                && backbone.getName().contains(backboneName)) {

                            VirtualAddress virtualAddress = (VirtualAddress) entry
                                    .getKey();

                            String endpoint = routeEntry.getEndpoint();

                            if (endpoint != null) {
                                backbone.addEndpoint(virtualAddress, endpoint);
                            }

                            activeRouteMap.put(virtualAddress, backbone);

                            movedVirtualAddressList.add(virtualAddress);

                        }
                    }

                }

                // tell nmcore which services have new securityproperties
                if (backbone.getSecurityTypesRequired() != null) {
                    nmCore.updateSecurityProperties(movedVirtualAddressList,
                            backbone.getSecurityTypesRequired());
                    for (VirtualAddress virtualAddress : movedVirtualAddressList) {
                        potentialRouteMap.remove(virtualAddress);
                    }
                }

            }
        }

    }

    private void removeActiveRoutes(Backbone backbone) {

        synchronized (backboneAddingLock) {
            List<VirtualAddress> obsoleteVirtualAddressList = new ArrayList<VirtualAddress>();

            if (!activeRouteMap.isEmpty()) {
                logger.debug("Removing active routes reachable over unbound backbone "
                        + backbone.getName());

                Iterator<Entry<VirtualAddress, Backbone>> iter = activeRouteMap
                        .entrySet().iterator();

                while (iter.hasNext()) {
                    Map.Entry<VirtualAddress, Backbone> entry = (Map.Entry<VirtualAddress, Backbone>) iter
                            .next();

                    Backbone backboneRef = (Backbone) entry.getValue();

                    if (backboneRef.equals(backbone)) {

                        VirtualAddress virtualAddress = (VirtualAddress) entry
                                .getKey();

                        obsoleteVirtualAddressList.add(virtualAddress);

                    }

                }

                for (VirtualAddress virtualAddress : obsoleteVirtualAddressList) {
                    activeRouteMap.remove(virtualAddress);
                }
            }
        }
    }

    /**
     * Sends a message over the communication channel from which the service
     * came.
     *
     * @param senderVirtualAddress
     *            VirtualAddress of the sender
     * @param receiverVirtualAddress
     *            VirtualAddress of the receiver
     * @param data
     *            data to be sent
     * @return success status response of the network manager
     */
    @Override
    public NMResponse sendDataSynch(VirtualAddress senderVirtualAddress,
                                    VirtualAddress receiverVirtualAddress, byte[] data) {
        Backbone b = (Backbone) activeRouteMap.get(receiverVirtualAddress);
        if (b == null && receiverVirtualAddress != null
                && potentialRouteMap.get(receiverVirtualAddress) != null) {

            NMResponse nmResponse = new NMResponse(NMResponse.STATUS_ERROR);
            nmResponse
                    .setMessage("Currently the backbone that is assigned to this VirtualAddress is not available.");

            return nmResponse;
        } else if (b == null) {
            NMResponse nmResponse = new NMResponse(NMResponse.STATUS_ERROR);
            nmResponse.setMessage("Unknown how to reach VirtualAddress.");

            return nmResponse;
        }
        NMResponse fromBackbone = b.sendDataSynch(senderVirtualAddress, receiverVirtualAddress, data);
        logger.trace("status from backbone : "+fromBackbone.getStatus());
        return fromBackbone;
        //return b.sendDataSynch(senderVirtualAddress, receiverVirtualAddress,
        //                data);
    }

    /**
     * Sends a message over the communication channel from which the service
     * came.
     *
     * @param senderVirtualAddress
     *            VirtualAddress of the sender
     * @param receiverVirtualAddress
     *            VirtualAddress of the receiver
     * @param data
     *            data to be sent
     * @return success status response of the network manager
     */
    @Override
    public NMResponse sendDataAsynch(VirtualAddress senderVirtualAddress,
                                     VirtualAddress receiverVirtualAddress, byte[] data) {
        Backbone b = (Backbone) activeRouteMap.get(receiverVirtualAddress);
        if (b == null && receiverVirtualAddress != null
                && potentialRouteMap.get(receiverVirtualAddress) != null) {

            NMResponse nmResponse = new NMResponse(NMResponse.STATUS_ERROR);
            nmResponse
                    .setMessage("Currently the backbone that is assigned to this VirtualAddress is not available.");

            return nmResponse;
        } else if (b == null) {
            NMResponse nmResponse = new NMResponse(NMResponse.STATUS_ERROR);
            nmResponse.setMessage("Unknown how to reach VirtualAddress.");

            return nmResponse;
        }
        NMResponse fromBackbone = b.sendDataAsynch(senderVirtualAddress, receiverVirtualAddress, data);
        logger.trace("status from backbone : "+fromBackbone.getStatus());
        return fromBackbone;
    }

    /**
     * Receives a message which also specifies the communication channel used by
     * the sender. This will then update the list of services and which backbone
     * they use.
     *
     * @param senderVirtualAddress
     *            VirtualAddress of the sender
     * @param receiverVirtualAddress
     *            VirtualAddress of the receiver
     * @param data
     *            data to be sent
     * @param originatingBackbone
     *            which backbone is used
     * @return success status response of the network manager
     */
    @Override
    public NMResponse receiveDataSynch(VirtualAddress senderVirtualAddress,
                                       VirtualAddress receiverVirtualAddress, byte[] data,
                                       Backbone originatingBackbone) {

        return receiveData(senderVirtualAddress, receiverVirtualAddress, data,
                originatingBackbone, true);
    }

    /**
     * Receives a message which also specifies the communication channel used by
     * the sender. This will then update the list of services and which backbone
     * they use.
     *
     * @param senderVirtualAddress
     *            VirtualAddress of the sender
     * @param receiverVirtualAddress
     *            VirtualAddress of the receiver
     * @param data
     *            data to be sent
     * @param originatingBackbone
     *            which backbone is used
     * @return success status response of the network manager
     */
    @Override
    public NMResponse receiveDataAsynch(VirtualAddress senderVirtualAddress,
                                        VirtualAddress receiverVirtualAddress, byte[] data,
                                        Backbone originatingBackbone) {

        return receiveData(senderVirtualAddress, receiverVirtualAddress, data,
                originatingBackbone, false);
    }

    /**
     * Method to receive data as synchronous and asynchronous are handled the
     * same way
     *
     * @param senderVirtualAddress
     * @param receiverVirtualAddress
     * @param data
     * @param originatingBackbone
     * @param synch
     * @return
     */
    private NMResponse receiveData(VirtualAddress senderVirtualAddress,
                                   VirtualAddress receiverVirtualAddress, byte[] data,
                                   Backbone originatingBackbone, boolean synch) {

        Backbone b = (Backbone) activeRouteMap.get(senderVirtualAddress);
        if (b == null) {
            activeRouteMap.put(senderVirtualAddress, originatingBackbone);
        }

        // TODO #NM refactoring check case when there is no core what to do
        // Has to be considered as future feature for relay nodes
        if (nmCore != null) {
            if (synch) {
                return nmCore.receiveDataSynch(senderVirtualAddress,
                        receiverVirtualAddress, data);
            } else {
                return nmCore.receiveDataAsynch(senderVirtualAddress,
                        receiverVirtualAddress, data);
            }
        } else {
            return new NMResponse(NMResponse.STATUS_ERROR);
        }
    }

    /**
     * This function is called when the configuration is updated from the web
     * page.
     *
     * @param updates
     *            updated configuration data
     */
    @Override
    @SuppressWarnings("rawtypes")
    public void applyConfigurations(Hashtable updates) {
        if (updates.containsKey(BackboneRouterConfigurator.COMMUNICATION_TYPE)) {
            logger.info("default route: "
                    + (String) configurator
                    .get(BackboneRouterConfigurator.COMMUNICATION_TYPE));
        }
    }

    // Ruft jeden Backbone auf, der gefunden wird, um Nachricht zu broadcasten
    /**
     * Broadcasts a message over the all communication channel.
     *
     * @param senderVirtualAddress
     *            VirtualAddress of the sender
     * @param data
     *            data to be sent
     * @return success if data could be delivered on at least one backbone.
     *         error if no backbone was successful
     */
    @Override
    public NMResponse broadcastData(VirtualAddress senderVirtualAddress,
                                    byte[] data) {
        boolean success = false;
        for (Backbone bb : availableBackbones.values()) {
            logger.debug("BBRouter broadcastData (from " + senderVirtualAddress
                    + ") over Backbone: " + bb.getClass().getName());
            NMResponse response = bb.broadcastData(senderVirtualAddress, data);
            if (response != null
                    && response.getStatus() == NMResponse.STATUS_SUCCESS) {
                success = true;
            }
        }

        if (success)
            return new NMResponse(NMResponse.STATUS_SUCCESS);
        else
            return new NMResponse(NMResponse.STATUS_ERROR);
    }

    /**
     * This function returns information about the backbone of the
     * VirtualAddress The return format is BackboneType:BackboneAddresse
     * Example: BackboneSOAP:http://202.12.11.11/axis/services
     *
     * @param virtualAddress
     *            VirtualAddress of the node to request the route from
     * @return BackboneType:BackboneAddresse
     */
    @Override
    public String getRoute(VirtualAddress virtualAddress) {
        Backbone b = activeRouteMap.get(virtualAddress);
        if (b == null) {
            return null;
        } else {
            return b.getName().concat(":")
                    .concat(b.getEndpoint(virtualAddress));
        }
    }

    @Override
    public String getRouteBackbone(VirtualAddress virtualAddress) {
        Backbone b = activeRouteMap.get(virtualAddress);
        if (b == null) {
            return null;
        } else {
            return b.getName();
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * eu.linksmart.network.routing.BackboneRouter#addRoute(eu.linksmart.network
      * .VirtualAddress, java.lang.String)
      */
    @Override
    public boolean addRoute(VirtualAddress virtualAddress, String backboneName) {
        return processAddingRoute(virtualAddress, backboneName, null);

    }

    private boolean processAddingRoute(VirtualAddress virtualAddress,
                                       String backboneName, String endpoint) {

        synchronized (backboneAddingLock) {

            if (backboneName == null || backboneName.isEmpty()) {
                return false;
            }

            Backbone backbone = availableBackbones.get(backboneName);

            if (backbone == null) {

                // Put virtual address in potential route map

                if (potentialRouteMap.containsKey(virtualAddress)) {
                    return false;
                } else {

                    // TODO LinkSmart Developer In future, if more than one
                    // backbone can be assigned to VirtualAddress,
                    // first check if map is available, and then add backbone
                    // and endpoint.
                    RouteEntry routeEntry = new RouteEntry(backboneName,
                            endpoint);

                    List<RouteEntry> routeEntryList = new ArrayList<RouteEntry>();

                    routeEntryList.add(routeEntry);

                    potentialRouteMap.put(virtualAddress, routeEntryList);

                    return true;
                }

            } else {

                // Assign a backbone to the service route

                if (activeRouteMap.containsKey(virtualAddress)) {
                    return false;
                } else {

                    if (endpoint != null) {
                        if (!backbone.addEndpoint(virtualAddress, endpoint)) {
                            return false;
                        }
                    }

                    activeRouteMap.put(virtualAddress, backbone);

                    return true;
                }

            }
        }

    }

    @Override
    public void addRouteForRemoteService(VirtualAddress senderVirtualAddress,
                                         VirtualAddress remoteVirtualAddress) {
        Backbone senderBackbone = activeRouteMap.get(senderVirtualAddress);
        if (senderBackbone != null) {
            addRoute(remoteVirtualAddress, senderBackbone.getName());
            senderBackbone.addEndpointForRemoteService(senderVirtualAddress,
                    remoteVirtualAddress);
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * eu.linksmart.network.routing.BackboneRouter#addRouteToBackbone(eu.linksmart
      * .network.VirtualAddress, java.lang.String, java.lang.String)
      */
    @Override
    public boolean addRouteToBackbone(VirtualAddress virtualAddress,
                                      String backboneName, String endpoint) {

        if (virtualAddress == null) {
            return false;
        }

        if (endpoint == null || endpoint.isEmpty()) {
            return false;
        }

        return processAddingRoute(virtualAddress, backboneName, endpoint);
    }

    @Override
    public boolean removeRoute(VirtualAddress virtualAddress, String backbone) {
        synchronized (backboneAddingLock) {

            if (potentialRouteMap.get(virtualAddress) != null) {
                return (potentialRouteMap.remove(virtualAddress) != null);
            }

            // if backbone is null remove all routes
            if (backbone == null) {
                return (activeRouteMap.remove(virtualAddress) != null);
            }
            if (activeRouteMap.get(virtualAddress) == null
                    || !activeRouteMap.get(virtualAddress).getClass().getName()
                    .equals(backbone)) {
                return false;
            }
            activeRouteMap.remove(virtualAddress);
        }
        return true;
    }

    /**
     * Returns a list of communication channels available to the network
     * manager.
     *
     * @return list of communication channels
     */
    @Override
    public List<String> getAvailableBackbones() {
        return new ArrayList<String>(availableBackbones.keySet());
    }

    /**
     * Adds a new backbone to the list of available backbones, if a backbone
     * with this name does not exist already.
     *
     * @param backbone
     *            the Backbone to add
     * @return whether the Backbone was added
     */
    private boolean addBackbone(Backbone backbone) {

        if (backbone == null) {
            return false;
        }

        if (availableBackbones.containsValue(backbone)) {
            return false;
        }
        availableBackbones.put(backbone.getName(), backbone);
        logger.info("Added backbone to router: " + backbone.getName());
        return true;
    }

    /**
     * Returns list of security properties required via a given backbone.
     *
     * @param backbone
     *            A string with the (class)name of the backbone we are
     *            interested in.
     * @return a list of security parameters configured for that backbone. See
     *         the backbone's parameters file and/or the configuraton interface
     *         for more details ,null if backbone not available yet
     */
    public List<SecurityProperty> getBackboneSecurityProperties(String backbone) {
        if (!availableBackbones.containsKey(backbone)) {
            return null;
        } else {
            Backbone b = availableBackbones.get(backbone);
            return b.getSecurityTypesRequired();
        }
    }

    @Override
    public Map<VirtualAddress, Backbone> getCopyOfActiveRouteMap() {
        HashMap<VirtualAddress, Backbone> copiedMap = new HashMap<VirtualAddress, Backbone>();
        copiedMap.putAll(activeRouteMap);
        return copiedMap;
    }

    @Override
    public Map<VirtualAddress, List<RouteEntry>> getCopyOfPotentialRouteMap() {
        HashMap<VirtualAddress, List<RouteEntry>> copiedMap = new HashMap<VirtualAddress, List<RouteEntry>>();
        copiedMap.putAll(potentialRouteMap);
        return copiedMap;
    }

}
