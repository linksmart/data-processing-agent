package eu.linksmart.services.event.connectors;

import eu.almanac.event.datafusion.utils.generic.Component;
import eu.linksmart.api.event.components.Feeder;
import eu.linksmart.api.event.components.IncomingConnector;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.services.event.feeders.EventFeeder;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.services.event.intern.Utils;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.serialization.DefaultDeserializer;
import eu.linksmart.services.utils.serialization.Deserializer;
import org.slf4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by José Ángel Carvajal on 19.06.2017 a researcher of Fraunhofer FIT.
 */
public class BigFileConnector extends Component implements IncomingConnector {
    static protected Logger loggerService = Utils.initLoggingConf(FileConnector.class);
    static protected Configurator conf =  Configurator.getDefaultConfig();
    final Class<? extends EventEnvelope> type;
    protected List<String> filePaths = new ArrayList<>();

    public BigFileConnector(Class<? extends EventEnvelope> type, String... filePaths) throws UntraceableException, TraceableException {
        super(FileConnector.class.getSimpleName(), "Feeder that inserts statements and Events at loading time", Feeder.class.getSimpleName());

        this.type = type;

        for(String f: filePaths)
            if(f!=null&&!f.equals(""))
                this.filePaths.add(f);

    }
    public void loadFiles(){
        filePaths.forEach(this::loadFile);

    }
    protected void loadFile(String filePath){

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
                if(inputStream.markSupported())
                    found =true;
            }
            if(!found)
                loggerService.warn("There is no bootstrapping file ");
            else {

                loadStream(inputStream);
            }

        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
        } finally {
            try {
                assert inputStream != null;
                inputStream.close();
            } catch (IOException e) {
                loggerService.error(e.getMessage(), e);
            }
        }
    }
    protected void loadStream(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String strLine;
        EventEnvelope eventList;

        //Read File Line By Line
        while ((strLine = br.readLine()) != null)   {

            try {

                eventList = SharedSettings.getDeserializer().parse(strLine, type);
                EventFeeder.getFeeder().feed(eventList);

            } catch ( Exception e) {
                loggerService.error(e.getMessage(),e);
            }

        }
        //Close the input stream
        br.close();
    }



    @Override
    public boolean isUp() {
        return true;
    }

}
