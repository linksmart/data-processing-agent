package it.ismb.pertlab.pwal.api.devices.model;


import it.ismb.pertlab.pwal.api.devices.interfaces.ControllableDevice;
import it.ismb.pertlab.pwal.api.utils.SemanticModel;

import com.fasterxml.jackson.annotation.JsonProperty;


@SemanticModel(name="class",value="http://almanac-project.eu/ontologies/smartcity.owl#ColorDimmableLight")
public interface PhilipsHue extends ControllableDevice
{
    //up luminosity
    void stepUp();
    //down luminosity
    void stepDown();
    //turn on 
    void turnOn();
    //turn off
    void turnOff();
    //retrieve if the lamp is on or off
    @JsonProperty("isOn")
    Boolean isOn();
    //get lamp color
    String getRGBColor();
    //set lamp color
    void setRGBColor(int R, int G, int B);
    //set brightness
    void setBrightness(Integer brightness);
    //get brightness
    Integer getBrightness();
    //set hue
    void setHue(Integer hue);
    //get hue 
    Integer getHue();
    //set color saturation
    void setSaturation(Integer saturation);
    //get saturation
    Integer getSaturation();
}
