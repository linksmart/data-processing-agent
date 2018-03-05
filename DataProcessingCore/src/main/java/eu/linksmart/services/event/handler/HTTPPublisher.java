package eu.linksmart.services.event.handler;

import eu.linksmart.services.event.intern.Const;
import eu.linksmart.api.event.components.Publisher;
import eu.linksmart.api.event.types.Statement;
import eu.linksmart.api.event.exceptions.StatementException;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.function.Utils;
import io.swagger.client.ApiClient;
import io.swagger.client.api.ScApi;
import io.swagger.client.model.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.http.client.fluent.*;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.util.*;

/**
 * Created by José Ángel Carvajal on 23.08.2016 a researcher of Fraunhofer FIT.
 */
public class HTTPPublisher implements Publisher{
    private static final String DEFAULT = "default";
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
    private String SC_API_NAME = "HTTP";

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
        List<Object> brokerHostname = Configurator.getDefaultConfig().getList(Const.EVENTS_OUT_HTTP_SERVERS_CONF_PATH);
        if(alias.size()!=brokerHostname.size())
            LogManager.getLogger(DefaultMQTTPublisher.class).error("Inconsistent configuration in "+
                            Const.EVENTS_OUT_HTTP_SERVERS_ALIASES_CONF_PATH+ " and/or " +
                            Const.EVENTS_OUT_HTTP_SERVERS_CONF_PATH+ " and/or "
            );
        else {
            for(int i=0;i<alias.size();i++)
                knownInstances.put(
                        alias.get(i).toString().trim(),
                        brokerHostname.get(i).toString().trim()

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

    public HTTPPublisher(Statement statement) {
        outputs = statement.getOutput();
        scopes =  statement.getScope();
        id = statement.getId();
        try {
            initScopes();
        } catch ( StatementException e) {
            loggerService.error(e.getMessage(), e.getCause());
        }

    }
    public HTTPPublisher(String id,String[] outputs, String[] scopes){
        this.outputs =  Arrays.asList(outputs);
        this.scopes =  Arrays.asList(scopes);
        this.id = id;

        try {
            initScopes();
        } catch ( StatementException e) {
            loggerService.error(e.getMessage(), e.getCause());
        }

    }

    private void initScopes() throws StatementException {


       scopes.forEach(scope-> {
                   if (!knownInstances.containsKey(scope.toLowerCase())) {
                       try {

                           if (SCclient != null ){

                               Service service =null;
                               try{
                                   service =SCclient.idGet(scope);
                               }catch (RestClientException e){
                                   loggerService.error(e.getMessage(),e);

                               }

                               final URI url = new URI(service.getApis().get(SC_API_NAME));


                               outputs.forEach(o ->
                                       requesters.put(
                                               url.toString()+o,
                                               Request.Post(
                                                       makeUri(url.toString(),o)
                                               )
                                       )
                               );

                           }else
                               loggerService.error("Scope:" + scope + "not found");
                       }catch (Exception ignored){
                           //nothing
                       }
                   }else {

                       outputs.forEach(o ->
                                       requesters.put(
                                              scope+o,
                                               Request.Post(
                                                       makeUri(scope,o)
                                                       )
                                       )
                       );

                   }
               }
       );
    }
    private String makeUri(String scope, String output){
        return knownInstances.get(scope)+ output;
    }

    @Override
    public boolean publish(byte[] payload) {
        requesters.values().forEach(r ->{
            try {
                Response response = r.bodyByteArray(payload).execute();
            }
            catch (Exception e){
                loggerService.error(e.getMessage(),e);
            }

        });
        return false;
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
