package eu.linksmart.network.routing.impl;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;

import eu.linksmart.gc.api.network.routing.BackboneRouter;
import eu.linksmart.gc.api.utils.Configurator;
import org.osgi.service.cm.ConfigurationAdmin;

public class BackboneRouterConfigurator extends Configurator {

    public static String CONFIGURATION_FILE = "/resources/BackboneRouter.properties";

    public static final String COMMUNICATION_TYPE = "Network.CommunicationType";

    private static String BACKBONE_ROUTER_PID = "eu.linksmart.backbone.router";

    private BackboneRouter backboneRouter;

    public BackboneRouterConfigurator(BackboneRouter _backboneRouter, BundleContext context, ConfigurationAdmin configAdmin) {
        super(context, Logger.getLogger(BackboneRouterConfigurator.class.getName()), BACKBONE_ROUTER_PID, CONFIGURATION_FILE,configAdmin);
        super.init();
        this.backboneRouter = _backboneRouter;
    }

    @Override
    public void applyConfigurations(Hashtable updates) {
        if (backboneRouter != null) {
            this.backboneRouter.applyConfigurations(updates);
        }
    }
}
