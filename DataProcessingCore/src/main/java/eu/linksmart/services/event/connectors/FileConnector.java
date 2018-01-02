package eu.linksmart.services.event.connectors;

import eu.linksmart.api.event.components.IncomingConnector;
import eu.linksmart.services.event.feeders.BootstrappingBeanFeeder;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.AgentUtils;
import eu.almanac.event.datafusion.utils.generic.Component;
import eu.linksmart.api.event.components.Feeder;

import eu.linksmart.services.utils.configuration.Configurator;
import org.slf4j.Logger;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by José Ángel Carvajal on 13.08.2015 a researcher of Fraunhofer FIT.
 */
public class FileConnector extends Component implements IncomingConnector {
    static protected Logger loggerService = AgentUtils.initLoggingConf(FileConnector.class);
    static protected Configurator conf =  Configurator.getDefaultConfig();
    protected List<String> filePaths = new ArrayList<>();

    public FileConnector(String... filePaths){
        super(FileConnector.class.getSimpleName(), "Feeder that inserts statements and Events at loading time", Feeder.class.getSimpleName());

        for(String f: filePaths)
            if(f!=null&&!f.equals(""))
                this.filePaths.add(f);

    }
    public void loadFiles(){
        filePaths.forEach(this::loadFile);

    }
    protected void loadFile(String filePath)  {

        InputStream inputStream = null;
        try {
            boolean found =false;
            File f = new File(filePath);
            if(f.exists() && !f.isDirectory()) {
                loggerService.info("Loading file "+ filePath);
                inputStream = new FileInputStream(filePath);

                found =true;
            }else {
                loggerService.info("Loading resource "+ filePath);
                ClassLoader classloader = Thread.currentThread().getContextClassLoader();
                inputStream = classloader.getResourceAsStream(filePath);

                found =inputStream != null && inputStream.markSupported();
            }
            if(!found)
                loggerService.warn("There is no bootstrapping file ");
            else
                loadStream(IOUtils.toString(inputStream));

        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
            if(conf.containsKeyAnywhere(Const.PERSISTENT_ENABLED) && conf.getBoolean(Const.PERSISTENT_ENABLED) &&
                    conf.containsKeyAnywhere(Const.FAIL_IF_PERSISTENCE_FAILS) && conf.getBoolean(Const.FAIL_IF_PERSISTENCE_FAILS)) {
                loggerService.error("Fail in persistence process and the programmatic exit is set");
                System.exit(-1);
            }
        } finally {
            try {
                assert inputStream != null;
                inputStream.close();
            } catch (IOException e) {
                loggerService.error(e.getMessage(), e);
            }
        }
    }
    protected void loadStream(String inputStream) throws IOException {
        BootstrappingBeanFeeder.feed(inputStream);
    }




    @Override
    public boolean isUp() {
        return false;
    }
}
