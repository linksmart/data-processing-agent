/**
 * This file is a part of the basic OSGi tutorial. This is tutorial will be
 * consist of many breakdowns. This tutorial is growing step
 * by step to cover all the technical directive for building a good
 * OSGi applications.
 */
package it.ismb.pert.example5;

public class TunerDeviceImpl implements TunerDevice {
	/**
	 * Place holder for the tuner state value.
	 */
	int state 	= 	-1;
	public TunerDeviceImpl() {
		state  = 0;	
	}
	@Override
	public void noDriverFound() {
		state =  -1;

	}
	@Override
	public void setState(int val) {
		dump("setState("+val+")");
		this.state = val;

	}
	@Override
	public int getState() {
		dump("getState()<-" + state);
		return state;
	}
	
	private void dump(String str){
			System.out.println(str);
	}

}
