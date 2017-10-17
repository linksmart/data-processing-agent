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
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by José Ángel Carvajal on 13.10.2017 a researcher of Fraunhofer FIT.
 */
public class KeyRetriever {
    private Request request;
    private Timer timer = new Timer();
    private boolean running = false;
    private Set<String> servicesIds = new HashSet<>();
    // intentionally it's using the default one and not the serialized defined on
    private Deserializer deserializer = new DefaultDeserializer();
    private static Configurator conf = Configurator.getDefaultConfig();
    private static Logger loggerService = Utils.initLoggingConf(KeyRetriever.class);
    private static KeyRetriever defaultRetriever = init();
    private int interval;
    public ConcurrentMap<String,String> idsKey = new ConcurrentHashMap<>();
    private long lastRequest = new Date().getTime();

    private KeyRetriever(String url, int interval) throws IllegalArgumentException {
        this.interval = interval;
        URI uri = URI.create(url+"meta/contains/key");
        request = Request.Get(uri);


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
    static KeyRetriever getDefaultRetriever() throws IllegalArgumentException {
        return defaultRetriever;
    }

    void retrieveKey(){
        try {
            if ( (new Date()).getTime() - lastRequest < 1000 )
                return;

            Response response = request.execute();

            ServiceRegistrations registrations = deserializer.parse(response.returnContent().asString(), ServiceRegistrations.class);

            registrations.getServices().forEach(registration -> {
                        if (!idsKey.containsKey(registration.getId()))
                            updateKeyRegister(registration);

                    }
            );


        } catch (IOException e) {
          loggerService.error(e.getMessage(),e);
        }
    }
    private void  updateKeyRegister(Registration registration){

        idsKey.put(registration.getId(),registration.getMeta().get("key").toString());

    }
    public void startService(){
        if( !running ){
            timer.schedule(new TimerTask() {
                               @Override
                               public void run() {
                                   running =true;
                                   retrieveKey();
                               }
                           },
                    0,
                    interval

            );
        }
    }
    public void stopService(){
        if(running)
            timer.cancel();
        running = false;
    }

}
