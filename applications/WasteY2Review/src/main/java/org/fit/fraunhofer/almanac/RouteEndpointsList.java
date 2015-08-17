package org.fit.fraunhofer.almanac;

import java.util.ArrayList;

/**
 * Created by Werner-Kytölä on 27.05.2015.
 */
public class RouteEndpointsList {
    ArrayList<RouteEndpoint> routeEndpointsList;

    public RouteEndpointsList() {
        routeEndpointsList = new ArrayList();
    }

    public boolean add(RouteEndpoint routeEndpoint){
        return routeEndpointsList.add(routeEndpoint);
    }

    public boolean  remove(RouteEndpoint routeEndpoint){
        return routeEndpointsList.remove(routeEndpoint);
    }
}
