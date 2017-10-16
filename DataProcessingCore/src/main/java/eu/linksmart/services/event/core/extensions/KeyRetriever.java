package eu.linksmart.services.event.core.extensions;

import eu.linksmart.sdk.catalog.service.Registration;
import eu.linksmart.sdk.catalog.service.ServiceRegistrations;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.services.event.intern.Utils;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.serialization.DefaultDeserializer;
import eu.linksmart.services.utils.serialization.Deserializer;
import eu.linksmart.services.utils.serialization.JWSDeserializer;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.util.*;

/**
 * Created by José Ángel Carvajal on 13.10.2017 a researcher of Fraunhofer FIT.
 */
public class KeyRetriever {
    private Request request;
    private Timer timer = new Timer();
    private Set<String> servicesIds = new HashSet<>();
    // intentionally it's using the default one and not the serialized defined on
    private Deserializer deserializer = new DefaultDeserializer();
    private static Configurator conf = Configurator.getDefaultConfig();
    private static Logger loggerService = Utils.initLoggingConf(KeyRetriever.class);
    private static KeyRetriever defaultRetriever = init();

    private KeyRetriever(String url, int interval) throws IllegalArgumentException {

        URI uri = URI.create(url+"meta/contains/key");
        request = Request.Get(uri);
        timer.schedule(new TimerTask() {
                           @Override
                           public void run() {
                               retrieveKey();
                           }
                       },
                0,
                interval

        );

    }

    private static KeyRetriever init(){
        try {

            return new KeyRetriever(
                    conf.getString(Const.LINKSMART_SERVICE_CATALOG_ENDPOINT),
                    1000

            );
        }catch (Exception e){
           loggerService.error(e.getMessage(),e);
        }
        return null;
    }
    public static KeyRetriever getDefaultRetriever() throws IllegalArgumentException {
        return defaultRetriever;
    }

    private void retrieveKey(){
        try {
            Response response = request.execute();

            ServiceRegistrations registrations = deserializer.parse(response.returnContent().asString(), ServiceRegistrations.class);

            registrations.getServices().forEach(registration -> {
                        if (!servicesIds.contains(registration.getId()))
                            updateKeyRegister(registration);

                    }
            );


        } catch (IOException e) {
          loggerService.error(e.getMessage(),e);
        }
    }
    private void  updateKeyRegister(Registration registration){

        ((Map<String,String>)SharedSettings.getSharedObject(JWS.publicKeys)).put(registration.getId(),registration.getMeta().get("key").toString());

    }

}
