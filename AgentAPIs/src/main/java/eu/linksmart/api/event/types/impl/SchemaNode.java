package eu.linksmart.api.event.types.impl;
import com.fasterxml.jackson.annotation.*;
import eu.linksmart.api.event.ceml.data.ClassesDescriptor;
import eu.linksmart.api.event.ceml.data.DataDefinition;
import eu.linksmart.api.event.ceml.data.DataDescriptor;
import eu.linksmart.api.event.ceml.data.DataDescriptors;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.JsonSerializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/*
 *  Copyright [2013] [Fraunhofer-Gesellschaft]
         *
         * Licensed under the Apache License, Version 2.0 (the "License");
         * you may not use this file except in compliance with the License.
         * You may obtain a copy of the License at
         *
         * http://www.apache.org/licenses/LICENSE-2.0
         *
         * Unless required by applicable law or agreed to in writing, software
         * distributed under the License is distributed on an "AS IS" BASIS,
         * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
         * See the License for the specific language governing permissions and
         * limitations under the License.
         *
         *
         */
/**
 * This interfaces represent a schema of a set of data that a model will get for learning/training, evaluating or predicting.
 *
 *
 *
 *
 * @author Jose Angel Carvajal Soto
 * @since       1.1.1
 * @see eu.linksmart.api.event.ceml.data.DataDescriptors
 * @see eu.linksmart.api.event.types.JsonSerializable
 * @see eu.linksmart.api.event.ceml.data.ClassesDescriptor
 *
 * */
public class SchemaNode implements JsonSerializable {

    private transient static final Logger loggerService = LogManager.getLogger(SchemaNode.class);
    private String type, name, ofDefinition, ofType;
    private Set<String> required;
    @JsonProperty("enum")
    private Set<String> enumeration;
    private Set targets;
    private boolean needed = true, skip=false, target=false;
    private Map<String,SchemaNode> properties;

    private List<SchemaNode> items;
    private Map<String,SchemaNode> definition;
    @JsonIgnore
    private Map<String, SchemaNode> resolveMap = new Hashtable<>();
    @JsonIgnore
    private List<SchemaNode> resolveList = new ArrayList<>();
    private Number minValue, maxValue, ceilingValue, floorValue;
    private Object defaultValue;
    @JsonIgnore
    private SchemaNode parent;
    @JsonIgnore
    private Set<String> names = new HashSet<>();
    @JsonIgnore
    private int size = -1;
    private int index = -1 , targetSize=0;
    private boolean built = false;


