package eu.linksmart.services.event.ceml.api;

import com.fasterxml.jackson.core.type.TypeReference;
import eu.linksmart.api.event.ceml.CEMLRequest;
import eu.linksmart.api.event.types.impl.GeneralRequestResponse;
import eu.linksmart.services.event.ceml.core.CEML;
import eu.linksmart.services.event.ceml.core.CEMLManager;
import eu.linksmart.services.event.connectors.FileConnector;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.SharedSettings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by José Ángel Carvajal on 12.10.2016 a researcher of Fraunhofer FIT.
 */
public class FileCemlAPI extends FileConnector {
    public FileCemlAPI(String... filePaths) {
        super(filePaths);
    }

    @Override
    protected void loadStream(String inputStream){
        try {
            loggerService.info("Bootstrapping CEML request...");
            List<CEMLRequest> requests = (List<CEMLRequest>) SharedSettings.getDeserializer().parsePacked(inputStream,new TypeReference<List<CEMLManager>>(){});

            ArrayList<GeneralRequestResponse> responses = new ArrayList<>();

            requests.forEach(i->{

                Collection<GeneralRequestResponse> response = CEML.create(i).getResponses();

                if(response.stream().anyMatch(r->r.getStatus()>300)){
                    loggerService.error("CEML bootstrapping phase failed!");
                    if(conf.containsKeyAnywhere(eu.linksmart.services.event.intern.Const.FAIL_IF_PERSISTENCE_FAILS) && conf.getBoolean(Const.FAIL_IF_PERSISTENCE_FAILS) && i.isEssential()) {
                        loggerService.error("Fail loading statement " + i.getId() + " in persistence process and the essensiality setting is enabled (" + Const.FAIL_IF_PERSISTENCE_FAILS + ") exit is set");
                        System.exit(-1);
                    }
                }

                responses.addAll(response);
            });

            responses.stream().filter(i->i.getStatus()>299).forEach(i->loggerService.error(i.getHeadline()+": "+i.getMessage()));
            loggerService.info("... CEML request finished");

        } catch (Exception e) {

            loggerService.error(e.getMessage(),e);
        }
    }
}
