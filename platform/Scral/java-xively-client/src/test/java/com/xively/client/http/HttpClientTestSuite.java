package com.xively.client.http;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.xively.client.http.impl.ApiKeyRequesterTest;
import com.xively.client.http.impl.DatapointRequesterTest;
import com.xively.client.http.impl.DatastreamRequesterTest;
import com.xively.client.http.impl.FeedRequesterTest;
import com.xively.client.http.impl.TriggerRequesterTest;

@RunWith(Suite.class)
@SuiteClasses({ DatapointRequesterTest.class, DatastreamRequesterTest.class, FeedRequesterTest.class, TriggerRequesterTest.class,
		ApiKeyRequesterTest.class })
public class HttpClientTestSuite
{	
}
