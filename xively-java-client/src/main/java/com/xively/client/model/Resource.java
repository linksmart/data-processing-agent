// Copyright (c) 2003-2013, LogMeIn, Inc. All rights reserved.
// This is part of Xively4J library, it is under the BSD 3-Clause license.
package com.xively.client.model;

import com.xively.client.utils.ObjectUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Resource - is the {@link Feed} and/or {@link Datastream} that a
 * {@link Permission} maps to.
 * 
 * Value Object.
 * 
 * @author s0pau
 * 
 */
@JsonInclude(Include.NON_NULL)
@JsonRootName("resources")
public class Resource
{
	@JsonProperty("feed_id")
	private int feedId;

	@JsonProperty("datastream_id")
	private String datastreamId;

	@JsonCreator
	public Resource(@JsonProperty("feed_id") int feedId, @JsonProperty("datastream_id") String datastreamId)
	{
		this.feedId = feedId;
		this.datastreamId = datastreamId;
	}

	public int getFeedId()
	{
		return feedId;
	}

	public String getDatastreamId()
	{
		return datastreamId;
	}

	@JsonIgnore
	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}

		if (this == obj)
		{
			return true;
		}

		if (!(obj instanceof Resource))
		{
			return false;
		}

		Resource other = (Resource) obj;

		if (this.feedId != other.feedId)
		{
			return false;
		}

		if (!ObjectUtil.nullCheckEquals(this.datastreamId, other.datastreamId))
		{
			return false;
		}

		return true;
	}

	@JsonIgnore
	@Override
	public int hashCode()
	{
		int retval = 1;
		retval += feedId * 37;
		retval += datastreamId == null ? 0 : datastreamId.hashCode() * 37;
		return retval;
	}
}
