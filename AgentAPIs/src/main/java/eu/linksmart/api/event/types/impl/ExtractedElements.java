package eu.linksmart.api.event.types.impl;

import java.util.*;

/**
 * Created by José Ángel Carvajal on 06.06.2018 a researcher of Fraunhofer FIT.
 */
public class ExtractedElements<T>  {
    private Map<String, Integer> featureByName = new Hashtable<>(), featureByInput = new Hashtable<>(), featureByTarget = new Hashtable<>();
    private List<T> featureSpace = new ArrayList<>();
    private List<Integer>  inputs = new ArrayList<>(),targets = new ArrayList<>();


    public ExtractedElements<T> add(String key, T element, boolean target){
        if(!featureByName.containsKey(key)) {
            featureByName.put(key, featureSpace.size());
            if(target) {
                this.featureByTarget.put(key, this.featureSpace.size());
                targets.add(featureSpace.size());
            }else {
                this.featureByInput.put(key, this.featureSpace.size());
                inputs.add(featureSpace.size());
            }
            featureSpace.add(element);
        }
        return this;
    }
    public ExtractedElements<T> add( T element, boolean target){
        if(!featureSpace.contains(element)) {
          add(String.valueOf(featureSpace.size()),element,target);
        }
        return this;
    }
    public ExtractedElements<T> add( T element){
        return add(element,false);
    }

    public ExtractedElements<T> add(String key, T element){
        return add(key,element,false);
    }
    public ExtractedElements<T> addTarget( T element){
        return add(element,true);
    }

    public ExtractedElements<T> addTarget(String key, T element){
        return add(key,element,true);
    }
    public ExtractedElements<T> addInput( T element){
        return add(element,false);
    }

    public ExtractedElements<T> addInput(String key, T element){
        return add(key,element,false);
    }
    public T get(String key){
        return featureSpace.get(featureByName.get(key));
    }
    public T get(int i){
        return featureSpace.get(i);
    }

    public T getInput(String key){
        return featureSpace.get(featureByInput.get(key));
    }
    public T getInput(int i){
        return featureSpace.get(inputs.get(i));
    }

    public T getTarget(String key){
        return featureSpace.get(featureByTarget.get(key));
    }
    public T getTarget(int i){
        return featureSpace.get(targets.get(i));
    }


}
