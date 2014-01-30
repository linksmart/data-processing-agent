package eu.ebbits.pwal.impl.driver.wsn.client;

import org.apache.log4j.Logger;

import eu.ebbits.pwal.impl.driver.wsn.WSNDriverImpl;

/**
 * Utility class used to decode the data
 *
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author    ISMB
 * @version    %I%, %G%
 * @since    M36demo 1.0
 * 
 */
public abstract class FormatDecoder {
    //===================numeric constants=================
    private static final int N_0X000000FF = 0x000000FF;
    private static final long N_0XFFFFFFFFL = 0xFFFFFFFFL;
    private static final int N_0XFF = 0xff;
    private static final int N_0X80 = 0x80;
    private static final long N_0X7FFFFFFF = 0x7fffffff;
    private static final int N_0XFF000000 = 0xFF000000;
    private static final int N_0XFF0000 = 0xFF0000;
    private static final int N_0XFF00 = 0xFF00;
    private static final int N_127 = 127;
    private static final int N_256 = 256;
    private static final int N_0 = 0;
    private static final int N_1 = 1;
    private static final int N_3 = 3;
    private static final int BUFFER_16 = 2;
    private static final int BUFFER_32 = 4;
    
    private static Logger log = Logger.getLogger(FormatDecoder.class.getName());


    /**
     * Fills up the payload using the values passed as parameter
     *  
     * @param type The payload type identification.
     * @param seqNo The packet's sequence number.
     * @param command The identifier of the control command sent to the sensor node.
     * @param data1 Field specifying a parameter, as required by the command.
     * @param data2 Field specifying a parameter, as required by the command.
     * @param data3 Field specifying a parameter, as required by the command.
     * 
     * @return    the payload as <code>byte[]</code>
     */
    public static byte[] fillUpPayload(short type, short seqNo,
            short command, short data1, short data2, short data3) {
        //create the payload
        byte[] payload = new byte[WSNDriverImpl.PAYLOAD_SIZE];
        //index to the next byte to be filled
        int i=0; 
    
        //filling up the payload
        FormatDecoder.encode16U(payload, i, type, false);
        i += 2;
        FormatDecoder.encode16U(payload, i, seqNo, false);
        i += 2;
        FormatDecoder.encode16U(payload, i, command, false);
        i += 2;
        FormatDecoder.encode16U(payload, i, data1, false);
        i += 2;
        FormatDecoder.encode16U(payload, i, data2, false);
        i += 2;
        FormatDecoder.encode16U(payload, i, data3, false);
        
        return payload;
    }
    
    
    /**
     * Decodes two bytes of a payload in unsigned int
     * 
     * @param rawpayload - raw payload to decode
     * @param startindex - index of the first byte to be decoded
     * 
     * @return     value decoded as <code>int</code>
     */
    // FIXME verificare che tutti i metodi nei creatori dei pacchetti usino
    // decode8,decode16 e decode32
    public static int decode16U(byte[] rawpayload, int startindex) {
        int hi3, hi4;

        hi4 = (N_0X000000FF & ((int) rawpayload[startindex]));
        hi3 = (N_0X000000FF & ((int) rawpayload[startindex + 1]));

        return(int) ((hi3 << WSNDriverImpl.N_8 | hi4) & N_0XFFFFFFFFL);
    }

