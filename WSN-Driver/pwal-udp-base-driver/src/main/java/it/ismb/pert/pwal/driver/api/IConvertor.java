/**
 * This file is a part of the basic OSGi tutorial. This is tutorial will be
 * consist of many breakdowns. This tutorial is growing step
 * by step to cover all the technical directive for building a good
 * OSGi applications.
 */
package it.ismb.pert.pwal.driver.api;

public interface IConvertor {
	
	IMessage net2host(Object input);
	Object host2net(IMessage input);
	
}
