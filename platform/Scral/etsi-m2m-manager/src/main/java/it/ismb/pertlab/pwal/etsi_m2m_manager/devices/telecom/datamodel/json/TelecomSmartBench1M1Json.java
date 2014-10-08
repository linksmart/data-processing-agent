package it.ismb.pertlab.pwal.etsi_m2m_manager.devices.telecom.datamodel.json;

public class TelecomSmartBench1M1Json
{
    private String starttime;
    private String suspended;
    private String transits;
    private String sittings;

    public String getStarttime()
    {
        return starttime;
    }

    public void setStarttime(String starttime)
    {
        this.starttime = starttime;
    }

    public String getSuspended()
    {
        return suspended;
    }

    public void setSuspended(String suspended)
    {
        this.suspended = suspended;
    }

    public String getTransits()
    {
        return transits;
    }

    public void setTransits(String transit)
    {
        this.transits = transit;
    }

    public String getSittings()
    {
        return sittings;
    }

    public void setSittings(String sitting)
    {
        this.sittings = sitting;
    }
}
