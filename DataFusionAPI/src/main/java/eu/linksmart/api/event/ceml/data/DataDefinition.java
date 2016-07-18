package eu.linksmart.api.event.ceml.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
 class DataDefinition implements DataDescriptors{

    private final int size;
    private final List<DataNominalDescriptor> nominalDescriptors;

    public DataDefinition(int n){
        this.size=n;
        nominalDescriptors = null;
    }

    public DataDefinition(DataNominalDescriptor... definitions){
        this.size=definitions.length;

        nominalDescriptors =Arrays.asList(definitions);
    }

    public int getSize() {
        return size;
    }

    public List<DataNominalDescriptor> getNominalDescriptors() {
        return nominalDescriptors;
    }
    public DataNominalDescriptor getNominalDescriptor(int i) {
        return nominalDescriptors.get(i);
    }

}
