package eu.linksmart.api.event.types.impl;

import java.lang.UnsupportedOperationException;

import java.util.*;

/**
 * Created by José Ángel Carvajal on 06.06.2018 a researcher of Fraunhofer FIT.
 */
public class ExtractedElements<T> implements List<T>  {
    private Map<String, Integer> featureByName = new Hashtable<>(), featureByInput = new Hashtable<>(), featureByTarget = new Hashtable<>();
    private List<T> featureSpace = new ArrayList<>();
    private List<Integer>  inputs = new ArrayList<>(),targets = new ArrayList<>();

    public List<T> getTargetsList(){
        return  this.new Targets();
    }
    public List<T> getInputsList(){
        return this.new Inputs();
    }
    public Map<String,T> toMap(){return this.new MappedElements();}
    public Map<String,T> toMappedInputs(){return this.new MappedInputs();}
    public Map<String,T> toMappedTargets(){return this.new MappedTargets();}
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

    @Override
    public int size() {
        return featureSpace.size();
    }
    public int inputSize() {
        return inputs.size();
    }
    public int targetSize() {
        return targets.size();
    }
    @Override
    public boolean isEmpty() {
        return featureSpace.isEmpty();
    }



    public boolean isInputEmpty() {
        return inputs.isEmpty();
    }
    public boolean isTargetEmpty() {
        return targets.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return featureSpace.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return featureSpace.iterator();
    }

    @Override
    public Object[] toArray() {
        return featureSpace.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return featureSpace.toArray(a);
    }

