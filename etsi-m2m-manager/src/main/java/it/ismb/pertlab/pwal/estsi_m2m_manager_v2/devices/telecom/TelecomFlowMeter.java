package it.ismb.pertlab.pwal.estsi_m2m_manager_v2.devices.telecom;

import it.ismb.pertlab.pwal.api.devices.model.FlowMeter;
import it.ismb.pertlab.pwal.api.devices.model.Location;
import it.ismb.pertlab.pwal.api.devices.model.Unit;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceNetworkType;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceType;
import it.ismb.pertlab.pwal.api.shared.PwalHttpClient;
import it.ismb.pertlab.pwal.etsi_m2m_manager.devices.telecom.datamodel.json.TelecomWaterJson;
import it.ismb.pertlab.pwal.etsi_m2m_manager.model.EtsiM2MMessageParser;
import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.ContentInstance;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class TelecomFlowMeter implements FlowMeter {

	private String pwalId;
	private String id;
	private Double flow; 
//	private CloseableHttpClient client;
	private HttpGet contentInstancesRequest;
	private String contentInstancesUrl;
	private EtsiM2MMessageParser messageParser;
	private static final Logger log = LoggerFactory.getLogger(TelecomPhMeter.class);
	
	public TelecomFlowMeter(String contentInstancesUrl)
	{
//		this.client = client;
		this.contentInstancesUrl = contentInstancesUrl+"/latest";
		contentInstancesRequest = new HttpGet(this.contentInstancesUrl);
		//These headers should be dynamic
		contentInstancesRequest.setHeader("Authorization", "Basic d2F0ZXI6d2F0ZXJwYXNzd29yZA==");
		contentInstancesRequest.setHeader("Content-Type", "application/xml");
		contentInstancesRequest.setHeader("Accept", "application/xml");
		contentInstancesRequest.setHeader("From", "http://m2mtilab.dtdns.net:8082/etsi/almanac/applications/APP");
		this.messageParser = new EtsiM2MMessageParser();
	}

	@Override
	public Double getFlow() {
		try {
			CloseableHttpResponse flowResponse = PwalHttpClient.getInstance().executeRequest(contentInstancesRequest);
//			ContentInstances cis = this.messageParser.parseContentInstances(phResponse.getEntity().getContent());
			if(flowResponse.getEntity().getContent() != null)
			{
				ContentInstance ci = this.messageParser
						.parseContentInstance(flowResponse.getEntity()
								.getContent());
				if (ci != null) {
					// log.debug("Received message: "+this.messageParser.toXml(ContentInstances.class,
					// ci));
					Gson gson = new Gson();
					TelecomWaterJson values = gson.fromJson(ci.getContent()
							.getTextContent(), TelecomWaterJson.class);
					this.flow = values.getFlow();
					log.debug("Json parsed: {}", values.toString());
				}
			}
		} catch (IOException | IllegalStateException | JAXBException e) {
			log.error("getFlow: ",e.getCause());
		}
		return this.flow;
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
		return DeviceType.FLOW_METER_SENSOR;
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
