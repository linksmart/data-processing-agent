package eu.linksmart.services.event.handler;


import eu.linksmart.services.event.handler.EmailPublisher.DataFormat;
import eu.linksmart.services.event.handler.email.EmailEventHandler;
import eu.linksmart.services.event.handler.email.EmailTextSerializer;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.api.event.exceptions.StatementException;
import eu.linksmart.api.event.types.Statement;

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
 * Handler to asynchronously send textual query results to email recipients.
 *
 * @author Nadir Raimondo
 * @since  1.8.0
 *
 */
public class TextEmailEventHandler extends EmailEventHandler{
	
	private final static String EMAIL_BODY_MSG = "*** This is an automatically generated email, please do not reply\n\n" +
				 								 "*** LinkSmart Agent: '%s'\n*** Query name: '%s'\n*** Query id: '%s'\n\nQuery result(s):\n";
	
	public TextEmailEventHandler(Statement stmt) throws StatementException {
		super(stmt, 
			  new EmailPublisher(stmt, DataFormat.TEXT, String.format(EMAIL_BODY_MSG, SharedSettings.getId(), stmt.getName(), stmt.getId())),
			  new EmailTextSerializer());
	}

}
