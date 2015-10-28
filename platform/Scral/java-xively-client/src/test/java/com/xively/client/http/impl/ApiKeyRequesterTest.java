package com.xively.client.http.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import com.xively.client.http.api.ApiKeyRequester;
import com.xively.client.http.api.FeedRequester;
import com.xively.client.http.exception.HttpException;
import com.xively.client.http.util.exception.ParseToObjectException;
import com.xively.client.model.ApiKey;
import com.xively.client.model.Feed;
import com.xively.client.model.Permission;
import com.xively.client.model.Permission.AccessMethod;
import com.xively.client.model.Resource;

public class ApiKeyRequesterTest
{
	private static int feedId;

	private ApiKeyRequester requester;
	private ApiKey apiKey1;
	private ApiKey apiKey2;

	@BeforeClass
	public static void setUpClass() throws Exception
	{
		// setup a general purpose feed set up for testing all it's children
		Feed feed = TestUtil.getObjectMapper().readValue(new FileInputStream(new File(TestUtil.fixtureUri + "feed1.json")),
				Feed.class);
    /*
		feed = XivelyService.instance().feed().create(feed);
		feedId = feed.getId();
    */
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

		// apiKey1 fixture
		apiKey1 = new ApiKey();
		apiKey1.setLabel("Simple Test Key");
		apiKey1.setPrivateAccess(true);

		List<AccessMethod> am1 = new ArrayList<>();
		am1.add(AccessMethod.get);
		Permission p1 = new Permission(null, am1, null);
		List<Permission> permissions1 = new ArrayList<>();
		permissions1.add(p1);
		apiKey1.setPermissions(permissions1);

		// apiKey2 fixture
		apiKey2 = new ApiKey();
		apiKey2.setLabel("Magic Test Key");
		apiKey2.setPrivateAccess(true);

		List<AccessMethod> am2 = new ArrayList<>();
		am2.add(AccessMethod.get);
		List<Resource> resources = new ArrayList<>();
		Resource r2 = new Resource(feedId, null);
		resources.add(r2);
		Permission p2 = new Permission("66.66.66.66", am2, resources);

		List<AccessMethod> am3 = new ArrayList<>();
		am3.add(AccessMethod.put);
		am3.add(AccessMethod.post);
		am3.add(AccessMethod.delete);
		Permission p3 = new Permission(null, am3, null);

		List<Permission> permissions2 = new ArrayList<>();
		permissions2.add(p2);
		permissions2.add(p3);
		apiKey2.setPermissions(permissions2);

		requester = new ApiKeyRequesterImpl();
		//apiKey1 = requester.create(apiKey1);
	}

	@After
	public void tearDown() throws Exception
	{
		// tear down only if it were ever persisted, i.e., id != null
		if (apiKey1.getApiKey() != null)
		{
			tearDownFixture(apiKey1.getApiKey());
		}
		if (apiKey2.getApiKey() != null)
		{
			tearDownFixture(apiKey2.getApiKey());
		}
		AppConfig.getInstance().reload();
		requester = null;
	}

	private void tearDownFixture(String fixtureId)
	{
    /*
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
    */
	}

	@Test
  @Ignore
	public void testCreate()
	{
		try
		{
			ApiKey retval = requester.create(apiKey2);

			// update fields returned from creation before comparing all fields
			assertTrue(retval.getApiKey() != null);
			apiKey2.setApiKey(retval.getApiKey());

			assertTrue(apiKey2.memberEquals(retval));
		} catch (HttpException e)
		{
			fail("failed on requesting to create a apiKey");
		}
	}

	@Test
  @Ignore
	public void testJSONAcceptHeaderAndConverstion()
	{
		try
		{
			ApiKey retval = requester.get(apiKey1.getApiKey());
			assertTrue(apiKey1.memberEquals(retval));
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
		try
		{
			ApiKey retval = requester.get(apiKey1.getApiKey());
			assertTrue(apiKey1.memberEquals(retval));
		} catch (HttpException e)
		{
			fail("failed on requesting to get a apiKey");
		}
	}

	@Test
  @Ignore
	public void testGetByFeedId()
	{
		try
		{
			Collection<ApiKey> retval = requester.getByFeedId(feedId);
			assertTrue(retval.size() == 1);
			assertTrue(retval.contains(apiKey2));
		} catch (HttpException e)
		{
			fail("failed on requesting to get a apiKey");
		}
	}

	@Test
  @Ignore
	public void testDelete()
	{
		try
		{
			requester.delete(apiKey1.getApiKey());
		} catch (HttpException e)
		{
			fail("failed on requesting to delete a apiKey");
		}

		try
		{
			requester.get(apiKey1.getApiKey());
			fail("should not have been able to retrieve deleted apiKey");
		} catch (HttpException e)
		{
			// pass
		}
	}
}
