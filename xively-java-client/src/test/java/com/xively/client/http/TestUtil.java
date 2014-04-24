package com.xively.client.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import com.xively.client.AppConfig;
import com.xively.client.http.util.ParserUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestUtil
{
	public static final String fixtureUri = "src/test/res/";

	public static String getStringFromFile(String fileName)
	{
		try
		{
			FileInputStream fileStream = null;
			try
			{
				fileStream = new FileInputStream(new File(fixtureUri + fileName));
				FileChannel fileChannel = fileStream.getChannel();
				MappedByteBuffer bb = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
				return Charset.defaultCharset().decode(bb).toString();
			} finally
			{
				fileStream.close();
			}
		} catch (IOException io)
		{
			throw new RuntimeException(io);
		}
	}

	public static void loadDefaultTestConfig()
	{
		AppConfig.getInstance().reload();
	}

	public static ObjectMapper getObjectMapper()
	{
		return ParserUtil.getObjectMapper();
	}
}
