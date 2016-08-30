package eu.linksmart.api.event.datafusion.types;

import java.io.Serializable;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public interface JsonSerializable extends Serializable{

    JsonSerializable build() throws Exception;
 //   void rebuild(T me) throws Exception;
    public void destroy()throws Exception;
}
