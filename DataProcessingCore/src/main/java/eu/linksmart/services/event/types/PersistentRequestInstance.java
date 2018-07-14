package eu.linksmart.services.event.types;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import eu.linksmart.api.event.types.PersistentRequest;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.services.utils.configuration.Configurator;
import io.swagger.annotations.ApiModelProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import eu.linksmart.api.event.types.JsonSerializable;

import java.io.*;
import java.util.*;

/**
 * Created by José Ángel Carvajal on 30.10.2017 a researcher of Fraunhofer FIT.
 */
public abstract class PersistentRequestInstance implements PersistentRequest, JsonSerializable {
    protected static final transient Logger loggerService = LogManager.getLogger(PersistentRequestInstance.class);
    protected static final transient Configurator conf = Configurator.getDefaultConfig();
    private static final transient Timer persistentRequestTracker = new Timer();
    private static final transient Map<String, PersistentRequestInstance> requests = new HashMap<>();

    public static String getPersistentFile() {
        return persistentFile;
    }

    private static final transient String persistentFile = conf.containsKeyAnywhere(Const.CONNECTOR_PERSISTENT_FILE) ? conf.getString(Const.CONNECTOR_PERSISTENT_FILE) + "-" + SharedSettings.getId() + ".json" : null;


    @JsonProperty("persistent")
    protected boolean persistent = false;

    @JsonProperty("essential")
    protected boolean essential = false;

    @ApiModelProperty(notes = "Unique identifier of the statement in the agent")
    @JsonProperty("id")
    protected String id = getId();

    public void addToRequests(String id) {
        requests.put(id, this);
    }

    public void removeFromRequests(String id) {
        requests.remove(id);
    }

    @JsonProperty("persistent")
    @ApiModelProperty(notes = "Indicates if the request should be stored persistently")
    @Override
    public boolean isPersistent() {
        return persistent;
    }

    @JsonProperty("essential")
    @ApiModelProperty(notes = "Indicates if the request fails to be loaded the service should be crash or not. Note: if the agent is not configured to crash, it will not crash regardless of this setting")
    @Override
    public boolean isEssential() {
        return essential;
    }

    @JsonProperty("persistent")
    @ApiModelProperty(notes = "Indicates if the request should be stored persistently")
    @Override
    public void isPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    public String getId() {
        if (id == null || id.equals(""))
            id = eu.linksmart.services.utils.function.Utils.hashIt((new Date()).toString());
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("essential")
    @ApiModelProperty(notes = "Indicates if the request fails to be loaded the service should be crash or not. Note: if the agent is not configured to crash, it will not crash regardless of this setting")
    @Override
    public void isEssential(boolean essential) {
        if (!conf.getBoolean(Const.PERSISTENT_ENABLED) &&
                !conf.getBoolean(Const.FAIL_IF_PERSISTENCE_FAILS) &&
                essential
                ) {
            loggerService.warn("The essentiality feature cannot be held by the agent " + SharedSettings.getId() + ", due to this setting is not enabled in it");
            return;
        }
        this.essential = essential;
    }

    public void destroy() throws Exception {

        removeFromRequests(this.getId());
    }

    static {
        if (persistentFile != null) {

            persistentRequestTracker.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            synchronized (persistentFile) {
                                OutputStream outputStream = null;
                                try {
                                    Map<String, List<String>> toStore = new HashMap<>();
                                    requests.values().stream().filter(p -> p.persistent).forEach(r -> {

                                        try {
                                            if (!toStore.containsKey(r.getClass().getCanonicalName()))
                                                toStore.put(r.getClass().getCanonicalName(), new ArrayList<>());

                                            String serialized = Base64.getEncoder().encodeToString(SharedSettings.getSerializer().serialize(r));

                                            if (!toStore.get(r.getClass().getCanonicalName()).contains(serialized)) {
                                                toStore.get(r.getClass().getCanonicalName()).add(serialized);
                                            }
                                        } catch (Exception e) {
                                            loggerService.error(e.getMessage(), e);
                                            System.exit(-1);
                                        }
                                    });

                                    outputStream = new FileOutputStream(persistentFile);
                                    if (!toStore.isEmpty()) {
                                        loggerService.info("Writing persistent file " + persistentFile);
                                        outputStream.write(SharedSettings.getSerializer().serialize(toStore));

                                    } else {
                                        loggerService.info("Clearing persistent file " + persistentFile);
                                    }
                                    outputStream.flush();
                                    outputStream.close();
                                } catch (IOException e) {
                                    loggerService.error(e.getMessage(), e);
                                } finally {
                                    try {
                                        if (outputStream != null)
                                            outputStream.close();
                                    } catch (Exception ignored) {
                                    }
                                }
                            }


                        }
                    },
                    6000,
                    conf.getInt(Const.PERSISTENT_STORAGE_PERIOD)
            );
        }

    }
}
