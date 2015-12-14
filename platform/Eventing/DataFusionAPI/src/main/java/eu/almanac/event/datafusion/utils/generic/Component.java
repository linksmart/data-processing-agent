package eu.almanac.event.datafusion.utils.generic;

import eu.linksmart.api.event.datafusion.AnalyzerComponent;

import java.util.ArrayList;

/**
 * Created by José Ángel Carvajal on 14.12.2015 a researcher of Fraunhofer FIT.
 */
public abstract class Component implements AnalyzerComponent {


    public Component() {
        if(!AnalyzerComponent.loadedComponents.containsKey(getImplementationOf()))
            AnalyzerComponent.loadedComponents.put(getImplementationOf(),new ArrayList<String>());
        AnalyzerComponent.loadedComponents.get(getImplementationOf()).add(getImplementationName());

    }

    @Override
    public String getImplementationName(){
        return this.getClass().getSimpleName();
    }
}
