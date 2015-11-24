package de.fraunhofer.fit.event.ceml;

import java.awt.image.ShortLookupTable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


import de.fraunhofer.fit.event.ceml.type.DataSetStructure;
import de.fraunhofer.fit.event.ceml.type.DataStructure;
import de.fraunhofer.fit.event.ceml.type.InstancesStructure;
import de.fraunhofer.fit.payload.impress.GPRTtype;
import eu.linksmart.api.event.datafusion.DataFusionWrapper;
import eu.linksmart.api.event.datafusion.DataFusionWrapperAdvanced;
import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.logging.LoggerService;
import eu.linksmart.gc.utils.function.Utils;
import weka.classifiers.Classifier;
import weka.classifiers.UpdateableClassifier;
import weka.classifiers.functions.SGD;
import weka.core.*;

/**
 * Created by angel on 13/11/15.
 */
public class CEML {
    static private Configurator conf = Configurator.getDefaultConfig();
    static private LoggerService loggerService = Utils.initDefaultLoggerService(CEML.class);

    // created learning objects and the type of them
    static protected Map<String,Object> learningObjects = new HashMap<>(),  objectsTypes = new HashMap<>();
    // describe the data structure used in the learning algorithms
    static protected Map<String, DataStructure> loadedStructures = new HashMap<>();
    // defined which algorithms use which data structure
    static protected Map<String, String> learningObjectDataStructure = new HashMap<>();

