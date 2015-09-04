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
import com.xively.client.http.api.DatastreamRequester;
import com.xively.client.http.api.FeedRequester;
import com.xively.client.http.exception.HttpException;
import com.xively.client.http.util.exception.ParseToObjectException;
import com.xively.client.model.Datapoint;
import com.xively.client.model.Datastream;
import com.xively.client.model.Feed;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DatastreamRequesterTest
{
	private static int feedId;
	private static final String datastreamId1 = "test_stream_1";
	private static final String datastreamId2 = "test_stream_2";

	private DatastreamRequester requester;
	private Datastream datastream1;
	private Datastream datastream2;
	private static ObjectMapper mapper;

	@BeforeClass
	public static void setUpClass() throws Exception
	{
		// setup a general purpose feed set up for testing all it's children
		Feed feed = TestUtil.getObjectMapper().readValue(new FileInputStream(new File(TestUtil.fixtureUri + "feed1.json")),
				Feed.class);
		//feed = XivelyService.instance().feed().create(feed);
		//feedId = feed.getId();
	}

	@AfterClass
	public static void tearDownClass()
	{
		FeedRequester requester = new FeedRequesterImpl();
		//requester.delete(feedId);
	}

	@Before
	public void setUp() throws Exception
	{
		TestUtil.loadDefaultTestConfig();
		mapper = TestUtil.getObjectMapper();

		datastream1 = mapper.readValue(new FileInputStream(new File(TestUtil.fixtureUri + "datastream1.json")), Datastream.class);
		datastream2 = mapper.readValue(new FileInputStream(new File(TestUtil.fixtureUri + "datastream2.json")), Datastream.class);

		requester = XivelyService.instance().datastream(feedId);
	}

	@After
	public void tearDown() throws Exception
	{
		tearDownFixture(datastreamId1);
		tearDownFixture(datastreamId2);
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
			Datastream retval = requester.create(datastream2);

			// set fields updated by create so we can compare all other fields
			assertTrue(retval.getUpdatedAt() != null);
			datastream2.setUpdatedAt(retval.getUpdatedAt());

			assertTrue(datastream2.memberEquals(retval));
		} catch (HttpException e)
		{
			fail("failed on requesting to create a datastream");
		}
	}

	@Test
  @Ignore
	public void testCreateMultiple()
	{
		try
		{
			Collection<Datastream> retval = requester.create(datastream1, datastream2);
			assertEquals(2, retval.size());
			assertTrue(retval.contains(datastream1));
			assertTrue(retval.contains(datastream2));
		} catch (HttpException e)
		{
			fail("failed on requesting to create multiple datastreams");
		}
	}

	@Test
  @Ignore
	public void testJSONAcceptHeaderAndConverstion()
	{
		datastream1 = requester.create(datastream1);

		try
		{
			Datastream retval = requester.get(datastreamId1);
			assertTrue(datastream1.memberEquals(retval));
		} catch (HttpException e)
		{
			fail("failed on requesting to get a datastream");
		} catch (ParseToObjectException e)
		{
			fail("response is not a valid json");
		}
	}

	@Test
  @Ignore
	public void testGet()
	{
		datastream1 = requester.create(datastream1);

		try
		{
			Datastream retval = requester.get(datastreamId1);
			assertTrue(datastream1.memberEquals(retval));
		} catch (HttpException e)
		{
			fail("failed on requesting to get a datastream");
		}
	}

	@Test
	@Ignore
	public void testGetHistoryWithDatapoints()
	{
		Datapoint datapoint1 = null;
		Datapoint datapoint2 = null;
		try
		{
			datapoint1 = mapper
					.readValue(new FileInputStream(new File(TestUtil.fixtureUri + "datapoint1.json")), Datapoint.class);
			datapoint2 = mapper
					.readValue(new FileInputStream(new File(TestUtil.fixtureUri + "datapoint1.json")), Datapoint.class);

			datastream1 = requester.create(datastream1);
			DatapointRequester dpRequester = XivelyService.instance().datapoint(feedId, datastreamId1);
			dpRequester.create(datapoint1);
			dpRequester.create(datapoint2);
		} catch (Exception e)
		{
			fail(String.format("fail to set up test, %s", e.getLocalizedMessage()));
		}

		try
		{
			Datastream retval = requester.getHistoryWithDatapoints(datastream1.getId(), "2012-02-02T00:00:00.000000Z",
					"2013-02-03T00:00:00.000000Z", 86400);
			assertEquals(1, retval.getDatapoints().size());

			assertEquals(datastream1.getId(), retval.getId());

			assertEquals(1, retval.getDatapoints().size());
			Datapoint dp = (Datapoint) retval.getDatapoints().toArray()[0];
			assertEquals(datapoint1.getAt(), dp.getAt());
			assertEquals(datapoint1.getValue(), dp.getValue());
		} catch (HttpException e)
		{
			fail("failed on requesting to get datapoints with parameters");
		}
	}

	@Test
  @Ignore
	public void testUpdate()
	{
		datastream1 = requester.create(datastream1);
		datastream1.setValue("666");

		try
		{
			Datastream retval = requester.update(datastream1);
			assertTrue(datastream1.memberEquals(retval));
		} catch (HttpException e)
		{
			fail("failed on requesting to update a datastream");
		}
	}

	@Test
  @Ignore
	public void testDelete()
	{
		datastream1 = requester.create(datastream1);

		try
		{
			requester.delete(datastreamId1);
		} catch (HttpException e)
		{
			fail("failed on requesting to delete a datastream");
		}

		try
		{
			requester.get(datastreamId1);
			fail("should not be able to get deleted datasteram");
		} catch (HttpException e)
		{
			// pass
		}
	}
}
