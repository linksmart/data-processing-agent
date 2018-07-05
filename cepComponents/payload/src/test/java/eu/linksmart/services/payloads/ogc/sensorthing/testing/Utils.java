package eu.linksmart.services.payloads.ogc.sensorthing.testing;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
//import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.services.payloads.ogc.sensorthing.*;
import eu.linksmart.services.payloads.ogc.sensorthing.Sensor;
import eu.linksmart.services.payloads.ogc.sensorthing.base.CommonControlInfoImpl;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.Interval;
import eu.linksmart.services.payloads.ogc.sensorthing.linked.*;
import org.geojson.LngLatAlt;
import org.geojson.Point;
import org.geojson.Polygon;

import java.time.OffsetDateTime;
//import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by José Ángel Carvajal on 06.04.2016 a researcher of Fraunhofer FIT.
 */
public class Utils {
    public static Object parse(String toParse, Class theClass) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new SimpleModule("Observation", Version.unknownVersion()).addAbstractTypeMapping(eu.linksmart.services.payloads.ogc.sensorthing.Observation.class, ObservationImpl.class));
       // mapper.registerModule(new SimpleModule("EventEnvelope", Version.unknownVersion()).addAbstractTypeMapping(EventEnvelope.class, ObservationImpl.class));
        mapper.registerModule(new SimpleModule("CommonControlInfo", Version.unknownVersion()).addAbstractTypeMapping(CommonControlInfo.class, CommonControlInfoImpl.class));
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            Object object= mapper.readValue(toParse, theClass);
            return object;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


    }
    public static void testParsing(String json, Class theClass, Object toTest) {

        ObjectMapper mapper = new ObjectMapper();
        //mapper.registerModule(new SimpleModule("Observation", Version.unknownVersion()).addAbstractTypeMapping(eu.linksmart.services.payloads.ogc.sensorthing.Observation.class, ObservationImpl.class));
        //mapper.registerModule(new SimpleModule("EventEnvelope", Version.unknownVersion()).addAbstractTypeMapping(EventEnvelope.class, ObservationImpl.class));
        //mapper.registerModule(new SimpleModule("CommonControlInfo", Version.unknownVersion()).addAbstractTypeMapping(CommonControlInfo.class, CommonControlInfoImpl.class));
        //mapper.registerModule(new SimpleModule("Observation", Version.unknownVersion()).addAbstractTypeMapping(Observation.class, ObservationImpl.class));
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String strFoi1 = "1";
        try {
            strFoi1 = mapper.writeValueAsString(toTest);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        String strFoi2 = "2";
        Object foi2 = parse(json, theClass);
        try {
            strFoi2 = mapper.writeValueAsString(foi2);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        Map o1=null, o2=null;
        o1 = (Map) parse(strFoi1, Hashtable.class);
        o2 = (Map) parse(strFoi2, Hashtable.class);

        boolean strTest = strFoi1.equals(strFoi2);
        System.out.println("-------------------------------------");
        System.out.println("Testing from "+ (strTest? "Json String vs Serialized Object:":"Json Map object vs Serialized Map Object")+" of class " + theClass.getSimpleName());
        System.out.println("Expecting:");
        System.out.println(strFoi1);
        System.out.println("Received:");
        System.out.println(strFoi2);
        System.out.println("-------------------------------------");
        if( !strTest)
            assertTrue(o1!=null && o1.equals(o2));
        else
            assertEquals(strFoi1,strFoi2);
    }



    public static eu.linksmart.services.payloads.ogc.sensorthing.FeatureOfInterest constructFeatureOfInterest(boolean childObjects) {
        eu.linksmart.services.payloads.ogc.sensorthing.FeatureOfInterest featureOfInterest = new FeatureOfInterestImpl();
        featureOfInterest.setId(1);
        featureOfInterest.setDescription("This is a weather station.");
        featureOfInterest.setEncodingType("application/vnd.geo+json");
        Map<String,Object> f = new LinkedHashMap<>();
        f.put("type","Point");
        f.put("coordinates",new double[]{-114.06,51.05});
        featureOfInterest.setFeature(f);

        return featureOfInterest;

    }


    public static Sensor constructSensor(boolean childObjects) {
        Sensor sensor = new SensorImpl();
        sensor.setId(1);

        sensor.setDescription("TMP36 - Analog Temperature sensor");
        sensor.setEncodingType("application/pdf");
        sensor.setMetadata("http://example.org/TMP35_36_37.pdf");

        return sensor;

    }


    public static eu.linksmart.services.payloads.ogc.sensorthing.Observation constructObservation(boolean childObjects) {
        eu.linksmart.services.payloads.ogc.sensorthing.Observation observation = new ObservationImpl();
        observation.setId(1);

        observation.setPhenomenonTime(Date.from(OffsetDateTime.parse("2014-12-31T11:59:59.00+08:00").toInstant()));
        observation.setResultTime(Date.from(OffsetDateTime.parse("2014-12-31T11:59:59.00+08:00").toInstant()));
        observation.setResult(70.4);


        return observation;

    }


    public static ObservedProperty constructObservedProperty(boolean childObjects) {

        ObservedProperty observedProperty = new ObservedPropertyImpl();
        observedProperty.setId(1);
        observedProperty.setDescription("The dewpoint temperature is the temperature to which the air must " +
                "be cooled, at constant pressure, for dew to form. As the grass and other objects " +
                "near the ground cool to the dewpoint, some of the water vapor in the atmosphere " +
                "condenses into liquid water on the objects.");
        observedProperty.setName("DewPoint Temperature");
        observedProperty.setDefinition("http://dbpedia.org/page/Dew_point");
        if (childObjects){
            //TODO: datastream
        }
        return observedProperty;

    }


    public static Datastream constructDatastream(boolean childObjects) {
        Datastream datastream = new DatastreamImpl();
        datastream.setId(1);
        datastream.setDescription("This is a datastream measuring the temperature in an oven.");
        Map<String,Object> f = new LinkedHashMap<>();
        f.put("symbol","°C");
        f.put("name","degree Celsius");
        f.put("definition", "http://unitsofmeasure.org/ucum.html#para-30");
        datastream.setUnitOfMeasurement(f);
        datastream.setObservationType("http://www.opengis.net/def/observationType/OGCOM/2.0/OM_Measurement");
        f = new LinkedHashMap<>();
        f.put("type","Polygon");
        List<LngLatAlt> l =new LinkedList<LngLatAlt>();
        l.add(new LngLatAlt(100,0));
        l.add(new LngLatAlt(101,0));
        l.add(new LngLatAlt(101,1));
        l.add(new LngLatAlt(100,1));
        l.add(new LngLatAlt(100,0));
        datastream.setObservedArea(new Polygon(l));


        try {
            datastream.setPhenomenonTime(Interval.parse("2014-03-01T13:00:00Z/2015-05-11T15:30:00Z"));
            datastream.setResultTime(Interval.parse("2014-03-01T13:00:00Z/2015-05-11T15:30:00Z"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(childObjects){
            //todo: child objects
        }
        return datastream;


    }

    public static HistoricalLocation constructHistoricalLocation(boolean childObjects) {
        HistoricalLocation historicalLocation = new HistoricalLocationImpl();
        historicalLocation.setId(1);
        historicalLocation.setTime(Date.from(OffsetDateTime.parse("2015-01-25T12:00:00-07:00").toInstant()));
        if(childObjects){
            //todo
        }
        return historicalLocation;


    }




    public static Location constructLocation(boolean childObjects) {
        Location location = new LocationImpl();
        location.setId(1);
        location.setLocation(new Point(-114.06,51.05));
        location.setEncodingType("application/vnd.geo+json");
        if(childObjects){
            //todo
        }

        return location;


    }



    public static Thing constructThing(boolean childObjects) {
        Thing thing = new ThingImpl();
        thing.setId(1);
        thing.setDescription("This thing is an oven.");
        thing.addProperty("owner", "John Doe");
        thing.addProperty("color", "Silver");
        if(childObjects){
            //todo
        }

        return thing;


    }


}
