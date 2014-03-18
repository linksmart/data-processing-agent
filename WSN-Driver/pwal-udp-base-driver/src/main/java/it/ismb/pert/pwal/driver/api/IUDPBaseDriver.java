/**
 * This file is a part of the basic OSGi tutorial. This is tutorial will be
 * consist of many breakdowns. This tutorial is growing step
 * by step to cover all the technical directive for building a good
 * OSGi applications.
 */
/**
 * 
 */
package it.ismb.pert.pwal.driver.api;

import java.net.DatagramPacket;

/**
 * @author atalla
 *
 */
public interface IUDPBaseDriver {
	/**
	*	@brief This function sends an uni-cast UDP data packet to a known WSN-node 
	*	@param Request is composed by DatagramPacket and any new extensions. 
	*	@return IMessage is a Response message DatagramPacket which needs to be parsed and analyzed
	**/	
	DatagramPacket send(DatagramPacket request);	
}
