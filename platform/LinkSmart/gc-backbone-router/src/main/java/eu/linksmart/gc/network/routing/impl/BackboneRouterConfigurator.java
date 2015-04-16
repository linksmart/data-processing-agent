package eu.linksmart.gc.network.routing.impl;
/*
 * In case of German law being applicable to this license agreement, the following warranty and liability terms shall apply:
 *
 * 1. Licensor shall be liable for any damages caused by wilful intent or malicious concealment of defects.
 * 2. Licensor's liability for gross negligence is limited to foreseeable, contractually typical damages.
 * 3. Licensor shall not be liable for damages caused by slight negligence, except in cases
 *    of violation of essential contractual obligations (cardinal obligations). Licensee's claims for
 *    such damages shall be statute barred within 12 months subsequent to the delivery of the software.
 * 4. As the Software is licensed on a royalty free basis, any liability of the Licensor for indirect damages
 *    and consequential damages - except in cases of intent - is excluded.
 *
 * This limitation of liability shall also apply if this license agreement shall be subject to law
 * stipulating liability clauses corresponding to German law.
 */
/**
 * Copyright (C) 2006-2010 [Telefonica I+D]
 *                         the HYDRA consortium, EU project IST-2005-034891
 *
 * This file is part of LinkSmart.
 *
 * LinkSmart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE
 * version 3 as published by the Free Software Foundation.
 *
 * LinkSmart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with LinkSmart.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Configuration parameters of the Network Manager
 */


import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;

import eu.linksmart.gc.api.network.routing.BackboneRouter;
import eu.linksmart.gc.api.utils.Configurator;
import org.osgi.service.cm.ConfigurationAdmin;


public class BackboneRouterConfigurator extends Configurator {

    /* Configuration PID & file path. */
    public static String CONFIGURATION_FILE = "/resources/BackboneRouter.properties";

    public static final String COMMUNICATION_TYPE = "Network.CommunicationType";

    private static String BACKBONE_ROUTER_PID = "eu.linksmart.backbone.router";

    private BackboneRouter backboneRouter;

    /**
     * Constructor. Creates a new "NetworkManagerConfigurator" object
     *
     * @param _backboneRouter is one of the Backbone Router implementation
     * @param context the bundle's execution context
     */
    public BackboneRouterConfigurator(BackboneRouter _backboneRouter,
                                      BundleContext context) {

        super(context, Logger.getLogger(BackboneRouterConfigurator.class.getName()),
                BACKBONE_ROUTER_PID, CONFIGURATION_FILE);
        this.backboneRouter = _backboneRouter;
    }

    /**
     * Constructor. Creates a new "NetworkManagerConfigurator" object
     *
     * @param _backboneRouter is one of the Backbone Router implementation
     * @param context the bundle's execution context
     * @param configAdmin configAdmin reference for proper setup
     */
    public BackboneRouterConfigurator(BackboneRouter _backboneRouter,
                                      BundleContext context, ConfigurationAdmin configAdmin) {

        super(context, Logger.getLogger(BackboneRouterConfigurator.class.getName()),
                BACKBONE_ROUTER_PID, CONFIGURATION_FILE,configAdmin);
        super.init();
        this.backboneRouter = _backboneRouter;
    }


    /**
     * Apply the configuration changes
     *
     * @param updates the configuration changes
     */
    @Override
    public void applyConfigurations(Hashtable updates) {
        if (backboneRouter != null) {
            this.backboneRouter.applyConfigurations(updates);
        }
    }

}
