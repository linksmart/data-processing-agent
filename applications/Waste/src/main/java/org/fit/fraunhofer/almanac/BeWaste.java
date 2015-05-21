package org.fit.fraunhofer.almanac;

import com.google.gson.Gson;

/**
 * Created by Werner-Kytölä on 08.05.2015.
 */
public class BeWaste {
    public static void main(String [] args){
        System.out.println("hola");

        WasteMqttClient wasteMqttClient = new WasteMqttClient();
        IssueManagement issueMgmt = new IssueManagement(wasteMqttClient, 2);

        issueMgmt.print();
    }
}
