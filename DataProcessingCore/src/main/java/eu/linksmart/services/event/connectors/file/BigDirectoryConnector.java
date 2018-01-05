package eu.linksmart.services.event.connectors.file;

import eu.linksmart.services.event.connectors.FileConnector;
import eu.linksmart.services.event.feeders.EventFeeder;

import java.io.*;
import java.util.Arrays;

/**
 * Created by José Ángel Carvajal on 02.12.2016 a researcher of Fraunhofer FIT.
 */
public class BigDirectoryConnector extends FileConnector {


    @Override
    protected void loadFile(String filePath) {
        try {

            File folder = new File(filePath);

            if(folder.exists() && folder.isDirectory())
                Arrays.stream(folder.listFiles()).forEach(this::readLineByLineOf);
            else
                loggerService.error("The given path "+filePath+" is either a file or doesn't exist!");

        }catch (Exception e){
            loggerService.error(e.getMessage(),e);
        }
    }

    protected void readLineByLineOf(File file){
        try {
            FileInputStream fstream = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

            String strLine;

            //Read File Line By Line
            boolean next = (strLine = br.readLine()) != null;
            while (next) {
                try {
                    loadStream(strLine);
                    next = (strLine = br.readLine()) != null;
                } catch (IOException e) {

                    loggerService.error(e.getMessage(),e);
                    next =false;
                }
            }

            //Close the input stream
            br.close();
        }catch (Exception e){
            loggerService.error(e.getMessage(),e);
        }
    }

    @Override
    protected void loadStream(String inputStream) {
            EventFeeder.getFeeder().feed(inputStream);

    }
}
