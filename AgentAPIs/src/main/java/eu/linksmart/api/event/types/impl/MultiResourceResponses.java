package eu.linksmart.api.event.types.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.linksmart.api.event.types.HTTPResponses;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 15.07.2016 a researcher of Fraunhofer FIT.
 */
public  class MultiResourceResponses<ResourceObject> implements HTTPResponses<Map<String,ResourceObject>> {
    @JsonIgnore
    Collection<GeneralRequestResponse> generalRequestResponses = new ArrayList<>();
    @JsonIgnore
    Map<String,ResourceObject> resources= new Hashtable<>();
    @JsonIgnore
    int  overallStatus = 0;

    boolean containsSuccess = false;

    public MultiResourceResponses(Collection<GeneralRequestResponse> arrayList, Map<String, ResourceObject> resources) {
        this.generalRequestResponses = arrayList;
        this.resources = resources;
    }
    public MultiResourceResponses() {
    }
    @Override
    public void setResponses(Collection<GeneralRequestResponse> generalRequestResponses) {
        this.generalRequestResponses = generalRequestResponses;
    }
    public void addAllResponses(Collection<GeneralRequestResponse> generalRequestResponses) {
        generalRequestResponses.forEach(this::addResponse);
    }
    @Override
    public void addResponse(GeneralRequestResponse generalRequestResponse) {
        generalRequestResponses.add(generalRequestResponse);
        if(!generalRequestResponses.isEmpty()&&overallStatus< generalRequestResponse.getStatus())
            overallStatus = generalRequestResponse.getStatus();
        else if(generalRequestResponses.isEmpty()){
            overallStatus = generalRequestResponse.getStatus();
        }
        if(generalRequestResponse.getStatus()>=200 && generalRequestResponse.getStatus()<300)
            containsSuccess = true;


    }

    @Override
    @JsonProperty
    public Collection<GeneralRequestResponse> getResponses() {
        return generalRequestResponses;
    }
    @Override
    @JsonIgnore
    public GeneralRequestResponse getResponsesTail() {
        return ((ArrayList<GeneralRequestResponse>)generalRequestResponses).get(generalRequestResponses.size()-1);
    }
    @Override
    @JsonProperty
    public int getOverallStatus() {
        return overallStatus;
    }

    @Override
    @JsonProperty
    public Map<String, ResourceObject> getResources() {
        return resources;
    }
    @JsonIgnore
    public  ResourceObject getHeadResource() {
        return resources.values().iterator().next();
    }
    @JsonIgnore
    public  String getHeadResourceKey() {
        return resources.keySet().iterator().next();
    }
    @Override
    public void setResources(Map<String, ResourceObject> resources) {
        this.resources = resources;
    }
    public void addResources(String key, ResourceObject resource) {
        if (resources== null)
            this.resources = new Hashtable<>();
        resources.put(key, resource);
    }
    @JsonIgnore
    public boolean containsSuccess() {
        return containsSuccess;
    }



}
