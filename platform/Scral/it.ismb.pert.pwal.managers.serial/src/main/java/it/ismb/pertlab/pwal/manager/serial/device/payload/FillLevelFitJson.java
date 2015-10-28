package it.ismb.pertlab.pwal.manager.serial.device.payload;

public class FillLevelFitJson
{
    private String type;
    private String depth;
    private String level;
    
    public String getType()
    {
        return type;
    }
    public void setType(String type)
    {
        this.type = type;
    }
    public String getDepth()
    {
        return depth;
    }
    public void setDepth(String depth)
    {
        this.depth = depth;
    }
    public String getLevel()
    {
        return level;
    }
    public void setLevel(String level)
    {
        this.level = level;
    }
    @Override
    public String toString()
    {
        return "FillLevelFitJson [type=" + type + ", depth=" + depth
                + ", level=" + level + "]";
    }
}
