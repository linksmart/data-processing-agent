package eu.linksmart.services.payloads.ogc.sensorthing.testing;

/**
 * Created by José Ángel Carvajal on 04.04.2016 a researcher of Fraunhofer FIT.
 */

import eu.linksmart.services.payloads.ogc.sensorthing.*;
import eu.linksmart.services.payloads.ogc.sensorthing.linked.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class OGCSensorThingLinkedUnitTest {

    /**
     * FeatureOfInterest Tests:
     *
     *
     * */
      String featureOfInterestJSON =
            "{" +
            "\"@iot.id\":1," +
            "\"@iot.selfLink\":\"http://linksmart.eu/v1.0/FeaturesOfInterest(1)\"," +
            "\"Observations@iot.navigationLink\":\"FeaturesOfInterest(1)/Observations\"," +
            "\"description\":\"This is a weather station.\"," +
            "\"encodingType\":\"application/vnd.geo+json\"," +
            "\"feature\":{" +
                    "\"type\": \"Point\"," +
                    "\"coordinates\":[-114.06,51.05]" +
            "}" +
            "}";





  // @Test
    public void TestParsingFeatureOfInterest() {

        Utils.testParsing(featureOfInterestJSON, FeatureOfInterestImpl.class, UtilsLinked.constructFeatureOfInterest());

    }
    /**
     * Sensor Tests:
     *
     *
     * */

    String sensorJSON =
            "{\n" +
                    " \"@iot.id\": 1,\n" +
                    " \"@iot.selfLink\": \"http://linksmart.eu/v1.0/Sensors(1)\",\n" +
                    " \"Datastreams@iot.navigationLink\": \"Sensors(1)/Datastreams\",\n" +
                    " \"description\": \"TMP36 - Analog Temperature sensor\",\n" +
                    " \"encodingType\": \"application/pdf\",\n" +
                    " \"metadata\": \"http://example.org/TMP35_36_37.pdf\"\n" +
                    "}";



   // @Test
    public void TestParsingSensor() {

        Utils.testParsing(sensorJSON, Sensor.class, UtilsLinked.constructSensor(false));

    }

    /**
     * Observation Tests:
     *
     *
     * */
    String observationJSON =
            "{\n" +
                    " \"@iot.id\": 1,\n" +
                    " \"@iot.selfLink\": \"http://linksmart.eu/v1.0/Observations(1)\",\n" +
                    " \"FeatureOfInterest@iot.navigationLink\": \"Observations(1)/FeatureOfInterest\",\n" +
                    " \"Datastream@iot.navigationLink\":\"Observations(1)/Datastream\",\n" +
                    " \"phenomenonTime\": \"2014-12-31T11:59:59.00+08:00\",\n" +
                    " \"resultTime\": \"2014-12-31T11:59:59.00+08:00\",\n" +
                    " \"result\": 70.4\n" +

            "}";

    String observationChildJSON =
                    "{\n" +
                        " \"@iot.id\": 1,\n" +
                        " \"@iot.selfLink\": \"http://linksmart.eu/v1.0/Observation(1)\",\n" +
                        " \"phenomenonTime\": \"2014-12-31T11:59:59.00+08:00\",\n" +
                        " \"resultTime\": \"2014-12-31T11:59:59.00+08:00\",\n" +
                        " \"result\": 70.4,\n" +
                        " \"FeatureOfInterest@iot.navigationLink\": \"Observation(1)/FeatureOfInterest\",\n" +
                        " \"Datastream@iot.navigationLink\": \"Observation(1)/Sensor\",\n" +
                        "\"featureOfInterest\": {\n" +
                            "\"@iot.id\": 2,\n" +
                            "\"@iot.selfLink\":\"http://linksmart.eu/v1.0/FeaturesOfInterest(2)\",\n" +
                            "\"description\":\"This is a weather station.\",\n" +
                            "\"encodingType\":\"application/vnd.geo+json\",\n" +
                            "\"feature\":{\n" +
                                "\"type\": \"Point\",\n" +
                                "\"coordinates\":[-114.06,51.05]\n" +
                            "}\n" +
                        "},\n"+
                        "\"datastream\": "+
                            "{\n" +
                            " \"@iot.id\": 3,\n" +
                            " \"@iot.selfLink\": \"http://linksmart.eu/v1.0/Datastreams(3)\",\n" +
                            " \"Thing@iot.navigationLink\": \"Datastreams(3)/Thing\",\n" +
                            " \"Sensor@iot.navigationLink\": \"Datastreams(3)/Sensor\",\n" +
                            " \"ObservedProperty@iot.navigationLink\": \"Datastreams(3)/ObservedProperty\",\n" +
                            " \"Observations@iot.navigationLink\": \"Datastreams(3)/Observations\",\n" +
                            " \"description\": \"This is a datastream measuring the temperature in an oven.\",\n" +
                            " \"unitOfMeasurement\": {\n" +
                                " \"name\": \"degree Celsius\",\n" +
                                " \"symbol\": \"°C\",\n" +
                                " \"definition\": \"http://unitsofmeasure.org/ucum.html#para-30\"\n" +
                                " },\n" +
                                " \"observationType\": \"http://www.opengis.net/def/observationType/OGCOM/2.0/OM_Measurement\",\n" +
                                " \"observedArea\": {\n" +
                                " \"type\": \"Polygon\",\n" +
                                " \"coordinates\": [[[100,0],[101,0],[101,1],[100,1],[100,0]]]\n" +
                            " },\n" +
                            "\"sensor\":{\n" +
                                " \"@iot.id\": 1,\n" +
                                " \"@iot.selfLink\": \"http://linksmart.eu/v1.0/Sensors(1)\",\n" +
                                " \"Datastreams@iot.navigationLink\": \"Sensors(1)/Datastreams\",\n" +
                                " \"description\": \"TMP36 - Analog Temperature sensor\",\n" +
                                " \"encodingType\": \"application/pdf\",\n" +
                                " \"metadata\": \"http://example.org/TMP35_36_37.pdf\"\n" +
                            "},\n"+
                            "\"observedProperty\":{\n" +
                            " \"@iot.id\": 1,\n" +
                            " \"@iot.selfLink\": \"http://linksmart.eu/v1.0/ObservedProperties(1)\",\n" +
                            " \"Datastreams@iot.navigationLink\": \"ObservedProperties(1)/Datastreams\",\n" +
                            " \"description\": \"The dewpoint temperature is the temperature to which the air must " +
                            "be cooled, at constant pressure, for dew to form. As the grass and other objects " +
                            "near the ground cool to the dewpoint, some of the water vapor in the atmosphere " +
                            "condenses into liquid water on the objects.\",\n" +
                            " \"name\": \"DewPoint Temperature\",\n" +
                            " \"definition\": \"http://dbpedia.org/page/Dew_point\"\n" +
                            "},"+
                            " \"phenomenonTime\": \"2014-03-01T13:00:00Z/2015-05-11T15:30:00Z\",\n" +
                           " \"resultTime\": \"2014-03-01T13:00:00Z/2015-05-11T15:30:00Z\",\n" +
                           "\"thing\": "+
                                 "{\n" +
                              " \"@iot.id\": 1,\n" +
                                " \"@iot.selfLink\": \"http://linksmart.eu/v1.0/Things(1)\",\n" +
                                " \"Locations@iot.navigationLink\": \"Things(1)/Locations\",\n" +
                                " \"Datastreams@iot.navigationLink\": \"Things(1)/Datastreams\",\n" +
                                " \"HistoricalLocations@iot.navigationLink\": \"Things(1)/HistoricalLocations\",\n" +
                                " \"description\": \"This thing is an oven.\",\n" +
                                " \"properties\": {\n" +
                                    " \"owner\": \"John Doe\",\n" +
                                    " \"color\": \"Silver\"" +
                                "},\n" +
                                "\"locations\": "+
                                    "[" +
                                        "{\n" +
                                        " \"@iot.id\": 1,\n" +
                                        " \"@iot.selfLink\": \"http://linksmart.eu/v1.0/Locations(1)\",\n" +
                                        " \"Things@iot.navigationLink\": \"Locations(1)/Things\",\n" +
                                        " \"HistoricalLocations@iot.navigationLink\": \"Locations(1)/HistoricalLocations\",\n" +
                                        " \"encodingType\": \"application/vnd.geo+json\",\n" +
                                        " \"location\": {\n" +
                                        " \"type\": \"Point\",\n" +
                                        " \"coordinates\": [-114.06,51.05]\n" +
                                        " }\n" +
                                        "}"+
                                    "],"+
                                "\"historicalLocations\":"+
                                "["+
                                    " {\n" +
                                    " \"@iot.id\": 1,\n" +
                                    " \"@iot.selfLink\": \"http://linksmart.eu/v1.0/HistoricalLocations(1)\",\n" +
                                    " \"Locations@iot.navigationLink\": \"HistoricalLocations(1)/Locations\",\n" +
                                    " \"Thing@iot.navigationLink\": \"HistoricalLocations(1)/Thing\",\n" +
                                    " \"time\": \"2015-01-25T12:00:00-07:00\"\n" +
                                    " }"+
                                "]"+
                            " }\n" +
                            "}"+
                        "}"+
                    "}";

    @Test
    public void TestParsingObservation() {

      //  UtilsLinked.testParsing(observationJSON, Observation.class, UtilsLinked.constructObservation(false));
        Utils.testParsing(observationChildJSON, Observation.class, UtilsLinked.constructObservation());

    }
    String observedPropertyJSON =
                    "{\n" +
                    " \"@iot.id\": 1,\n" +
                    " \"@iot.selfLink\": \"http://linksmart.eu/v1.0/ObservedProperties(1)\",\n" +
                    " \"Datastreams@iot.navigationLink\": \"ObservedProperties(1)/Datastreams\",\n" +
                    " \"description\": \"The dewpoint temperature is the temperature to which the air must " +
                        "be cooled, at constant pressure, for dew to form. As the grass and other objects " +
                        "near the ground cool to the dewpoint, some of the water vapor in the atmosphere " +
                        "condenses into liquid water on the objects.\",\n" +
                    " \"name\": \"DewPoint Temperature\",\n" +
                    " \"definition\": \"http://dbpedia.org/page/Dew_point\"\n" +
                    "}";

    String observedPropertyChildJSON =
            "";


   // @Test
    public void TestParsingObservedProperty() {

        //UtilsLinked.testParsing(observedPropertyJSON, ObservedProperty.class, UtilsLinked.constructObservedProperty(false));
        Utils.testParsing(observedPropertyChildJSON, ObservationImpl.class, UtilsLinked.constructObservedProperty(true));

    }
    String datastreamJSON =
            "{\n" +
                " \"@iot.id\": 1,\n" +
                " \"@iot.selfLink\": \"http://linksmart.eu/v1.0/Datastreams(1)\",\n" +
                " \"Thing@iot.navigationLink\": \"Datastreams(1)/Thing\",\n" +
                " \"Sensor@iot.navigationLink\": \"Datastreams(1)/Sensor\",\n" +
                " \"ObservedProperty@iot.navigationLink\": \"Datastreams(1)/ObservedProperty\",\n" +
                " \"Observations@iot.navigationLink\": \"Datastreams(1)/Observations\",\n" +
                " \"description\": \"This is a datastream measuring the temperature in an oven.\",\n" +
                " \"unitOfMeasurement\": {\n" +
                    " \"name\": \"degree Celsius\",\n" +
                    " \"symbol\": \"°C\",\n" +
                    " \"definition\": \"http://unitsofmeasure.org/ucum.html#para-30\"\n" +
                " },\n" +
                " \"observationType\": \"http://www.opengis.net/def/observationType/OGCOM/2.0/OM_Measurement\",\n" +
                " \"observedArea\": {\n" +
                    " \"type\": \"Polygon\",\n" +
                    " \"coordinates\": [[[100,0],[101,0],[101,1],[100,1],[100,0]]]\n" +
                " },\n" +
                " \"phenomenonTime\": \"2014-03-01T13:00:00Z/2015-05-11T15:30:00Z\",\n" +
                " \"resultTime\": \"2014-03-01T13:00:00Z/2015-05-11T15:30:00Z\"\n" +
            "}";

    String datastreamChildJSON =
            "";

    String historicalLocationJSON =" {\n" +
            " \"@iot.id\": 1,\n" +
            " \"@iot.selfLink\": \"http://linksmart.eu/v1.0/HistoricalLocations(1)\",\n" +
            " \"Locations@iot.navigationLink\": \"HistoricalLocations(1)/Locations\",\n" +
            " \"Thing@iot.navigationLink\": \"HistoricalLocations(1)/Thing\",\n" +
            " \"time\": \"2015-01-25T12:00:00-07:00\"\n" +
            " }";

    String historicalLocationChildJSON =
            "";



  // @Test
    public void TestParsingHistoricalLocation() {

        Utils.testParsing(historicalLocationJSON, HistoricalLocation.class, UtilsLinked.constructHistoricalLocation(false));
        //testParsing(datastreamJSON,Datastream.class,constructDatastream(true));

    }
    String locationJSON ="{\n" +
            " \"@iot.id\": 1,\n" +
            " \"@iot.selfLink\": \"http://linksmart.eu/v1.0/Locations(1)\",\n" +
            " \"Things@iot.navigationLink\": \"Locations(1)/Things\",\n" +
            " \"HistoricalLocations@iot.navigationLink\": \"Locations(1)/HistoricalLocations\",\n" +
            " \"encodingType\": \"application/vnd.geo+json\",\n" +
            " \"location\": {\n" +
            " \"type\": \"Point\",\n" +
            " \"coordinates\": [-114.06,51.05]\n" +
            " }\n" +
            "}";

    String locationChildJSON =
            "";



  //  @Test
    public void TestParsingLocation() {

        Utils.testParsing(locationJSON, Location.class, UtilsLinked.constructLocation(false));
        //testParsing(datastreamJSON,Datastream.class,constructDatastream(true));

    }

    String thingJSON =
            "{\n" +
            " \"@iot.id\": 1,\n" +
            " \"@iot.selfLink\": \"http://linksmart.eu/v1.0/Things(1)\",\n" +
            " \"Locations@iot.navigationLink\": \"Things(1)/Locations\",\n" +
            " \"Datastreams@iot.navigationLink\": \"Things(1)/Datastreams\",\n" +
            " \"HistoricalLocations@iot.navigationLink\": \"Things(1)/HistoricalLocations\",\n" +
            " \"description\": \"This thing is an oven.\",\n" +
            " \"properties\": {\n" +
            " \"owner\": \"John Doe\",\n" +
            " \"color\": \"Silver\"\n" +
            " }\n" +
            "}";

    String thingChildJSON =
            "";




   // @Test
    public void TestParsingThing() {

        Utils.testParsing(thingJSON, Thing.class, UtilsLinked.constructThing(false));
        //testParsing(datastreamJSON,Datastream.class,constructDatastream(true));

    }
}
