package eu.linksmart.testing.tooling;


import org.apache.commons.cli.*;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class Application  {

    public static Options getCommandArgsOptions(){
        Options options = new Options();

        options.addOption(
                OptionBuilder
                        .withLongOpt("broker")
                        .withArgName("URL")
                        .withType(String.class)
                        .hasArg()
                        .withDescription( "URL of the broker where the data will be send (Default: tcp://localhost:1883)" )
                        .create( "b" )
        );
        options.addOption(
                OptionBuilder
                        .withLongOpt("topic")
                        .withArgName("string")
                        .withType(String.class)
                                // .isRequired()
                        .hasArg()
                        .withDescription("Base topic where the data will be send (/federation1/fit/v2/)")
                        .create("t")
        );
        options.addOption(
                OptionBuilder
                        .withLongOpt("noThreads")
                        .withArgName("int")
                        .withType(Integer.class)
                                //.isRequired()
                        .hasArg()
                        .withDescription("Number of producer threads")
                        .create("n")
        );
        options.addOption(
                OptionBuilder
                        .withLongOpt("max")
                        .withArgName("int")
                        .withType(Integer.class)
                                //.isRequired()
                        .hasArg()
                        .withDescription("Maximum events per second")
                        .create("m")
        );
        options.addOption(
                OptionBuilder
                        .withLongOpt("index")
                        .withArgName("int")
                        .withType(Integer.class)
                                //.isRequired()
                        .hasArg()
                        .withDescription("Set id index of the producer/consumer")
                        .create("i")
        );
        options.addOption(
                OptionBuilder
                        .withLongOpt("payload")
                        .withArgName("string")
                        .withType(String.class)
                                //.isRequired()
                        .hasArg()
                        .withDescription("Base payload. If the base payload want to be together  with a counter insert <?> where the counter must be placed")
                        .create("p")
        );
        options.addOption(
                OptionBuilder
                        .withLongOpt("shareIndex")
                        .withDescription("If same is active all messages will be sent to the same topic instead a topic per thread")
                        .create("s")
        );

        options.addOption(
                OptionBuilder
                        .withLongOpt("qos")
                        .withType(Integer.TYPE)
                        .withDescription("Quality of service used (0,1 or 2)")
                        .hasArg()
                        .create("q")
        );
        options.addOption(
                OptionBuilder
                        .withLongOpt("lot")
                        .withType(Integer.TYPE)
                        .withDescription("Size of the lots. This means, after reach this number the thread will stop")
                        .hasArg()
                        .create("l")
        );
        return options;
    }
    public static CommandLine parseArg(String[] parameters)
    {
        Options options =getCommandArgsOptions();

        CommandLineParser parser = new GnuParser();
        try {
            return parser.parse( options, parameters);

        } catch (ParseException e) {
            System.err.println(e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("MQTT testing Tools", getCommandArgsOptions());
            return null;
        }

    }
    public static void main(String[] args) {


        int producer=0;
        int nThread =1, sid=1, max=400000,qos=2, lot=1000000;
        boolean shareIndex = false;
        String topic="/federation1/amiat/v2/observation/<sid>/<sid>", broker ="localhost", payload ="{\"ResultValue\":<i>,\"ResultType\":\"Counter\",\"Time\":<epoch>}" ;
        CommandLine argv = parseArg(args);
        if(argv.hasOption("n"))
            nThread = Integer.valueOf(argv.getOptionValue("n"));

        if(argv.hasOption("i"))
            sid = Integer.valueOf(argv.getOptionValue("i"));

        if(argv.hasOption("m"))
            max = Integer.valueOf(argv.getOptionValue("m"));

        if(argv.hasOption("q"))
            qos = Integer.valueOf(argv.getOptionValue("q"));

        if(argv.hasOption("l"))
            lot = Integer.valueOf(argv.getOptionValue("l"));

        if(argv.hasOption("t"))
            topic = argv.getOptionValue("t");

        if(argv.hasOption("b"))
            broker = argv.getOptionValue("b");

        if(argv.hasOption("p"))
            payload = argv.getOptionValue("p");

        shareIndex =argv.hasOption("s");




        List<Thread> threads = new ArrayList<>();

        for (int i=1; i<nThread+1;i++) {


            threads.add(new Thread(new Producer(i+sid, topic, broker, max, payload, lot, shareIndex, qos)));
            threads.get(threads.size() - 1).start();
        }

        threads.forEach((thread) -> {
            try {
                thread.join();


            }catch (Exception e){
                e.printStackTrace();
            }
        });

    }

}
