package it.ismb.pertlab.pwal.api.shared;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * This class is a thread safe http client that can be shared between PWAL components
 * @author GBo
 *
 */
public class PwalHttpClient {
	
	private PoolingHttpClientConnectionManager connectionManager;
	private CloseableHttpClient httpClient;
//	private static PwalHttpClient instance;
	
//	public static PwalHttpClient getInstance()
//	{
//		if(instance == null)
//			instance = new PwalHttpClient();
//		return instance;
//	}
	
	public PwalHttpClient()
	{	
		this.connectionManager = new PoolingHttpClientConnectionManager();
		this.httpClient = HttpClients.custom().setConnectionManager(
				this.connectionManager).build();
	}
	
	/**
	 * This method execute the HTTP request received as parameter.		
	 * @param request is the http base request class 
	 * @return the request response
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public CloseableHttpResponse executeRequest(HttpRequestBase request) throws ClientProtocolException, IOException
	{
		return this.httpClient.execute(request);
	}
}
