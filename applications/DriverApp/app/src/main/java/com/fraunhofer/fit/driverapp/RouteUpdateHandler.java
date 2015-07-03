package com.fraunhofer.fit.driverapp;

import org.fit.fraunhofer.almanac.RouteEndpoint;
import org.fit.fraunhofer.almanac.RouteEndpointsList;

import java.util.ArrayList;

/**
 * Created by devasya on 28.05.2015.
 */
public interface RouteUpdateHandler {
    public void handleUpdateNodeList(ArrayList<RouteEndpoint> routeEndpointsList);
    public void handleInitNodeList(ArrayList<RouteEndpoint> routeEndpointsList);
}
