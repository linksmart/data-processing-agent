package it.ismb.pertlab.pwal.estsi_m2m_manager_v2.devices.telecom.base;

import org.apache.http.client.methods.HttpGet;

public class TelecomBaseDevice
{
    protected HttpGet contentInstancesRequest;
    protected String contentInstancesUrl;
    
    public TelecomBaseDevice(String contentInstanceUrl)
    {
        this.contentInstancesUrl = contentInstanceUrl;
        contentInstancesRequest = new HttpGet(this.contentInstancesUrl);
        //These headers should be dynamic
        contentInstancesRequest.setHeader("Authorization", "Basic d2F0ZXI6d2F0ZXJwYXNzd29yZA==");
        contentInstancesRequest.setHeader("Content-Type", "application/xml");
        contentInstancesRequest.setHeader("Accept", "application/xml");
        contentInstancesRequest.setHeader("From", "http://m2mtilab.dtdns.net:8082/etsi/almanac/applications/APP");
    }

}
