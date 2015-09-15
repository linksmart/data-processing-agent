package com.xively.client.http.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;

import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.xively.client.AppConfig;
import com.xively.client.XivelyService;
import com.xively.client.http.TestUtil;
import com.xively.client.http.api.DatapointRequester;
import com.xively.client.http.exception.HttpException;
import com.xively.client.http.util.exception.ParseToObjectException;
import com.xively.client.model.Datapoint;
import com.xively.client.model.Datastream;
import com.xively.client.model.Feed;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DatapointRequesterTest
{
	private static int feedId;
	private static String datastreamId;

	private Datapoint datapoint1;
	private Datapoint datapoint2;

	private DatapointRequester requester;

	@BeforeClass
	public static void setUpClass() throws Exception
	{
		// setup a general purpose feed set up for testing all it's children
		Feed feed = TestUtil.getObjectMapper().readValue(new FileInputStream(new File(TestUtil.fixtureUri + "feed1.json")),
				Feed.class);
		//feed = XivelyService.instance().feed().create(feed);
		//feedId = feed.getId();

		Datastream datastream = TestUtil.getObjectMapper().readValue(
				new FileInputStream(new File(TestUtil.fixtureUri + "datastream1.json")), Datastream.class);
		//XivelyService.instance().datastream(feedId).create(datastream);
		//datastreamId = datastream.getId();
	}

	@AfterClass
	public static void tearDownClass()
	{
		//XivelyService.instance().feed().delete(feedId);
	}

	@Before
	public void setUp() throws Exception
	{
		TestUtil.loadDefaultTestConfig();
		ObjectMapper mapper = TestUtil.getObjectMapper();

		datapoint1 = mapper.readValue(new FileInputStream(new File(TestUtil.fixtureUri + "datapoint1.json")), Datapoint.class);
		datapoint2 = mapper.readValue(new FileInputStream(new File(TestUtil.fixtureUri + "datapoint2.json")), Datapoint.class);

		//requester = XivelyService.instance().datapoint(feedId, datastreamId);
	}

	@After
	public void tearDown() throws Exception
	{
		//tearDownFixture(datapoint1.getAt());
		//tearDownFixture(datapoint2.getAt());
		AppConfig.getInstance().reload();
		requester = null;
	}

	private void tearDownFixture(String fixtureId)
	{
		try
		{
			requester.delete(fixtureId);
		} catch (HttpException e)
		{
			// NOT_FOUND is ok as the test ran could have not created/deleted it
			if (HttpStatus.SC_NOT_FOUND != e.getStatusCode())
			{
				throw e;
			}
		}
	}

	@Test
  @Ignore
	public void testCreate()
	{
		try
		{
			Datapoint retval = requester.create(datapoint2);
			assertTrue(datapoint2.memberEquals(retval));
		} catch (HttpException e)
		{
			fail("failed on requesting to create a datapoint");
		}
	}

	@Test
  @Ignore
	public void testCreateMultiple()
	{
		try
		{
			Collection<Datapoint> retval = requester.create(datapoint1, datapoint2);
			assertEquals(2, retval.size());
			assertTrue(retval.contains(datapoint1));
			assertTrue(retval.contains(datapoint2));
		} catch (HttpException e)
		{
			fail("failed on requesting to create multiple datapoints");
		}
	}

	@Test
  @Ignore
	public void testJSONAcceptHeaderAndConverstion()
	{
		requester.create(datapoint1);

		try
		{
			Datapoint retval = requester.get(datapoint1.getAt());
			assertTrue(datapoint1.memberEquals(retval));
		} catch (HttpException e)
		{
			fail("failed on requesting to get a datapoint");
		} catch (ParseToObjectException e)
		{
			fail("response is not a valid json");
		}
	}

	@Test
  @Ignore
	public void testGet()
	{
		requester.create(datapoint1);

		try
		{
			Datapoint retval = requester.get(datapoint1.getAt());
			assertTrue(datapoint1.memberEquals(retval));
		} catch (HttpException e)
		{
			fail("failed on requesting to get a datapoint");
		}
	}

	@Test
  @Ignore
	public void testUpdate()
	{
		requester.create(datapoint1);
		datapoint1.setValue("555");

		try
		{
			requester.update(datapoint1);
			Datapoint retval = requester.update(datapoint1);
			assertTrue(datapoint1.memberEquals(retval));
		} catch (HttpException e)
		{
			fail("failed on requesting to update a datapoint");
		}
	}

	@Test
  @Ignore
	public void testDelete()
	{
		requester.create(datapoint1);

		try
		{
			requester.delete(datapoint1.getAt());
		} catch (HttpException e)
		{
			fail("failed on requesting to delete a datapoint");
		}

		try
		{
			requester.get(datapoint1.getAt());
			fail("should not be able to get deleted datapoint");
		} catch (HttpException e)
		{
			// pass
		}
	}
}
