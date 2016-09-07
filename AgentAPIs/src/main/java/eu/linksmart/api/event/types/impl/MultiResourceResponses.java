package eu.linksmart.api.event.types.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.HTTPResponses;
import eu.linksmart.api.event.types.JsonSerializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
/**
 *  Copyright [2013] [Fraunhofer-Gesellschaft]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */
/**
 * Basic and reference implementation of a MultiResourceResponses.
 * This response should be use in case an functionality of the API consist of several steps and each step
 * may return a separate response independent from the others.
 *
 * @author Jose Angel Carvajal Soto
 * @since       1.2.0
 * @see eu.linksmart.api.event.types.Statement
 * @see eu.linksmart.api.event.types.JsonSerializable
 *
 * */
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


    @Override
    public JsonSerializable build() throws TraceableException, UntraceableException {
        return this;
    }

    @Override
    public void destroy() throws Exception {
        ///nothing
    }
}
