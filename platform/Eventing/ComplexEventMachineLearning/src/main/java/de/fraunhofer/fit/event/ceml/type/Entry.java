package de.fraunhofer.fit.event.ceml.type;

/**
 * Created by angel on 1/12/15.
 */
public class Entry {
    private String name;
    private Object value;

    public Entry(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }


    public static Entry newEntry(Object value, String name) {
        return new Entry(name,value);
    }

}
