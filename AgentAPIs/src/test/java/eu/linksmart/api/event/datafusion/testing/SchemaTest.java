package eu.linksmart.api.event.datafusion.testing;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.linksmart.api.event.types.impl.ExtractedElements;
import eu.linksmart.api.event.types.impl.SchemaNode;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Created by José Ángel Carvajal on 05.06.2018 a researcher of Fraunhofer FIT.
 */
public class SchemaTest {

    private ObjectMapper objectMapper = new ObjectMapper();
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
                    "\"name\":\"simpleMapSchema\"," +
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
                    "\"name\":\"advancedMapSchema\"," +
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
                         "\"name\":\"simpleListArraySchema\"," +
                    "\"type\": \"array\"," +
                    "\"items\": [" +
                         "{\"name\": \"item1\",\"type\": \"string\"}, " +
                         "{\"name\": \"item2\",\"type\": \"int\"}" +
                    "]" +
                "}";

    static private String simpleAnonymousSchema =
            "{" +
                    "\"name\":\"simpleAnonymousSchema\"," +
                    "\"type\": \"array\"," +
                    "\"size\": 10," +
                    "\"ofType\": \"int\"" +
            "}";
    static private String boundedAnonymousSchema =
            "{" +
                    "\"name\":\"boundedAnonymousSchema\"," +
                    "\"type\": \"array\"," +
                    "\"minValue\": 5," +
                    "\"maxValue\": 10," +
                    "\"defaultValue\": 7," +
                    "\"ofType\": \"int\"" +
            "}";
    static private String mapDefTest =
            "{" +
                    "\"name\":\"mapDefTest\"," +
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
                    "\"name\":\"deepMapTest\"," +
                    "\"type\":\"object\"," +
                    "\"properties\":{" +
                        "\"root1\":"+ simpleMapSchema +
                        "," +
                        "\"root2\":" + simpleListArraySchema +
                    "}" +
                    "}";
    static private String deepListArraySchema =
            "{" +
                    "\"name\":\"deepListArraySchema\"," +
                    "\"type\": \"array\"," +
                    "\"items\": [" +
                        simpleMapSchema + "," +
                        simpleListArraySchema +
                        "]" +
                    "}";
    static private String listDefTest =
            "{" +
                    "\"name\":\"listDefTest\"," +
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

    private Map getStatement(String name){
        Map statement = null;
        String path = "./src/test/dataSchemasTests.json";
        File source = new File(path);
        if(!source.exists())
            Assert.fail("statement source file not found!");
        try {
            statement =parse( Files.readAllBytes(Paths.get(path)), Map.class);
            return (Map) statement.get(name);
        } catch (Exception e) {
            Assert.fail(e.getMessage());

        }
        return statement;
    }
    private <T> T  parse(byte[] content, Class<? extends T> clas)  {
        try {
            return  objectMapper.readValue(content, clas);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());

        }
        return null;
    }
    private String toString(Object object){
        try {
            return  objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());

        }
        return null;
    }
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
        test(toString(getStatement("simpleMapSchema")),map);
        test(toString(getStatement("simpleMapSchema")),o);

        // contra test
        map = new Hashtable();
        map.put("property1",1);
        contraTest(toString(getStatement("simpleMapSchema")),map);
        o = new TestOnly2();

        contraTest(toString(getStatement("simpleMapSchema")),o);
        map.clear();
        map.put("my", "bad");

        contraTest(toString(getStatement("simpleMapSchema")),map);

        o = new Object(){
            public String getP() {
                return p;
            }

            public void setP(String p) {
                this.p = p;
            }

            public String p;

        };
        contraTest(toString(getStatement("simpleMapSchema")),o);
    }

    @Test
    public void advancedMapTest(){
        // test
        Map map = createSimpleMap();
        Object o = new TestOnly1();
        test(toString(getStatement("advancedMapSchema")),map);
        test(toString(getStatement("advancedMapSchema")),o);
        test(toString(getStatement("simpleMapSchema")),map);
        test(toString(getStatement("simpleMapSchema")),o);
        map.put("property3",1);

        test(toString(getStatement("advancedMapSchema")),map);
      //  contraTest(simpleMapSchema,map);

        o =   new TestOnly3();
//        contraTest(simpleMapSchema,o);
        test(toString(getStatement("advancedMapSchema")),o);

        extract(toString(getStatement("advancedMapSchema")),o);
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




        test(toString(getStatement("simpleListArraySchema")),list);
        test(toString(getStatement("simpleListArraySchema")),o);
        list.clear();
        list.add(1);
        list.add("hola");
        o = new Object[]{1,"hola"};

        contraTest(toString(getStatement("simpleListArraySchema")),list);
        contraTest(toString(getStatement("simpleListArraySchema")),o);


    }
    @Test
    public void defArrayTest(){
        List list = createSimpleList();
        Map map = createSimpleMap();
        List root = new ArrayList();
        root.add(map);
        root.add(list);


        test(toString(getStatement("listDefTest")),root);
    }
    @Test
    public void defMapTest(){
        List list = createSimpleList();
        Map map = createSimpleMap();
        Map root = new Hashtable();
        root.put("property1",map);
        root.put("property2",list);


        test(toString(getStatement("mapDefTest")),root);

    }
    @Test
    public void deepArrayTest(){
        List list = createSimpleList();
        Map map = createSimpleMap();
        List root = new ArrayList();
        root.add(map);
        root.add(list);


        test(toString(getStatement("deepListArraySchema")),root);
    }
    @Test
    public void deepMapTest(){
        List list = createSimpleList();
        Map map = createSimpleMap();
        Map root = new Hashtable();
        root.put("root1",map);
        root.put("root2",list);



        test(toString(getStatement("deepMapTest")),root);
        extract(toString(getStatement("deepMapTest")),root);

    }
    @Test
    public void simpleAnonymousListTest(){

        List<Integer> list = new ArrayList();


        Integer[] o = new Integer[10];
        for(int i=0; i<10;i++){
            list.add(i);
            o[i]=1;
        }

        test(toString(getStatement("simpleAnonymousSchema")),list);
        test(toString(getStatement("simpleAnonymousSchema")),o);

        List<String> contraList = new ArrayList<>();
        String[] contraArray= new String[10];

        for(int i=0; i<10;i++){
            contraList.add("hola");
            contraArray[i]="hola";
        }
        contraTest(toString(getStatement("simpleAnonymousSchema")),contraList);
        contraTest(toString(getStatement("simpleAnonymousSchema")),contraArray);

        list.clear();
        o= new Integer[5];
        contraTest(toString(getStatement("simpleAnonymousSchema")),list);
        contraTest(toString(getStatement("simpleAnonymousSchema")),o);



    }
    @Test
    public void boundedAnonymousListTest(){

        List<Integer> list = new ArrayList();


        Integer[] o = new Integer[8];
        for(int i=0; i<8;i++){
            list.add(i);
            o[i]=1;
        }

        test(toString(getStatement("boundedAnonymousSchema")),list);
        test(toString(getStatement("boundedAnonymousSchema")),o);

        o = new Integer[2];
        list.clear();
        for(int i=0; i<2;i++){
            list.add(i);
            o[i]=1;
        }
        contraTest(toString(getStatement("boundedAnonymousSchema")),list);
        contraTest(toString(getStatement("boundedAnonymousSchema")),o);

        o = new Integer[15];
        list.clear();
        for(int i=0; i<15;i++){
            list.add(i);
            o[i]=1;
        }
        contraTest(toString(getStatement("boundedAnonymousSchema")),list);
        contraTest(toString(getStatement("boundedAnonymousSchema")),o);
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
