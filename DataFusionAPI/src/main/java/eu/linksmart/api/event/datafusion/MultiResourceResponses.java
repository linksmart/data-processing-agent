package eu.linksmart.api.event.datafusion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 15.07.2016 a researcher of Fraunhofer FIT.
 */
public  class MultiResourceResponses<ResourceObject> implements HTTPResponses<Map<String,ResourceObject>>{
    Collection<StatementResponse> statementResponses = new ArrayList<>();
    Map<String,ResourceObject> resources= new Hashtable<>();
    int overallStatus = 0;

    boolean containsSuccess = false;

    public MultiResourceResponses(Collection<StatementResponse> arrayList, Map<String, ResourceObject> resources) {
        this.statementResponses = arrayList;
        this.resources = resources;
    }
    public MultiResourceResponses() {
    }
    @Override
    public Collection<StatementResponse> getResponses() {
        return statementResponses;
    }
    @Override
    public void setResponses(Collection<StatementResponse> statementResponses) {
        this.statementResponses = statementResponses;
    }
    public void addAllResponses(Collection<StatementResponse> statementResponses) {
        statementResponses.forEach(this::addResponse);
    }
    @Override
    public void addResponse(StatementResponse statementResponse) {
        statementResponses.add(statementResponse);
        if(!statementResponses.isEmpty()&&overallStatus<statementResponse.getStatus())
            overallStatus = statementResponse.getStatus();
        else if(statementResponses.isEmpty()){
            overallStatus = statementResponse.getStatus();
        }
        if(statementResponse.getStatus()>=200 &&statementResponse.getStatus()<300)
            containsSuccess = true;


    }

    @Override
    public int getOverallStatus() {
        return overallStatus;
    }

    @Override
    public Map<String, ResourceObject> getResources() {
        return resources;
    }
    public  ResourceObject getHeadResource() {
        return resources.values().iterator().next();
    }

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

    public boolean containsSuccess() {
        return containsSuccess;
    }



}
