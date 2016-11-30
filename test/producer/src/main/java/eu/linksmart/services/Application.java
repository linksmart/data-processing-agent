package eu.linksmart.services;


import eu.linksmart.services.testing.PahoPublisher;
import eu.linksmart.services.testing.StaticBrokerPublisher;
import eu.linksmart.testing.tooling.Consumer;
import eu.linksmart.testing.tooling.Producer;
import eu.linksmart.testing.tooling.Publisher;
import org.apache.commons.cli.*;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Application {

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
                        .withDescription("Base topic where the data will be send (/federation1/amiat/v2/observation/<sid>/<sid>)")
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
                        .create("max")
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
                        .withLongOpt("message")
                        .withArgName("payload")
                        .withType(String.class)
                                //.isRequired()
                        .hasArg()
                        .withDescription("Base message payload. If the base payload want to be together  with a counter insert <?> where the counter must be placed. Default: {\"ResultValue\":<i>,\"ResultType\":\"Counter\",\"Time\":<epoch>}")
                        .create("m")
        );
        options.addOption(
                OptionBuilder
                        .withLongOpt("shareIndex")
                        .withDescription("If same is active all messages will be sent to the same topic instead a topic per thread")
                        .create("si")
        );
        options.addOption(
                OptionBuilder
                        .withLongOpt("sleepingTime")
                        .withType(Integer.TYPE)
                        .withDescription("sleeping time between publications")
                        .hasArg()
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

        options.addOption(
                OptionBuilder
                        .withLongOpt("publisher")
                        .withArgName("publisher")
                        .withType(String.class)
                                //.isRequired()
                        .hasArg()
                        .withDescription("Publisher selection. p or paho (for pure Paho), gc or ls (for linksmart version) ")
                        .create("p")
        );

        options.addOption(
                OptionBuilder
                        .withLongOpt("singlePublisher")
                        .withDescription("If the publisher is one for all connection or one for each")
                        .create("x")
        );
        options.addOption(
                OptionBuilder
                        .withLongOpt("validation")
                        .withDescription("The client subscribe to own messages to validate himself")
                        .create("v")
        );

        options.addOption(
                OptionBuilder
                        .withLongOpt("retain")
                        .withDescription("Retain policy activate in the producer")
                        .create("r")
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
        int nThread =1, sid=1, max=400000,qos=2, lot=1000000, sleeping =0;
        boolean shareIndex = false, singlePublisher=true, paho=true, validation=false, retain =false;
        String topic="/federation1/amiat/v2/observation/<sid>/<sid>", broker ="localhost", payload ="{\"ResultValue\":<i>,\"ResultType\":\"Counter\",\"Time\":<epoch>}" ;
        Publisher publisher =null;
        CommandLine argv = parseArg(args);

        if(argv.hasOption("help"))
            return;

        if(argv.hasOption("n"))
            nThread = Integer.valueOf(argv.getOptionValue("n"));

        if(argv.hasOption("i"))
            sid = Integer.valueOf(argv.getOptionValue("i"));

        if(argv.hasOption("max"))
            max = Integer.valueOf(argv.getOptionValue("max"));

        if(argv.hasOption("q"))
            qos = Integer.valueOf(argv.getOptionValue("q"));

        if(argv.hasOption("l"))
            lot = Integer.valueOf(argv.getOptionValue("l"));

        if(argv.hasOption("t"))
            topic = argv.getOptionValue("t");

        if(argv.hasOption("b"))
            broker = argv.getOptionValue("b");

        if(argv.hasOption("m"))
            payload = argv.getOptionValue("m");

        if(argv.hasOption("s"))
            sleeping =Integer.valueOf(argv.getOptionValue("s"));


        paho =!(argv.hasOption("p") && (argv.getOptionValue("p").toLowerCase().contains("gc") || argv.getOptionValue("p").toLowerCase().contains("ls")));


        shareIndex =argv.hasOption("si");

        singlePublisher = argv.hasOption("x");
        validation = argv.hasOption("v");
        retain = argv.hasOption("r");
        if(singlePublisher)
            if(paho)
                publisher = new PahoPublisher(broker, false);
            else
                publisher = new StaticBrokerPublisher(false);




        MqttClient client = null;
        if(validation) {
            try {
                client = new MqttClient("tcp://"+broker+":1883", UUID.randomUUID().toString(),new MemoryPersistence());
                client.setCallback(new Consumer(client, lot));
                MqttConnectOptions options = new MqttConnectOptions();
                options.setCleanSession(false);
                options.setMaxInflight(1000);
                options.setAutomaticReconnect(true);
                client.connect(options);

                while (!client.isConnected())
                    Thread.sleep(100);
                client.subscribe("/#", 2);
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



        List<Thread> threads = new ArrayList<>();

        for (int i=0; i<nThread;i++) {
            if(singlePublisher)
                threads.add(new Thread(new Producer(i + sid, topic, broker, max, payload, lot, shareIndex, qos, sleeping,retain, publisher)));
            else if(paho)
                threads.add(new Thread(new Producer(i + sid, topic, broker, max, payload, lot, shareIndex, qos, sleeping,retain, new PahoPublisher(broker,false))));
            else
                threads.add(new Thread(new Producer(i + sid, topic, broker, max, payload, lot, shareIndex, qos, sleeping,retain, new StaticBrokerPublisher(false))));

            threads.get(threads.size() - 1).start();
        }

        threads.forEach((thread) -> {
            try {
                thread.join();


            }catch (Exception e){
                e.printStackTrace();
            }
        });
        if(singlePublisher) {
            publisher.disconnect();
            publisher.close();
        }
        if(client!=null)
            try {
                client.disconnect();
                client.close();
            } catch (MqttException e) {
                e.printStackTrace();
            }

    }

}
