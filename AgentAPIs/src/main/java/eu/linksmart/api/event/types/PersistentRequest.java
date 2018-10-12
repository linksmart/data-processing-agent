package eu.linksmart.api.event.types;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by José Ángel Carvajal on 30.10.2017 a researcher of Fraunhofer FIT.
 */
public interface PersistentRequest {
    @JsonProperty("persistent")
    @ApiModelProperty(notes = "Indicates if the request should be stored persistently")
    boolean isPersistent();
    @JsonProperty("essential")
    @ApiModelProperty(notes = "Indicates if the request fails to be loaded the service should be crash or not. Note: if the agent is not configured to crash, it will not crash regardless of this setting")
    boolean isEssential();

    @JsonProperty("persistent")
    @ApiModelProperty(notes = "Indicates if the request should be stored persistently")
    void isPersistent(boolean isPersistent);
    @JsonProperty("essential")
    @ApiModelProperty(notes = "Indicates if the request fails to be loaded the service should be crash or not. Note: if the agent is not configured to crash, it will not crash regardless of this setting")
    void isEssential(boolean isEssential);
    /***
     * Returns the hash ID of the statement. By default this is the SHA256 of the statement.
     *
     * @return  The ID as string.
     * */
    String getId();
    /***
     * setts the hash ID of the statement. By default this is the SHA256 of the statement.
     *
     * @param id as string.
     * */
    void setId(String id);
}
