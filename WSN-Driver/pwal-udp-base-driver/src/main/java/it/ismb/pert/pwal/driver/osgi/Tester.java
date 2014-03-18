/**
 * This file is a part of the basic OSGi tutorial. This is tutorial will be
 * consist of many breakdowns. This tutorial is growing step
 * by step to cover all the technical directive for building a good
 * OSGi applications.
 */
package it.ismb.pert.pwal.driver.osgi;

import it.ismb.pert.pwal.driver.impl.TelosBDriver;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
public class Tester implements CommandProvider{
	TelosBDriver telosbdriver ;
	public Tester(TelosBDriver telosbdriver){
		this.telosbdriver =telosbdriver;
	}
	@Override
	public String getHelp(){
		return "Tester->Override getHelp()";
	}
	public void _version(CommandInterpreter ci){
		 ci.execute("getprop osgi.framework");
	}
	public void _getTemprature(CommandInterpreter ci){
		telosbdriver.getTemprature();
		ci.println("getTemprature");
	}
	
}
