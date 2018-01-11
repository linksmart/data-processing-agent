package eu.linksmart.services.event.core.extensions;

import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.serialization.DefaultDeserializer;
import eu.linksmart.services.utils.serialization.Deserializer;
import io.swagger.client.ApiClient;
import io.swagger.client.api.ScApi;
import io.swagger.client.model.APIIndex;
import io.swagger.client.model.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by José Ángel Carvajal on 13.10.2017 a researcher of Fraunhofer FIT.
 */
public class KeyRetriever {
    private Timer timer = new Timer();
    private boolean running = false;
    private Set<String> servicesIds = new HashSet<>();
    // intentionally it's using the default one and not the serialized defined on
    private Deserializer deserializer = new DefaultDeserializer();
    private static Configurator conf = Configurator.getDefaultConfig();
    private static Logger loggerService = LogManager.getLogger(KeyRetriever.class);
    private static KeyRetriever defaultRetriever = init();
    private int interval;
    public ConcurrentMap<String,String> idsKey = new ConcurrentHashMap<>();
    private long lastRequest = new Date().getTime();
    private final ScApi SCclient;

    private KeyRetriever(String url, int interval) throws IllegalArgumentException {
        this.interval = interval;
        URI uri = URI.create(url+"meta/contains/key");
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(url);
        SCclient = new ScApi(apiClient);

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

           APIIndex services=SCclient.jsonpathOperatorValueGet("meta","contains","key",new BigDecimal(1),new BigDecimal(1000));



            services.getServices().forEach(registration -> {
                        if (!idsKey.containsKey(registration.getId()))
                            updateKeyRegister(registration);

                    }
            );


        } catch (Exception e) {
          loggerService.error(e.getMessage(),e);
        }
    }
    private void  updateKeyRegister(Service registration){

        idsKey.put(registration.getId(),((Map)registration.getMeta()).get("key").toString());

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
