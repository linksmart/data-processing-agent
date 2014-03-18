/**
 * This file is a part of the basic OSGi tutorial. This is tutorial will be
 * consist of many breakdowns. This tutorial is growing step
 * by step to cover all the technical directive for building a good
 * OSGi applications.
 */
package it.ismb.pert.example5;

import org.osgi.service.device.Device;

/**
 * @author atalla
 * {@value}
 * @brief interface extends the Device interface
 */
public interface TunerDevice extends Device{
	/**
	 * @brief set the State Variable of the tuner to specific value
	 * @param numeric integer value to be assigned to tuner state
	 */
	void setState(int val);
	/**
	 * @brief get the tuner state value
	 * @return numeric integer value of the tuner state
	 */
	int getState();
}
