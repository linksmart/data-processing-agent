package eu.linksmart.services.event.core;

import eu.linksmart.sdk.catalog.service.*;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.services.event.intern.Utils;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.mqtt.broker.StaticBroker;
import eu.linksmart.services.utils.mqtt.broker.StaticBrokerService;
import eu.linksmart.services.utils.serialization.JWSSerializer;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by José Ángel Carvajal on 13.10.2017 a researcher of Fraunhofer FIT.
 */
public class ServiceRegistratorService {

    private transient static Properties info = null;
    private final Registration myRegistration;
    private transient final static Logger loggerService = Utils.initLoggingConf(ServiceRegistratorService.class);
    private transient final static Configurator conf = Configurator.getDefaultConfig();
    public static ConcurrentMap<String,Object> meta = new ConcurrentHashMap<>();

    public ServiceRegistratorService() {
        myRegistration = new RegistrationDocumentImpl();
        myRegistration.setId(SharedSettings.getId());
        myRegistration.setDescription(conf.getString(Const.AGENT_DESCRIPTION));


        meta.forEach(myRegistration::addMeta);

        if (conf.getBoolean(Const.ENABLE_REST_API)) {
            APIDescriptor descriptor = new APIDescriptorImpl();
            try {
                String host = InetAddress.getLocalHost().getHostName(), port = conf.getString("server_port"), protocol = "http";
                if (conf.containsKeyAnywhere("server_ssl_key-store"))
                    protocol = "https";

                descriptor.setUrl(protocol + "://" + host + ":" + port + "/");
                descriptor.setProtocol(protocol.toUpperCase());
                myRegistration.addApis(descriptor);
                APIDoc doc = new APIDocImpl();
                doc.setDescription("Open API V2");
                doc.setUrl(descriptor.getUrl() + "swagger-ui.html");
                myRegistration.addExternalDocs(doc);

                StaticBroker broker = new StaticBroker(conf.getString(Const.LINKSMART_BROKER),SharedSettings.getSerializer().toString(myRegistration), conf.getString(Const.LINKSMART_SERVICE_WILL_TOPIC).replace("<id>", SharedSettings.getId())+SharedSettings.getId());

                broker.publish(conf.getString(Const.LINKSMART_REGISTRATION_TOPIC).replace("<id>", SharedSettings.getId())+SharedSettings.getId(), SharedSettings.getSerializer().serialize(myRegistration));

            } catch (Exception e) {
                loggerService.error(e.getMessage(), e);
            }
        }
        APIDescriptor descriptor = new APIDescriptorImpl();
        StaticBrokerService.brokerServices.values().forEach(broker -> {
            descriptor.setProtocol("MQTT");
            descriptor.setUrl(broker.getBrokerURL());
            myRegistration.addApis(descriptor);
                }
        );
    }
}
