package eu.linksmart.gc.network.identity.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

import eu.linksmart.gc.api.utils.Part;

public class BloomFilterFactoryTest {

	private static final Part ATTR_1 = new Part("One", "Eins");
	private static final Part ATTR_2 = new Part("Two", "Zwei");
	
	/**
	 * Check if providing 'null' values causes exception
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCreateBloomFilterNull() {
			BloomFilterFactory.createBloomFilter(
					null,
					new Random().nextLong());
	}
	
	/**
	 * Check if providing empty array causes exception
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testCreateBloomFilterEmpty() {
		BloomFilterFactory.createBloomFilter(
				new String[]{},
				new Random().nextLong());
	}
	
	/**
	 * Create Bloom-filter with two values and then check if they are 
	 * found in the filter.
	 */
	@Test
	public void testContainsValueTrue() {
		long random = new Random().nextLong();
		//create filter
		boolean[] filter = BloomFilterFactory.createBloomFilter(
				new String[]{ATTR_1.getValue(), ATTR_2.getValue()},
				random);
		//check if attributes are included
		assertTrue(
				"Attribute 1 was not included!",
				BloomFilterFactory.containsValue(
						ATTR_1.getValue(), filter, random));
		assertTrue(
				"Attribute 2 was not included!",
				BloomFilterFactory.containsValue(
						ATTR_2.getValue(), filter, random));
	}
	
	/**
	 * Create Bloom-filter with one attribute and then check
	 * if other is included - it should not.
	 */
	@Test
	public void testContainsValueBadAttribute() {
		//random has to be fixed else false positiv may occur
		long random = 0;
		//create filter
		boolean[] filter = BloomFilterFactory.createBloomFilter(
				new String[]{ATTR_1.getValue()},
				random);
		//check if attributes are included
		assertFalse(
				"Attribute 2 found although not included!",
				BloomFilterFactory.containsValue(
						ATTR_2.getValue(), filter, random));
	}
	
	/**
	 * Create Bloom-filter with a random and then
	 * check with an other random. Should return false.
	 */
	@Test
	public void testContainsValueBadRandom() {
		long random1 = 0;
		long random2 = 9999999;
		//create filter
		boolean[] filter = BloomFilterFactory.createBloomFilter(
				new String[]{ATTR_1.getValue()},
				random1);
		//check if attributes are included
		assertFalse(
				"Attributes found although wrong random!",
				BloomFilterFactory.containsValue(
						ATTR_1.getValue(), filter, random2));
	}
}
