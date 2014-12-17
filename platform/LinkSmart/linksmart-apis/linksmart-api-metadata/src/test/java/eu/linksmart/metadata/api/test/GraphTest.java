package eu.linksmart.metadata.api.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.linksmart.metadata.Literal;
import eu.linksmart.metadata.SemanticResource;
import eu.linksmart.metadata.Value;
import eu.linksmart.metadata.Value.ValueType;

public class GraphTest {

	private static Logger LOG = LoggerFactory.getLogger(GraphTest.class
			.getName());

	private SemanticResource r;

	@Before
	public void beforeTest() {
		r = new SemanticResource("res:mytest");
		r.add("rdfs:label", new Literal("Hello world", "xsd:string", "en"));
		r.add("rdfs:label", new Value("Hallo Welt", ValueType.literal));
		r.add("rdfs:label", new Value("Hola mundo", ValueType.literal,
				"xsd:string", "es"));
		r.add("rdfs:label", new Value("default://value/type/is/uri"));
	}

	@After
	public void afterTest() {

	}

	/**
	 * Tests creation and resolution of literal values.
	 */
	@Test
	public void testLiterals() {

		// All values but the URI should have become instances of Literal
		assertEquals(3, r.getLabels().size());

		// This should match since no details specified
		assertTrue(r.getLabels().contains(new Literal("Hallo Welt")));

		// No match, language missing
		assertFalse(r.getLabels().contains(
				new Literal("Hola mundo", "xsd:string")));

	}
}