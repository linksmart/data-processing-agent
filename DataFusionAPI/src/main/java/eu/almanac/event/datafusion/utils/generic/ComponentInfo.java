package eu.almanac.event.datafusion.utils.generic;

import eu.linksmart.api.event.datafusion.components.AnalyzerComponent;

/**
 * Created by José Ángel Carvajal on 15.12.2015 a researcher of Fraunhofer FIT.
 */
public class ComponentInfo implements AnalyzerComponent {

    protected String implementationName;


    protected String[] implementationOf;
    protected String description;


    public String getImplementationName() {
        return implementationName;
    }

    public String[] getImplementationOf() {
        return implementationOf;
    }

    public String getDescription() {
        return description;
    }

    public void setImplementationName(String implementationName) {
        this.implementationName = implementationName;
    }

    public void setImplementationOf(String[] implementationOf) {
        this.implementationOf = implementationOf;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
