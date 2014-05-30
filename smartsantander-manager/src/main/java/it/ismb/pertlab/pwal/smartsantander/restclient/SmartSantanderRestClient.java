package it.ismb.pertlab.pwal.smartsantander.restclient;

import it.ismb.pertlab.pwal.smartsantander.datamodel.json.SmartSantanderSingleNodeJson;
import it.ismb.pertlab.pwal.smartsantander.datamodel.json.SmartSantanderTrafficIntensityJson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class SmartSantanderRestClient {
	private static Logger log = null;
	
	private String serviceEndpoint;
	private RestTemplate template;
	private List<SmartSantanderTrafficIntensityJson> measure = null;
	
	public SmartSantanderRestClient(String serviceEndpoint, Logger logger)
	{
		this.serviceEndpoint=serviceEndpoint;
		template=new RestTemplate();
		log = logger;
	}

	/**
	 * retrive the SmartSantader nodes list
	 * @return List of nodes available in SmartSantander network
	 */
	public List<SmartSantanderSingleNodeJson> getNodes() {
		
		HttpHeaders h=new HttpHeaders();
		h.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<SmartSantanderSingleNodeJson[]> entity=new HttpEntity<SmartSantanderSingleNodeJson[]>(h);
		log.debug("Contacting {}/GetNodes ",serviceEndpoint);
		ResponseEntity<SmartSantanderSingleNodeJson[]> r=template.exchange(serviceEndpoint+"GetNodes", HttpMethod.GET, entity, SmartSantanderSingleNodeJson[].class);
		if(r.getStatusCode().compareTo(HttpStatus.OK)==0)
		{
			log.debug("Returned these nodes: {} ",Arrays.asList(r.getBody()));
			return Arrays.asList(r.getBody());
		}
		return null;
	}
	
	public SmartSantanderTrafficIntensityJson getLastMeasures(String nodeId)
	{
		if(this.measure == null)
		{
			this.measure = getMeasures();
			log.info("Searching for measure belonging to the required nodeId: {}", nodeId);
			for (SmartSantanderTrafficIntensityJson traffic : this.measure) {
				if(traffic.getNodeId().equals(nodeId))
					return traffic;
			}
		}
		else
		{
			if(isOutOfDate())
			{
				this.measure = getMeasures();
			}
			log.info("Searching for measure belonging to the required nodeId: {}", nodeId);
			for (SmartSantanderTrafficIntensityJson traffic : this.measure) {
				if(traffic.getNodeId().equals(nodeId))
					return traffic;
			}
		}
		return null;
	}

	private List<SmartSantanderTrafficIntensityJson> getMeasures() {
		HttpHeaders h = new HttpHeaders();
		h.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<SmartSantanderTrafficIntensityJson[]> entity=new HttpEntity<SmartSantanderTrafficIntensityJson[]>(h);
		log.debug("Contacting {}/GetTrafficIntensityLastValues",serviceEndpoint);
		ResponseEntity<SmartSantanderTrafficIntensityJson[]> r = template.exchange(serviceEndpoint + "GetTrafficIntensityLastValues", HttpMethod.GET, entity, SmartSantanderTrafficIntensityJson[].class);
		if(r.getStatusCode().compareTo(HttpStatus.OK) == 0)
		{
			log.debug("Returned last values: {}", Arrays.asList(r.getBody()));
			return Arrays.asList(r.getBody());
		}
		else
			log.error("Request last values failed. HTTP status returned: {}", r.getStatusCode());
		return null;
	}
	
	@SuppressWarnings("deprecation")
	private Boolean isOutOfDate()
	{
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        try
        {
        	Date date = simpleDateFormat.parse(this.measure.get(0).getDate());
        	Date now = new Date();
        	if((date.getMinutes() - now.getMinutes()) != 0)
        	{
        		log.debug("Measure is out of date");
        		return true;
        	}
        	log.debug("Measure is not out of date");
        	return false;
        }
        catch(ParseException ex)
        {
        	log.error("Parse date exception: ", ex);
        }
        return false;
	}
}