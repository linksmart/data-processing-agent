package eu.linksmart.sdk.catalog.service;

import eu.linksmart.sdk.catalog.AggregatedResponseBase;

import java.util.List;

/**
 * Created by José Ángel Carvajal on 13.10.2017 a researcher of Fraunhofer FIT.
 */
public class ServicesResponseImpl extends AggregatedResponseBase implements ServiceRegistrations {
    List<Registration> services;

    public List<Registration> getServices() {
        return services;
    }

    public void setServices(List<Registration> services) {
        this.services = services;
    }
}
