package eu.linksmart.api.event.datafusion.types;

import eu.linksmart.api.event.datafusion.exceptions.TraceableException;
import eu.linksmart.api.event.datafusion.exceptions.UnknownUntraceableException;
import eu.linksmart.api.event.datafusion.exceptions.UntraceableException;

import java.io.Serializable;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public interface JsonSerializable extends Serializable{

    JsonSerializable build() throws TraceableException, UntraceableException;
 //   void rebuild(T me) throws Exception;
    public void destroy()throws Exception;
}
