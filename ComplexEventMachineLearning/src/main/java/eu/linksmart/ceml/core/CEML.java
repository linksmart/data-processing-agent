package eu.linksmart.ceml.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


import eu.linksmart.api.event.datafusion.*;
import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.logging.LoggerService;
import eu.linksmart.gc.utils.function.Utils;

/**
 * Created by angel on 13/11/15.
 */
public class CEML implements AnalyzerComponent {

    static AnalyzerComponent info;
    static private Configurator conf = Configurator.getDefaultConfig();
    static private LoggerService loggerService = Utils.initDefaultLoggerService(CEML.class);

    // created learning objects and the type of them
    static protected Map<String,Object> learningObjects = new HashMap<>(),  objectsTypes = new HashMap<>();
    // defined which algorithms use which data structure
    static protected Map<String, String> learningObjectDataStructure = new HashMap<>();

    static protected Map<String, Boolean> builtLearningObject = new HashMap<>();

    static public boolean createLearningObject(String objectName, String objectType) throws Exception {

        //TODO: add rollback mechanism in case last second fail
        //TODO: add initialization API



        if(learningObjects.containsKey(objectName))
            return false;

        learningObjects.put(objectName,Class.forName(objectType).newInstance());
        objectsTypes.put(objectName,objectType);

        //Hardcode HACK to set defaults, initialization API missing
       /* if(objectType.equals( "weka.classifiers.functions.SGD"))
            ((SGD)learningObjects.get(objectName)).setLossFunction(new SelectedTag(SGD.SQUAREDLOSS, SGD.TAGS_SELECTION));
        */
        for (CEPEngine dfw: CEPEngine.instancedEngines.values()      ) {
            CEPEngineAdvanced extended = dfw.getAdvancedFeatures();
            if(extended!=null)
                extended.insertObject(objectName,learningObjects.get(objectName));

        }



        return true;
    }

    static public boolean destroyLearningObject(String objectName){
        if(learningObjects.containsKey(objectName))
            return learningObjects.remove(objectName)!=null;
        return false;

    }
    static public Object getLearningObject(String objectName){
        return learningObjects.get(objectName);
    }

    static public Object invoke(String objectName, String methodName, Object... args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if(!learningObjects.containsKey(objectName))
            return null;
        Object lmObject = learningObjects.get(objectName);
        Class aClass = Class.forName( objectsTypes.get(objectName).toString());

        Class[] types=null;
        if(args!=null) {
            types = new Class[args.length];

            for (int i = 0; i < args.length; i++) {
                types[i] = Class.forName(args[i].getClass().getCanonicalName());

            }
        }

        Method method= aClass.getMethod(methodName,types);
       return method.invoke(lmObject,args);

    }


    static private Method getMethod(Class theClass, String methodName, Object... args){

        Class[] types=null;
        try {

            if(args!=null) {
                types = new Class[args.length];

                for (int i = 0; i < args.length; i++) {
                    types[i] = Class.forName(args[i].getClass().getCanonicalName());

                }
            }

            return  theClass.getMethod(methodName,types);

        }catch (Exception e){
            loggerService.error(e.getMessage(),e);
        }
        return null;

    }



}
