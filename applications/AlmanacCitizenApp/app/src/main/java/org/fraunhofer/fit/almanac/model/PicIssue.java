package org.fraunhofer.fit.almanac.model;

/**
 * Created by devasya on 18.06.2015.
 */
public class PicIssue {
    public String id;
    public  double latitude;
    public double longitude;
    public byte[] pic;
    public String name;
    public String comment;
    public String  getString(){
        return id+" lat:"+ latitude + " lon:" + longitude+"name:"+ name;
    }

}
