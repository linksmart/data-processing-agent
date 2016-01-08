package de.fraunhofer.fit;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.fit.testing.FileToEventFormatter;
import eu.almanac.ogc.sensorthing.api.datamodel.Observation;
import eu.linksmart.gc.utils.function.Utils;
import eu.linksmart.gc.utils.mqtt.broker.StaticBroker;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by José Ángel Carvajal on 22.12.2015 a researcher of Fraunhofer FIT.
 */
public class Application {
    public static void main(String[] args) {
        int mil = 50;
        String file = "experiment", brokername = "localhost", port = "1883";

        if(args.length>0)
            mil = Integer.valueOf(args[0]);
        if(args.length>1)
            file = args[1];

        if(args.length>2)
            file = args[2];

        if(args.length>3)
            file = args[3];

        System.out.println("Starting test with n=" + String.valueOf(mil)+" and with file ="+file);
        FileToEventFormatter fileToEventFormatter;
        StaticBroker broker;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(Utils.getDateFormat());
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            broker = new StaticBroker(brokername,port);
        } catch (MalformedURLException | MqttException e) {
            e.printStackTrace();
            return;
        }

        try {

                fileToEventFormatter = new FileToEventFormatter(file,"\t","yyyy-MM-dd' 'HH:mm:ss.SSS");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        Observation observation;

        try {
            int i =0;
            Observation last = null;
                while ((observation = fileToEventFormatter.next(Observation.class)) != null) {
                    try {

                        broker.publish("/federation1/fit/v2/observation/" + observation.getId() + "/" + observation.getDatastream().getId(), objectMapper.writeValueAsString(observation));
                        System.out.println(("/federation1/fit/v2/observation/" + observation.getId() + "/" + observation.getDatastream().getId()+"\t"+ objectMapper.writeValueAsString(observation)));
                         Thread.sleep(mil);
                        if(last!=null && last.getPhenomenonTime().after(observation.getPhenomenonTime()))
                            System.out.println("something");
                        last = observation;
                        i++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            System.out.println(i);

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            broker.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