    /**
     * Decodes a byte of the payload in unsigned int
     * 
     * @param rawpayload - raw payload to be decoded
     * @param i - index of the byte to be decoded
     * 
     * @return     value decoded as <code>int</code>
     */
    // FIXME verificare che tutti i metodi nei creatori dei pacchetti usino
    // decode8,decode16 e decode32
    public static int decode8U(byte[] rawpayload, int i) {
        return (rawpayload[i] & N_0XFF);
    }

    
    /**
     * Decodes 4 bytes of the payload in a double (unsigned)
     * 
     * @param rawpayload - raw payload to be decoded
     * @param startIndex - index of the first byte to be decoded
     * 
     * @return     value decoded as <code>double</code>
     */    
    // FIXME verificare che tutti i metodi nei creatori dei pacchetti usino
    // decode8,decode16 e decode32
    public static double decode32U(byte[] rawpayload, int startindex) {
        int hi1, hi2, hi3, hi4;

        // little endian

        hi4 = (N_0X000000FF & ((int) rawpayload[startindex]));
        hi3 = (N_0X000000FF & ((int) rawpayload[startindex + N_1]));
        hi2 = (N_0X000000FF & ((int) rawpayload[startindex + WSNDriverImpl.N_2]));
        hi1 = (N_0X000000FF & ((int) rawpayload[startindex + N_3]));

        log.debug("hi1 " + hi1);
        log.debug("hi2 " + hi2); 
        log.debug("hi3 " + hi3);
        log.debug("hi4 " + hi4);

        return ((long) (hi1 << WSNDriverImpl.N_24 | hi2 << WSNDriverImpl.N_16 | hi3 << WSNDriverImpl.N_8 | hi4)) & N_0XFFFFFFFFL;
    }

    /**
     * Decodes part of the payload, byte by byte, in unsigned int
     * 
     * @param rawpayload - raw payload to be decoded
     * @param start - index of the first byte to be decoded
     * @param datalen - number of bytes to be decoded
     * 
     * @return an array of bytes decoded as <code>byte[]</code>
     */
    public static byte[] decodeVector8U(byte[] rawpayload, int start,
            int datalen) {
        byte[] ret = new byte[datalen];
        for (int i = 0; i < datalen; i++) {
            ret[i] = rawpayload[start + i];
        }
        return ret;
    }

    /**
     * Decodes part of the payload, 2 bytes per time, in unsigned int
     * 
     * @param rawpayload - raw payload to be decoded
     * @param startoffsetBytes - index of the first byte to be decoded
     * @param datalenBytes - number of bytes to be decoded
     * 
     * @return an array of the values decoded as <code>int[]</code>
     */
    public static int[] decodeVector16U(byte[] rawpayload,
            int startoffsetBytes, int datalenBytes) {
        int hi;
        int low;

        // IsmbTools.Debug("da verificare");

        int[] ret = new int[datalenBytes / 2];
        for (int i = 0; i < (datalenBytes - 1); i += 2) {
            hi = N_0X000000FF & rawpayload[startoffsetBytes + i];
            low = N_0X000000FF & rawpayload[startoffsetBytes + i + 1];

            ret[i / 2] = (int) ((low << WSNDriverImpl.N_8 | hi) & N_0XFFFFFFFFL);

        }
        return ret;
    }

    /**
     * Returns a subarray from an original array.
     * Start and stop positions are included.
     * 
     * @param packet - original array
     * @param start - first index of the subarray
     * @param stop - last index of the subarray
     * 
     * @return   the subarray as <code>byte[]</code>
     */
    public static byte[] subarray(byte[] packet, int start, int stop) {
        byte[] ret = new byte[stop - start + 1];

        for (int i = start; i <= stop; i++) {
            ret[i - start] = packet[i];
        }
        return ret;

    }

