package de.fraunhofer.fit.event.ceml;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;



import weka.classifiers.Classifier;
import weka.classifiers.UpdateableClassifier;
import weka.classifiers.functions.*;
import weka.classifiers.functions.neural.*;
import weka.classifiers.functions.supportVector.*;
import weka.classifiers.bayes.*;
import weka.classifiers.bayes.net.*;
import weka.classifiers.bayes.net.estimate.*;
import weka.core.*;

/**
 * Created by angel on 13/11/15.
 */
public class CEML {

    static protected Map<String,Object> learningObjects = new HashMap<>(),  objectsTypes = new HashMap<>();

    static protected Map<String,Attribute> knwonAtributes = new HashMap<>();
    static public boolean createLearningObject(String objectName, String objectType)throws InstantiationException, IllegalAccessException, ClassNotFoundException {


        if(learningObjects.containsKey(objectName))
            return false;
        learningObjects.put(objectName,Class.forName(objectType).newInstance());
        objectsTypes.put(objectName,objectType);


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

    static public boolean classify(String learningObjectName, Map<String, Double> rawInstance){
        Instance inst = new DenseInstance(rawInstance.size());
        for (String key: rawInstance.keySet()) {

            if (!knwonAtributes.containsKey(key)) {
                knwonAtributes.put(learningObjectName, new Attribute(key));
            }

            Attribute attr = knwonAtributes.get(key);

            inst.setValue(attr,rawInstance.get(key));

        }
        UpdateableClassifier classifier = (UpdateableClassifier) learningObjects.get(learningObjectName);


        try {
            classifier.updateClassifier(inst);
        } catch (Exception e) {
            try {
                ((Classifier)classifier).buildClassifier((Instances) inst);

            }catch (Exception ex)
            {
                // Todo: initialize a debug service and use it instead
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }


}
