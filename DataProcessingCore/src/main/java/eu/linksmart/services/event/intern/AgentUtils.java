package eu.linksmart.services.event.intern;

import eu.linksmart.services.utils.configuration.Configurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by José Ángel Carvajal on 12.08.2015 a researcher of Fraunhofer FIT.
 */
public class AgentUtils extends eu.linksmart.services.utils.function.Utils {

    protected transient static Logger loggerService = LogManager.getLogger(AgentUtils.class);
    protected transient static Configurator conf =  Configurator.getDefaultConfig();
    public static String topicReplace(String originalTopic){
        if(originalTopic!=null) {
            if (SharedSettings.getId() != null)
                originalTopic = originalTopic
                        .replace("<id>", SharedSettings.getId());
            if (SharedSettings.getLs_code() != null)
                originalTopic = originalTopic.replace("<ls_code>", SharedSettings.getLs_code());
        }
        return originalTopic;
    }


}
