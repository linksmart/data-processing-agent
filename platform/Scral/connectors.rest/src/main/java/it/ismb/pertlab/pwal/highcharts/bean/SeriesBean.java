package it.ismb.pertlab.pwal.highcharts.bean;


import org.codehaus.jackson.annotate.JsonProperty;


public class SeriesBean {
    @JsonProperty("name")
    private String name;
    @JsonProperty("color")
    private String color;
    @JsonProperty("data")
    private double[] data;


    public SeriesBean(String name, double[] data) {
      this.name = name;
      this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public double[] getData() {
        return data;
    }

    public void setData(double[] data) {
        this.data = data;
    }
}