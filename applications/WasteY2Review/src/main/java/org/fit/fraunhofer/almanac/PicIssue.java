package org.fit.fraunhofer.almanac;

import java.util.Date;

/**
 * Created by Werner-Kytölä on 30.06.2015.
 */
public class PicIssue {
    private String id;;
    private double latitude;
    private double longitude;
    private byte[] pic;
    private String name;
    private String comment;

    public PicIssue(String id, double latitude, double longitude, byte[] pic, String name, String comment){
        this.id = id;
        if(!id.isEmpty()) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.pic = pic;
            this.name = name;
            this.comment = comment;
        }
    }
    public String id(){ return id; }
    public double latitude(){ return latitude; }
    public double longitude(){ return longitude; }
    public byte[] pic(){ return pic; }
    public String name(){ return name; }
    public String comment(){ return comment; }

    public String  getString(){
        return id + " lat:" + latitude + " lon:" + longitude + " name:" + name + " comment:" + comment;
    }

    public void print(){
        System.out.println("---------------------");
        System.out.println("id: " + id);
        System.out.println("name: " + name);
        System.out.println("comment: " + comment);
        System.out.println("latitude " + latitude);
        System.out.println("longitude: " + longitude);
    }
}
