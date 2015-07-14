package org.fit.fraunhofer.almanac;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Thing;
import org.geojson.LngLatAlt;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Werner-Kyt�l� on 08.05.2015.
 */
public class BeWaste {
    public static void main(String [] args){

        System.out.println("hola");

        WasteMqttClient wasteMqttClient = WasteMqttClient.getInstancePub();  // WasteMqttClient singleton
        WasteHttpClient wasteHttpClient = WasteHttpClient.getInstance();     // WasteHttpClient singleton


        // for test purposes: simulation

        // 0. Http client created, it will run some GETs to fetch data
//                org.geojson.LngLatAlt location = wasteHttpClient.getBinGeolocation("ea725b113eef37f8289962d4e2e39bb24657b4c5916b42701028e89ee5953fd7");
//                System.out.println("***Latitude: " + location.getLatitude() + " Longitude: " + location.getLongitude());
        ArrayList<Thing> binsInVicinity = wasteHttpClient.getBinsInVicinity(100, 45.07248713, 7.6934891);
        System.out.println("***bins in vicinity: " + binsInVicinity.size());

/*        HashMap<String,LngLatAlt> locationBinsInVicinity = wasteHttpClient.getLocationOfBinsInVicinity(100, 45.07248713, 7.6934891);
        System.out.println("***bins in vicinity with location: " + locationBinsInVicinity.size());

        ArrayList<Thing> binsInVicinityWithThreshold = wasteHttpClient.getBinsInVicinityWithFillLevelAboveThreshold(100, 45.06920467, 7.70862809, 20);
        System.out.println("***binsin vicinity with threshold: " + binsInVicinityWithThreshold.size());
*/

        // create issues out of the bins in the specific neighborhood
        if(!binsInVicinity.isEmpty()) {
            IssueManager issueMgmt = new IssueManager(binsInVicinity);
        }











//    IoTWeek solution with metadata file
/*        ArrayList<Thing> thingList = new ArrayList<Thing>();
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
            IssueManager issueMgmt = new IssueManager(thingList);

            System.out.println("Back to MAIN!");
            // the initial endpoints will be published every 10s. When the driver-app is
            // launched or restarted, it will listen to the initial endpoints to get going.
            while (true){
                issueMgmt.generateRoute("/almanac/route/initial");

                // for test purposes: simulation

                // 1. CitizenApp sends a picture issue:
                Gson gsonObj = new Gson();
                PicIssue picIssue = new PicIssue("123", 0.0, 0.0, null, "name123", "");
                String json = gsonObj.toJson(picIssue);
                wasteMqttClient.publish("almanac/citizenapp", json);

                // 2. SmartWaste sends a notification about an issue duplicate:
                DuplicateIssue dupIssue = new DuplicateIssue("456", "123");
                json = gsonObj.toJson(dupIssue);
                wasteMqttClient.publish("almanac/smartwaste/duplicate", json);
                // end test purposes

                try{
                    // pause for 10s
                    Thread.sleep(10000);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    */
    }
}