    public Class similar(Class original){
        if(original.isPrimitive()) {
            if(original.getSimpleName().equals("int") || original.getSimpleName().equals("short"))
                return Number.class;
            else if (original.getSimpleName().equals("boolean"))
                return Boolean.class;
        }if( original ==  String.class)
            return String.class;
        else if( original ==  Number.class)
            return Number.class;
        else if( original ==  Double.class)
            return Number.class;
        else if( original ==  Float.class)
            return Number.class;
        else if( original ==  Integer.class)
            return Number.class;
        else if( original ==  Boolean.class)
            return Boolean.class;
        else if( original ==  Date.class)
            return Long.class;
        else if( original ==  Long.class)
            return Long.class;
        else if( original ==  Object.class)
            return Map.class;
        else if( original ==  Map.class)
            return Map.class;
        else if( Map.class.isAssignableFrom(original))
            return Map.class;
        else if( original ==  List.class)
            return List.class;
        else if( List.class.isAssignableFrom(original) || Object[].class.isAssignableFrom(original) )
            return List.class;
        else if( original ==  Enum.class)
            return Enum.class;
        else if( Enum.class.isAssignableFrom(original))
            return Enum.class;
        else {
            return null;
        }
    }
    public static Number getNumber(Object original) {
        if (original instanceof Number)
            return (Number) original;
        return null;
    }
    public boolean isSimilar(Object object){
        if (object!=null)
            return similar(object.getClass())!=null && (
                        similar(object.getClass()) == similar(getNativeType()) ||
                        (getNativeType().isArray() && "array".equals(type)) ||
                        ( "enum".equals(type) && enumVerify(object, this.enumeration)  )
                );
        return false;
    }
    public boolean isSimilar(Class object){
        return similar(object)!=null && (
                            (similar(object) == similar(getNativeType())) ||
                            (getNativeType().isArray() && "array".equals(type) )||
                            ("enum".equals(type) )
                );
    }
    private static boolean enumVerify(Object object, Set enumeration){
        if(object.getClass() == String.class || String.class.isAssignableFrom(object.getClass())){
            return enumeration.contains(object.toString());
        }  else if(object.getClass() == Integer.class || Integer.class.isAssignableFrom(object.getClass())){
            return enumeration.contains(String.valueOf((Integer)object)) ||  enumeration.contains(object);
        }else if(object.getClass() == Double.class || Double.class.isAssignableFrom(object.getClass())){
            return enumeration.contains(String.valueOf((Double)object)) ||  enumeration.contains(object);
        } else
            return false;

    }
    @JsonIgnore
   static public Class getNativeType(String type){
        if("string".equals(type.toLowerCase()))
            return String.class;
        else if("number".equals(type.toLowerCase()))
            return Number.class;
        else if("float".equals(type.toLowerCase()))
            return Double.class;
        else if("double".equals(type.toLowerCase()))
            return Double.class;
        else if("int".equals(type.toLowerCase()))
            return Integer.class;
        else if("integer".equals(type.toLowerCase()))
            return Integer.class;
        else if("bool".equals(type.toLowerCase()))
            return Boolean.class;
        else if("boolean".equals(type.toLowerCase()))
            return Boolean.class;
        else if("long".equals(type.toLowerCase()))
            return Integer.class;
        else if("date".equals(type.toLowerCase()))
            return Long.class;
        else if("time".equals(type.toLowerCase()))
            return Long.class;
        else if("object".equals(type.toLowerCase()))
            return Map.class;
        else if("map".equals(type.toLowerCase()))
            return Map.class;
        else if("array".equals(type.toLowerCase()))
            return List.class;
        else if("enum".equals(type.toLowerCase()))
            return Enum.class;
        else {
            return null;
        }
    }
   public ExtractedElements collect(Object o) {
       ExtractedElements collector = new ExtractedElements<>();
       if (validate(o, collector))
           return collector;
       return null;
   }
    private void extract(ExtractedElements collector,SchemaNode node, Object element){
       if(collector!=null){
           if(node.getName()!=null)
               collector.add(node.getName(),element,node.isTarget());
           else
               collector.add(element,node.isTarget());

       }

    }
   public boolean validate(Object object) {
       return validate(object, null);
   }
   private boolean validateObjectAsMap(Object object, ExtractedElements collector){
       Map map = ((Map) object);
       for (String key : properties.keySet()) {
           SchemaNode current = properties.get(key);
           if (map.containsKey(key)) {
               Object element = map.get(key);
               if (current.isSimilar(element.getClass())) {
                   if (current.getNativeType() == String.class) {
                       if (current.defaultValue != null && "".equals(element.toString()) && !element.toString().equals(current.defaultValue.toString()))
                           map.put(key, defaultValue.toString());
                       extract(collector,current,element);
                   } else if ((similar(current.getNativeType()) == Number.class) && (current.minValue == null || getNumber(element).doubleValue() < current.minValue.doubleValue()) && (current.maxValue == null || getNumber(element).doubleValue() > current.maxValue.doubleValue())) {
                       if (current.ceilingValue != null && getNumber(element).doubleValue() > current.ceilingValue.doubleValue())
                           map.put(key, current.ceilingValue);
                       if (current.floorValue != null && getNumber(element).doubleValue() < current.floorValue.doubleValue())
                           map.put(key, current.floorValue);
                       extract(collector,current,element);
                   } else if ((similar(current.getNativeType()) == Long.class) && (current.minValue == null || getNumber(element).longValue() < current.minValue.longValue()) && (current.maxValue == null || getNumber(element).longValue() > current.maxValue.longValue())) {
                       if (current.ceilingValue != null && getNumber(element).longValue() > current.ceilingValue.longValue())
                           map.put(key, current.ceilingValue);
                       if (current.floorValue != null && getNumber(element).longValue() < current.floorValue.longValue())
                           map.put(key, current.floorValue);
                       extract(collector,current,element);
                   } else if ((current.getNativeType() == Enum.class) && ! enumVerify(element,current.getEnumeration()) ) {
                       return false;

                   } else if (!current.validate(element,collector)) {
                       return false;
                   }


                   continue;
               }
               return false;

           } else if (current.isNeeded()) {
               return false;
           }
       }

       return true;

   }
   private boolean validateObjectAsList(Object object, ExtractedElements collector){
       List list;
       if (object instanceof List)
           list = ((List) object);
       else
           list = Arrays.asList((Object[]) object);
       if (items != null && minValue == null && list.size() != items.size())
           return false;
       if ((maxValue != null && list.size() > maxValue.intValue()) || (minValue != null && list.size() < minValue.intValue()))
           return false;
       if (maxValue == null && minValue == null && list.size() != size())
           return false;
       if (items == null && size() > 0 && list.size() > 0)
           if (ofType != null && object.getClass().isArray() && similar(getNativeType(ofType)) != similar(object.getClass().getComponentType()))
               return false;

           else if (ofType != null && !object.getClass().isArray() && object instanceof List && similar(((List) object).get(0).getClass()) != similar(getNativeType(ofType)))
               return false;

       if (items != null && items.size() >= list.size()) {
           int j = 0;
           for (int i = 0; i < list.size(); i++) {
               if (items.size() < list.size())
                   return false;
               SchemaNode current = items.get(i);
               Object element = list.get(j);
               if (current.isSimilar(element)) {
                   if (current.getNativeType() == String.class) {
                       if (current.defaultValue != null && "".equals(element.toString()) && !element.toString().equals(current.defaultValue.toString()))
                           list.set(j, defaultValue.toString());
                       extract(collector,current,element);
                   } else if ((similar(current.getNativeType()) == Number.class) && (current.minValue == null || getNumber(element).doubleValue() < current.minValue.doubleValue()) && (current.maxValue == null || getNumber(element).doubleValue() > current.maxValue.doubleValue())) {
                       if (current.ceilingValue != null && getNumber(element).doubleValue() > current.ceilingValue.doubleValue())
                           list.set(j, current.ceilingValue);
                       if (current.floorValue != null && getNumber(element).doubleValue() < current.floorValue.doubleValue())
                           list.set(j, current.floorValue);
                       extract(collector,current,element);
                   } else if ((similar(current.getNativeType()) == Long.class) && (current.minValue == null || getNumber(element).longValue() < current.minValue.longValue()) && (current.maxValue == null || getNumber(element).longValue() > current.maxValue.longValue())) {
                       if (current.ceilingValue != null && getNumber(element).longValue() > current.ceilingValue.longValue())
                           list.set(j, current.ceilingValue);
                       if (current.floorValue != null && getNumber(element).longValue() < current.floorValue.longValue())
                           list.set(j, current.floorValue);
                       extract(collector,current,element);
                   } else if ((current.getNativeType() == Enum.class) && !(current.enumeration.contains(element))) {
                       return false;

                   } else if (!current.validate(element,collector)) {
                       return false;

                   }
                   j++;

               } else if (current.defaultValue != null && element.getClass().isAssignableFrom(current.defaultValue.getClass()) && items.size() == list.size()) {
                   try {
                       list.set(i, current.defaultValue);
                       j++;
                       extract(collector,current,element);
                   } catch (Exception e) {
                       return false;
                   }
               } else if (current.defaultValue != null && element.getClass().isAssignableFrom(current.defaultValue.getClass()) && items.size() > list.size()) {
                   try {
                       list.add(i, current.defaultValue);
                       j++;
                       extract(collector,current,element);
                   } catch (Exception e) {
                       return false;
                   }
               } else if (current.isNeeded()) {
                   return false;
               }
           }
       }


       return true;
   }
   private boolean validate(Object object, ExtractedElements collector) {
       if (skip)
           return true;
       if (object instanceof Map) {
          return validateObjectAsMap(object, collector);

       } else if (object instanceof List || object instanceof Object[]) {
           return validateObjectAsList(object, collector);

       } else if ("enum".equals(this.type) && this.enumeration !=null ) {
           extract(collector,this, object);
           return true;
       }else {
           return validatePOJO(object,collector);
       }

   }