    public boolean add(T element){
        if(!featureSpace.contains(element)) {
            add(element, false);
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        // the elements should be append only
        throw new UnsupportedOperationException();
    }


    @Override
    public boolean containsAll(Collection<?> c) {
        return featureSpace.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        if (c != null){
            for (T element : c)
                if (!featureSpace.add(element))
                    return false;
        }else
            return false;

        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        // the elements should be append only
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        // the elements should be append only
        throw new UnsupportedOperationException();

    }

    @Override
    public boolean retainAll(Collection<?> c) {
        // the elements should be append only
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        // the elements should be append only
        throw new UnsupportedOperationException();
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

    @Override
    public T set(int index, T element) {
        // the elements should be append only
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, T element) {
        // the elements should be append only
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove(int index) {
        // the elements should be append only
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        return featureSpace.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return featureSpace.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return featureSpace.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return featureSpace.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return featureSpace.subList(fromIndex,toIndex);
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
    public class Targets implements List<T>{
        private Targets(){}

        @Override
        public int size() {
            return targets.size();
        }

        @Override
        public boolean isEmpty() {
            return targets.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return ExtractedElements.this.contains(o);
        }

        @Override
        public Iterator<T> iterator() {
            // too complicated and not useful
            throw new UnsupportedOperationException();
        }

        @Override
        public Object[] toArray() {
            Object[] array = new Object[targets.size()];
            for(int i=0;i<targets.size();i++)
                array[i] = featureSpace.get(targets.get(i));
            return array;
        }

        @Override
        public <T1> T1[] toArray(T1[] a) {
            T1[] array = (T1[]) toArray();
            return array;
        }

        @Override
        public boolean add(T t) {
            addTarget(t);
            return true;
        }

        @Override
        public boolean remove(Object o) {
            // read only
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return ExtractedElements.this.containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends T> c) {
            // read only
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int index, Collection<? extends T> c) {
            // read only
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            // read only
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            // read only
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object o) {
            return o == this || o.getClass() == Targets.class && ((Targets) o).getPartent().targets.equals(targets);

        }
        protected ExtractedElements<T> getPartent(){
            return ExtractedElements.this;

        }
        @Override
        public int hashCode() {
            return targets.hashCode();
        }

        @Override
        public T get(int index) {
            return getTarget(index);
        }

        @Override
        public T set(int index, T element) {

            // read only
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int index, T element) {

            // read only
            throw new UnsupportedOperationException();
        }

        @Override
        public T remove(int index) {
            // read only
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(Object o) {

            return targets.indexOf(featureSpace.indexOf(o));
        }

        @Override
        public int lastIndexOf(Object o) {
            return targets.lastIndexOf(featureSpace.lastIndexOf(o));
        }

        @Override
        public ListIterator<T> listIterator() {

            // read only
            throw new UnsupportedOperationException();
        }

        @Override
        public ListIterator<T> listIterator(int index) {
            // read only
            throw new UnsupportedOperationException();
        }

        @Override
        public List<T> subList(int fromIndex, int toIndex) {

            // read only
            throw new UnsupportedOperationException();
        }
    }
    public class Inputs implements List<T>{
        private Inputs(){}

        @Override
        public int size() {
            return inputs.size();
        }

        @Override
        public boolean isEmpty() {
            return inputs.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return ExtractedElements.this.contains(o);
        }

        @Override
        public Iterator<T> iterator() {
            // too complicated and not useful
            throw new UnsupportedOperationException();
        }

        @Override
        public Object[] toArray() {
            Object[] array = new Object[inputs.size()];
            for(int i=0;i<inputs.size();i++)
                array[i] = featureSpace.get(inputs.get(i));
            return array;
        }

        @Override
        public <T1> T1[] toArray(T1[] a) {
            T1[] array = (T1[]) toArray();
            return array;
        }

        @Override
        public boolean add(T t) {
            addTarget(t);
            return true;
        }

        @Override
        public boolean remove(Object o) {
            // read only
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return ExtractedElements.this.containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends T> c) {
            // read only
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int index, Collection<? extends T> c) {
            // read only
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            // read only
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            // read only
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object o) {
            return o == this || o.getClass() == Targets.class && ((Targets) o).getPartent().targets.equals(targets);

        }
        protected ExtractedElements<T> getPartent(){
            return ExtractedElements.this;

        }
        @Override
        public int hashCode() {
            return inputs.hashCode();
        }

        @Override
        public T get(int index) {
            return getTarget(index);
        }

        @Override
        public T set(int index, T element) {

            // read only
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int index, T element) {

            // read only
            throw new UnsupportedOperationException();
        }

        @Override
        public T remove(int index) {
            // read only
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(Object o) {

            return inputs.indexOf(featureSpace.indexOf(o));
        }

        @Override
        public int lastIndexOf(Object o) {
            return inputs.lastIndexOf(featureSpace.lastIndexOf(o));
        }

        @Override
        public ListIterator<T> listIterator() {

            // read only
            throw new UnsupportedOperationException();
        }

        @Override
        public ListIterator<T> listIterator(int index) {
            // read only
            throw new UnsupportedOperationException();
        }

        @Override
        public List<T> subList(int fromIndex, int toIndex) {

            // read only
            throw new UnsupportedOperationException();
        }
    }

    public class MappedElements implements Map<String, T>{
        private MappedElements(){}
        @Override
        public int size() {
            return ExtractedElements.this.size();
        }

        @Override
        public boolean isEmpty() {
            return ExtractedElements.this.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return ExtractedElements.this.featureByName.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return ExtractedElements.this.featureSpace.contains(value);
        }

        @Override
        public T get(Object key) {
            return ExtractedElements.this.get(key.toString());
        }


        @Override
        public T put(String key, T value) {
            if(containsKey(key))
                featureSpace.set(featureByName.get(key),value);
            else
                add(key,value);

            return get(key);
        }

        @Override
        public T remove(Object key) {
            // the elements should be append only
            throw new UnsupportedOperationException();

        }

        @Override
        public void putAll(Map m) {
            m.forEach((k,v)->add(k.toString(),(T) v));
        }

        @Override
        public void clear() {
            // the elements should be append only
            throw new UnsupportedOperationException();
        }

        @Override
        public Set keySet() {
            return featureByName.keySet();
        }

        @Override
        public Collection values() {
            return featureSpace;
        }

        @Override
        public Set<Entry<String,T>> entrySet() {
            // too complicated and not useful
            throw new UnsupportedOperationException();
        }
    }
    public class MappedInputs implements Map<String, T>{
        private MappedInputs(){}
        @Override
        public int size() {
            return ExtractedElements.this.inputs.size();
        }

        @Override
        public boolean isEmpty() {
            return ExtractedElements.this.inputs.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return ExtractedElements.this.featureByInput.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return ExtractedElements.this.featureSpace.contains(value);
        }

        @Override
        public T get(Object key) {
            return ExtractedElements.this.getInput(key.toString());
        }


        @Override
        public T put(String key, T value) {
            if(containsKey(key))
                featureSpace.set(featureByInput.get(key),value);
            else
                add(key,value);

            return get(key);
        }

        @Override
        public T remove(Object key) {
            // the elements should be append only
            throw new UnsupportedOperationException();

        }

        @Override
        public void putAll(Map m) {
            m.forEach((k,v)->addTarget(k.toString(),(T) v));
        }

        @Override
        public void clear() {
            // the elements should be append only
            throw new UnsupportedOperationException();
        }

        @Override
        public Set keySet() {
            return featureByInput.keySet();
        }

        @Override
        public Collection values() {
            // too complicated and not useful
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<Entry<String,T>> entrySet() {
            // too complicated and not useful
            throw new UnsupportedOperationException();
        }
    }
    public class MappedTargets implements Map<String, T>{
        private MappedTargets(){}
        @Override
        public int size() {
            return ExtractedElements.this.targets.size();
        }

        @Override
        public boolean isEmpty() {
            return ExtractedElements.this.targets.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return ExtractedElements.this.featureByTarget.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return ExtractedElements.this.featureSpace.contains(value);
        }

        @Override
        public T get(Object key) {
            return ExtractedElements.this.getTarget(key.toString());
        }


        @Override
        public T put(String key, T value) {
            if(containsKey(key))
                featureSpace.set(featureByTarget.get(key),value);
            else
                add(key,value);

            return get(key);
        }

        @Override
        public T remove(Object key) {
            // the elements should be append only
            throw new UnsupportedOperationException();

        }

        @Override
        public void putAll(Map m) {
            m.forEach((k,v)->addTarget(k.toString(),(T) v));
        }

        @Override
        public void clear() {
            // the elements should be append only
            throw new UnsupportedOperationException();
        }

        @Override
        public Set keySet() {
            return featureByTarget.keySet();
        }

        @Override
        public Collection values() {
            // too complicated and not useful
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<Entry<String,T>> entrySet() {
            // too complicated and not useful
            throw new UnsupportedOperationException();
        }
    }

}
