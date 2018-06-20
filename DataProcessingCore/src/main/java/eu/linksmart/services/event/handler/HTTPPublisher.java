package eu.linksmart.services.event.handler;

import com.google.common.io.CharStreams;
import eu.linksmart.api.event.components.CEPEngine;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.EventBuilder;
import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.api.event.components.Publisher;
import eu.linksmart.api.event.types.Statement;
import eu.linksmart.api.event.exceptions.StatementException;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.function.Utils;
import io.swagger.client.ApiClient;
import io.swagger.client.api.ScApi;
import io.swagger.client.model.Service;
import org.apache.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.http.client.fluent.*;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.*;

/**
 * Created by José Ángel Carvajal on 23.08.2016 a researcher of Fraunhofer FIT.
 */
public class HTTPPublisher implements Publisher{
    private final Class<? extends EventEnvelope> outputType;
    private List<String> outputs;
    private List<String> scopes;
    private String id;
    private Logger loggerService = LogManager.getLogger(this.getClass());
    private static transient Configurator conf = Configurator.getDefaultConfig();
    private Map<String,Request> requesters = new HashMap<>();
    // using host, port and protocol form Service Catalog
    static private transient ScApi SCclient = null;
    static {

        if(Utils.isRestAvailable(conf.getString(eu.linksmart.services.utils.constants.Const.LINKSMART_SERVICE_CATALOG_ENDPOINT))){
            ApiClient apiClient = new ApiClient();
            apiClient.setBasePath(conf.getString(eu.linksmart.services.utils.constants.Const.LINKSMART_SERVICE_CATALOG_ENDPOINT));
            SCclient = new ScApi(apiClient);
        }


    }
    /***
     * Location are the brokers unknown with an alias by the Handlers
     * */
    public final static Map<String,String> knownInstances= new Hashtable<>();

