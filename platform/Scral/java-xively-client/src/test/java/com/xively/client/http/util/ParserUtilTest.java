package com.xively.client.http.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import com.xively.client.http.TestUtil;
import com.xively.client.model.Datapoint;
import com.xively.client.model.Datastream;
import com.xively.client.model.Feed;
import com.xively.client.model.Feed.Status;
import com.xively.client.model.Location;
import com.xively.client.model.Location.Domain;

public class ParserUtilTest
{
	@Test
	public void testSimpleParseToConnectedObject()
	{
		String json = TestUtil.getStringFromFile("datapoint2.json");
		Datapoint retval = ParserUtil.toConnectedObject(json, Datapoint.class);
		assertEquals("2013-02-02T00:00:00.000000Z", retval.getAt());
		assertEquals("222", retval.getValue());
	}

	@Test
	public void testParseListFeedsToConnectedObjects()
	{
		String json = TestUtil.getStringFromFile("listFeeds.json");
		Collection<Feed> retval = ParserUtil.toConnectedObjects(json, Feed.class);
		try
		{
			assertEquals(2, retval.size());

			Object[] fList = retval.toArray();
			Feed feed1 = (Feed) fList[0];
			Feed feed2 = (Feed) fList[1];

			// feed1
			assertEquals(new URI("http://api.testsite.com/v2/feeds/11111.json"), feed1.getFeedUri());
			assertEquals("Test Feed - ONE", feed1.getTitle());
			assertEquals(Status.live, feed1.getStatus());
			assertEquals(new URI("https://testsite.com/users/JohnDoe"), feed1.getCreatorUri());
			assertEquals("2010-06-08T09:30:21.472927Z", feed1.getUpdatedAt());
			assertEquals("2010-05-03T23:43:01.238734Z", feed1.getCreatedAt());

			Location location = new Location();
			location.setDomain(Domain.physical);
			assertEquals(location, feed1.getLocation());

			assertEquals(2, feed1.getDatastreams().size());
			Object[] dsList = feed1.getDatastreams().toArray();
			Datastream ds1 = new Datastream();
			ds1.setId("test_feed-stream10");
			ds1.setMaxValue("10000.0");
			ds1.setTags(Arrays.asList("humidity"));
			ds1.setValue("111");
			ds1.setMinValue("-10.0");
			ds1.setUpdatedAt("2010-07-02T10:21:57.101496Z");
			assertEquals(ds1, dsList[0]);

			Datastream ds2 = new Datastream();
			ds2.setId("test_feed-stream11");
			ds2.setMaxValue("10000.0");
			ds2.setTags(Arrays.asList("humidity"));
			ds2.setValue("222");
			ds2.setMinValue("-10.0");
			ds2.setUpdatedAt("2010-07-02T10:21:57.176209Z");
			assertEquals(ds2, dsList[1]);

			// feed2
			assertEquals(new URI("http://api.testsite.com/v2/feeds/22.json"), feed2.getFeedUri());
			assertEquals("Test Feed - Title 2", feed2.getTitle());
			assertEquals(2, feed2.getDatastreams().size());
			dsList = feed2.getDatastreams().toArray();
			ds1 = (Datastream) dsList[0];
			ds2 = (Datastream) dsList[1];
			assertEquals("test_feed-stream20", ds1.getId());
			assertEquals("test_feed-stream21", ds2.getId());
		} catch (Exception e)
		{
			fail("Error while comparing response");
		}
	}

	@Test
	public void testParseDatastreamHistoryToConnectedObjects()
	{
		String json = TestUtil.getStringFromFile("datastreamHistory.json");
		Collection<Datastream> retval = ParserUtil.toConnectedObjects(json, Datastream.class);
		assertEquals(1, retval.size());

		Datastream ds = (Datastream) retval.toArray()[0];
		assertEquals("test_datastream_history", ds.getId());
		assertEquals("1.0", ds.getMaxValue());
		assertEquals("2013-01-04T10:30:00.119435Z", ds.getUpdatedAt());
		assertEquals("0.00334173", ds.getValue());

		assertEquals(8, ds.getDatapoints().size());
		Datapoint[] dpList = ds.getDatapoints().toArray(new Datapoint[0]);

		Datapoint dp = new Datapoint();
		dp.setAt("2013-01-01T14:14:55.118845Z");
		dp.setValue("0.25741970");
		assertEquals(dp, dpList[0]);

		dp.setAt("2013-02-02T14:29:55.123420Z");
		dp.setValue("0.86826886");
		assertEquals(dp, dpList[1]);

		dp.setAt("2013-02-02T14:44:55.111267Z");
		dp.setValue("0.28586252");
		assertEquals(dp, dpList[2]);

		dp.setAt("2013-03-03T14:59:55.126180Z");
		dp.setValue("0.48122377");
		assertEquals(dp, dpList[3]);

		dp.setAt("2013-03-03T15:14:55.121795Z");
		dp.setValue("0.60897230");
		assertEquals(dp, dpList[4]);

		dp.setAt("2013-03-03T15:29:55.105327Z");
		dp.setValue("0.52898451");
		assertEquals(dp, dpList[5]);

		dp.setAt("2013-04-04T15:44:55.115502Z");
		dp.setValue("0.36369879");
		assertEquals(dp, dpList[6]);

		dp.setAt("2013-04-04T15:59:55.111692Z");
		dp.setValue("0.54204623");
		assertEquals(dp, dpList[7]);
	}
}
