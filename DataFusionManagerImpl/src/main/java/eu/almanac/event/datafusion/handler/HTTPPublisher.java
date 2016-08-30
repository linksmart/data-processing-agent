package eu.almanac.event.datafusion.handler;

import eu.almanac.event.datafusion.intern.Const;
import eu.linksmart.api.event.datafusion.components.Publisher;
import eu.linksmart.api.event.datafusion.Statement;
import eu.linksmart.api.event.datafusion.StatementException;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.function.Utils;
import org.slf4j.Logger;
import org.apache.http.client.fluent.*;

import java.util.*;

/**
 * Created by José Ángel Carvajal on 23.08.2016 a researcher of Fraunhofer FIT.
 */
public class HTTPPublisher implements Publisher{
    private static final String DEFAULT = "default";
    private List<String> outputs;
    private List<String> scopes;
    private String id;
    private Logger loggerService = Utils.initLoggingConf(this.getClass());
    private transient Configurator conf = Configurator.getDefaultConfig();
    private Map<String,Request> requesters = new HashMap<>();

    /***
     * Location are the brokers unknown with an alias by the Handlers
     * */
    public final static Map<String,Map.Entry<String,String>> knownInstances= new Hashtable<>();
    public static boolean addKnownLocations(String statement) throws StatementException {
        String[] nameURL = statement.toLowerCase().replace("add instance", "").trim().split("=");
        if (nameURL.length == 2) {
            String namePort[] = nameURL[1].split(":");

            knownInstances.put(nameURL[0], new AbstractMap.SimpleImmutableEntry<>(namePort[0], namePort[1]));

        } else {
            return false;
        }
        return true;
    }
    static {
        List<Object> alias = Configurator.getDefaultConfig().getList(Const.EVENTS_OUT_HTTP_SERVERS_ALIASES_CONF_PATH);
        List<Object> brokerHostname = Configurator.getDefaultConfig().getList(Const.EVENTS_OUT_HTTP_SERVERS_CONF_PATH);
        List<Object> brokerPort = Configurator.getDefaultConfig().getList(Const.EVENTS_OUT_HTTP_SERVERS_PORT_CONF_PATH);
        if(alias.size()!=brokerHostname.size()&& alias.size()!=brokerPort.size())
            Utils.initLoggingConf(DefaultMQTTPublisher.class).error("Inconsistent configuration in "+
                            Const.EVENTS_OUT_HTTP_SERVERS_ALIASES_CONF_PATH+ " and/or " +
                            Const.EVENTS_OUT_HTTP_SERVERS_CONF_PATH+ " and/or " +
                            Const.EVENTS_OUT_HTTP_SERVERS_PORT_CONF_PATH
            );
        else {
            for(int i=0;i<alias.size();i++)
                knownInstances.put(
                        alias.get(i).toString(),
                        new AbstractMap.SimpleEntry<>(
                                brokerHostname.get(i).toString(),
                                brokerPort.get(i).toString()
                        )
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
        outputs = Arrays.asList(statement.getOutput());
        scopes =  Arrays.asList(statement.getScope());
        id = statement.getID();
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
                   if (!knownInstances.containsKey(scope.toLowerCase()))
                       loggerService.error("Scope:" + scope+ "not found");
                   else {
                       outputs.forEach(o ->
                                       requesters.put(
                                              makeUri(scope,o),
                                               Request.Post(
                                                       makeUri(scope,o)
                                                       )
                                       )
                       );
                       if(!requesters.containsKey(knownInstances.get(scope.toLowerCase()).getKey() +"/" + knownInstances.get(scope.toLowerCase()).getValue()))
                           requesters.put(
                                        makeUri(scope),
                                        Request.Post(
                                               makeUri(scope)
                                        )
                                   );
                   }
               }
       );
    }
    private String makeUri(String scope){
        return knownInstances.get(scope.toLowerCase()).getKey() +":" + knownInstances.get(scope.toLowerCase()).getValue();
    }
    private String makeUri(String scope, String output){
        return makeUri(scope)+ "/" + output;
    }

    @Override
    public boolean publish(byte[] payload) {
        requesters.values().forEach(r ->{
            try {r.bodyByteArray(payload).execute();}
            catch (Exception e){loggerService.error(e.getMessage(),e);}

        });
        return false;
    }

    @Override
    public boolean publish(byte[] payload, String output, String scope) {
        if(requesters.containsKey(makeUri(scope,output))){
            try {
                requesters.get(makeUri(scope,output)).bodyByteArray(payload).execute();
                return true;
            }catch (Exception e){
                loggerService.error(e.getMessage(),e);
                return false;
            }

        }

        return false;
    }

    @Override
    public boolean publish(byte[] payload, String output) {
        if(requesters.containsKey(makeUri(DEFAULT,output))){
            try {
                requesters.get(makeUri(DEFAULT,output)).bodyByteArray(payload).execute();
                return true;
            }catch (Exception e){
                loggerService.error(e.getMessage(),e);
                return false;
            }

        }

        return false;
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
