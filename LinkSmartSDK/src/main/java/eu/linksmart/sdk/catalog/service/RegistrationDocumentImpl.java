package eu.linksmart.sdk.catalog.service;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 13.10.2017 a researcher of Fraunhofer FIT.
 */
public class RegistrationDocumentImpl implements Registration {
    protected String id, description;
    protected Map<String, Object> meta =new Hashtable<>();
    protected List<APIDescriptor> apis = new ArrayList<>();
    protected List<APIDoc> externalDocs = new ArrayList<>();

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Map<String, Object> getMeta() {
        return meta;
    }

    @Override
    public void setMeta(Map<String, Object> meta) {
        this.meta = meta;
    }

    @Override
    public void addMeta(String key, Object meta) {
        this.meta.put(key,meta);
    }

    @Override
    public List<APIDescriptor> getApis() {
        return apis;
    }

    @Override
    public void setApis(List<APIDescriptor> apis) {
        this.apis = apis;
    }

    @Override
    public void addApis(APIDescriptor api) {
        apis.add(api);
    }

    @Override
    public List<APIDoc> getExternalDocs() {
        return externalDocs;
    }

    @Override
    public void setExternalDocs(List<APIDoc> externalDocs) {
        this.externalDocs = externalDocs;
    }

    @Override
    public void addExternalDocs(APIDoc externalDoc) {
        externalDocs.add(externalDoc);
    }


}
