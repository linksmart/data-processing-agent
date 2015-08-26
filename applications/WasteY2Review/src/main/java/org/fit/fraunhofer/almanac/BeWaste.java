package org.fit.fraunhofer.almanac;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Thing;
import org.geojson.LngLatAlt;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Werner-Kyt�l� on 08.05.2015.
 */
public class BeWaste {
    public static void main(String [] args){

        System.out.println("hola");

        WasteMqttClient wasteMqttClient = WasteMqttClient.getInstancePub();  // WasteMqttClient singleton
        WasteHttpClient wasteHttpClient = WasteHttpClient.getInstance();     // WasteHttpClient singleton



        // for test purposes: simulation

        Set<String> XivelyBinsSet = new HashSet<String>();
        XivelyBinsSet.add("e52118b45384c45280aab95edac54e4c9bab7169");
        XivelyBinsSet.add("49e306699d306b96cd171c5a00c3533720d27a43");
        XivelyBinsSet.add("91f010bb67ae1071433ff507a767afb3a3562b41");
        XivelyBinsSet.add("bbe4aef0289e002f6bc61f99405d0f28ba0fa878");
        XivelyBinsSet.add("56ffb0521865b165435f3e74ea25231d20e904c1");
        XivelyBinsSet.add("474e5f891912caf7cccb67e353cbee13b8c9b336");
        XivelyBinsSet.add("0b60ab7ad145ba4204ae541aa4d428d87f558c1e");
        XivelyBinsSet.add("6a2a241332749463701d4d9607c02bc903c8bea1");
        XivelyBinsSet.add("207aeb06506f2824dc1d30cfa4f6920722ca27ee");
        XivelyBinsSet.add("3427c3d6abe587552645504213fd222b7e151222");


        // 0. Http client created, it will run some GETs to fetch data
//                org.geojson.LngLatAlt location = wasteHttpClient.getBinGeolocation("ea725b113eef37f8289962d4e2e39bb24657b4c5916b42701028e89ee5953fd7");
//                System.out.println("***Latitude: " + location.getLatitude() + " Longitude: " + location.getLongitude());
        ArrayList<Thing> binsInVicinity = wasteHttpClient.getBinsInVicinity(500, 45.0696754455566, 7.68231105804443);
        System.out.println("***bins in vicinity: " + binsInVicinity.size());
        HashMap<String,org.geojson.LngLatAlt>  LocationBinsInVicinity= wasteHttpClient.getLocationOfBinsInVicinity(500, 45.0696754455566, 7.68231105804443);
        System.out.println("***location of bins in vicinity: " + LocationBinsInVicinity.size());

        for (String s : XivelyBinsSet) {
            if (LocationBinsInVicinity.containsKey(s)){
                System.out.println("***Xively bin is in the vicinity: " + s);
            }
        }

/*        HashMap<String,LngLatAlt> locationBinsInVicinity = wasteHttpClient.getLocationOfBinsInVicinity(100, 45.07248713, 7.6934891);
        System.out.println("***bins in vicinity with location: " + locationBinsInVicinity.size());

        ArrayList<Thing> binsInVicinity = wasteHttpClient.getBinsInVicinityWithFillLevelAboveThreshold(100, 45.06920467, 7.70862809, 20);
        System.out.println("***full bins in vicinity: " + binsInVicinity.size());
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