   private boolean validatePOJO(Object object, ExtractedElements collector) { // this must be revised

       if (properties == null)
           return false;


       Map<String, Method> methods = new Hashtable<>();
       Arrays.stream(object.getClass().getMethods()).forEach(m -> methods.put(m.getName(), m));


       for (String key : properties.keySet()) {
           SchemaNode current = properties.get(key);
           if (!methods.containsKey(key) && !methods.containsKey("get" + key) && !methods.containsKey("get" + caps(key)))
               if (current.isNeeded()) {
                   return false;
               } else
                   continue;
           Method method = methods.getOrDefault(key, methods.getOrDefault("get" + key, methods.get("get" + caps(key))));
           if (!current.isSimilar(method.getReturnType()))
               return false;
      //     if ("object".equals(current.type) || "array".equals(current.type)) {

               try {
                   Object leaf = method.invoke(object);
                   if ("object".equals(current.type) || "array".equals(current.type))
                        return current.validate(leaf,collector);
                   else
                       extract(collector,current,leaf);

               } catch (IllegalAccessException | InvocationTargetException e) {
                   loggerService.error(e.getMessage(), e);
                   return false;
               }
//           }

       }

       return true;
   }
    static String caps(String string){
        return string.substring(0,1).toUpperCase()+string.substring(1,string.length());
    }
    @Override
    public JsonSerializable build() throws TraceableException, UntraceableException {
        boolean defaultNeeded;
        if(name==null)
            throw new UntraceableException("A Schema should have always a name!");
        if(getRoot().names.contains(name))
            throw new UntraceableException("All given names must be unique!");
        else
            getRoot().names.add(name);
        if((properties==null && "object".equals(type.toLowerCase())) || ( "array".equals(type.toLowerCase()) && ((size()>0 && minValue!=null && maxValue!=null) || (size()<0 &&minValue==null && maxValue==null)) ) || (enumeration==null && "enum".equals(type.toLowerCase())))
            throw new UntraceableException("If is array must have items defined! If is a object must have properties! If is a enm must have enum!");
        if ((properties!=null && items!=null) || (properties!=null && enumeration!=null) || (items!=null && enumeration!=null))
            throw new UntraceableException("A data definition cannot be object and/or array and/or enum! ");
        if((maxValue!=null ||minValue!=null) && items!=null)
            throw new UntraceableException("The maximum and minimum value of an array cannot be set if the array is explicitly defined. Each element has to be set as needed or not!");
        if(required==null) {
            required = new HashSet<>();
            defaultNeeded=true;
        }else
            defaultNeeded=false;

        if(properties!=null)
            for(String key: properties.keySet()) {
                if(properties.get(key).ofDefinition==null) {
                    properties.get(key).setName(key);
                    properties.get(key).setNeeded(required.contains(key) || defaultNeeded);

                    buildSubCollection(properties.get(key));
                }else if (getRoot().getDefinition().containsKey(properties.get(key).ofDefinition)){
                    properties.put(key, getRoot().getDefinition().get(properties.get(key).ofDefinition));
                    properties.get(key).name = properties.get(key).name+":"+name;

                    buildSubCollection(properties.get(key));
                }else
                    throw new UntraceableException("The definition "+properties.get(key).ofDefinition + " was given but not defined in root node!");
            }
        if(items!=null) {
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).ofDefinition == null) {
                    items.get(i).setIndex(i);
                    if(items.get(i).name==null)
                        items.get(i).setName(name+"["+i+"]");

                    buildSubCollection(items.get(i));
                } else if (getRoot().getDefinition().containsKey(items.get(i).ofDefinition)) {
                    items.set(i, getRoot().getDefinition().get(items.get(i).ofDefinition));
                    items.get(i).name = name+"["+i+"]";

                    buildSubCollection(items.get(i));
                } else
                    throw new UntraceableException("The definition " + items.get(i).ofDefinition + " was given but not defined in root node!");
            }
        }else if(size>0 || (maxValue!=null && maxValue.intValue()>0)){
            if(maxValue!=null )
                size = maxValue.intValue();

            items = new ArrayList<>();
            for (int i = 0; i < size-targetSize; i++) {
                SchemaNode node = new SchemaNode();
                node.type = ofType;
                node.index = i;
                node.setName(name+"["+i+"]");
                buildSubCollection(node);

                items.add(node);
            }
            for (int i = 0; i < targetSize; i++) {
                SchemaNode node = new SchemaNode();
                node.type = ofType;
                node.setName(name+"["+i+size+"]");
                node.target =true;
                node.index = i+size;
                if (node.targets == null )
                    node.targets = Set.of("0","1");

                buildSubCollection(node);
                items.add(node);
            }

        }

        built = true;

        return this;
    }
    private void buildSubCollection(SchemaNode value) throws TraceableException, UntraceableException {
        value.setParent(this);
        value.setTarget(target?target:value.target);
        value.build();
        getRoot().resolveList.add(value);
        getRoot().resolveMap.put(value.getName(),value);

    }

    private DataDescriptor.DescriptorTypes legacyType(Class cla){
        if(cla == Number.class || cla == Double.class || cla == Float.class || cla == float.class || cla == double.class  )
            return DataDescriptor.DescriptorTypes.NUMBER;
        else if(cla == Integer.class || cla == int.class || cla == short.class)
            return DataDescriptor.DescriptorTypes.INTEGER;
        else if(cla == Date.class || cla == Long.class || cla == long.class)
            return DataDescriptor.DescriptorTypes.DATE;
        else if(cla == Boolean.class || cla == boolean.class )
            return DataDescriptor.DescriptorTypes.BOOLEAN;
        else if(cla == Enum.class )
            return DataDescriptor.DescriptorTypes.NOMINAL_CLASSES;
        else
            return DataDescriptor.DescriptorTypes.OBJECT;

    }

    public DataDescriptor toLegacy() throws TraceableException, UntraceableException {
        if(parent==null){
            if(items==null && properties == null && enumeration == null){
                return DataDescriptors.factory(name,getSize()-targetSize,targetSize,legacyType(getNativeType(ofType)));
            } else if(properties!=null){
                DataDescriptor descriptors[] = new DataDescriptor[properties.size()];
                int i =0;
                for(SchemaNode node: properties.values()) {
                    descriptors[i] = node.toLegacy();
                    i++;
                }
                return DataDescriptors.factory(descriptors);

            } else if(items != null) {

                DataDescriptor descriptors[] = new DataDescriptor[items.size()];

                for(int i=0; i<items.size();i++)
                    descriptors[i] = items.get(i).toLegacy();

                return DataDescriptors.factory(descriptors);

            }else {
                throw new UntraceableException("An enumeration cannot be used in the root of the data schema or data descriptors!");
            }
        }else {
            if(getNativeType()==Enum.class){
                return ClassesDescriptor.factory(name,new ArrayList<>(enumeration),target);

            }else
                return DataDescriptor.factory(legacyType(getNativeType()),name,null,null,null,target);

        }

    }
    @Override
    public void destroy() throws Exception {

    }
    @JsonIgnore
    public SchemaNode getRoot() {
        if(parent!=null)
            return parent.getRoot();
        else
            return this;

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public Set<String> getRequired() {
        return required;
    }

    public void setRequired(Set<String> required) {
        this.required = required;
    }

    public Map<String, SchemaNode> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, SchemaNode> properties) {
        this.properties = properties;
    }

    public List<SchemaNode> getItems() {
        return items;
    }

    public void setItems(List<SchemaNode> items) {
        this.items = items;
    }

    public Number getMinValue() {
        return minValue;
    }

    public void setMinValue(Number minValue) {
        this.minValue = minValue;
    }

    public Number getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Number maxValue) {
        this.maxValue = maxValue;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }
    public SchemaNode getParent() {
        return parent;
    }

    public void setParent(SchemaNode parent) {
        this.parent = parent;
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    public boolean isNeeded() {
        return needed;
    }

    public void setNeeded(boolean needed) {
        this.needed = needed;
    }

    public String getOfDefinition() {
        return ofDefinition;
    }

    public void setOfDefinition(String ofDefinition) {
        this.ofDefinition = ofDefinition;
    }

    public Map<String, SchemaNode> getDefinition() {
        return definition;
    }

    public void setDefinition(Map<String, SchemaNode> definition) {
        this.definition = definition;
    }
    @JsonIgnore
    public Class getNativeType(){
        return getNativeType(type);
    }

    public Number getCeilingValue() {
        return ceilingValue;
    }

    public void setCeilingValue(Number ceilingValue) {
        this.ceilingValue = ceilingValue;
    }

    public Number getFloorValue() {
        return floorValue;
    }

    public void setFloorValue(Number floorValue) {
        this.floorValue = floorValue;
    }

    public String getOfType() {
        return ofType;
    }

    public void setOfType(String ofType) {
        this.ofType = ofType;
    }

    public Set<String> getEnumeration() {
        return enumeration;
    }

    public void setEnumeration(Set<String> enumeration) {
        this.enumeration = enumeration;
    }
    @JsonGetter("size")
    public int size() {
        if(items!=null)
            return items.size();
        return size;
    }

    public int getSize() {
        return size();
    }
    @JsonSetter("size")
    public void setSize(int size) {
        this.size = size;
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public boolean isTarget() {
        return target;
    }

    public void setTarget(boolean target) {
        this.target = target;
    }
    public boolean isInput() {
        return !target;
    }

    public void setInput(boolean input) {
        this.target = !input;
    }

    public Set<String> getTargets() {
        return targets;
    }

    public void setTargets(Set<String> targets) {
        this.targets = targets;
    }

    public int getTargetSize() {
        return targetSize;
    }

    public void setTargetSize(int targetSize) {
        this.targetSize = targetSize;
    }

    public Map<String, SchemaNode> getResolveMap() {
        return resolveMap;
    }

    public void setResolveMap(Map<String, SchemaNode> resolveMap) {
        this.resolveMap = resolveMap;
    }

    public List<SchemaNode> getResolveList() {
        return resolveList;
    }

    public void setResolveList(List<SchemaNode> resolveList) {
        this.resolveList = resolveList;
    }

    public Set<String> getNames() {
        return names;
    }

    public void setNames(Set<String> names) {
        this.names = names;
    }


    public boolean isBuilt() {
        return built;
    }
}
