package eu.ebbits.pwal.api.driver.device.coolingcircuit.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * Provides methods for encoding and decoding sensor data according to the
 * following text-based protocol:
 * <code>*&lt;type&gt;&lt;id&gt;=&lt;value&gt;$&lt;checksum&gt;#</code> where
 * <dl>
 * <dt>type</dt>
 * <dd>one character, e.g. <b>t</b>emperature, water <b>f</b>low</dd>
 * <dt>id</dt>
 * <dd>one character, e.g. <b>1</b> for first sensor/actuator</dd>
 * <dt>value</dt>
 * <dd>String</dd>
 * <dt>checksum</dt>
 * <dd>Checksum, calculated using {@link #checksum(String)}
 * 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author    ISMB
 * @version    %I%, %G%
 * @since      PWAL 0.1.0
 * 
 */
public final class Protocol {

    public static final byte BEGIN = (byte) 42; // '*'
    public static final byte DELIMITER = (byte) 35; // '#'
    public static final byte CHECKSUM = '$';

    /** Character that must be found at the beginning of the string **/ 
    public static final String BEGIN_STRING = "*";
    /** Character that must be found at the end of the string **/
    public static final String DELIMITER_STRING = "#";
    /** Checksum value **/
    public static final String CHECKSUM_STRING = "$";

    private Protocol(){
    }
    
    /**
     * Decodes a sensor result. Returns an array of String with two elements
     * containing sensor name and value, e.g. [LDR2,123]
     * 
     * @param result
     *          The complete String sent by the sensor
     * @return array containing sensor name and value
     */
    public static String[] decode(String result) {
        // LOG.info(result);
        if (StringUtils.isEmpty(result)) {
            // LOG.debug("null result from sensor");
            return null;
        }
        if (!result.startsWith(BEGIN_STRING)
                || !result.endsWith(DELIMITER_STRING)) {
            // LOG.debug("Incomplete result from sensor: " + result);
            return null;
        }
        String content = result.substring(1, result.length() - 1);
        return decodeEvent(content);
    }

    /**
     * Encodes a key-value pair according to the protocol.
     * 
     * @param key
     *          the key, as a String
     * @param value
     *          the value, as a byte
     * @return a String containing the key-value pair according to the protocol,
     *        i.e. including the begin, end and checksum delimiter strings, as
     *        well as the checksum.
     */
    public static String encode(String key, byte value) {
        String event = key + "=" + value;
        return BEGIN_STRING + event + CHECKSUM_STRING
                + new String(new byte[] { checksum(event) }) + DELIMITER_STRING;
    }

    /**
     * Decodes multiple sensor values at once.
     * 
     * @param results
     *          the sensor values to be decoded, as a String
     * @return a List containing the key-value pairs as string arrays, e.g.
     *        [l1,97]
     */
    public static List<String[]> decodeMultiple(String results) {
        String[] events = StringUtils.substringsBetween(results, BEGIN_STRING,
                DELIMITER_STRING);

        List<String[]> result = new ArrayList<String[]>();
        for (String event : events) {
            String[] newResult = decodeEvent(event);
            if (newResult != null) {
                result.add(newResult);
            }
        }
        return result;
    }

    /**
     * Decodes a single sensor value. Checks the checksum and then calls
     * splitEvent().
     * 
     * @param event
     *          the sensor value, as a String (without begin and delimiter
     *          character)
     * @return the sensor event as a key-value pair in a String array, e.g.
     *        [l1,97], null if wrong checksum
     */
    private static String[] decodeEvent(String event) {
        String eventToUse = event.substring(event.lastIndexOf(BEGIN_STRING) + 1);
        if (eventToUse.charAt(eventToUse.length() - 2) == CHECKSUM) {
            if (!check(eventToUse.substring(0, eventToUse.length() - 2),
                    (byte) eventToUse.charAt(eventToUse.length() - 1))) {
                // LOG.debug("Event dropped. Wrong checksum.");
                return null;
            }
            // LOG.debug("Checksum OK");
            eventToUse = eventToUse.substring(0, eventToUse.length() - 2);
        }
        return splitEvent(eventToUse);
    }

    /**
     * Splits the sensor value at the = character.
     * 
     * @param event
     *          the sensor value as a String (only = character left as a
     *          delimiter)
     * @return the sensor event as a key-value pair in a String array, e.g.
     *        [l1,97]
     */
    private static String[] splitEvent(String event) {
        if (StringUtils.countMatches(event, "=") != 1) {
            // LOG.debug("Incomplete result from sensor: " + event);
            return null;
        }
        String[] result = event.split("=");
        if (result.length != 2) {
            return null;
        }
        return result;
    }

    /**
     * Checks the checksum of a sensor value
     * 
     * @param event
     *          the sensor value String (without begin character and without
     *          checksum or other delimiters), e.g. "l1=98"
     * @param checksum
     *          the checksum to be checked
     * @return whether the checksum matches the calculated one
     */
    private static boolean check(String event, byte checksum) {
        return checksum == Protocol.checksum(event);
    }

    /**
     * Calculates the checksum for a given event, using XOR on each character of
     * the String and {@link #CHECKSUM}.
     * 
     * @param event
     *          the sensor value String (without begin character and without
     *          checksum or other delimiters), e.g. "l1=98"
     * @return the checksum
     */
    private static byte checksum(String event) {
        byte checksum = CHECKSUM;
        for (byte b : event.getBytes()) {
            checksum = (byte) (checksum ^ b);
        }
        return checksum;
    }
}
