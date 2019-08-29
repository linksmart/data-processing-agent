package eu.linksmart.services.event.handler;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.activation.DataSource;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.linksmart.api.event.components.Publisher;
import eu.linksmart.api.event.types.BaseEmail;
import eu.linksmart.api.event.types.Statement;
import eu.linksmart.services.event.handler.email.EmailDispatcher;
import eu.linksmart.services.event.handler.email.EmailMimeTypes;
import eu.linksmart.services.event.handler.email.EmailServerSettings;
import eu.linksmart.services.event.handler.email.EmailServerSettings.ScopeKey;
import eu.linksmart.services.event.handler.email.InputStreamDataSource;

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
 * Allows to send textual data as the body of an email message.
 *
 * @author Nadir Raimondo
 * @since  1.8.0
 *
 */
public class EmailPublisher implements Publisher {
	private final static String DEFAULT_MIME = "application/octet-stream";
	
	/**
	 * Wrapper class for commons email 
	 * @author nadir
	 */
	protected class CommonsEmail implements BaseEmail{
		protected Email email;
		
		protected CommonsEmail(Email email){ this.email = email; }

		@Override
		public String getHostName() { return email.getHostName(); }
		@Override
		public void send() throws Exception { email.send(); }
	}
	
	/**
	 * Data format 
	 * @author nadir
	 */
	public enum DataFormat{
		TEXT, HTML, ATTACHMENT
	}
	private String id;
	private final boolean async;

	private final String name, defaultMsg, mimeType, extension;
	private final DataFormat format;
	private final EmailDispatcher dispatcher;
	private final EmailValidator validator;
	private final List<String> recipients;
	private final List<EmailServerSettings> servers;


	private final Logger loggerService = LogManager.getLogger(this.getClass());
	
	/**
	 * Class contructor 
	 * @param stmt the statement handled by the publisher
	 * @param format the format of the data that will provided through the publish method
	 */
	public EmailPublisher(Statement stmt, DataFormat format) {
		this(stmt, format, null);
	}
	
	/**
	 * Class contructor 
	 * @param stmt the statement handled by the publisher
	 * @param format the format of the data that will provided through the publish method
	 * @param defaultMsg the default body of the mail. If the DataFormat is TEXT the defaultMsg is prepended to the payload,  if format is HTML the defaultMsg is the alternative text if html is not supported, if format is ATTACHMENT the defaultMsg will be the body of the message
	 */
	public EmailPublisher(Statement stmt, DataFormat format, String defaultMsg) {
		this(stmt, format, defaultMsg, null, true);
	}
	
	/**
	 * Class contructor 
	 * @param stmt the statement handled by the publisher
	 * @param format the format of the data that will provided through the publish method
	 * @param async if true emails are sent synchronously (it could be a slow process)
	 */
	public EmailPublisher(Statement stmt, DataFormat format, boolean async) {
		this(stmt, format, null, null, async);
	}
	
	/**
	 * Class contructor 
	 * @param stmt the statement handled by the publisher
	 * @param format the format of the data that will provided through the publish method
	 * @param defaultMsg the default body of the mail. If the DataFormat is TEXT the defaultMsg is prepended to the payload,  if format is HTML the defaultMsg is the alternative text if html is not supported, if format is ATTACHMENT the defaultMsg will be the body of the message
	 * @param async if true emails are sent synchronously (it could be a slow process)
	 */
	public EmailPublisher(Statement stmt, DataFormat format, String defaultMsg, boolean async) {
		this(stmt, format, defaultMsg, null, async);
	}

	/**
	 * 
	 * Class contructor 
	 * @param stmt the statement handled by the publisher
	 * @param format the format of the data that will provided through the publish method
	 * @param defaultMsg the default body of the mail. If the DataFormat is TEXT the defaultMsg is prepended to the payload,  if format is HTML the defaultMsg is the alternative text if html is not supported, if format is ATTACHMENT the defaultMsg will be the body of the message
	 * @param mimeType the mime type for the data attached. Default is application/octet-stream
	 * @param async if true emails are sent synchronously (it could be a slow process)
	 */
	public EmailPublisher(Statement stmt, DataFormat format, String defaultMsg, String mimeType, boolean async) {
		if(stmt == null || format == null){
			throw new IllegalArgumentException();
		}
		
		this.id = stmt.getId();
		this.name = stmt.getName();
		this.format = format;
		this.mimeType = mimeType==null?DEFAULT_MIME:mimeType;
		this.extension = EmailMimeTypes.getExtension(mimeType);
		
		this.defaultMsg = defaultMsg;

	
		this.servers = new ArrayList<>();
		this.recipients = new ArrayList<>();
		this.async = async;
		this.dispatcher = async?EmailDispatcher.getInstance():null;
		this.validator = EmailValidator.getInstance();

		initScopes(stmt.getScope());
		initRecipients(stmt.getOutput());
	}

	@Override
	public boolean publish(byte[] payload) {
		return sendEmails(this.servers, this.recipients, payload);
	}

	@Override
	public boolean publish(byte[] payload, String output, String scope) {
		//commons email API automatically check the output validity
		try{ return sendEmails(Arrays.asList(new EmailServerSettings(scope)), Arrays.asList(output), payload); }
		catch (IllegalArgumentException e){
			loggerService.warn("'"+scope+"' is not a valid scope");
			return false;
		}
	}