    static protected Map<String, Boolean> buildedLearningObject = new HashMap<>();

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
        for (DataFusionWrapper dfw: DataFusionWrapper.instancedEngines.values()      ) {
            DataFusionWrapperAdvanced extended = dfw.getAdvancedFeatures();
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


   static private DataStructure getStructureLearningObject(String learningObjectName){
       // do the learning object has a data structure which can work with?
       if(!learningObjectDataStructure.containsKey(learningObjectName))
           return null;
       return loadedStructures.get(learningObjectDataStructure.get(learningObjectName));

   }
    static private Object getBuildedLerningObject( String learningObjectName){
        // do the learning object exist?
        if(!learningObjects.containsKey(learningObjectName))
            return null;
        Object lerner = learningObjects.get(learningObjectName);

        // do the learning object has a data structure which can work with?
        DataStructure dataStructure = null;
        if(( dataStructure = getStructureLearningObject(learningObjectName))==null)
            return null;

        if(!buildedLearningObject.containsKey(learningObjectName))
            buildedLearningObject.put(learningObjectName, false);

        if(!buildedLearningObject.get(learningObjectName)) {
          /*           TODO: do this with reflection
          Method mth = getMethod(
                    lerner.getClass(),
                    "buildClassifier",
                     dataStructure.getInstances()
            );
            if(mth==null)
                return false;
            try {
                mth.invoke(lerner, dataStructure.getInstances());
            } catch (Exception e) {
                loggerService.error(e.getMessage(),e);
                return false;
            }*/
            try {
                ((Classifier)lerner).buildClassifier(dataStructure.getInstances());
            } catch (Exception e) {
                loggerService.error(e.getMessage(),e);
                return null;

            }
            buildedLearningObject.put(learningObjectName,true);
        }

        return lerner;
    }
    static private Instance createInstece(int n, Instances instances){
        Instance inst = new DenseInstance(n);

        inst.setDataset(instances);
        return inst;
    }
    static private boolean learn(Object lerner,Instance inst){
        try {
           /* Method mth = getMethod(
                    lerner.getClass(),
                    "updateClassifier",
                   inst
            );
            mth.invoke(lerner,inst);*/
            ((UpdateableClassifier)lerner).updateClassifier(inst);
        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
            return false;
        }
        return true;
    }
    static public boolean classify(String learningObjectName, GPRTtype gprt, GPRTtype toClassify){

        DataStructure dataStructure = null;
        if(( dataStructure = getStructureLearningObject(learningObjectName))==null)
            return false;
        // do the learning object exist?
        if(!learningObjects.containsKey(learningObjectName))
            return false;
        Object lerner =getBuildedLerningObject(learningObjectName);


        String id = String.valueOf(gprt.getDeviceID())+String.valueOf(gprt.getVariableID());

        Instance inst = createInstece(dataStructure.getAttributes().size(), dataStructure.getInstances());

        inst.setValue(dataStructure.getAttributeByID(id) ,String.valueOf(gprt.getValue()));

        return learn(lerner, inst);

    }
    static public boolean classify(String learningObjectName, Map<String,Object> values, Object toClassify){

        //TODO: construct the classification vector from 1 to 5 with the target at end !
        Integer i =0;

        Object lerner = getBuildedLerningObject(learningObjectName);
        if(( lerner = getBuildedLerningObject(learningObjectName))==null)
            return false;

        DataStructure dataStructure = null;
        if(( dataStructure = getStructureLearningObject(learningObjectName))==null)
            return false;
        Instance instance = createInstece(values.size(),dataStructure.getInstances());

        for (String key: values.keySet()) {
            if(!dataStructure.getAttributes().containsKey(key))
                return false;

            Attribute att = dataStructure.getAttributes().get(key);

            if(values.get(key) instanceof GPRTtype) {
                GPRTtype gprt =(GPRTtype) values.get(key);
                if(att.isNumeric())
                    instance.setValue(att,getNumeric(gprt.getValue()));
                else
                    instance.setValue(att,gprt.getValue().toString());
            }else {
                if (att.isNumeric())
                    instance.setValue(att, getNumeric(values.get(key)));
                else
                    instance.setValue(att, values.get(key).toString());
            }
        }
        Attribute attribute = dataStructure.getAttributes().get(dataStructure.getAttributesStructures().get(dataStructure.getAttributesStructures().size()-1));
        if (attribute.isNumeric())
            instance.setValue(attribute, getNumeric(toClassify));
        else
            instance.setValue(attribute,toClassify.toString());

        return learn(lerner,instance);
    }
    static public boolean classify(String learningObjectName, GPRTtype[] values, GPRTtype toClassify){

        //TODO: construct the classification vector from 1 to 5 with the target at end !


        Object lerner = getBuildedLerningObject(learningObjectName);
        if(( lerner = getBuildedLerningObject(learningObjectName))==null)
            return false;

        DataStructure dataStructure = null;
        if(( dataStructure = getStructureLearningObject(learningObjectName))==null)
            return false;
        Instance instance = createInstece(values.length,dataStructure.getInstances());
        Integer i =0;
/**/
   /*     for (GPRTtype event : values) {
            String id = String.valueOf(values[i].getDeviceID())+String.valueOf(values[i].getVariableID());


            if(!dataStructure.getAttributes().containsKey(key))
                return false;

            Attribute att = dataStructure.getAttributes().get(key);

            if(values.get(key) instanceof GPRTtype) {
                GPRTtype gprt =(GPRTtype) values.get(key);
                if(att.isNumeric())
                    instance.setValue(att,getNumeric(gprt.getValue()));
                else
                    instance.setValue(att,gprt.getValue().toString());
            }else {
                if (att.isNumeric())
                    instance.setValue(att, getNumeric(values.get(key)));
                else
                    instance.setValue(att, values.get(key).toString());
            }
        }
        Attribute attribute = dataStructure.getAttributes().get(dataStructure.getAttributesStructures().get(dataStructure.getAttributesStructures().size()-1));
        if (attribute.isNumeric())
            instance.setValue(attribute, getNumeric(toClassify));
        else
            instance.setValue(attribute,toClassify.toString());

        return learn(lerner,instance);*/
        return false;
    }
    static private Double getNumeric(Object numeric){
        if(numeric instanceof Double)
            return (Double) numeric;
        if(numeric instanceof Integer)
            return Double.valueOf((Integer)numeric);
        if(numeric instanceof Float)
            return Double.valueOf((Float)numeric);
        if(numeric instanceof Long)
            return Double.valueOf((Long)numeric);
        if(numeric instanceof Short)
            return Double.valueOf((Short)numeric);

        return null;

    }
    static public boolean createDataStructure(DataStructure structure) throws Exception {

        structure.buildInstances();
        loadedStructures.put(structure.getName(),structure);
        for (String learningObjectName:structure.getUsedBy() ) {
            if(learningObjectDataStructure.containsKey(learningObjectName)){
                if(buildedLearningObject.containsKey(learningObjectName))
                    if(buildedLearningObject.get(learningObjectName)) {
                        throw new Exception("The selected learning object has already build being build around a data structure. " +
                                "Current version do no allowed changes after the learning object has being build. Remove the object to change it");
                    }
                learningObjectDataStructure.remove(learningObjectName);
                buildedLearningObject.remove(learningObjectName);

            }
            learningObjectDataStructure.put(learningObjectName,structure.getName());
            buildedLearningObject.put(learningObjectName,false);

        }
        return true;

    }
}
