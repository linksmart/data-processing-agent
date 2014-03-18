/**
 * This file is a part of the basic OSGi tutorial. This is tutorial will be
 * consist of many breakdowns. This tutorial is growing step
 * by step to cover all the technical directive for building a good
 * OSGi applications.
 */
package it.ismb.pert.pwal.driver.api;

public class MID {
		private static MID mid = new MID();
		private int cnt = 0;
		private MID(){}
		public MID getMID(){
			return mid;
		}
		public int nextMID(){
			return cnt++;
		}
}