	@Override
	public boolean publish(byte[] payload, String output) {
		//commons email API automatically check the output validity
		return sendEmails(this.servers, Arrays.asList(output), payload);
	}

	/**
	 * Sends emails to the provided recipients through the provided servers
	 * @param servers server used to send emails
	 * @param recipients recipient email addresses
	 * @param payload data that must be sent to recipients
	 * @return false if sending the mail fails on each mail server
	 */
	private boolean sendEmails(List<EmailServerSettings> servers, List<String> recipients, byte[] payload){
		if(servers.size() == 0 || recipients.size() == 0){
			loggerService.warn("The publisher has not been configured properly. Email will not sent to recipients");
			return false;
		}

		int succeeded = 0;
		

		for(EmailServerSettings es: servers){
			try {
				BaseEmail email = initEmail(es, recipients, payload);
				
				if(this.async){ //asynchronous dispatch
					if(this.dispatcher.dispatch(email))
						succeeded++;
				}
				else email.send();
				

			} catch (Exception  e) {
				loggerService.error(e.getMessage(),e);
			}
		}

		return succeeded>0;
	}

	@Override
	public List<String> getOutputs() {
		return recipients;
	}

	//XXX throws a runtime exception if outputs are invalid
	@Override
	public void setOutputs(List<String> outputs) {
		this.recipients.clear();
		initRecipients(outputs);
	}

	@Override
	public List<String> getScopes() {
		return this.servers.stream().map(scope -> scope.toString()).collect(Collectors.toList());
	}

	//XXX throws a runtime exception if scopes are invalid
	@Override
	public void setScopes(List<String> scopes) {
		this.servers.clear();
		initScopes(scopes);
	}

	@Override
	public String getId() { return this.id; }

	@Override
	public void setId(String id) { this.id = id; }

	@Override
	public void close() {
		if(this.async) EmailDispatcher.release();
	}

	/**
	 * Initialize the mail server list from the provided scopes skipping invalid scopes
	 * @param scopes list of scopes
	 * @throws IllegalArgumentException if all the scopes provided are not valid
	 */
	private void initScopes(List<String> scopes){
		if(scopes == null) return;

		for(String scope : scopes)
			this.servers.add(new EmailServerSettings(scope)); 
	}


	/**
	 * Initialize the email address list from the recipients skipping invalid email addresses
	 * @param recipients list of recipients' email addresses
	 * @throws IllegalArgumentException if all the recipients provided are not valid
	 */
	private void initRecipients(List<String> recipients){
		if(recipients == null) return;

		for(String recipient : recipients){
			if(!this.validator.isValid(recipient))
				throw new IllegalArgumentException("'"+recipient+"' is not a valid email address");
			else this.recipients.add(recipient);
		}
	}
	
	/**
	 * Create the email depending on the data format
	 * @param format data format of the email
	 * @param payload data to be sent
	 * @return the created email
	 * @throws EmailException is the payload format is not correct
	 */
	private Email createEmail(DataFormat format, byte[] payload) throws EmailException{
		switch (this.format) {
		case HTML:
			HtmlEmail he = new HtmlEmail();
			he.setHtmlMsg(new String(payload));
			if(this.defaultMsg!=null) he.setMsg(this.defaultMsg);
			return he;
		case ATTACHMENT:
			MultiPartEmail mpe = new MultiPartEmail();
			if(this.defaultMsg!=null) mpe.setMsg(this.defaultMsg);
			
			DataSource source = new InputStreamDataSource(new ByteArrayInputStream(payload),this.mimeType);
			mpe.attach(source, "report"+extension, "Report file");
			return mpe;
		default:
			SimpleEmail se = new SimpleEmail();
			se.setMsg((defaultMsg!=null?defaultMsg:"")+new String(payload));
			return se;
		}
	}

	/**
	 * Create and initialize the mail
	 * @param es server settings
	 * @param recipients list of email addresses of recipients
	 * @param payload data to be sent
	 * @return the created email
	 * @throws EmailException
	 */
	private BaseEmail initEmail(EmailServerSettings es, List<String> recipients, byte[] payload) throws EmailException{
		Email email = createEmail(format, payload);
		
		email.setFrom(es.get(ScopeKey.FROM)); //mandatory field
		email.setHostName(es.get(ScopeKey.SERVER)); //mandatory field

		if(es.contains(ScopeKey.PORT)) email.setSmtpPort(Integer.parseInt(es.get(ScopeKey.PORT)));
		if(es.contains(ScopeKey.BOUNCETO)) email.setBounceAddress(es.get(ScopeKey.BOUNCETO));
		if(es.contains(ScopeKey.REPLAYTO)) email.addReplyTo(es.get(ScopeKey.REPLAYTO));

		email.setSSLOnConnect(es.isSslOnConnectEnabled());
		email.setStartTLSEnabled(es.isStartTlsEnabled());
		email.setStartTLSRequired(es.isStartTlsEnabled());

		if(es.contains(ScopeKey.PORT) && (email.isStartTLSEnabled() || email.isSSLOnConnect()))
			email.setSslSmtpPort(es.get(ScopeKey.PORT)); //replace the setSmtpPort

		if(es.contains(ScopeKey.USER) && es.contains(ScopeKey.PASSWORD)) email.setAuthentication(es.get(ScopeKey.USER), es.get(ScopeKey.PASSWORD));

		
		email.setSubject(this.name);
		for(String recipient : recipients)
			email.addBcc(recipient);
		
		
		return new CommonsEmail(email);
	}


}
