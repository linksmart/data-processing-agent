package eu.linksmart.api.event.datafusion.testing;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.linksmart.api.event.types.impl.ExtractedElements;
import eu.linksmart.api.event.types.impl.SchemaNode;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Created by José Ángel Carvajal on 05.06.2018 a researcher of Fraunhofer FIT.
 */
public class SchemaTest {
    private void test(String schema, Object o, boolean test){

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            SchemaNode schemaNode = objectMapper.readValue(schema,SchemaNode.class);
            schemaNode.build();
            boolean valid= schemaNode.validate(o);
            if((!valid && test) || (valid && !test))
                fail("fail!");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    private void test(String schema, Object o){

       test(schema, o,true);
    }
    private void contraTest(String schema, Object o){

        test(schema, o,false);
    }
    static private String simpleMapSchema =
            "{" +
                    "\"type\":\"object\"," +
                    "\"properties\":{" +
                        "\"property1\":{" +
                            "\"type\":\"string\"" +
                        "}," +
                        "\"property2\":{" +
                            "\"type\":\"int\"" +
                        "}" +
                    "}" +
            "}";
    static private String advancedMapSchema =
            "{" +
                    "\"type\":\"object\"," +
                    "\"required\":[\"property1\",\"property2\"]," +
                    "\"properties\":{" +
                            "\"property1\":{" +
                                "\"type\":\"string\"" +
                            "}," +
                            "\"property2\":{" +
                                "\"type\":\"int\"" +
                            "}," +
                                "\"property3\":{" +
                                "\"type\":\"int\"" +
                            "}" +
                    "}" +
            "}";
    static private String simpleListArraySchema =
                 "{" +
                    "\"type\": \"array\"," +
                    "\"items\": [" +
                         "{\"name\": \"item1\",\"type\": \"string\"}, " +
                         "{\"name\": \"item2\",\"type\": \"int\"}" +
                    "]" +
                "}";
    static private String advancedListSchema =
            "{" +
                    "\"type\": \"array\"," +
                    "\"items\": [" +
                        "{\"type\": \"string\"}, " +
                        "{\"type\": \"int\"}" +
                        "{\"type\": \"string\",\"needed\":false }, " +
                        "{\"type\": \"boolean\"}, " +
                    "]" +
            "}";
    static private String simpleAnonymousSchema =
            "{" +
                    "\"type\": \"array\"," +
                    "\"size\": 10," +
                    "\"ofType\": \"int\"" +
            "}";
    static private String boundedAnonymousSchema =
            "{" +
                    "\"type\": \"array\"," +
                    "\"minValue\": 5," +
                    "\"maxValue\": 10," +
                    "\"defaultValue\": 7," +
                    "\"ofType\": \"int\"" +
            "}";
    static private String mapDefTest =
            "{" +
                    "\"type\":\"object\"," +
                    "\"properties\":{" +
                            "\"property1\":{\"ofDefinition\":\"test1\"}," +
                            "\"property2\":{\"ofDefinition\":\"test2\"}" +
                    "}," +
                    "\"definition\": {" +
                        "\"test1\":"+simpleMapSchema+", "+
                        "\"test2\":"+simpleListArraySchema+""+
                    "}"+
            "}";
    static private String deepMapTest =
            "{" +
                    "\"type\":\"object\"," +
                    "\"properties\":{" +
                        "\"root1\":"+ simpleMapSchema +
                        "," +
                        "\"root2\":" + simpleListArraySchema +
                    "}" +
                    "}";
    static private String deepListArraySchema =
            "{" +
                    "\"type\": \"array\"," +
                    "\"items\": [" +
                        simpleMapSchema + "," +
                        simpleListArraySchema +
                        "]" +
                    "}";
    static private String listDefTest =
            "{" +
                    "\"type\":\"array\"," +
                    "\"items\":[" +
                        "{\"ofDefinition\":\"test1\"}," +
                        "{\"ofDefinition\":\"test2\"}" +
                    "]," +
                    "\"definition\": {" +
                        "\"test1\":"+simpleMapSchema+", "+
                        "\"test2\":"+simpleListArraySchema+" "+
                    "}"+
                    "}";


    private Map createSimpleMap(){
        Map map = new Hashtable();
        map.put("property1","hola");
        map.put("property2",1);
        return map;
    }
    private List createSimpleList(){
        List list = new ArrayList();
        list.add("hola");
        list.add(1);
        return list;
    }
    private Object[] createSimpleArray(){
        return  new Object[]{"hola",1};
    }
    @Test
    public void simpleMapTest(){
        // test
        Map map = createSimpleMap();
        Object o = new TestOnly1();
        test(simpleMapSchema,map);
        test(simpleMapSchema,o);

        // contra test
        map = new Hashtable();
        map.put("property1",1);
        contraTest(simpleMapSchema,map);
        o = new TestOnly2();

        contraTest(simpleMapSchema,o);
        map.clear();
        map.put("my", "bad");

        contraTest(simpleMapSchema,map);

        o = new Object(){
            public String getP() {
                return p;
            }

            public void setP(String p) {
                this.p = p;
            }

            public String p;

        };
        contraTest(simpleMapSchema,o);
    }

    @Test
    public void advancedMapTest(){
        // test
        Map map = createSimpleMap();
        Object o = new TestOnly1();
        test(advancedMapSchema,map);
        test(advancedMapSchema,o);
        test(simpleMapSchema,map);
        test(simpleMapSchema,o);
        map.put("property3",1);

        test(advancedMapSchema,map);
      //  contraTest(simpleMapSchema,map);

        o =   new TestOnly3();
//        contraTest(simpleMapSchema,o);
        test(advancedMapSchema,o);

        extract(advancedMapSchema,o);
    }

    private void extract(String advancedMapSchema, Object o) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            SchemaNode schemaNode = objectMapper.readValue(advancedMapSchema,SchemaNode.class);
            schemaNode.build();
            ExtractedElements elements= schemaNode.collect(o);
            if(elements==null)
                fail("fail!");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void simpleListArrayTest(){
        List list = createSimpleList();

        Object[] o =createSimpleArray();


        test(simpleListArraySchema,list);
        test(simpleListArraySchema,o);
        list.clear();
        list.add(1);
        list.add("hola");
        o = new Object[]{1,"hola"};

        contraTest(simpleListArraySchema,list);
        contraTest(simpleListArraySchema,o);


    }
    @Test
    public void defArrayTest(){
        List list = createSimpleList();
        Map map = createSimpleMap();
        List root = new ArrayList();
        root.add(map);
        root.add(list);


        test(listDefTest,root);
    }
    @Test
    public void defMapTest(){
        List list = createSimpleList();
        Map map = createSimpleMap();
        Map root = new Hashtable();
        root.put("property1",map);
        root.put("property2",list);


        test(mapDefTest,root);
    }
    @Test
    public void deepArrayTest(){
        List list = createSimpleList();
        Map map = createSimpleMap();
        List root = new ArrayList();
        root.add(map);
        root.add(list);


        test(deepListArraySchema,root);
    }
    @Test
    public void deepMapTest(){
        List list = createSimpleList();
        Map map = createSimpleMap();
        Map root = new Hashtable();
        root.put("root1",map);
        root.put("root2",list);



        test(deepMapTest,root);
        extract(deepMapTest,root);

    }
    @Test
    public void simpleAnonymousListTest(){

        List<Integer> list = new ArrayList();


        Integer[] o = new Integer[10];
        for(int i=0; i<10;i++){
            list.add(i);
            o[i]=1;
        }
        test(simpleAnonymousSchema,list);
        test(simpleAnonymousSchema,o);

        List<String> contraList = new ArrayList<>();
        String[] contraArray= new String[10];

        for(int i=0; i<10;i++){
            contraList.add("hola");
            contraArray[i]="hola";
        }
        contraTest(simpleAnonymousSchema,contraList);
        contraTest(simpleAnonymousSchema,contraArray);

        list.clear();
        o= new Integer[5];
        contraTest(simpleAnonymousSchema,list);
        contraTest(simpleAnonymousSchema,o);



    }
    @Test
    public void boundedAnonymousListTest(){

        List<Integer> list = new ArrayList();


        Integer[] o = new Integer[8];
        for(int i=0; i<8;i++){
            list.add(i);
            o[i]=1;
        }
        test(boundedAnonymousSchema,list);
        test(boundedAnonymousSchema,o);

        o = new Integer[2];
        list.clear();
        for(int i=0; i<2;i++){
            list.add(i);
            o[i]=1;
        }
        contraTest(boundedAnonymousSchema,list);
        contraTest(boundedAnonymousSchema,o);

        o = new Integer[15];
        list.clear();
        for(int i=0; i<15;i++){
            list.add(i);
            o[i]=1;
        }
        contraTest(boundedAnonymousSchema,list);
        contraTest(boundedAnonymousSchema,o);
    }
    // the class below could be anonymous but then is not accessible from reflection outside this package, which brakes the test.
    // Therefore, this dummy test class had been made to test the code. They need to be public!
    public class TestOnly1{
        public String getProperty1() {
            return property1;
        }

        public void setProperty1(String property1) {
            this.property1 = property1;
        }

        public int getProperty2() {
            return property2;
        }

        public void setProperty2(int property2) {
            this.property2 = property2;
        }

        public String property1="hola";
        public int property2=1;
    }
    // the class below could be anonymous but then is not accessible from reflection outside this package, which brakes the test.
    // Therefore, this dummy test class had been made to test the code. They need to be public!
    public class TestOnly2{
        public int getProperty1() {
            return property1;
        }

        public void setProperty1(int property1) {
            this.property1 = property1;
        }

        public String getProperty2() {
            return property2;
        }

        public void setProperty2(String property2) {
            this.property2 = property2;
        }

        public int property1;
        public String property2;
    }
    // the class below could be anonymous but then is not accessible from reflection outside this package, which brakes the test.
    // Therefore, this dummy test class had been made to test the code. They need to be public!
    public class TestOnly3{
        public int getProperty3() {
            return property3;
        }

        public void setProperty3(int property3) {
            this.property3 = property3;
        }

        public String getProperty1() {
            return property1;
        }

        public void setProperty1(String property1) {
            this.property1 = property1;
        }

        public int getProperty2() {
            return property2;
        }

        public void setProperty2(int property2) {
            this.property2 = property2;
        }

        public String property1 ="hola";
        public int property2, property3;
    }
}
