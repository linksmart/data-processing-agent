package eu.linksmart.sdk.catalog.service;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 * Created by José Ángel Carvajal on 13.10.2017 a researcher of Fraunhofer FIT.
 */
@JsonDeserialize(as = ServicesResponseImpl.class)
@JsonSerialize(as = ServicesResponseImpl.class)
public interface ServiceRegistrations {
    String getDescription();

    void setDescription(String description);

    int getPage();

    void setPage(int page);

    int getPer_page();

    void setPer_page(int per_page);

    int getTotal();

    void setTotal(int total);

    List<Registration> getServices();

    void setServices(List<Registration> services);
}
