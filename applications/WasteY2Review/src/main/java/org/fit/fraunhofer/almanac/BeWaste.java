package org.fit.fraunhofer.almanac;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Datastream;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Location;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.ObservedProperty;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Thing;
import org.geojson.GeoJsonObject;
import org.geojson.LngLatAlt;

import java.io.*;
import java.util.*;

/**
 * Created by Werner-Kyt�l� on 08.05.2015.
 */
public class BeWaste {
    public static void main(String [] args){

        System.out.println("hola");

        WasteMqttClient wasteMqttClient = WasteMqttClient.getInstancePub();  // WasteMqttClient singleton
        WasteHttpClient wasteHttpClient = WasteHttpClient.getInstance();     // WasteHttpClient singleton



        // for test purposes: simulation

/*        Set<String> XivelyBinIds = new HashSet<String>();
        XivelyBinIds.add("e52118b45384c45280aab95edac54e4c9bab7169");
        XivelyBinIds.add("49e306699d306b96cd171c5a00c3533720d27a43");
        XivelyBinIds.add("91f010bb67ae1071433ff507a767afb3a3562b41");
        XivelyBinIds.add("bbe4aef0289e002f6bc61f99405d0f28ba0fa878");
        XivelyBinIds.add("56ffb0521865b165435f3e74ea25231d20e904c1");
        XivelyBinIds.add("474e5f891912caf7cccb67e353cbee13b8c9b336");
        XivelyBinIds.add("0b60ab7ad145ba4204ae541aa4d428d87f558c1e");
        XivelyBinIds.add("6a2a241332749463701d4d9607c02bc903c8bea1");
        XivelyBinIds.add("207aeb06506f2824dc1d30cfa4f6920722ca27ee");
        XivelyBinIds.add("3427c3d6abe587552645504213fd222b7e151222");
*/
        String[] XivelyBinIds = {"e52118b45384c45280aab95edac54e4c9bab7169", "49e306699d306b96cd171c5a00c3533720d27a43",
                                 "91f010bb67ae1071433ff507a767afb3a3562b41", "bbe4aef0289e002f6bc61f99405d0f28ba0fa878",
                                 "56ffb0521865b165435f3e74ea25231d20e904c1", "474e5f891912caf7cccb67e353cbee13b8c9b336"};
//                                 "0b60ab7ad145ba4204ae541aa4d428d87f558c1e", "6a2a241332749463701d4d9607c02bc903c8bea1",
//                                 "207aeb06506f2824dc1d30cfa4f6920722ca27ee", "3427c3d6abe587552645504213fd222b7e151222"};
        double[] XivelyBinLatitude = {
                                        45.057015,
                                        45.062447,
                                        45.063079,
                                        45.062180,
                                        45.060872,
                                        45.053894
 /*                                     45.0696754455566, 45.0727729797363,
                                      45.0708389282227, 45.072725,
                                      45.0696258544922, 45.06730091,
                                      45.062345, 45.061375,
                                      45.061148, 45.072725*/
                                     };
        double[] XivelyBinLongitude = {
                                        7.670879,
                                        7.674726,
                                        7.677705,
                                        7.680028,
                                        7.679103,
                                        7.675624
/*                                        7.68231105804443, 7.69353008270264,
                                        7.67727470397949, 7.671748,
                                        7.66570854187012, 7.66843825,
                                        7.679798, 7.693145,
                                        7.700376, 7.671748*/
                                      };

        ArrayList<Thing> XivelyBinsArray = new ArrayList<Thing>();

//        for (String s : XivelyBinIds) {
          for(int i = 0; i < 6 ; i++){
            Thing xivelyBin = new Thing();
            xivelyBin.setId(XivelyBinIds[i]);
            xivelyBin.setDescription("The WasteBin connected to the WasteBinSimulator network.");
            xivelyBin.setMetadata("http://almanac-project.eu/ontologies/smartcity.owl#WasteBin");
            Location location = new Location();
            location.setTime(new Date());
            GeoJsonObject geometry = new org.geojson.Point(XivelyBinLongitude[i], XivelyBinLatitude[i]);

            location.setGeometry(geometry);

            HashSet<Location> ob = new HashSet<Location>();
            ob.add(location);
            xivelyBin.setLocations(ob);
/*            Datastream datastream = new Datastream();
            datastream.setId("65fa3b218346ad5788214305639b2bd10082ef2a4539dbcaade63eb00a5177f7");
            ObservedProperty observedProperty = new ObservedProperty();
            observedProperty.setId("01eaf22a265210e4ed2a6ffbbe20fa5a20533032ac2c2a1dee23e1bf85c741ed");
            observedProperty.setUrn("http://almanac-project.eu/ontologies/smartcity.owl#TemperatureState");
            observedProperty.setUnitOfMeasurement("C");
            datastream.setObservedProperty(observedProperty);
            xivelyBin.addDatastream(datastream);*/
            XivelyBinsArray.add(xivelyBin);
        }

        if(!XivelyBinsArray.isEmpty()) {
            IssueManager issueMgmt = new IssueManager(XivelyBinsArray);


            System.out.println("Back to MAIN!");
            // the initial endpoints will be published every 10s. When the driver-app is
            // launched or restarted, it will listen to the initial endpoints to get going.
            while (true) {
                issueMgmt.generateRoute("/almanac/route/initial");

                // for test purposes: simulation

                try{
                    // pause for 10s
                    Thread.sleep(10000);
                }catch (Exception e) {
                    e.printStackTrace();
                }
                // 1. OTRS integration sends an ACCEPTED in consequence of a ticket creation triggered by the CitizenApp:
                Gson gsonObj = new Gson();
                String mesg = new String("ACCEPTED");
                String json = gsonObj.toJson(mesg);
                wasteMqttClient.publish("almanac/ticket", json);

                try{
                    // pause for 10s
                    Thread.sleep(10000);
                }catch (Exception e) {
                    e.printStackTrace();
                }
                // 2. DF event: the toy bin got full
                mesg = "DF";
                json = gsonObj.toJson(mesg);
                wasteMqttClient.publish("almanac/DF", json);
                // end test purposes

                try{
                    // pause for 10s
                    Thread.sleep(10000);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


            // 0. Http client created, it will run some GETs to fetch data
//                org.geojson.LngLatAlt location = wasteHttpClient.getBinGeolocation("ea725b113eef37f8289962d4e2e39bb24657b4c5916b42701028e89ee5953fd7");
//                System.out.println("***Latitude: " + location.getLatitude() + " Longitude: " + location.getLongitude());
/*        ArrayList<Thing> binsInVicinity = wasteHttpClient.getBinsInVicinity(100, 45.0696754455566, 7.68231105804443);
        System.out.println("***bins in vicinity: " + binsInVicinity.size());
        HashMap<String,org.geojson.LngLatAlt>  LocationBinsInVicinity= wasteHttpClient.getLocationOfBinsInVicinity(100, 45.0696754455566, 7.68231105804443);
        System.out.println("***location of bins in vicinity: " + LocationBinsInVicinity.size());

        for (String s : XivelyBinsSet) {
            if (LocationBinsInVicinity.containsKey(s)){
                System.out.println("***Xively bin is in the vicinity: " + s);
            }
        }
*/

/*        HashMap<String,LngLatAlt> locationBinsInVicinity = wasteHttpClient.getLocationOfBinsInVicinity(100, 45.07248713, 7.6934891);
        System.out.println("***bins in vicinity with location: " + locationBinsInVicinity.size());

        ArrayList<Thing> fullBinsInVicinity = wasteHttpClient.getBinsInVicinityWithFillLevelAboveThreshold(100, 45.06920467, 7.70862809, 20);
        System.out.println("***full bins in vicinity: " + fullBinsInVicinity.size());
*/

        // create issues out of the bins in the specific neighborhood
/*        if(!binsInVicinity.isEmpty()) {
            // FIXME: should actually pass fullBinsInVicinity: they are indeed issues
            IssueManager issueMgmt = new IssueManager(binsInVicinity);
        }
*/





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
