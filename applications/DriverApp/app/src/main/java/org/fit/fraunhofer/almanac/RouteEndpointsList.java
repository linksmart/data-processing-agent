package org.fit.fraunhofer.almanac;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by Werner-Kytlä on 27.05.2015.
 */
public class RouteEndpointsList {
    ArrayList<RouteEndpoint> routeEndpointsList;

    public RouteEndpointsList() {
        this.routeEndpointsList = new ArrayList();
    }

    public RouteEndpointsList(ArrayList<RouteEndpoint> routeEndpointsList) {
        this.routeEndpointsList = new ArrayList();
        this.routeEndpointsList.addAll(routeEndpointsList) ;
    }

    public RouteEndpointsList(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream  out = new ObjectInputStream(in);
        routeEndpointsList = (ArrayList<RouteEndpoint>) out.readObject();
    }

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(routeEndpointsList);
        return out.toByteArray();
    }

    public boolean add(RouteEndpoint routeEndpoint){
        return routeEndpointsList.add(routeEndpoint);
    }

    public boolean  remove(RouteEndpoint routeEndpoint){
        return routeEndpointsList.remove(routeEndpoint);
    }
    public String getString(){
        StringBuilder routes = new StringBuilder();
        for(RouteEndpoint r: routeEndpointsList){
            routes.append(r.getString());
        }
        return  routes.toString();
    }

    public ArrayList<RouteEndpoint> getRouteList() {
        return routeEndpointsList;
    }
}