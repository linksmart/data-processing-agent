package eu.linksmart.gc.examples.weather.service;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;

public class WeatherServiceServlet extends HttpServlet {

	private static final long serialVersionUID = 4799084413431453345L;
	
	private static Logger LOG = Logger.getLogger(WeatherServiceServlet.class.getName());
	
	private int temperature = 25; 

	public WeatherServiceServlet() {
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		String queryString = request.getQueryString();
		
		if(queryString != null) {
			if(request.getCharacterEncoding() != null)
				queryString = URLDecoder.decode(queryString, request.getCharacterEncoding());
			else
				queryString = URLDecoder.decode(queryString, "UTF-8");
		}

		String responseString = null;

        if ((queryString != null) && queryString.equals("loc=nowhere")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "");
        }

		try {
			
			JSONObject getJson = new JSONObject();
			getJson.put("Temperature", temperature);
			
			responseString = getJson.toString();
			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
		}
		
		response.setContentLength(responseString.length());
		response.getOutputStream().write(responseString.getBytes());
		response.getOutputStream().close();
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		process(request, response);
	}
	
	public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
		process(request, response);
	}
	
	public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		String pathInfo = request.getPathInfo();
		
		if(pathInfo == null || pathInfo.length() == 0) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "pathInfo is empty");
			return;
		}
		
		LOG.info("deleting id: " + request.getPathInfo().substring(1));
		
		String responseString = "delete successfull";
		
		response.setContentLength(responseString.length());
		response.getOutputStream().write(responseString.getBytes());
		response.getOutputStream().close();
		
	}
	
	private void process(HttpServletRequest request, HttpServletResponse response) throws IOException {

		//
		// create JSONObject from message payload
		//
		StringBuilder requestBuilder = new StringBuilder();
		
		if (request.getContentLength() > 0) {
			try {
				BufferedReader reader = request.getReader();
				for (String line = null; (line = reader.readLine()) != null;)
					requestBuilder.append(line);
				reader.close();
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "unable to read from request stream");
				return;
			}
		} else {
			response.sendError(HttpServletResponse.SC_NO_CONTENT, "request content is empty");
			return;
		}
		
		String responseString = null;

		try {
			
			JSONObject contentJson = new JSONObject(requestBuilder.toString());
			temperature = contentJson.getInt("Temperature");
			
			JSONObject returnJson = new JSONObject();
			returnJson.put("Temperature", temperature);
			
			responseString = returnJson.toString();
			
		} catch (JSONException e) {
			LOG.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
			return;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
		}

		response.setContentLength(responseString.length());
		response.getOutputStream().write(responseString.getBytes());
		response.getOutputStream().close();
	}
}
