package org.fit.fraunhofer.almanac;

/**
 * Created by Werner-Kytölä on 03.07.2015.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class WasteHttpClient{

    /***************** CONSTANTS */
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String RESOURCECAT_GET_URL = "http://linksmart.cnet.se:44441/ogc/blablabla";

    private static final int SUCCESSFUL_LOWERLIMIT = 200;
    private static final int SUCCESSFUL_UPPERLIMIT = 300;

    private static void sendGET(String getUrl) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpget = new HttpGet(RESOURCECAT_GET_URL);

            System.out.println("Executing request " + httpget.getRequestLine());

            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                @Override
                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= SUCCESSFUL_LOWERLIMIT && status < SUCCESSFUL_UPPERLIMIT) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };
            String responseBody = httpclient.execute(httpget, responseHandler);
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
        } finally {
            httpclient.close();
        }
    }
}
