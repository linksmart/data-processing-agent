package com.xively.client.http.impl;

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
import com.xively.client.http.api.FeedRequester;
import com.xively.client.http.api.TriggerRequester;
import com.xively.client.http.exception.HttpException;
import com.xively.client.http.util.exception.ParseToObjectException;
import com.xively.client.model.Datastream;
import com.xively.client.model.Feed;
import com.xively.client.model.Trigger;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TriggerRequesterTest
{
	private static int feedId;
	private static String datastreamId;

	private TriggerRequester requester;
	private Trigger trigger1;
	private Trigger trigger2;

	@BeforeClass
	public static void setUpClass() throws Exception
	{
		// setup a general purpose feed set up for testing all it's children
		Feed feed = TestUtil.getObjectMapper().readValue(new FileInputStream(new File(TestUtil.fixtureUri + "feed1.json")),
				Feed.class);
		//feed = XivelyService.instance().feed().create(feed);
		//feedId = feed.getId();
		//datastreamId = ((Datastream) (feed.getDatastreams().toArray()[0])).getId();
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
		ObjectMapper mapper = TestUtil.getObjectMapper();

		trigger1 = mapper.readValue(new FileInputStream(new File(TestUtil.fixtureUri + "trigger1.json")), Trigger.class);
		trigger1.setFeedId(feedId);
		trigger1.setDatastreamId(datastreamId);

		trigger2 = mapper.readValue(new FileInputStream(new File(TestUtil.fixtureUri + "trigger2.json")), Trigger.class);
		trigger2.setFeedId(feedId);
		trigger2.setDatastreamId(datastreamId);

		requester = XivelyService.instance().trigger();
		//trigger1 = requester.create(trigger1);
	}

	@After
	public void tearDown() throws Exception
	{
		// tear down only if it were ever persisted, i.e., id != null
		if (trigger1.getId() != null)
		{
			tearDownFixture(trigger1.getId());
		}
		if (trigger2.getId() != null)
		{
			tearDownFixture(trigger2.getId());
		}
		AppConfig.getInstance().reload();
		requester = null;
	}

	private void tearDownFixture(int fixtureId)
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
	public void testCreateAndList()
	{
		try
		{
			Trigger retval = requester.create(trigger2);

			// update fields that returned after creation before comparing every
			// other field
			assertTrue(retval.getId() != null);
			trigger2.setId(retval.getId());
			assertTrue(retval.getLogin() != null);
			trigger2.setLogin(retval.getLogin());
			assertTrue(retval.getNotifiedAt() != null);
			trigger2.setNotifiedAt(retval.getNotifiedAt());

			assertTrue(trigger2.memberEquals(retval));
		} catch (HttpException e)
		{
			fail("failed on requesting to create a trigger");
		}

		try
		{
			Collection<Trigger> retvals = requester.list();

			assertTrue(retvals.contains(trigger1));
			assertTrue(retvals.contains(trigger2));
		} catch (HttpException e)
		{
			fail("failed on requesting to list feed");
		}
	}

	@Test
  @Ignore
	public void testJSONAcceptHeaderAndConverstion()
	{
		try
		{
			Trigger retval = requester.get(trigger1.getId());
			assertTrue(trigger1.memberEquals(retval));
		} catch (HttpException e)
		{
			fail("failed on requesting to get a trigger");
		} catch (ParseToObjectException e)
		{
			fail("response is not a valid json");
		}
	}

	@Test
  @Ignore
	public void testGet()
	{
		try
		{
			Trigger retval = requester.get(trigger1.getId());
			assertTrue(trigger1.memberEquals(retval));
		} catch (HttpException e)
		{
			fail("failed on requesting to get a trigger");
		}
	}

	@Test
  @Ignore
	public void testGetByFeedId()
	{
		trigger2 = requester.create(trigger2);
		try
		{
			Collection<Trigger> retval = requester.getByFeedId(feedId);
			assertTrue(retval.size() == 2);
			assertTrue(retval.contains(trigger1));
			assertTrue(retval.contains(trigger2));
		} catch (HttpException e)
		{
			fail("failed on requesting to get a trigger");
		}
	}

	@Test
  @Ignore
	public void testUpdate()
	{
		trigger1.setThresholdValue(6.66d);

		try
		{
			Trigger retval = requester.update(trigger1);
			assertTrue(trigger1.memberEquals(retval));
		} catch (HttpException e)
		{
			fail("failed on requesting to update a trigger");
		}
	}

	@Test
  @Ignore
	public void testDelete()
	{
		try
		{
			requester.delete(trigger1.getId());
		} catch (HttpException e)
		{
			fail("failed on requesting to delete a trigger");
		}

		try
		{
			requester.get(trigger1.getId());
			fail("should not be able to get deleted trigger");
		} catch (HttpException e)
		{
			// pass
		}
	}
}
