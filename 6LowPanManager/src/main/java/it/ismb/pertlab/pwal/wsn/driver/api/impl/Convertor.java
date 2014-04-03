/**
 * This file is a part of the basic OSGi tutorial. This is tutorial will be
 * consist of many breakdowns. This tutorial is growing step
 * by step to cover all the technical directive for building a good
 * OSGi applications.
 */
package it.ismb.pertlab.pwal.wsn.driver.api.impl;

import it.ismb.pertlab.pwal.wsn.driver.api.IConvertor;
import it.ismb.pertlab.pwal.wsn.driver.api.IMessage;

public abstract class Convertor implements IConvertor {

	public Convertor() {
			dump("Abstract Convertor ");
	}

	
	@Override
	public IMessage net2host(Object input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object host2net(IMessage input) {
		// TODO Auto-generated method stub
		return null;
	}

	private void dump(String string) {
		// TODO Auto-generated method stub
		
	}

}
