/**
 * 
 */
package it.ismb.pertlab.pwal.api.devices.model;

import it.ismb.pertlab.pwal.api.utils.SemanticModel;


/**
 * @author bonino
 *
 */
@SemanticModel(name="class",value="http://almanac-project.eu/ontologies/smartcity.owl#WasteBin")
public interface WasteBin extends Thermometer, FillLevel
{

}
