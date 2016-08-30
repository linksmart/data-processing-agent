package eu.linksmart.api.event.datafusion.types;

import java.util.Collection;

/**
 * Created by José Ángel Carvajal on 15.07.2016 a researcher of Fraunhofer FIT.
 */
public interface Responses <Resource> {


    public Collection<GeneralRequestResponse> getResponses() ;

    public void setResponses(Collection<GeneralRequestResponse> generalRequestResponses) ;
    public void addAllResponses(Collection<GeneralRequestResponse> generalRequestResponses) ;
    public Resource getResources() ;

    public void setResources( Resource resources) ;
    public void addResponse(GeneralRequestResponse GeneralRequestResponse);
    public boolean containsSuccess();
    GeneralRequestResponse getResponsesTail();

}
