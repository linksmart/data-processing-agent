package eu.linksmart.api.event.types.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

/**
 * Created by José Ángel Carvajal on 11.12.2017 a researcher of Fraunhofer FIT.
 */
public class AsyncRequest {
    @JsonProperty
    final private String id = UUID.randomUUID().toString();
    @JsonProperty
    private byte[] resource;
    @JsonProperty
    private Map<String,Object> properties;
    @JsonProperty
    private Type requestType= Type.MQTT;
    @JsonProperty
    private String returnEndpoint;
    @JsonProperty
    private String returnErrorEndpoint;
    @JsonProperty
    private List<String> targets;

    public String getId() {
        return id;
    }

    public byte[] getResource() {
        return resource;
    }

    public Type getRequestType() {
        return requestType;
    }

    public String getReturnEndpoint() {
        return returnEndpoint;
    }

    public void setResource(byte[] resource) {
        this.resource = resource;
    }

    public void setRequestType(Type requestType) {
        this.requestType = requestType;
    }

    public void setReturnEndpoint(String returnEndpoint) {
        this.returnEndpoint = returnEndpoint;
    }

    public enum Type{
        MQTT
    }

    public String getReturnErrorEndpoint() {
        return returnErrorEndpoint;
    }

    public void setReturnErrorEndpoint(String returnErrorEndpoint) {
        this.returnErrorEndpoint = returnErrorEndpoint;
    }

    public List<String> getTargets() {
        return targets;
    }

    public void setTargets(List<String> targets) {
        this.targets = targets;
    }
    public void addTarget(String value) {
        if( targets == null)
            targets = new ArrayList<>();

        targets.add(value);

    }

    public void addProperty(String key, Object property) {
        if( properties == null)
            properties = new HashMap<>();

        properties.put(key,property);

    }
    public Object getProperty(String key) {
        return properties.getOrDefault(key,null);

    }
    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

}
