package eu.almanac.event.datafusion.utils.generic;


/**
 * Created by Caravajal on 01.04.2015.
 */
@Deprecated
public interface GenericCEP<T> {
    String GENERATED = "@CEPEvent";
    String TIMESTAMP = "@Timestamp";
    T aggregateToAnEvent(T cepEvent);
    void addValue(String key, Object value);
    Object getValue(String key);
    void setBaseName(String name);
    boolean isGenerated();
}
