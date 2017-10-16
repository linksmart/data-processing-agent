package eu.linksmart.sdk.catalog.service;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by José Ángel Carvajal on 13.10.2017 a researcher of Fraunhofer FIT.
 */

@JsonDeserialize(as = APIDescriptorImpl.class)
@JsonSerialize(as = APIDescriptorImpl.class)
public interface APIDescriptor {
    String getProtocol();

    void setProtocol(String protocol);

    String getUrl();

    void setUrl(String url);
}
