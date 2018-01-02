package eu.linksmart.services.event.intern;

import eu.linksmart.services.event.feeders.StatementFeeder;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.function.Utils;
import org.slf4j.Logger;

/**
 * Created by José Ángel Carvajal on 12.08.2015 a researcher of Fraunhofer FIT.
 */
public class AgentUtils extends eu.linksmart.services.utils.function.Utils {

    protected transient static Logger loggerService = Utils.initLoggingConf(AgentUtils.class);
    protected transient static Configurator conf =  Configurator.getDefaultConfig();
    public static String topicReplace(String originalTopic){
        return originalTopic
                .replace("<id>",SharedSettings.getId())
                .replace("<ls_code>",SharedSettings.getLs_code());
    }


}
