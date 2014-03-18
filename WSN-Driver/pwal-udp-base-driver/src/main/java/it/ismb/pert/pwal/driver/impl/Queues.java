package it.ismb.pert.pwal.driver.impl;

import it.ismb.pert.pwal.driver.api.IMessage;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 
 * @author atalla
 *Singletone Pattern
 */
public class Queues {
	private static Queues queues = new Queues();
	Queue<IMessage> OQueue = new LinkedList<IMessage>();
	Queue<IMessage> IQueue = new LinkedList<IMessage>();
	private Queues(){}
	public static Queues getInstance(){
		return queues;
	}
	public Queue getOQueue(){return OQueue;}
	public Queue getIQueue(){return IQueue;}	
}
