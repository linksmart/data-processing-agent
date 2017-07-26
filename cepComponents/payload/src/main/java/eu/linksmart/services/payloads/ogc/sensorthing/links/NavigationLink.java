package eu.linksmart.services.payloads.ogc.sensorthing.links;

import eu.linksmart.services.payloads.ogc.sensorthing.CommonControlInfo;

/**
 * Created by José Ángel Carvajal on 26.07.2017 a researcher of Fraunhofer FIT.
 */
public interface NavigationLink {

    static String getSelfLink(String clazz, String id, String linking) {
        return clazz.replace("Impl","")+"("+id+")/"+linking;
    }
}
