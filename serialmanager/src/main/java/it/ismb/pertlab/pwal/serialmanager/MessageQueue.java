package it.ismb.pertlab.pwal.serialmanager;

import java.util.concurrent.ArrayBlockingQueue;

public class MessageQueue extends Thread {

	private ArrayBlockingQueue<Byte> queue;
	private SerialManager manager;
	
	public MessageQueue(ArrayBlockingQueue<Byte> arrayBlockingQueue, SerialManager manager) {
		queue=arrayBlockingQueue;
		this.manager=manager;
	}

	@Override
	public void run() {
		StringBuffer ret=new StringBuffer();
    	while(true)
    	{
    		try {
    			Byte b=queue.take();
				ret.append((char)b.byteValue());
				if(b.byteValue()=='\n')
				{
					manager.dispatchMessage(ret.toString());
					ret=new StringBuffer();
				}
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
	}
	
	public void put(byte bl)
	{
		try {
			queue.put(bl);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
