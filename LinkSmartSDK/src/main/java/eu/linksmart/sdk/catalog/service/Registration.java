package eu.linksmart.sdk.catalog.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 13.10.2017 a researcher of Fraunhofer FIT.
 */

@JsonDeserialize(as = RegistrationDocumentImpl.class)
@JsonSerialize(as = RegistrationDocumentImpl.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface Registration {
    String getId();

    void setId(String id);

    String getDescription();

    void setDescription(String description);

    Map<String, Object> getMeta();

    void setMeta(Map<String, Object> meta);

    void addMeta(String key, Object meta);

    List<APIDescriptor> getApis();

    void setApis(List<APIDescriptor> apis);

    void addApis(APIDescriptor api);

    List<APIDoc> getExternalDocs();

    void setExternalDocs(List<APIDoc> externalDocs);

    void addExternalDocs(APIDoc externalDoc);
}
