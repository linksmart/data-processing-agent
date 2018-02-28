package eu.linksmart.services.event.handler.email;


import java.util.Map;
import java.util.stream.Collectors;

import eu.linksmart.api.event.components.ComplexEventPropagationHandler;
import eu.linksmart.api.event.components.Publisher;
import eu.linksmart.api.event.exceptions.StatementException;
import eu.linksmart.api.event.types.Statement;
import eu.linksmart.services.event.handler.base.BaseMapEventHandler;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.services.utils.serialization.Serializer;

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
 * Handler to send query results to email recipients.  
 *
 * @author Nadir Raimondo
 * @since  1.8.0
 *
 */
public class EmailEventHandler extends BaseMapEventHandler implements ComplexEventPropagationHandler {

	private Publisher publisher;
	private Serializer serializer;

	public EmailEventHandler(Statement stmt, Publisher publisher, Serializer serializer) throws StatementException {
		super(stmt);
		this.publisher = publisher;
		this.serializer = serializer;

		try {
			loggerService.info("The Agent (ID:" + SharedSettings.getId() + ") generating events for statement ID "+query.getId()+" to the following recipient(s): " + publisher.getOutputs().stream().collect(Collectors.joining(",")));

		}catch (IllegalArgumentException e){
			throw new StatementException(query.getId(), "Statement", e.getMessage());
		}
	}

	@Override
	public  synchronized void destroy(){
		publisher.close();
		serializer.close();
	}

	@Override
	protected void processMessage(Map[] events) {
		processMaps(events);
	}

	@Override
	protected void processLeavingMessage(Map[] events) {
		processMaps(events);
	}
	
	
	protected void processMaps(Map[] events){
        if (events == null || events.length==0)
        	return;

		query.setLastOutput(events);
		try {
			 publisher.publish(serializer.serialize(query.getLastOutput()));
		} catch (Exception e) {
			loggerService.error(e.getMessage(), e);
		}
	}
	
    @Override
    public Publisher getPublisher() {
        return publisher;
    }

    @Override
    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

	public Serializer getSerializer() {
		return serializer;
	}

	public void setSerializer(Serializer serializer) {
		this.serializer = serializer;
	}
	
    
}
