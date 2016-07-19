package eu.linksmart.api.event.ceml;

import java.io.Serializable;
import java.lang.reflect.MalformedParameterizedTypeException;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public interface JsonSerializable extends Serializable{

    JsonSerializable build() throws Exception;

}
