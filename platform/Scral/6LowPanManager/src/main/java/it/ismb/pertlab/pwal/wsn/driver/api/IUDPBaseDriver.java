package it.ismb.pertlab.pwal.wsn.driver.api;

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
