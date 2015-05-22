package eu.almanac.client.event.datafusion;

import com.espertech.esper.epl.generated.EsperEPL2GrammarParser;
import com.espertech.esper.epl.parse.ParseHelper;
import com.espertech.esper.epl.parse.ParseRuleSelector;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.almanac.event.datafusion.utils.payload.IoTPayload.IoTEntityEvent;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.tree.Tree;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Created by Caravajal on 27.10.2014.
 */
public class DFClientOld {

     static void main(String[] args) {
        CommandLine cmd = parseArg(args);
        String queryNameHash="";


        if(cmd==null)
            return;

        try {
            MqttClient client = new MqttClient(cmd.hasOption("b") ? cmd.getOptionValue("b") : "tcp://localhost:1883", UUID.randomUUID().toString().replace("-", "_").replace("#", "_"), new MemoryPersistence());

            client.connect();


            IoTEntityEvent query = new IoTEntityEvent("DataFusionManager");
            if (cmd.hasOption("name")) {
                query.addProperty("Name");
                try {
                    MessageDigest SHA256 = MessageDigest.getInstance("SHA-256");
                    queryNameHash =(new BigInteger(1,SHA256.digest(cmd.getOptionValue("name").getBytes()))).toString();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                query.getProperties("Name").addIoTStateObservation(queryNameHash);
            } else {

                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("DFClient", getCommandArgsOptions());
                client.disconnect();
                client.close();
                return;
            }
            if (cmd.hasOption("query")) {
                query.addProperty("Statement");
                query.getProperties("Statement").addIoTStateObservation(cmd.getOptionValue("query"));
            } else if (cmd.hasOption("file")){
                try {
                    Boolean addPleaseCheck =true;
                    String content = FileUtils.readFileToString(new File(cmd.getOptionValue("file")), "utf-8").replace("\n","").replace("\r","");

                    ParseRuleSelector eplParseRule = new ParseRuleSelector()
                    {
                        public Tree invokeParseRule(EsperEPL2GrammarParser parser) throws RecognitionException
                        {
                            return parser.startEPLExpressionRule();
                        }
                    };
                    ParseHelper.parse(content, content, true, eplParseRule, true);
                    query.addProperty("Statement");
                    query.getProperties("Statement").addIoTStateObservation(content);

                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
            if (cmd.hasOption("entity")) {
                query.addProperty("Source");
                query.getProperties("Source").addIoTStateObservation(cmd.getOptionValue("entity"));
            }
            if (cmd.hasOption("input")) {
                query.addProperty("Input");
                query.getProperties("Input").addIoTStateObservation(cmd.getOptionValue("input"));
            }
            if (cmd.hasOption("output")) {
                query.addProperty("Output");
                query.getProperties("Output").addIoTStateObservation(cmd.getOptionValue("output"));
            }
            if (cmd.hasOption("scope")) {
                query.addProperty("Scope");
                query.getProperties("Scope").addIoTStateObservation(cmd.getOptionValue("scope"));
            }
            Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

            client.publish("query", gson.toJson(query).getBytes(), 0, false);

            System.out.println("Query sent successfully hash code for the query is:"+queryNameHash);
            client.disconnect();
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
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
