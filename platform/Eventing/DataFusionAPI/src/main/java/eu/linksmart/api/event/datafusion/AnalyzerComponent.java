package eu.linksmart.api.event.datafusion;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 14.12.2015 a researcher of Fraunhofer FIT.
 */
public interface AnalyzerComponent {
    static public final Map<String,ArrayList<String>> loadedComponents = new Hashtable<>();

    public String getImplementationName();
    public String getImplementationOf();

}
