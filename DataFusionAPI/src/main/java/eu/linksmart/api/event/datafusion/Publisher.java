package eu.linksmart.api.event.datafusion;

import java.util.AbstractMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 23.08.2016 a researcher of Fraunhofer FIT.
 */
public interface Publisher {


    boolean publish(byte[] payload);

    boolean publish(byte[] payload, String output, String scope);

    boolean publish(byte[] payload, String output);
    public List<String> getOutputs() ;

    public void setOutputs(List<String> outputs) ;

    public List<String> getScopes() ;

    public void setScopes(List<String> scopes);

    public String getId();

    public void setId(String id) ;
    void close();
}
