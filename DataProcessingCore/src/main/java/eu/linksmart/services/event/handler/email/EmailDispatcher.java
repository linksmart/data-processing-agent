package eu.linksmart.services.event.handler.email;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.linksmart.api.event.types.BaseEmail;
import eu.linksmart.services.event.intern.Const;

/**
 *  Copyright [2018] [ISMB]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * Dispatch email asynchronously
 *
 * @author Nadir Raimondo
 * @since  1.8.0
 *
 */
public class EmailDispatcher implements Runnable{
	
	/**
	 * Private email implementation to unlock the dispatch thread
	 * @author nadir
	 */
	private class EmptyEmail implements BaseEmail{
		@Override
		public String getHostName() { return "None"; }
		@Override
		public void send() throws Exception {}
	}
	
	private final Logger loggerService = LogManager.getLogger(this.getClass());



	private transient static EmailDispatcher instance;
	private static int references = 0;
	
	private boolean running;
	private Thread t;

	private final BlockingQueue<BaseEmail> emails;

	/**
	 * Get an instance of the dispatcher. To properly release the resources the release method must be called
	 * when the dispatcher is no longer necessary
	 * @return dispatcher instance
	 */
	public static EmailDispatcher getInstance()
	{
		synchronized (EmailDispatcher.class) {
			if(references++==0){
				instance = new EmailDispatcher();
				instance.startDispatch();
			}
			
			return instance;
		}
	}
	
	/**
	 * Release the dispatcher
	 */
	public static void release()
	{
		synchronized (EmailDispatcher.class) {
			//stop the dispatch thread if all the references are released
			if(--references == 0){ 
				instance.stopDispatch(); 
				instance = null;
			}
		}
	}

	private EmailDispatcher(){
		this.emails = new LinkedBlockingQueue<>(Const.EMAIL_DISPATCHER_QUEUE_CAPACITY);
	}
	
	/**
	 * Dispatch an email asynchronously
	 * @param email that must be dispatched
	 * @return false if the email queue is full and dispatch fails, true otherwise
	 */
	public boolean dispatch(BaseEmail email){
		if(this.emails.offer(email)) //email dispatch process could require some time to complete. A dedicated thread will dispatch the email
			return true;

		loggerService.warn("Queue is full - dropping emails");
		return false;
	}
	
	/**
	 * Start the dispatch thread
	 */
	private void startDispatch() { 
		loggerService.info("Starting dispatch thread");
		
		this.running = true;
		this.t = new Thread(this);
		this.t.start();
	}
	
	/**
	 * Stop the dispatch thread
	 */
	private void stopDispatch() { 
		loggerService.info("Releasing dispatch thread");

		this.running = false;
		this.emails.add(new EmptyEmail()); //add an empty email to unlock the queue if necessary

		loggerService.info("Joining dispatch thread");
		try { this.t.join(Const.EMAIL_DISPATCHER_JOIN_INTERVAL); } 
		catch (InterruptedException e) {
			loggerService.error(e.getMessage(), e);
		}

		loggerService.info("Release complete");
	}
	

	@Override
	public void run() {
		while(this.running){
			try{
				BaseEmail email = this.emails.take();
				
				loggerService.info("Sending mail through the host "+email.getHostName());
				email.send();
			}
			catch(Exception e){
				loggerService.error(e.getMessage(),e);
			}
		}
	}
}