    public Statement.Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Statement.Publisher publisher) {
        this.publisher = publisher;
    }

    private Statement.Publisher publisher = Statement.Publisher.HTTP_POST;

    public static boolean addKnownLocations(String statement) throws StatementException {
        String[] nameURL = statement.toLowerCase().replace("add instance", "").trim().split("=");
        if (nameURL.length == 2) {

            knownInstances.put(nameURL[0].trim(), nameURL[1].trim());

        } else {
            return false;
        }
        return true;
    }
    static {
        List<Object> alias = Configurator.getDefaultConfig().getList(Const.EVENTS_OUT_HTTP_SERVERS_ALIASES_CONF_PATH);
        List<Object> defEndpoint = Configurator.getDefaultConfig().getList(Const.EVENTS_OUT_HTTP_SERVERS_CONF_PATH);
        if(alias.size()!=defEndpoint.size())
            LogManager.getLogger(DefaultMQTTPublisher.class).error("Inconsistent configuration in "+
                            Const.EVENTS_OUT_HTTP_SERVERS_ALIASES_CONF_PATH+ " and/or " +
                            Const.EVENTS_OUT_HTTP_SERVERS_CONF_PATH+ " and/or "
            );
        else {
            for(int i=0;i<alias.size();i++)
                if(conf.containsKeyAnywhere(Const.EVENTS_OUT_HTTP_SERVERS_CONF_PATH+"_"+alias.get(i).toString().trim()))
                knownInstances.put(
                        alias.get(i).toString().trim(),
                        conf.getString(Const.EVENTS_OUT_HTTP_SERVERS_CONF_PATH+"_"+alias.get(i).toString().trim())

                );
        }
    }
    public static boolean removeKnownLocations(String alias) throws StatementException {
        if( knownInstances.containsKey(alias))
            knownInstances.remove(alias);
        else
            return false;

        return true;
    }

    public HTTPPublisher(Statement statement) throws StatementException{
        outputs = statement.getOutput();
        scopes =  statement.getScope();
        id = statement.getId();
        publisher = statement.getPublisher();
        outputType = EventBuilder.getBuilder(statement.getResultType()).BuilderOf();

        initScopes(statement);


    }

    private void initScopes(Statement statement) throws StatementException {


        for (String scope : scopes) {
            if (!knownInstances.containsKey(scope)) {


                if (SCclient != null && statement.getLSApiKeyName()!=null) {

                    Service service = null;
                    try {
                        service = SCclient.idGet(scope);
                    } catch (RestClientException e) {
                        loggerService.error(e.getMessage(), e);
                        throw new StatementException(statement.getId(), "Bad Request", e.getMessage(), e);
                    }
                    try {
                        final URI url = new URI(service.getApis().get(statement.getLSApiKeyName()));
                        knownInstances.put(scope, url.toString());

                        outputs.forEach(o ->
                                requesters.put(
                                        scope + o,
                                        prepareRequest(scope, o)
                                )
                        );
                    } catch (Exception e) {

                        loggerService.error("Service catalog url is not an URL");
                        loggerService.error(e.getMessage(), e);
                        throw new StatementException(statement.getId(), "Bad Request", "Service catalog url is not an URL");
                    }


                } else {
                    loggerService.error("Scope:" + scope + " not found");
                    throw new StatementException(statement.getId(), "Bad Request", "Scope:" + scope + " not found");

                }

            } else {

                outputs.forEach(o ->
                        requesters.put(
                                scope + o,
                                prepareRequest(scope, o)
                        )
                );

            }
        }

    }
    private Request prepareRequest(String scope,String path){
        switch (publisher){
            case HTTP_GET:
            case REST_GET:
                return Request.Get(makeUri(scope,path));
            case HTTP:
            case REST:
            case HTTP_POST:
            case REST_POST:
            default:
                return Request.Post(makeUri(scope,path));
        }

    }
    private String makeUri(String scope, String output){
        return knownInstances.get(scope)+ output;
    }

    @Override
    public boolean publish(byte[] payload) {
        final boolean[] success = {true};
        requesters.values().forEach(r ->{
            try {
                processResponse( r.bodyByteArray(payload).execute());
            }
            catch (Exception e){
                loggerService.error(e.getMessage(),e);
                success[0] =false;
            }

        });
        return success[0];
    }

    private boolean processResponse(Response response) {
        HttpResponse httpResponse;
        try {
            httpResponse = response.returnResponse();
        } catch (IOException e) {
            loggerService.error(e.getMessage(), e);
            return false;
        }
        String rawEvent;
        try (final Reader reader = new InputStreamReader(httpResponse.getEntity().getContent())) {
            rawEvent = CharStreams.toString(reader);
        } catch (IOException e) {
            loggerService.error("remote endpoint gives following response code "+httpResponse.getStatusLine().getStatusCode()+" and the agent is unable to process the response body");
            loggerService.error(e.getMessage(), e);
            return false;
        }
        if (httpResponse.getStatusLine().getStatusCode() < 300) {
            switch (publisher) {
                case HTTP_GET:
                case REST_GET:
                    EventEnvelope envelope;
                    try {
                        envelope = SharedSettings.getDeserializer().parse(rawEvent, outputType);
                    } catch (IOException e) {
                        loggerService.error("message arrived but cannot be serialized to class "+outputType.getSimpleName()+" original payload "+rawEvent, e);
                        loggerService.error(e.getMessage(), e);
                        return false;
                    }
                    try {
                        CEPEngine.instancedEngines.values().iterator().next().addEvent(envelope, outputType);
                    } catch (TraceableException | UntraceableException e) {
                        loggerService.error(e.getMessage(), e);
                        return false;
                    }

            }
        }else {
            loggerService.error("remote endpoint gives following code error "+httpResponse.getStatusLine().getStatusCode()+" with  message body" + rawEvent);
        }



        return true;
    }
    @Override
    public boolean publish(byte[] payload, String output, String scope) {
        return publish(payload);
    }

    @Override
    public boolean publish(byte[] payload, String output) {
       return publish(payload);

    }

    @Override
    public List<String> getOutputs() {
        return outputs;
    }

    @Override
    public void setOutputs(List<String> outputs) {
        this.outputs = outputs;
    }

    @Override
    public List<String> getScopes() {
        return scopes;
    }

    @Override
    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void close() {

    }
}
