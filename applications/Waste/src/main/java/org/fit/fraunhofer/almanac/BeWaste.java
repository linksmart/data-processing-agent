package org.fit.fraunhofer.almanac;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Thing;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Werner-Kyt�l� on 08.05.2015.
 */
public class BeWaste {
    public static void main(String [] args){

        System.out.println("hola");

        ArrayList<Thing> thingList = new ArrayList<Thing>();
        WasteMqttClient wasteMqttClient = new WasteMqttClient();

        try {

            File file = new File("metadata-3.txt");
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();

            String str = new String(data, "UTF-16");
            String [] lines = str.split("\n");
            ObjectMapper mapper = new ObjectMapper();

            for(int i = 0; i < lines.length; i++)
                thingList.add( mapper.readValue(lines[i], Thing.class));

        }catch (Exception e){
            e.printStackTrace();

        }

        if(!thingList.isEmpty()) {
            IssueManagement issueMgmt = new IssueManagement(wasteMqttClient, thingList);

            System.out.println("Back to MAIN!");
            // the initial endpoints will be published every 10s. When the driver-app is
            // launched or restarted, it will listen to the initial endpoints to get going.
            while (true){
                issueMgmt.generateRoute("/almanac/route/initial");

                try{
                    // pause for 10s
                    Thread.sleep(10000);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