    /**
     * Decodes a byte of the payload in signed int
     * 
     * @param payload - raw payload to be decoded 
     * @param i - index of the byte to be decoded
     * 
     * @return  the byte decoded as signed int
     */
    public static int decode8(byte[] payload, int i) {
        int val = FormatDecoder.decode8U(payload, i);

        if (val <= (N_127)) {
            return val;
        } else {
            return val - (N_256);
        }
    }

    
    /**
     * Decodes 4 bytes of the payload in a double (signed)
     * 
     * @param rawpayload - raw payload to be decoded
     * @param startIndex - index of the first byte to be decoded
     * 
     * @return     value decoded as <code>double</code>
     */
    public static double decode32(byte[] payload, int startindex) {
        int hi1, hi2, hi3, hi4;
        boolean isnegative = false;

        hi4 = (N_0X000000FF & ((int) payload[startindex]));
        hi3 = (N_0X000000FF & ((int) payload[startindex + N_1]));
        hi2 = (N_0X000000FF & ((int) payload[startindex + WSNDriverImpl.N_2]));
        hi1 = (N_0X000000FF & ((int) payload[startindex + N_3]));

        // hi1 -> 0x7f;
        // hi2 -> 0xff;
        // hi3 -> 0xff;
        // hi4 -> 0xff;

        log.debug("h4 " + hi4);
        log.debug("h3 " + hi3);
        log.debug("h2 " + hi2);
        log.debug("h1 " + hi1);

        if ((hi1 & N_0X80) != 0) {
            isnegative = true;
        }

        hi1 = hi1 & (~N_0X80);

        log.debug("h4 " + hi4);
        log.debug("h3 " + hi3);
        log.debug("h2 " + hi2);
        log.debug("h1 " + hi1);

        long anUnsignedInt = ((long) (hi1 << WSNDriverImpl.N_24 | hi2 << WSNDriverImpl.N_16 | hi3 << WSNDriverImpl.N_8 | hi4)) & N_0XFFFFFFFFL;

        if (isnegative) {
            anUnsignedInt = -(N_0X7FFFFFFF - anUnsignedInt + 1);
        }
        /*
         * anUnsignedInt = 2147483649L; //anUnsignedInt = ~ anUnsignedInt;
         * 
         * if((anUnsignedInt & 0x80000000)!=0){ //if(!(anUnsignedInt > (2 ^
         * 31))){ //anUnsignedInt = (2 ^ 32) - anUnsignedInt; anUnsignedInt = (~
         * anUnsignedInt) + 1; }
         */
        return anUnsignedInt;
    }
    

    /**
     * Decodes two bytes of a payload in unsigned int
     * 
     * @param rawpayload - raw payload to decode
     * @param startindex - index of the first byte to be decoded
     * @param bigendian - a <code>boolean</code>, true if the payload is big endian, false otherwise
     * 
     * @return     value decoded as <code>int</code>
     */
    public static int decode16U(byte[] rawpayload, int startindex,
            boolean bigendian) {

        byte[] mybuffer = new byte[BUFFER_16];

        // FIXME invertito per una prova : da verificare

        if (bigendian) {
            mybuffer[N_1] = rawpayload[startindex];
            mybuffer[N_0] = rawpayload[startindex + N_1];
        } else {
            mybuffer[N_0] = rawpayload[startindex];
            mybuffer[N_1] = rawpayload[startindex + N_1];
        }
        return FormatDecoder.decode16U(mybuffer, N_0);
    }

    
    
    /**
     * Decodes 4 bytes of the payload in a double (unsigned)
     * 
     * @param rawpayload - raw payload to be decoded
     * @param startIndex - index of the first byte to be decoded
     * @param bigendian - a <code>boolean</code>, true if the payload is big endian, false otherwise
     * 
     * @return     value decoded as <code>double</code>
     */
    public static double decode32U(byte[] rawpayload, int startindex,
            boolean bigendian) {
        byte[] mybuffer = new byte[BUFFER_32];

        if (bigendian) {
            mybuffer[N_0] = rawpayload[startindex + N_3];
            mybuffer[N_1] = rawpayload[startindex + WSNDriverImpl.N_2];
            mybuffer[WSNDriverImpl.N_2] = rawpayload[startindex + N_1];
            mybuffer[N_3] = rawpayload[startindex];
        } else {
            mybuffer[N_3] = rawpayload[startindex + N_3];
            mybuffer[WSNDriverImpl.N_2] = rawpayload[startindex + WSNDriverImpl.N_2];
            mybuffer[N_1] = rawpayload[startindex + N_1];
            mybuffer[N_0] = rawpayload[startindex];
        }
        return FormatDecoder.decode32U(mybuffer, N_0);
    }

