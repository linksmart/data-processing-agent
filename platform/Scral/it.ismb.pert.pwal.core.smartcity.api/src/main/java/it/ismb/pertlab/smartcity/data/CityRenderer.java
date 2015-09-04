/**
 * 
 */
package it.ismb.pertlab.smartcity.data;

import it.ismb.pertlab.smartcity.api.SmartCity;

/**
 * @author bonino
 *
 */
public interface CityRenderer
{
	public String render(SmartCity city);
	public String renderCityOnly(SmartCity city, String prefix);
}
