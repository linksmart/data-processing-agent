package pwalgui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.client.WebClient;


public class PwalApplication implements ApplicationConfiguration {

    public void configure(Application application) {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(WebClient.PAGE_TITLE, "PWAL");
        application.addEntryPoint("/PWAL", PwalHomePage.class, properties);
        application.addEntryPoint("/", PwalDevicesListTree.class, properties);
        application.addStyleSheet( RWT.DEFAULT_THEME_ID, "theme/theme.css" );
    }

}
