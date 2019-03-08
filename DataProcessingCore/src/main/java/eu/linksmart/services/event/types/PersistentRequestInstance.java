package eu.linksmart.services.event.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.linksmart.api.event.types.JsonSerializable;
import eu.linksmart.api.event.types.PersistentRequest;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.services.utils.configuration.Configurator;
import io.swagger.annotations.ApiModelProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * Created by José Ángel Carvajal on 30.10.2017 a researcher of Fraunhofer FIT.
 */
public abstract class PersistentRequestInstance implements PersistentRequest, JsonSerializable {
    protected static final transient Logger loggerService = LogManager.getLogger(PersistentRequestInstance.class);
    protected static final transient Configurator conf = Configurator.getDefaultConfig();
    private static final transient Timer persistentRequestTracker = new Timer();
    private static final transient Map<String, PersistentRequestInstance> requests = new HashMap<>();
    private static transient boolean PersistentFileOnceExisted = false;

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

    public static void setPersistentFileOnceExisted(boolean existed) {
        PersistentFileOnceExisted = existed;
    }

    public static boolean getPersistentFileOnceExisted() {
        return PersistentFileOnceExisted;
    }

    public void addToRequests(String id) {
        synchronized (PersistentRequest.synchLock) {
            requests.put(id, this);
        }
    }

    public void removeFromRequests(String id) {
        synchronized (PersistentRequest.synchLock) {
            requests.remove(id);
        }
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
        if (id == null || id.equals("")) {
            id = UUID.randomUUID().toString();
            addToRequests(id);
        }
        return id;
    }

    public void setId(String id) {
        removeFromRequests(this.id);
        this.id = id;
        addToRequests(this.id);
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
        if (conf.getBoolean(Const.PERSISTENT_ENABLED) && persistentFile != null) {

            persistentRequestTracker.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            synchronized (PersistentRequest.synchLock) {
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


                                    File f = new File(persistentFile);
                                    if (!toStore.isEmpty()) {
                                        outputStream = new FileOutputStream(f);
                                        loggerService.info("Writing persistent file " + persistentFile);
                                        outputStream.write(SharedSettings.getSerializer().serialize(toStore));
                                        outputStream.flush();
                                        outputStream.close();
                                        setPersistentFileOnceExisted(true);
                                    } else if (f.exists() && getPersistentFileOnceExisted()) {
                                        outputStream = new FileOutputStream(f);
                                        outputStream.flush();
                                        outputStream.close();

                                        if (f.length() == 0) {
                                            loggerService.info("Deleting file " + persistentFile);
                                            f.delete();
                                        }
                                    }
                                } catch (IOException e) {
                                    loggerService.error(e.getMessage(), e);
                                } finally {
                                    loggerService.info("Finished with " + persistentFile);
                                    try {
                                        if (outputStream != null)
                                            outputStream.close();
                                    } catch (Exception ignored) {
                                    }
                                }
                            }
                        }
                    },
                    conf.getInt(Const.PERSISTENT_STORAGE_PERIOD),
                    conf.getInt(Const.PERSISTENT_STORAGE_PERIOD)
            );
        }
    }
}
