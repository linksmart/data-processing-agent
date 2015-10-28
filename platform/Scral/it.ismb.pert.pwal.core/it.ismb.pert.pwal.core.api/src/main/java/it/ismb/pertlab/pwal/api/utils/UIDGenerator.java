/*
 * ALMANAC UID Generator
 *  
 * Copyright (c) 2015 Dario Bonino
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package it.ismb.pertlab.pwal.api.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The UID generator singleton. It provides means to generate ALMANAC-compliant
 * IDs given a String or a sequence of Strings as input.
 * 
 * @author <a href="mailto:bonino@ismb.it">Dario Bonino</a>
 *
 */
public class UIDGenerator
{
	// the only UID generator created to support the singleton pattern
	private static UIDGenerator generator;
	
	// the class logger
	private Logger logger;
	
	/**
	 * Private class constructor for implementing the singleton pattern
	 */
	private UIDGenerator()
	{
		this.logger = LoggerFactory.getLogger(UIDGenerator.class);
	}
	
	/**
	 * Provides an instance of UIDGenerator. If an instance it is already
	 * available, the it will be reused.
	 * 
	 * @return The {@link UIDGenerator} instance.
	 */
	public static UIDGenerator getInstance()
	{
		if (UIDGenerator.generator == null)
			UIDGenerator.generator = new UIDGenerator();
		
		return UIDGenerator.generator;
	}
	
	/**
	 * Generates a UID given a {@String} source id (e.g., a
	 * network-level serial number)
	 * 
	 * @param seedText
	 *            The text to from which the UID shall be generated.
	 * @return The generated UID as a {@link String}.
	 */
	public String uidFromString(String seedText)
	{
		// the uid, initially null
		String uid = null;
		
		try
		{
			// the SHA-256 digester
			MessageDigest mdGenerator = MessageDigest.getInstance("SHA-256");
			
			// create the digest of the string as bytes
			byte digest[] = mdGenerator.digest(seedText.getBytes());
			
			// convert the digest bytes to their hexadecimal representation
			uid = this.toHexString(digest);
		}
		catch (NoSuchAlgorithmException e)
		{
			// log the error
			this.logger.error("Error while generating unique id", e);
		}
		
		return uid;
	}
	
	/**
	 * Provides a UID given a starting array of Strings (which are concatenated
	 * by using the "separator" character and then fed to the String version of
	 * this method).
	 * 
	 * @param seeds
	 *            The Strings from which generating the uid
	 * @param separator
	 *            The Character to be used as separator when concatenating
	 *            Strings.
	 * @return Th generated uid as a {@link String}.
	 */
	public String uidFromStringArray(String seeds[], String separator)
	{
		// concatenate the strings
		StringBuffer strCat = new StringBuffer();
		
		for (int i = 0; i < seeds.length; i++)
		{
			// get the current seed string
			strCat.append(seeds[i]);
			
			// add dash separator between seeds
			strCat.append(separator);
		}
		
		return this.uidFromString(strCat.toString());
	}
	
	/**
	 * Converts an array of bytes into the corresponding hexadecimal notation
	 * 
	 * @param byteArray
	 *            The array of bytes to be converted
	 * @return A {@String} representing the given array of bytes in the
	 *         hexadecimal notation.
	 */
	private String toHexString(byte byteArray[])
	{
		// the buffer to accumulate characters
		StringBuffer hexBytes = new StringBuffer();
		
		// convert each byte
		for (int i = 0; i < byteArray.length; i++)
			hexBytes.append(String.format("%02x", byteArray[i]));
		
		// render the buffer as a string
		return hexBytes.toString();
	}
	
}
