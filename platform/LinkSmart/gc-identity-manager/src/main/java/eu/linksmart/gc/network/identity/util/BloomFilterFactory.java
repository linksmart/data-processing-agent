package eu.linksmart.gc.network.identity.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Factory for creating Bloom-filters. The relation
 * between array bit size and stored items is always 1 to 15.
 * This means that by calculating 10 hashes the collision
 * probability is 0.000747.
 * @author Vinkovits
 *
 */
public class BloomFilterFactory {
	private final static int NR_HASHES = 10;
	private final static int BITS_PER_ITEM = 15;
	private final static String HASH_ALGORITHM = "SHA-256";

	/**
	 * Calculates a bloom filter for the provided
	 * items and an initial random.
	 * @param values Items to be put into the filter
	 * @param random Initial entropy
	 * @return
	 */
	public static boolean[] createBloomFilter(String[] values, long random) {
		if(values == null || values.length == 0) {
			throw new IllegalArgumentException("Cannot create filter for no values!");
		}
		int length = values.length * BITS_PER_ITEM;
		boolean[] bloomFilter = new boolean[length];
		//calculate hash for each value
		for(String value : values) {
			bloomFilter = addValue(value, bloomFilter, random, length);
		}
		return bloomFilter;
	}

	/**
	 * Calculates the two necessary hash values based on the value and random.
	 * @param value
	 * @param random
	 * @return
	 */
	protected static long[] calculateHashes(String value, long random) {
		//XOR random into value to avoid determinacy
		byte[] item = xor(value, random);
		//calculate hashes
		byte[] h1 = null;
		byte[] h2 = null;
		try {
			MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
			//calculate first hash
			digest.update(item);
			h1 = digest.digest();
			//calculate second hash
			digest.update(h1);
			h2 = digest.digest();
		} catch (NoSuchAlgorithmException e) {
			//absence of algorithm is unlikely
		}
		if(h1 != null && h2 != null) {
			//only use first 8 bytes of hashes
			long h1Long = getLong(Arrays.copyOf(h1, 8));
			long h2Long = getLong(Arrays.copyOf(h2, 8));
			return new long[]{h1Long,h2Long};
		} else {
			return null;
		}		
	}

	/**
	 * Sets the relevant bits for this item in the filter.
	 * @param value
	 * @param bloomFilter
	 * @param random
	 * @param length
	 * @return
	 */
	protected static boolean[] addValue(
			String value,
			boolean[] bloomFilter,
			long random, int length) {
		//get hash of value and random
		long[] hashes = calculateHashes(value, random);
		if(hashes != null) {
			//calculate indexes by h1+i*h2 mod m
			for(int i=0; i < NR_HASHES; i++) {
				long index = Math.abs((hashes[0] + i*hashes[1])%length);
				bloomFilter[(int)index] = true;
			}
		} else {
			//problem calculating hash so operation has to be aborted
			return null;
		}
		return bloomFilter;
	}

	/**
	 * Checks if a given value is included in the filter.
	 * @param value
	 * @param bloomFilter
	 * @param random The random used to create the Bloom-Filter
	 * @return
	 */
	public static boolean containsValue(
			String value,
			boolean[] bloomFilter,
			long random) {
		int length = bloomFilter.length;
		//get hash of value and random
		long[] hashes = calculateHashes(value, random);
		if(hashes != null) {
			//calculate indexes by h1+i*h2 mod m
			for(int i=0; i < NR_HASHES; i++) {
				long index = Math.abs((hashes[0] + i*hashes[1])%length);
				if(bloomFilter[(int)index] != true) {
					//index does not match and value is not included
					return false;
				}
			}
			//if we reach this far then the value was included
			return true;
		} else {
			//problem calculating hash so operation has to be aborted
			return false;
		}
	}

	/**
	 * XORs the two values, where value2 will be trimmed
	 * or extended to length of value1.
	 * @param value1
	 * @param value2
	 * @return
	 */
	protected static byte[] xor(String value1, long value2){
		byte[] v = value1.getBytes();
		byte[] rand = Long.toString(value2).getBytes();
		//trim or pad value b
		rand = Arrays.copyOf(rand, v.length);
		//xor the items in the array
		byte[] xor = new byte[v.length];
		for(int i=0; i < v.length; i++) {
			xor[i] = (byte)(v[i] ^ rand[i]);
		}
		return xor;
	}

	
	/**
	 * Creates from an 8 long byte array a long value.
	 * @param array
	 * @return
	 */
	protected static long getLong(byte[] array){
		return
		((long)(array[0] & 0xff) << 56) |
		((long)(array[1] & 0xff) << 48) |
		((long)(array[2] & 0xff) << 40) |
		((long)(array[3] & 0xff) << 32) |
		((long)(array[4] & 0xff) << 24) |
		((long)(array[5] & 0xff) << 16) |
		((long)(array[6] & 0xff) << 8) |
		((long)(array[7] & 0xff));
	}
	
	/**
	 * Creates a bit-string from a bit array.
	 * @param set
	 * @return
	 */
	public static String createString(boolean[] filter){
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<filter.length; i++) {
			sb.append(filter[i]?"1" : "0");
		}
		return sb.toString();
	}
	
	/**
	 * Creates a boolean[] from the bit-string.
	 * @param string
	 * @return
	 */
	public static boolean[] createBooleanArray(String string){
		if(!string.matches("[01]*")) {
			throw new IllegalArgumentException("String may only contain '0' and '1'");
		}
		boolean[] filter = new boolean[string.length()];
		for(int i=0; i< string.length(); i++) {
			filter[i] = (string.charAt(i) == '1')?true:false;
		}
		return filter;
	}
}
