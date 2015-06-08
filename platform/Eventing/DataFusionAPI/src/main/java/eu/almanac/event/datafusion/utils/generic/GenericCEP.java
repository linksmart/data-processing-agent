package eu.almanac.event.datafusion.utils.generic;


/**
 * Created by Caravajal on 01.04.2015.
 */
public interface GenericCEP<T> {
    public static final String GENERATED = "@CEPEvent";
    public static final String TIMESTAMP = "@Timestamp";
    public T aggregateToAnEvent(T cepEvent);
    public void addValue(String key, Object value);
    public Object getValue(String key);
    public void setBaseName(String name);
    public boolean isGenerated();
}
