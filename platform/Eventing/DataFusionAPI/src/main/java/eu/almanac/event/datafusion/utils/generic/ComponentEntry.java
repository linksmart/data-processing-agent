package eu.almanac.event.datafusion.utils.generic;

/**
 * Created by José Ángel Carvajal on 14.12.2015 a researcher of Fraunhofer FIT.
 */
public class ComponentEntry {
    private String implementationName;

    private String implantationOf;
    public String getImplantationOf() {
        return implantationOf;
    }

    public void setImplantationOf(String implantationOf) {
        this.implantationOf = implantationOf;
    }

    public String getImplementationName() {
        return implementationName;
    }

    public void setImplementationName(String implementation) {
        this.implementationName = implementation;
    }

}
