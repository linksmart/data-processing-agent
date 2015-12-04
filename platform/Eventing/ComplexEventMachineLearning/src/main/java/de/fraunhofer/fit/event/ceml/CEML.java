package de.fraunhofer.fit.event.ceml;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


import de.fraunhofer.fit.event.ceml.type.Entry;
import de.fraunhofer.fit.event.ceml.type.requests.builded.DataStructure;
import de.fraunhofer.fit.event.ceml.type.requests.builded.LearningRequest;
import de.fraunhofer.fit.payload.impress.GPRTtype;
import eu.linksmart.api.event.datafusion.DataFusionWrapper;
import eu.linksmart.api.event.datafusion.DataFusionWrapperAdvanced;
import eu.linksmart.api.event.datafusion.Statement;
import eu.linksmart.api.event.datafusion.StatementException;
import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.logging.LoggerService;
import eu.linksmart.gc.utils.function.Utils;
import weka.classifiers.Classifier;
import weka.classifiers.UpdateableClassifier;
import weka.core.*;

/**
 * Created by angel on 13/11/15.
 */
public class CEML {
    static {
        for (DataFusionWrapper dfw: DataFusionWrapper.instancedEngines.values()      ) {
            DataFusionWrapperAdvanced extended = dfw.getAdvancedFeatures();
            if(extended!=null) {
                extended.insertObject("creator", new Entry(null,null) );
            }

        }
    }
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
    static public Instance createInstece(int n, Instances instances){
        Instance inst = new DenseInstance(n);

        inst.setDataset(instances);
        return inst;
    }
    static public boolean learn(Object lerner,Instance inst){
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

    static public int predict(Object lerner,Instance inst){
        try {
           /* Method mth = getMethod(
                    lerner.getClass(),
                    "updateClassifier",
                   inst
            );
            mth.invoke(lerner,inst);*/
            int index =-1;

            double possibilities[] = ((Classifier)lerner).distributionForInstance(inst), max =-1.0;
            for(int i=0;i<possibilities.length;i++)
                if(max<possibilities[i]) {
                    max = possibilities[i];
                    index = i;
                }

            return index;
        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
            return -1;
        }
    }



    static public boolean classify(String learningObjectName, GPRTtype gprt, Object toClassify){

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
        Attribute attribute = dataStructure.getAttributes().get(dataStructure.getAttributesStructures().get(dataStructure.getAttributesStructures().size()-1));
        if (attribute.isNumeric())
            inst.setValue(attribute, getNumeric(toClassify));
        else
            inst.setValue(attribute,toClassify.toString());
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
    static public boolean classify(String learningObjectName, GPRTtype[] values, Object toClassify){

        //TODO: construct the classification vector from 1 to 5 with the target at end !


        Object lerner = getBuildedLerningObject(learningObjectName);
        if(( lerner = getBuildedLerningObject(learningObjectName))==null)
            return false;

        DataStructure dataStructure = null;
        if(( dataStructure = getStructureLearningObject(learningObjectName))==null)
            return false;
        Instance instance = createInstece(dataStructure.getAttributes().size(),dataStructure.getInstances());
        Integer i =0;

        for (GPRTtype event : values) {
            String id = String.valueOf(values[i].getDeviceID())+String.valueOf(values[i].getVariableID())+i.toString();



            instance.setValue(dataStructure.getAttributeByID(id) ,String.valueOf(event.getValue()));
            i++;
        }
        Attribute attribute = dataStructure.getAttributes().get(dataStructure.getAttributesStructures().get(dataStructure.getAttributesStructures().size()-1).getAttributeName());
        if (attribute.isNumeric())
            instance.setValue(attribute, getNumeric(toClassify));
        else
            instance.setValue(attribute,toClassify.toString());

        return learn(lerner,instance);
    }
    static public Double getNumeric(Object numeric){
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
    static public Instance populateInstance(Map<String, Object> events, LearningRequest originalRequest){
        // TODO: check what makes more sense originalRequest.getData().getAttributes().size() or eventMap.size()
        Instance instance = CEML.createInstece(originalRequest.getData().getAttributes().size(),originalRequest.getData().getInstances());

        for(String key: events.keySet()){
            Object aux;
            String toCompare =  key;
            if(key.toLowerCase().equals("target"))
                toCompare = originalRequest.getData().getLearningTarget().name();

            if((aux=getObject(toCompare,originalRequest.getData().getAttributes()))!=null ){
                Attribute attribute = (Attribute)aux;
                if(attribute.isNumeric()){
                    Double input = CEML.getNumeric(events.get(key));
                    instance.setValue(attribute,input);

                }else if(attribute.isDate()&& events.get(key) instanceof Date){
                    String input = Utils.getDateFormat().format(events.get(key));
                    instance.setValue(attribute,input);

                }else {
                    String input = events.get(key).toString();
                    instance.setValue(attribute,input);
                }

            }else if(events.get(key) instanceof Object[]){
                popularVectorInstance(events.get(key),originalRequest,key,instance);
            }


        }
        return instance;
    }

    private static Instance popularVectorInstance(Object vector, LearningRequest originalRequest, String key, Instance instance){
        String toCompare;
        Object aux;
        Object[] auxs = (Object[])vector;
        for(int i =0; i<auxs.length;i++){
            toCompare = key+String.valueOf(i);
            if((aux=getObject(toCompare,originalRequest.getData().getAttributes()))!=null ){
                Attribute attribute = (Attribute)aux;
                if(attribute.isNumeric()){
                    Double input = CEML.getNumeric(auxs[i]);
                    instance.setValue(attribute,input);

                }else if(attribute.isDate()&& auxs[i]instanceof Date){
                    String input = Utils.getDateFormat().format(auxs[i]);
                    instance.setValue(attribute,input);

                }else {
                    String input = auxs[i].toString();
                    instance.setValue(attribute,input);
                }
            }
        }
        return instance;
    }
    private static Object getObject(String key, Map map){
        Object value=null;
        if (map.containsKey(key))
            value = map.get(key);
        if(map.containsKey(key.toLowerCase()))
            value = map.get(key.toLowerCase());
        if(map.containsKey(key.toUpperCase()))
            value = map.get(key.toUpperCase());
        if(key.length()>1)
            if(map.containsKey(key.substring(0,1).toUpperCase()+key.substring(1).toLowerCase()))
                value =map.get(key.substring(0,1).toUpperCase()+key.substring(1).toLowerCase());

        return value;

    }
    public static Entry newEntry(String name, Object[] object){
        return new Entry(name,object);
    }

}