    /**
     * Encodes a 2 bytes unsigned int in a byte array
     * 
     * @param value - value to be encoded
     * @param bigendian - a <code>boolean</code>, true if the value is big endian, false otherwise 
     * 
     * @return   a <code>byte[]</code> containing the value encoded
     */
    public static byte[] encode16U(int value, boolean bigendian) {
        byte[] ret = new byte[2];

        byte byteH = (byte) ((value >> WSNDriverImpl.N_8) & N_0XFF);
        byte byteL = (byte) (value & N_0XFF);
        if (bigendian) {
            ret[0] = byteH;
            ret[1] = byteL;
        } else {
            ret[0] = byteL;
            ret[1] = byteH;
        }
        return ret;
    }


        
    /**
     * Encodes a 2 bytes unsigned int in a byte array passed as parameter
     *  
     * @param resultdestination - destination for the encoded value
     * @param resultindex - index of in which to insert the first btye of the encoded value
     * @param value - value to be encoded
     * @param bigendian - a <code>boolean</code>, true if the value is big endian, false otherwise
     *  
     */
    public static void encode16U(byte[] resultdestination, int resultindex,
            int value, boolean bigendian) {
        byte[] enc = encode16U(value, bigendian);

        resultdestination[resultindex] = enc[0];
        resultdestination[resultindex + 1] = enc[1];
    }

    /**
     * Compares two {@link Float} numbers, wrapped into {@link String}s.
     * Normally used when checking file versions.
     * 
     * @param f1
     *            The first number to compare
     * @param f2
     *            The second number to compare
     * @return the value 0 if f1 is numerically equal to f2; a value less than 0
     *         if f1 is numerically less than f2; and a value greater than 0 if
     *         f1 is numerically greater than f2.
     * @see Float
     * @throws NumberFormatException
     *             If a numeric conversion error occurred. The message of this
     *             exception is <code>"first"</code> or <code>"second"</code>,
     *             depending on which String caused the error. The first string
     *             is checked before the second one.
     */
    public static int floatCompare(String f1, String f2) {
        Float float1, float2;

        try {
            float1 = Float.valueOf(f1);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("first, "+e.getStackTrace());
        }

        try {
            float2 = Float.valueOf(f2);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("second, "+e.getStackTrace());
        }

        return Float.compare(float1, float2);
    }

    /**
     * Translates a hex number into a byte array.
     * 
     * @param hexNumber
     *            A {@link String} representing the hex value to convert. This
     *            parameter must contain only hex-mappable data, so, for
     *            istance, a leading '0x' or '0X' should not be present.
     * @return The converted number or <i>null</i> if some error occurred.
     */
    public static byte[] hex2byte(String hexNumber) {
        if (hexNumber == null || hexNumber.length() % 2 != 0) {
            return null;
        }

        byte[] byteArray = new byte[hexNumber.length() / 2];

        try {
            for (int i = 0; i < byteArray.length; i++) {
                byteArray[i] = (byte) Integer.parseInt(hexNumber.substring(
                        2 * i, 2 * i + 2), WSNDriverImpl.N_16);
            }
        } catch (NumberFormatException e) {
            log.warn("hex2byte received an invalid hex number: " + hexNumber);
            return null;
        }

        return byteArray;
    }

    /**
     * Gives the hex representation of the provided byte
     * 
     * @param b
     *            The byte to represent
     * @return The representation
     */
    public static String byte2hex(byte b) {
        int lower, upper;

        lower = (b + N_256) % WSNDriverImpl.N_16;
        byte shiftedB = (byte) (b >> WSNDriverImpl.N_4);
        upper = (shiftedB + N_256) % WSNDriverImpl.N_16;

        return new StringBuilder().append(Integer.toHexString(upper)).append(
                Integer.toHexString(lower)).toString();
    }

    /**
     * Gives the hex representation of the provided array of bytes
     * 
     * @param b
     *            The array of bytes to convert
     * @return The desired hex representation
     */
    public static String byte2hex(byte b[]) {
        return byte2hex(b, "");
    }

    /**
     * Gives the hex representation of the provided array of bytes, using the
     * provided separator between bytes
     * 
     * @param b
     *            The array of bytes to convert
     * @param separator
     *            An optional separator that should be inserted between bytes
     * @return The desired hex representation
     */
    public static String byte2hex(byte b[], String separator) {
        StringBuilder buf = new StringBuilder();

        for (byte n : b) {
            buf.append(byte2hex(n) + separator);
        }
        return buf.toString();
    }

