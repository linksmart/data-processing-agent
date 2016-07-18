package eu.linksmart.api.event.datafusion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 15.07.2016 a researcher of Fraunhofer FIT.
 */
public interface Responses <Resource> {


    public Collection<StatementResponse> getResponses() ;

    public void setResponses(Collection<StatementResponse> StatementResponses) ;
    public void addAllResponses(Collection<StatementResponse> StatementResponses) ;
    public Resource getResources() ;

    public void setResources( Resource resources) ;
    public void addResponse(StatementResponse StatementResponse);
    public boolean containsSuccess();

}
