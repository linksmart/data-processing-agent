/**
 * 
 */
package it.ismb.pertlab.smartcity.data;

import it.ismb.pertlab.smartcity.api.District;

/**
 * @author bonino
 *
 */
public interface DistrictRenderer
{
	public String render(District district);
	public String renderDistrictOnly(District district);
}
