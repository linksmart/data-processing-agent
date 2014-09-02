package it.ismb.pertlab.pwal.estsi_m2m_manager_v2.devices.telecom;

import it.ismb.pertlab.pwal.api.devices.model.Location;
import it.ismb.pertlab.pwal.api.devices.model.PhMeter;
import it.ismb.pertlab.pwal.api.devices.model.Unit;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceNetworkType;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;

import java.io.IOException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TelecomPhMeter implements PhMeter {

	
	private String pwalId;
	private String id;
	private Double ph; 
	private CloseableHttpClient client;
	private HttpGet contentInstancesRequest;
	private String contentInstancesUrl;
	private static final Logger log = LoggerFactory.getLogger(TelecomPhMeter.class);
	
	public TelecomPhMeter(String contentInstancesUrl, CloseableHttpClient client)
	{
		this.client = client;
		this.contentInstancesUrl = contentInstancesUrl;
		contentInstancesRequest = new HttpGet(this.contentInstancesUrl);
		//These headers should be dynamic
		contentInstancesRequest.setHeader("Authorization", "Basic d2F0ZXI6d2F0ZXJwYXNzd29yZA==");
		contentInstancesRequest.setHeader("Content-Type", "application/xml");
		contentInstancesRequest.setHeader("Accept", "application/xml");
		contentInstancesRequest.setHeader("From", "http://m2mtilab.dtdns.net:8082/etsi/almanac/applications/APP");
	}
	
	@Override
	public Double getPh() {
		try {
			CloseableHttpResponse phResponse = this.client.execute(contentInstancesRequest);
		} catch (IOException e) {
			;
		}
		return this.ph;
	}

	@Override
	public String getPwalId() {
		return this.pwalId;
	}

	@Override
	public void setPwalId(String pwalId) {
		this.pwalId = pwalId;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getType() {
		return DeviceType.PH_METER;
	}

	@Override
	public String getNetworkType() {
		return DeviceNetworkType.M2M;
	}

	@Override
	public String getUpdatedAt() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUpdatedAt(String updatedAt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Location getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLocation(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Unit getUnit() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUnit(Unit unit) {
		// TODO Auto-generated method stub
		
	}

}