    /**
     * Translates a byte array into the integer value it represents. This method
     * supports:
     * <ul>
     * <li><b>unsigned integers</b> in RAW binary form</li>
     * <li><b>signed integers</b> in two's complement</li>
     * </ul>
     * 
     * @param dataArray
     *            The source byte array
     * @param bigEndian
     *            Whether the integer value is encoded in big or little endian
     * @param signed
     *            Whether the encoded integer is signed or not
     * @return The encoded integer
     * @deprecated Whenever possible and if that does not cause further
     *             problems, please use the method
     *             {@link #byte2long(byte[], boolean, boolean)} as it correctly
     *             handles large values. This method is a wrapper around it,
     *             anyway.
     */
    public static int byte2int(byte[] dataArray, boolean bigEndian,
            boolean signed) {
        return (int) byte2long(dataArray, bigEndian, signed);
    }

    /**
     * Translates a byte array into the integer value it represents. This method
     * supports:
     * <ul>
     * <li><b>unsigned integers</b> in RAW binary form</li>
     * <li><b>signed integers</b> in two's complement</li>
     * </ul>
     * 
     * @param dataArray
     *            The source byte array
     * @param bigEndian
     *            Whether the integer value is encoded in big or little endian
     * @param signed
     *            Whether the encoded integer is signed or not
     * @return The encoded integer
     * 
     */
    public static long byte2long(byte[] dataArray, boolean bigEndian,
            boolean signed) {
        long toReturn;

        if (bigEndian) {
            if (signed) {
                toReturn = dataArray[0];
            } else {
                toReturn = (dataArray[0] >= 0 ? dataArray[0]
                        : dataArray[0] + N_256);
            }

            for (int i = 1; i < dataArray.length; i++) {
                toReturn = N_256
                        * toReturn
                        + (dataArray[i] >= 0 ? dataArray[i]
                                : dataArray[i] + N_256);
            }
        } else {
            if (signed) {
                toReturn = dataArray[dataArray.length - 1];

                for (int i = dataArray.length - 2; i >= 0; i--) {
                    toReturn = N_256
                            * toReturn
                            + (dataArray[i] >= 0 ? dataArray[i] : dataArray[i]
                                    +N_256);
                }
            } else {
                if (dataArray[dataArray.length - 1] >= 0) {
                    toReturn = dataArray[dataArray.length - 1];
                } else {
                    toReturn = dataArray[dataArray.length - 1] + N_256;
                }

                for (int i = dataArray.length - 2; i >= 0; i--) {
                    toReturn = N_256
                            * toReturn
                            + (dataArray[i] >= 0 ? dataArray[i] : dataArray[i]
                                    + N_256);
                }
            }

        }

        return toReturn;
    }

    /**
     * Computes a bit-to-bit masking between the provided mask and the data
     * extracted from the provided array
     * 
     * @param mask
     *            The mask to use in the computation; it defines the length of
     *            the result
     * @param data
     *            The array from which fetch the data
     * @param offset
     *            The offset at which the method starts to fetch the data from
     *            the provided array
     * @return A array (long as the mask is) with the result of the bit-to-bit
     *         masking
     */
    public static byte[] maskData(byte[] mask, byte[] data, int offset) {
        byte[] masked = new byte[mask.length];

        // FIXME: I fear there's something wrong with this...
        for (int i = 0; i < mask.length; i++) {
            masked[i] = (byte) (mask[i] & data[offset + i]);
        }
        return masked;
    }

    /**
     * Returns the provided number as {@link String}, adding leading zeros if
     * necessary.
     * 
     * @param number
     *            The integer to format
     * @param minimumDigits
     *            The minimum number of digits that the String representation
     *            should have
     * @return A {@link String} representation of the number, following the
     *         provided rules.
     */
    public static String addLeadingZeros(int number, int minimumDigits) {
        StringBuilder numAsStr = new StringBuilder().append(number);

        if (numAsStr.length() >= minimumDigits) {
            return numAsStr.toString();
        }

        StringBuilder toReturn = new StringBuilder();
        for (int i = 0; i < minimumDigits - numAsStr.length(); i++) {
            toReturn.append('0');
        }
        return toReturn.append(numAsStr).toString();
    }

    
    /**
     * Encodes an unsigned int in a byte and inserts it in the destination array passed as paramete
     * 
     * @param datapayload - destination of the encoded value
     * @param i - index in which to insert the value 
     * @param value - value to be encoded
     */
    public static void encode8U(byte[] datapayload, int i, int value) {
        datapayload[i] = (byte) value;
    }

