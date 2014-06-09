package it.ismb.pertlab.pwal.etsi_m2m_manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import it.ismb.pertlab.pwal.api.devices.interfaces.DevicesManager;
import it.ismb.pertlab.pwal.api.devices.model.types.DeviceNetworkType;
import it.ismb.pertlab.pwal.etsi_m2m_manager.model.EtsiM2MMessageParser;
import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.Applications;
import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.NamedReferenceCollection;
import it.ismb.pertlab.pwal.etsi_m2m_manager.model.jaxb.ReferenceToNamedResource;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class EtsiM2MManager extends DevicesManager{
	private String baseUrl;
	private CloseableHttpClient client;
	
	public EtsiM2MManager(String baseUrl)
	{
		client=HttpClients.createDefault();
		this.baseUrl=baseUrl;
		
	}
	
	@Override
	public void run() {
		while(!t.isInterrupted())
		{		
			discoverApplications();
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				t.interrupt();
			}
		}
	}

	private void discoverApplications() {
		HttpGet applicationsRequest=new HttpGet(this.baseUrl+"/applications");
		applicationsRequest.setHeader("Authorization", "Basic d2F0ZXI6d2F0ZXJwYXNzd29yZA==");
		applicationsRequest.setHeader("Content-Type", "application/xml");
		applicationsRequest.setHeader("Accept", "application/xml");
		applicationsRequest.setHeader("From", "http://m2mtilab.dtdns.net:8082/etsi/almanac/applications/APP");
		
		try {
			CloseableHttpResponse resp=client.execute(applicationsRequest);
			EtsiM2MMessageParser parser=new EtsiM2MMessageParser();
			parser.parseApplications(resp.getEntity().getContent());
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Override
	public String getNetworkType() {
		return DeviceNetworkType.M2M;
	}

}
