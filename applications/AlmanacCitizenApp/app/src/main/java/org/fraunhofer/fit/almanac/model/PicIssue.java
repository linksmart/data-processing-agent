package org.fraunhofer.fit.almanac.model;

/**
 * Created by devasya on 18.06.2015.
 */
public class PicIssue {
    public String origin; //Id uniquely identifying the runtime/device

    public String id;//Id uniquely identifying this issue.This will be null initially and will be updated once server responds with 201 created

    //Location where issue is found
    public  double latitude;
    public double longitude;

    //user entered nsme for the issue
    public String name;

    //comment by user
    public String comment;

    //Picture
    public byte[] pic;

    public String contentType; //mime type of the picture

    public String  getString(){
        return id+" lat:"+ latitude + " lon:" + longitude+"name:"+ name;
    }

}
