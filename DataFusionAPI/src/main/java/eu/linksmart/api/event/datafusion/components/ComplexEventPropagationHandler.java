package eu.linksmart.api.event.datafusion.components;

/**
 * Created by Jose Angel Carvajal on 13.08.2015 a researcher of Fraunhofer FIT.
 */
public interface ComplexEventPropagationHandler extends ComplexEventHandler {
    /***
     * Location are the brokers unknown with an alias by the Handlers
     * */

    public void setEnveloper(Enveloper enveloper);
    public void setPublisher(Publisher publisher);
    public void setSerializer(Serializer serializer);

    public Enveloper getEnveloper();
    public Publisher getPublisher();
    public Serializer getSerializer();


}
