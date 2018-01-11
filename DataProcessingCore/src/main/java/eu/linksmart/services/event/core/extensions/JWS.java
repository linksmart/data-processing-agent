package eu.linksmart.services.event.core.extensions;

import eu.linksmart.services.event.types.StatementInstance;
import eu.linksmart.services.event.connectors.MqttIncomingConnectorService;
import eu.linksmart.services.event.core.ServiceRegistratorService;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.serialization.JWSDeserializer;
import eu.linksmart.services.utils.serialization.JWSSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

/**
 * Created by José Ángel Carvajal on 16.10.2017 a researcher of Fraunhofer FIT.
 */
public class JWS {

    static private Configurator conf = Configurator.getDefaultConfig();
    static private Logger loggingService = LogManager.getLogger(JWS.class);
    private static String publicKey;
    private static JWSDeserializer deserializer;
    private static JWSSerializer serializer;


    static  {
        try {
            serializer = new JWSSerializer(SharedSettings.getSerializer());
            publicKey = serializer.getPublicKeyInBase64String();
            deserializer = new JWSDeserializer(serializer.getPublicKeyInBase64String(),SharedSettings.getDeserializer());



            Arrays.stream(conf.getStringArray(Const.FeederPayloadAlias))
                    .filter(i -> conf.containsKeyAnywhere(Const.EVENT_IN_TOPIC_CONF_PATH + "_" + i) && conf.containsKeyAnywhere(Const.FeederPayloadClass + "_" + i))
                    .forEach(alias -> Arrays.asList(conf.getStringArray(Const.EVENTS_IN_BROKER_CONF_PATH)).forEach(broker -> {
                        try {
                           // MqttIncomingConnectorService.getReference().removeListener(conf.getString(Const.EVENT_IN_TOPIC_CONF_PATH + "_" + alias));
                            SharedSettings.addSharedObject(Const.EVENT_IN_TOPIC_CONF_PATH + "_" + alias,true);
                            MqttIncomingConnectorService.getReference().addListener(broker, conf.getString(Const.EVENT_IN_TOPIC_CONF_PATH + "_" + alias), new JwsObserver(conf.getString(Const.EVENT_IN_TOPIC_CONF_PATH + "_" + alias)));
                        } catch (Exception e) {
                            LogManager.getLogger(JwsObserver.class).error(e.getMessage(), e);
                        }
                    }));
            StatementInstance.DEFAULT_HANDLER = JwsHandler.class.getCanonicalName();

            KeyRetriever.getDefaultRetriever();

            ServiceRegistratorService.meta.put("key",serializer.getPublicKeyInBase64String());
            loggingService.info("JWS extension loaded");
        } catch (Exception e) {
            loggingService.error(e.getMessage(),e);
        }
    }

    public static String getPublicKey() {
        return publicKey;
    }

    public static JWSDeserializer getDeserializer() {
        return deserializer;
    }

    public static JWSSerializer getSerializer() {
        return serializer;
    }
}
