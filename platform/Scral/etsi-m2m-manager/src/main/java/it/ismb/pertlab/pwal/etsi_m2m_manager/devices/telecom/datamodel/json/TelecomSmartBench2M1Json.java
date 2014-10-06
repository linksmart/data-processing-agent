package it.ismb.pertlab.pwal.etsi_m2m_manager.devices.telecom.datamodel.json;

import it.ismb.pertlab.pwal.estsi_m2m_manager.devices.telecom.base.TelecomBaseJson;

public class TelecomSmartBench2M1Json extends TelecomBaseJson
{

    private String temperature;
    private String humidity;
    private String pressure;
    private String transits;
    private String sittings;
    private String pollution;
    private String light;

    public String getTemperature()
    {
        return temperature;
    }

    public void setTemperature(String temperature)
    {
        this.temperature = temperature;
    }

    public String getHumidity()
    {
        return humidity;
    }

    public void setHumidity(String humidity)
    {
        this.humidity = humidity;
    }

    public String getPressure()
    {
        return pressure;
    }

    public void setPressure(String pressure)
    {
        this.pressure = pressure;
    }

    public String getTransits()
    {
        return transits;
    }

    public void setTransits(String transits)
    {
        this.transits = transits;
    }

    public String getSittings()
    {
        return sittings;
    }

    public void setSittings(String sittings)
    {
        this.sittings = sittings;
    }

    public String getPollution()
    {
        return pollution;
    }

    public void setPollution(String pollution)
    {
        this.pollution = pollution;
    }

    public String getLight()
    {
        return light;
    }

    public void setLight(String light)
    {
        this.light = light;
    }
}
