package eu.linksmart.api.event.datafusion;

import eu.almanac.event.datafusion.utils.generic.Component;
import eu.almanac.event.datafusion.utils.generic.ComponentInfo;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 14.12.2015 a researcher of Fraunhofer FIT.
 */
public interface AnalyzerComponent {
    static public final Map<String,Map<Component,ComponentInfo>> loadedComponents = new Hashtable();



}
