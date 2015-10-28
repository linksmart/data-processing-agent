package it.ismb.pertlab.pwal.manager.serial.device.payload;

public class FlowMeterFitJson
{
    private String type;
    private String flow;
    
    public String getType()
    {
        return type;
    }
    public void setType(String type)
    {
        this.type = type;
    }
    public String getFlow()
    {
        return flow;
    }
    public void setFlow(String flow)
    {
        this.flow = flow;
    }
    @Override
    public String toString()
    {
        return "FlowMeterFitJson [type=" + type + ", flow=" + flow + "]";
    }
}
