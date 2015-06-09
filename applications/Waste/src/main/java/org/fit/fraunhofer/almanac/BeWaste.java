package org.fit.fraunhofer.almanac;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.gson.Gson;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Thing;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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


//            org.geojson.LngLatAlt point =((org.geojson.Point) ((it.ismb.pertlab.ogc.sensorthings.api.datamodel.Location) thingList.get(0).getLocations().toArray()[0]).getGeometry()).getCoordinates();
//            System.out.println(point.getLatitude());
//            System.out.println(point.getLongitude());
//            System.out.println();
        }catch (Exception e){
            e.printStackTrace();

        }

        if(!thingList.isEmpty()) {
            IssueManagement issueMgmt = new IssueManagement(wasteMqttClient, thingList);
//            issueMgmt.print();
        }

    }
}
