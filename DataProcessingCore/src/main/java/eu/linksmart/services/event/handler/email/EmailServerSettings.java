package eu.linksmart.services.event.handler.email;

import java.util.HashMap;


//server=smtp.gmail.com,port=587,user=you@gmail.com,password=yourpassword,from=from@gmail.com,replayto=from@gmail.com,security=SslOnConnect/StartTls

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
 * Parse and store server settings for email sending purpose
 *
 * @author Nadir Raimondo
 * @since  1.8.0
 *
 */
public class EmailServerSettings{
	public enum ScopeKey{
		SERVER,PORT,USER,PASSWORD,SECURITY,FROM,REPLAYTO,BOUNCETO
	}
	
	private final HashMap<ScopeKey, String> params;
	private final String scope;
	
	/**
	 * Parse a scope string
	 * @param scope the scope that will be parsed
	 * @throws IllegalArgumentException if the arguments are not valid
	 */
	public EmailServerSettings(String scope){
		this.scope = scope;
		this.params = new HashMap<>();
		
		parseScope(scope);
	}
	
	/**
	 * Parse a scope
	 * @param scope a comma separated key=value string. Non-separator comma must be skipped with \ character
	 * @return the mail server configuration correspondent to the provided scope
	 * @throws IllegalArgumentException if the parse process fails
	 */
	private void parseScope(String scope){
		int index = -1;
		String[] tokens = scope.split("(?<!\\\\),"); //split with comma delimiter (commas, if presente, must be skipped with the \ character)
		
		for(String token : tokens){
			token = token.replace("\\,", ",").trim();
			index = token.indexOf('='); //search for the first equal character, is better to avoid the split function because username and password can contain any character
			
			if(index<0) throw new IllegalArgumentException("Malformed parameter '"+token+"'");
			put(token.substring(0,index), token.substring(index+1,token.length())); //throws exception
		}
		
		validate(); //throws exception
	}
	
	/**
	 * Associates the specified value with the specified key in settings. If the map previously contained a mapping for the key, an exception is thrown
	 * @param key key with which the specified value is to be associated
	 * @param value value to be associated with the specified key
	 * @throws IllegalArgumentException if the map previously contained a mapping for the key
	 */
	private void put(String key, String value){
		for (ScopeKey sk : ScopeKey.values()) {
			if(sk.toString().equalsIgnoreCase(key)){
				if(params.containsKey(sk))
					throw new IllegalArgumentException("Duplicate key '"+key+"'");
				
				params.put(sk, value);
				return;
			}
		}
		throw new IllegalArgumentException("'"+key+"' is not a valid parameter");
	}
	
	public String toString(){
		return scope;
	}
	
	/**
	 * Returns true if settings contain a mapping for the specified key.
	 * @param key the key whose presence in this map is to be tested
	 * @return true if this map contains a mapping for the specified key.
	 */
	public boolean contains(ScopeKey key){
		return params.containsKey(key);
	}
	
	/**
	 * Returns the value to which the specified key is mapped, or null if this map contains no mapping for the key.
	 * @param key the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
	 */
	public String get(ScopeKey key){
		return params.get(key);
	}
	
	/**
	 * Returns true if ssl on connect is enabled
	 * @return true if ssl on connect is enabled
	 */
	public boolean isSslOnConnectEnabled(){
		return "SslOnConnect".equals(get(ScopeKey.SECURITY));
	}
	
	/**
	 * Returns true if start tls is enabled
	 * @return true if start tls is enabled
	 */
	public boolean isStartTlsEnabled(){
		return "StartTls".equals(get(ScopeKey.SECURITY));
	}
	
	/**
	 * Validate the settings
	 * @throws IllegalArgumentException if the settings are not valid
	 */
	public void validate(){
		if(!contains(ScopeKey.SERVER) || !contains(ScopeKey.FROM))
			throw new IllegalArgumentException("Server and from parameters are mandatory");
		
		if(contains(ScopeKey.SECURITY) && !isSslOnConnectEnabled() && !isStartTlsEnabled())
			throw new IllegalArgumentException("Unrecognized security options "+get(ScopeKey.SECURITY));
		
		if(contains(ScopeKey.USER) && !contains(ScopeKey.PASSWORD))
			throw new IllegalArgumentException("If user parameter is present also the password must be provided");
		
		try{
			int port = Integer.parseInt(get(ScopeKey.PORT));
			if(port<0 || port>65535) throw new IllegalArgumentException("Invalid port range");
		}
		catch(NumberFormatException e){
			throw new IllegalArgumentException("Invalid port");
		}
		//XXX other checks?
	}
	

	
}