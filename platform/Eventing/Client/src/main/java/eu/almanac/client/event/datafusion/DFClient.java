package eu.almanac.client.event.datafusion;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import eu.almanac.event.datafusion.utils.payload.SenML.Event;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.File;
import java.io.IOException;
import java.util.*;
/**
 * Created by Caravajal on 27.10.2014.
 */
public class DFClient {

    public static void main(String[] args) {
        CommandLine cmd = parseArg(args);


        if(cmd==null)
            return;

        try {
            MqttClient client = new MqttClient(cmd.hasOption("b") ? cmd.getOptionValue("b") : "tcp://localhost:1883", UUID.randomUUID().toString().replace("-", "_").replace("#", "_"));

            client.connect();


            Event query = new Event("DataFusionManager");
            if (cmd.hasOption("name")) {
                query.addProperty("Name");
                query.getEbyName("Name").setSv(cmd.getOptionValue("name"));
            } else {

                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("DFClient", getCommandArgsOptions());
                client.disconnect();
                client.close();
                return;
            }
            if (cmd.hasOption("query")) {
                query.addProperty("Statement");
                query.getEbyName("Statement").setSv(cmd.getOptionValue("query"));
            } else if (cmd.hasOption("file")){
                try {
                    String content = FileUtils.readFileToString(new File(cmd.getOptionValue("file")), "utf-8").replace("\n","".replace("\r",""));

                    query.addProperty("Statement");
                    query.getEbyName("Statement").setSv(content);

                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
            if (cmd.hasOption("entity")) {
                query.addProperty("Source");
                query.getEbyName("Source").setSv(cmd.getOptionValue("entity"));
            }
            if (cmd.hasOption("input")) {
                query.addProperty("Input");
                query.getEbyName("Input").setSv(cmd.getOptionValue("input"));
            }
            if (cmd.hasOption("output")) {
                query.addProperty("Output");
                query.getEbyName("Output").setSv(cmd.getOptionValue("output"));
            }
            if (cmd.hasOption("scope")) {
                query.addProperty("Scope");
                query.getEbyName("Scope").setSv(cmd.getOptionValue("scope"));
            }
            Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

            client.publish("query", gson.toJson(query).getBytes(), 0, false);

            client.disconnect();
            client.close();
        } catch (MqttException e) {
            e.printStackTrace();
        }


    }
    public static Options getCommandArgsOptions(){
        Options options = new Options();

        options.addOption(
                OptionBuilder
                        .withLongOpt("broker")
                        .withArgName("URL")
                        .hasArg()
                        .withDescription(  "URL of the broker where the statements will be send (Default: tcp://localhost:1883)" )
                        .create( "b" )
        );
        options.addOption(
                OptionBuilder
                        .withLongOpt("name")
                        .withArgName("string")
                        .isRequired()
                        .hasArg()
                        .withDescription("Name of the query (Mandatory)")
                        .create("n")
        );
        options.addOption(
                OptionBuilder
                        .withLongOpt("query")
                        .withArgName("DFL")
                        .hasArg()
                        .withDescription(  "The body of the query/statement (Mandatory or option -f)" )
                        .create( "q" )
        );
        options.addOption(
                OptionBuilder
                        .withLongOpt("file")
                        .withArgName( "DFL" )
                        .hasArg()
                        .withDescription(  "Query from a file (Mandatory)" )
                        .create( "f" )
        );
        options.addOption(
                OptionBuilder
                        .withLongOpt("entity")
                        .withArgName("IoTEntityURI")
                        .hasArg()
                        .withDescription(  "IoT Entity which will be the source of the events generated by the query (Optional)" )
                        .create( "e" )
        );
        options.addOption(
                OptionBuilder
                        .withLongOpt("input")
                        .withArgName("MQTT_Topic")
                        .hasArg()
                        .withDescription(  "Topics where the events involved in the query are being published (Optional,Deprecated)" )
                        .create( "i" )
        );
        options.addOption(
                OptionBuilder
                        .withLongOpt("output")
                        .withArgName("MQTT_Topic")
                        .hasArg()
                        .withDescription(  "Allows the specification of other topic which the events generated by the query will be published (Optional)" )
                        .create( "o" )
        );

        options.addOption(
                OptionBuilder
                        .withLongOpt("scope")
                        .withArgName("string")
                        .hasArg()
                        .withDescription(  "In which broker the punishment will be visible (Optional)" )
                        .create( "S" )
        );
        return options;
    }
    public static CommandLine parseArg(String[] parameters)
    {
        Options options =getCommandArgsOptions();

        CommandLineParser parser = new GnuParser();
        try {
            CommandLine cmd = parser.parse( options, parameters);

            if(!cmd.hasOption("q") && !cmd.hasOption("f"))
                throw new ParseException("Missing required options either q or f");

            return cmd;
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("DFClient", getCommandArgsOptions());
            return null;
        }

    }
}