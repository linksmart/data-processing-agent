package eu.linksmart.services.event.core.extensions;

import eu.linksmart.api.event.exceptions.StatementException;
import eu.linksmart.api.event.types.Statement;
import eu.linksmart.services.event.handler.ComplexEventHandler;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.services.utils.serialization.Serializer;

/**
 * Created by José Ángel Carvajal on 16.10.2017 a researcher of Fraunhofer FIT.
 */
public class JwsHandler extends ComplexEventHandler {
    public JwsHandler(Statement query) throws StatementException, Exception {
        super(query);
        serializer = JWS.getSerializer();
    }
}