    /**
     * Encodes an unsigned int in four bytes and inserts it in the destination array passed as paramete
     * 
     * @param resultdestination - destination of the encoded value
     * @param resultindex - index in which to insert the first byte of the encoded value
     * @param value - value to be encoded
     * @param bigendian - a <code>boolean</code>, true if the value is big endian, false otherwise 
     */
    public static void encode32U(byte[] resultdestination, int resultindex,
            int value, boolean bigendian) {
        byte[] enc = encode32U(value, bigendian);

        resultdestination[resultindex] = enc[N_0];
        resultdestination[resultindex + N_1] = enc[N_1];
        resultdestination[resultindex + WSNDriverImpl.N_2] = enc[WSNDriverImpl.N_2];
        resultdestination[resultindex + N_3] = enc[N_3];
    }


    /**
     * Encodes an unsigned int in four bytes
     * 
     * @param value - value to be encoded
     * @param bigendian - a <code>boolean</code>, true if the value is big endian, false otherwise
     *  
     */
    private static byte[] encode32U(int value, boolean bigendian) {
        byte[] ret = new byte[BUFFER_32];

        byte byteHH = (byte) ((value >> WSNDriverImpl.N_24) & N_0XFF);
        byte byteH = (byte) ((value >> WSNDriverImpl.N_16) & N_0XFF);
        byte byteL = (byte) ((value >> WSNDriverImpl.N_8) & N_0XFF);
        byte byteLL = (byte) (value & N_0XFF);
        if (bigendian) {
            ret[N_0] = byteHH;
            ret[N_1] = byteH;
            ret[WSNDriverImpl.N_2] = byteL;
            ret[N_3] = byteLL;
        } else {
            ret[N_3] = byteHH;
            ret[WSNDriverImpl.N_2] = byteH;
            ret[N_1] = byteL;
            ret[N_0] = byteLL;
        }
        return ret;
    }

    /**
     * Decoodes an array of bytes in a IEE754 <code>double</code>
     * 
     * @param bytes - the array of bytes to be decoded
     * 
     * @return   the IEE754 <code>double</code> decoded
     */
    public static double decodeIEEE754(byte[] bytes) {
        int num = ((bytes[N_0] << WSNDriverImpl.N_24) & N_0XFF000000)
                | ((bytes[N_1] << WSNDriverImpl.N_16) & N_0XFF0000) | ((bytes[WSNDriverImpl.N_2] << WSNDriverImpl.N_8) & N_0XFF00)
                | (bytes[N_3] & N_0XFF);
        float f = Float.intBitsToFloat(num);

        return f;
    }

    /**
     * Converts an array of <code>float</code>s in an array of <code>int</code>s
     * 
     * @param in - the array of <code>float</code>s to be converted
     * 
     * @return   the array of <code>int</code>s
     */
    public static int[] float2Int(float[] in) {
        int[] ret = new int[in.length];
        for (int i = 0; i < in.length; i++) {
            ret[i] = (int) Math.round(in[i]);
        }
        return ret;
    }

    /**
     * Converts an array of <code>double</code>s in an array of <code>int</code>s
     * 
     * @param in - the array of <code>double</code>s to be converted
     * 
     * @return   the array of <code>int</code>s
     */    
    public static int[] double2Int(double[] pdf) {
        int[] ret = new int[pdf.length];

        for (int i = 0; i < pdf.length; i++) {
            ret[i] = (int) pdf[i];
        }

        return ret;
    }

    /**
     * Encodes an array of bytes in an array of unsigned <code>int</code>s, 2 bytes per time
     * 
     * @param input - array of bytes to be encoded
     * @param bigendian - a <code>boolean</code>, true if the value is big endian, false otherwise
     * 
     * @return   array of unsigned <code>int</code>s encoded
     */
    public static int[] encode16UVector(byte[] input,boolean bigendian) {
        int [] ret = new int[input.length/2];
        for(int i=0;i<ret.length;i++) {
            ret[i]  = FormatDecoder.decode16U(input, 2*i, bigendian);
        }
        return ret;
    }
}