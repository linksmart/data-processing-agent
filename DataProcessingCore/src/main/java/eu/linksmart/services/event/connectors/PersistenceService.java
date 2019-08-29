package eu.linksmart.services.event.connectors;

import com.fasterxml.jackson.core.type.TypeReference;
import eu.linksmart.api.event.types.PersistentRequest;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.services.event.types.PersistentRequestInstance;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * Created by José Ángel Carvajal on 02.11.2017 a researcher of Fraunhofer FIT.
 */
public class PersistenceService extends FileConnector{

    private final Map<String, List<PersistentRequest>> requests = new HashMap<>();
    private static boolean loaded = false;
    private static final Object lock = new Object();



    public PersistenceService(String... filePaths) {
        super(filePaths);
    }

    @Override
    public synchronized void loadFiles() {
        synchronized (lock) {
            if (!loaded) {
                super.loadFiles();
                PersistentRequestInstance.setPersistentFileOnceExisted(true);
                loaded = true;
            }
        }

    }

    @Override
    protected void loadStream(String inputStream)  {
        Map<String,List<String>> raw = null;
      //  Class<T> type = ((Class<T>) ((ParameterizedType) getClass().toGenericString()).getActualTypeArguments()[0]);
        try {
            synchronized (PersistentRequest.synchLock) {
                raw = (Map<String, List<String>>) SharedSettings.getDeserializer().parse(inputStream, Map.class);
                // List<String> strings = raw.get( type.getCanonicalName());
                raw.forEach((k, v) -> {
                    requests.put(k, new ArrayList<>());
                    try {
                        Class tClass = Class.forName(k);
                        v.forEach(e -> {
                            try {
                                requests.get(k).add((PersistentRequest) SharedSettings.getDeserializer().deserialize(Base64.getDecoder().decode(e), tClass));
                            } catch (Exception ee) {
                                loggerService.error(ee.getMessage(), ee);
                                if (conf.getBoolean(Const.PERSISTENT_ENABLED)) {
                                    loggerService.error("Persistence service fail! Stopping agent");
                                    System.exit(-1);
                                }
                            }
                        });
                    } catch (ClassNotFoundException e) {
                        loggerService.error(e.getMessage(), e);
                        if (conf.getBoolean(Const.PERSISTENT_ENABLED)) {
                            loggerService.error("Persistence service fail! Stopping agent");
                            System.exit(-1);
                        }
                    }

                });
            }
        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
            if(conf.getBoolean(Const.PERSISTENT_ENABLED)){
                loggerService.error("Persistence service fail! Stopping agent");
                System.exit(-1);
            }
        }
    }
    public List getRequests(String type) {
        return requests.get(type);
    }
    public List consumeRequests(String type) {
        List<PersistentRequest> requests = this.requests.get(type);
        this.requests.remove(type);

        return requests;
    }
}
