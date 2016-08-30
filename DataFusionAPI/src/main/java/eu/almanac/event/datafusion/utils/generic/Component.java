package eu.almanac.event.datafusion.utils.generic;

import eu.linksmart.api.event.datafusion.components.AnalyzerComponent;

import java.util.Hashtable;

/**
 * Created by José Ángel Carvajal on 14.12.2015 a researcher of Fraunhofer FIT.
 */
public abstract class Component {

    ComponentInfo info =  new ComponentInfo();
    public Component(String implName, String desc, String implOf) {
        info.setImplementationName(implName);
        info.setImplementationOf(new String[]{implOf});
        info.setDescription(desc);

        if(!AnalyzerComponent.loadedComponents.containsKey(info.getImplementationName()))
            AnalyzerComponent.loadedComponents.put(info.getImplementationName(), new Hashtable<Component, ComponentInfo>());

       AnalyzerComponent.loadedComponents.get(info.getImplementationName()).put(this,info);

    }

    public Component(String implName, String desc, String... implOf) {
        info.setImplementationName(implName);
        info.setImplementationOf(implOf);
        info.setDescription(desc);

        if(!AnalyzerComponent.loadedComponents.containsKey(info.getImplementationName()))
            AnalyzerComponent.loadedComponents.put(info.getImplementationName(), new Hashtable<>());

        AnalyzerComponent.loadedComponents.get(info.getImplementationName()).put(this, info);

    }

}
