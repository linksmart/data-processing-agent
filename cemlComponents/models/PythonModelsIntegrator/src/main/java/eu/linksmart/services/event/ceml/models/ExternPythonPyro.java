package eu.linksmart.services.event.ceml.models;


import eu.linksmart.api.event.ceml.evaluation.TargetRequest;
import eu.linksmart.api.event.ceml.model.Model;
import eu.linksmart.services.event.intern.SharedSettings;

import java.util.*;

import net.razorvine.pyro.*;

/**
 * Created by Farshid Tavakolizadeh on 08.12.2016
 * Class implementing pyro remote object calls
 */


public class ExternPythonPyro extends PythonPyroBase<Object> {

    static {
        Model.loadedModels.put(ExternPythonPyro.class.getSimpleName(),ExternPythonPyro.class);

        SharedSettings.getSerializer().addModule( "PyroSerialier",PyroProxy.class, new PyroProxySerializer());
    }
    public ExternPythonPyro(List<TargetRequest> targets, Map<String, Object> parameters, Object learner) {
        super(targets, parameters, learner);
    }
}