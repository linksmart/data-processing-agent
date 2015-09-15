/**
 * 
 */
package it.ismb.pertlab.pwal.api.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;



/**
 * @author bonino
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface SemanticModel
{

	String value();

	String name();
	
}
