package de.fraunhofer.fit.event.ceml.type.requests;

import eu.linksmart.api.event.datafusion.DataFusionWrapper;
import eu.linksmart.api.event.datafusion.DataFusionWrapperAdvanced;
import weka.classifiers.Classifier;
import weka.core.Instances;

/**
 * Created by angel on 26/11/15.
 */
public class ModelStructure {
    protected String type;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
