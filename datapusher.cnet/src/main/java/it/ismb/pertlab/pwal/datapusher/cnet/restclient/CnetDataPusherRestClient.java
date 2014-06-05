package it.ismb.pertlab.pwal.datapusher.cnet.restclient;

import it.ismb.pertlab.pwal.datapusher.cnet.datamodel.ArrayOfIoTEntity;
import it.ismb.pertlab.pwal.datapusher.cnet.datamodel.IoTEntity;

import java.util.Arrays;

import org.slf4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class CnetDataPusherRestClient {

	private static Logger log = null;
	private String serviceEndpoint;
	private RestTemplate template;
	
	public CnetDataPusherRestClient(String endpoint, Logger logger)
	{
		this.template = new RestTemplate();
		log = logger;
		this.serviceEndpoint = endpoint;
	}
	
	public ArrayOfIoTEntity alreadyExists(String deviceName)
	{
		HttpHeaders h=new HttpHeaders();
		h.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
		HttpEntity<ArrayOfIoTEntity> entity=new HttpEntity<ArrayOfIoTEntity>(h);
		log.debug("Contacting {}?like={} ",serviceEndpoint,deviceName);
		ResponseEntity<ArrayOfIoTEntity> r=template.exchange(serviceEndpoint+"?like=" + deviceName, HttpMethod.GET, entity, ArrayOfIoTEntity.class);
		ArrayOfIoTEntity response = new ArrayOfIoTEntity();
		if(r.getStatusCode().compareTo(HttpStatus.OK)==0)
		{
			response = (ArrayOfIoTEntity)r.getBody();
			return response;
		}
		return null;
	}
	
	public IoTEntity pushNewIoTEntity(IoTEntity iotEntity)
	{
		HttpHeaders h=new HttpHeaders();
		h.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
		HttpEntity<IoTEntity> entity=new HttpEntity<IoTEntity>(iotEntity,h);
		log.debug("Contacting {} ",serviceEndpoint);
		ResponseEntity<IoTEntity> r=template.exchange(serviceEndpoint, HttpMethod.POST, entity, IoTEntity.class);
		IoTEntity response = new IoTEntity();
		if(r.getStatusCode().compareTo(HttpStatus.OK)==0)
		{
			response = (IoTEntity)r.getBody();
			log.debug("Recevied xml response: ");
			response.toXml();
			return response;
		}
		return null;
	}
	
	public Boolean pushNewValues(IoTEntity iotEntity)
	{
		HttpHeaders h=new HttpHeaders();
		h.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
		HttpEntity<IoTEntity> entity=new HttpEntity<IoTEntity>(iotEntity,h);
		log.debug("Contacting {} ",serviceEndpoint);
		ResponseEntity<Void> r=template.exchange(serviceEndpoint, HttpMethod.POST, entity, Void.class);
		if(r.getStatusCode().compareTo(HttpStatus.OK)==0)
		{
			return true;
		}
		return false;
	}
}