package eu.linksmart.sdk.catalog.service;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by José Ángel Carvajal on 13.10.2017 a researcher of Fraunhofer FIT.
 */

@JsonDeserialize(as = APIDocImpl.class)
@JsonSerialize(as = APIDocImpl.class)
public interface APIDoc {
    String getDescription();

    void setDescription(String description);

    String getUrl();

    void setUrl(String url);
}
