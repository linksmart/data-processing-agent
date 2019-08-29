package eu.linksmart.services.payloads.ogc.sensorthing.testing;

import eu.linksmart.services.payloads.ogc.sensorthing.*;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.Interval;
import eu.linksmart.services.payloads.ogc.sensorthing.linked.*;
import org.geojson.LngLatAlt;
import org.geojson.Point;
import org.geojson.Polygon;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Created by José Ángel Carvajal on 06.04.2016 a researcher of Fraunhofer FIT.
 */
public class UtilsLinked {




    public static FeatureOfInterestImpl constructFeatureOfInterest() {
        FeatureOfInterestImpl featureOfInterest = new FeatureOfInterestImpl();
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


    public static ObservationImpl constructObservation() {
        ObservationImpl observation = new ObservationImpl();
        observation.setId(1);

        try {

            observation.setPhenomenonTime(eu.linksmart.services.utils.function.Utils.formISO8601("2014-12-31T11:59:59.00+08:00"));
            observation.setResultTime(eu.linksmart.services.utils.function.Utils.formISO8601("2014-12-31T11:59:59.00+08:00"));
        }catch (Exception e){
            return null;
        }
        observation.setResult(70.4);
        observation.setFeatureOfInterest(constructFeatureOfInterest());
        observation.getFeatureOfInterest().setId(2);
        observation.setDatastream(constructDatastream(false));
        observation.getDatastream().setId(3);
        observation.getDatastream().addObservation(observation);
        observation.getDatastream().setSensor(constructSensor(false));
        observation.getDatastream().setObservedProperty(constructObservedProperty(false));
        observation.getDatastream().setThing(constructThing(false));
        observation.getDatastream().getThing().addLocation(constructLocation(false));
        observation.getDatastream().getThing().addHistoricalLocation(constructHistoricalLocation(false));

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
        try {
            historicalLocation.setTime(eu.linksmart.services.utils.function.Utils.formISO8601("2015-01-25T12:00:00-07:00"));
        }catch (Exception e){
            return null;
        }

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
