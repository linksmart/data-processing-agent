package eu.linksmart.services.payloads.ogc.sensorthing.base;

import com.fasterxml.jackson.annotation.*;
import eu.linksmart.services.payloads.ogc.sensorthing.CommonControlInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

/**
 * Created by José Ángel Carvajal on 01.04.2016 a researcher of Fraunhofer FIT.
 *
 * This abstract class describe de basic values that all classes of OGC SensorThing 1.0 must contain.
 * Therefore, all classes must inherit from this class or from a derivation of this class.
 */
//@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@iot.id")
public abstract class CommonControlInfoImpl implements CommonControlInfo {
    /**
     * id is the system-generated identifier of an entity.
     * id is unique among the entities of the same entity type in a SensorThings service
     */

    @JsonProperty("@iot.id")
    protected Object id;
  //  /** selfLink is the absolute URL of an entity that is unique among all other entities. */
  //  @JsonPropertyDescription("id is the system-generated identifier of an entity.")
  //  @JsonProperty(value = "@iot.selfLink")
  //  protected String selfLink;

    @JsonIgnore
    protected String baseURL = "http://linksmart.eu/v1.0/";

    /**
     * Empty constructor
     */
    public CommonControlInfoImpl()
    {
        if(id== null || id.equals(""))
            id = UUID.randomUUID().toString();
    }
    /** selfLink is the absolute URL of an entity that is unique among all other entities. */
    @Override
    @JsonPropertyDescription("id is the system-generated identifier of an entity.")
    @JsonProperty(value = "@iot.selfLink")
    public String getSelfLink() {

        return generateSelfLink();
    }
    /** selfLink is the absolute URL of an entity that is unique among all other entities. */
    @Override
    @JsonPropertyDescription("id is the system-generated identifier of an entity.")
    @JsonProperty(value = "@iot.selfLink")
    public void setSelfLink(String selfLink) {

        String[] strings= selfLink.split("//");


        baseURL=strings[0]+"//";
        strings= strings[1].split("/");
        int n=1;
        if(strings[strings.length-1].equals(""))
            n=2;
        for (int i=0; i< strings.length -n; i++){

            if(!strings[i].equals(""))
                baseURL+=strings[i];

             baseURL+="/";



        }
        if(strings[strings.length-1].contains(")")){
            String[] parts=strings[strings.length-1].split("\\(");
            for (String part : parts){
                if(part.contains(")")) {
                    String tmp = part.split("\\)")[0];
                    if(StringUtils.isNumeric(tmp)){
                        id = Long.valueOf(tmp);
                    }else
                        id =tmp;
                }
            }
        }
    }

    /**
     * Provides back the ID of the specific model entry instance, as a String
     *
     * @return the id
     */
    @Override
    @JsonPropertyDescription("id is the system-generated identifier of an entity.")
   // @JsonProperty(value = "@iot.id")
    public Object getId()
    {
        return id;
    }

    /**
     * Sets the ID of the specific model entry instance, as a String
     *
     * @param id
     *            the id to set
     */
    @Override
    @JsonPropertyDescription("id is the system-generated identifier of an entity.")
    @JsonSetter(value = "@iot.id")
    public void setId(Object id)
    {
        this.id = id;

    }
    private String generateSelfLink(){

        return baseURL+this.getClass().getSimpleName()+"("+id+")";


    }
    @Override
    public int hashCode(){
        return id.hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof CommonControlInfoImpl && ((CommonControlInfoImpl) obj).id.equals(id);
    }

    @Override
    public String toString(){
        if(id!=null)
            return "ID: "+id;
        return this.getClass().getCanonicalName();
    }

}
