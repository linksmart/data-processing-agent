/**
 * 
 */
package it.ismb.pertlab.smartcity.data;

import it.ismb.pertlab.smartcity.api.Quarter;

/**
 * @author bonino
 *
 */
public interface QuarterRenderer
{
	String render(Quarter quarter);
	String renderQuarterOnly(Quarter quarter);
}
