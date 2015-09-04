package it.ismb.pertlab.pwal.event.format.linksmart.cnet.jaxb;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EventModified
{
    @JsonProperty(value = "About")
    private String about;
    @JsonProperty(value = "Properties")
    private List<IoTProperty> property;
    
    
    public String getAbout()
    {
        return about;
    }
    public void setAbout(String about)
    {
        this.about = about;
    }
    public List<IoTProperty> getProperty()
    {
        if(this.property == null)
            this.property = new ArrayList<>();
        return property;
    }
    public void setProperty(List<IoTProperty> property)
    {
        this.property = property;
    }
    
    
}
