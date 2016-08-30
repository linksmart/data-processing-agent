package de.fraunhofer.fit.testing;

import eu.almanac.ogc.sensorthing.api.datamodel.Datastream;
import eu.almanac.ogc.sensorthing.api.datamodel.Observation;
import eu.linksmart.services.utils.function.Utils;

import java.io.BufferedReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import eu.linksmart.api.event.datafusion.EventType;
/**
 * Created by José Ángel Carvajal on 22.12.2015 a researcher of Fraunhofer FIT.
 */
public class FileToEventFormatter {

    private BufferedReader bufferedReader;
    private String separator = ",";
    private Map<String, Integer> columns =new Hashtable<>();
    private DateFormat dataFormat = Utils.getDateFormat();



    public FileToEventFormatter(String filePath,String separator, String dateFromat) throws FileNotFoundException {
        init(filePath,separator,dateFromat);

    }
    public FileToEventFormatter(String filePath,String separator) throws FileNotFoundException {
        init(filePath,separator,null);

    }

    public FileToEventFormatter(String filePath) throws FileNotFoundException {
        init(filePath,separator,null);

    }
    public FileToEventFormatter(String... args) throws FileNotFoundException {
        if(args.length==1)
            init(args[0],separator,null);
        else if(args.length==2)
            init(args[0],args[1],null);
        else if (args.length==3)
            init(args[0],args[1],args[2]);


    }
    private void init(String filePath, String separator, String dateFromat) throws FileNotFoundException {
        bufferedReader = new BufferedReader(new FileReader(filePath));
        this.separator = separator;
        String line;
        if (dateFromat != null)
            dataFormat = new SimpleDateFormat(dateFromat);

        try {
            line = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        int i = 0;
        for (String token : line.trim().split(separator)) {
            if (token.contains("id")) {
                columns.put("id", i);
                i++;
            } else if (token.contains("attributeId")) {
                columns.put("attributeId", i);
                i++;
            }else if (token.contains("timestamp")) {
                columns.put("timestamp", i);
                i++;
            }else if (token.contains("value")) {
                columns.put("value", i);
                i++;
            }else if (token.contains("description")) {
                columns.put("description", i);
                i++;
            }
        }




    }

    public <T extends EventType> T next(Class<T> toClass) throws IOException {
        String line = bufferedReader.readLine();
        if(line!= null &&line!="") {
            T event;
            try {
                event = toClass.newInstance();
                String[] aux = line.split(separator);
                event.setId(aux[columns.get("id")].trim());
                event.setAttributeId(aux[columns.get("attributeId")].trim());


                try {
                    Date s = dataFormat.parse(aux[columns.get("timestamp")].trim().substring(0,23));
                    event.setDate(s);
                } catch (Exception e) {
                    try {
                        Date s = dataFormat.parse(aux[columns.get("timestamp")].trim()+".00");
                        event.setDate(s);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }

                event.setValue(aux[columns.get("value")].trim());
                if(event instanceof Observation) {
                    ((Observation) event).setResultType(aux[columns.get("description")].trim());


                }
                return  (T)event;

            } catch (InstantiationException | IllegalAccessException | NullPointerException e ) {
                e.printStackTrace();
            }
        }

        return null;

    }
}
