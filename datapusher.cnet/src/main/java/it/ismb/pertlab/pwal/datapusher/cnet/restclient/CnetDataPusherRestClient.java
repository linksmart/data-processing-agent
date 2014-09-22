package it.ismb.pertlab.pwal.datapusher.cnet.restclient;

import it.ismb.pertlab.pwal.api.shared.PWALXmlMapper;
import it.ismb.pertlab.pwal.api.shared.PwalHttpClient;
import it.ismb.pertlab.pwal.datapusher.cnet.datamodel.ArrayOfIoTEntity;
import it.ismb.pertlab.pwal.datapusher.cnet.datamodel.IoTEntity;

import java.io.IOException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.slf4j.Logger;

public class CnetDataPusherRestClient
{

    private static Logger log = null;
    private String serviceEndpoint;
    private PWALXmlMapper xmlMapper;

    public CnetDataPusherRestClient(String endpoint, Logger logger)
    {
        log = logger;
        this.serviceEndpoint = endpoint;
        this.xmlMapper = new PWALXmlMapper();
    }

    /**
     * This method check if an IoTEntity already exists into the data
     * management.
     * 
     * @param deviceName is the device name used to search if the IoTEntity of
     *            the corresponding device already exist.
     * @return a filled IoTEntity with the device "about" reference if the
     *         device already exists or an empty IoTEntity if not.
     */
    public ArrayOfIoTEntity alreadyExists(String deviceName)
    {
        try
        {
            HttpGet getIoTEntity = new HttpGet(serviceEndpoint + "?like="
                    + deviceName);
            CloseableHttpResponse resp = PwalHttpClient.getInstance()
                    .executeRequest(getIoTEntity);
            ArrayOfIoTEntity response = this.xmlMapper.unmarshal(
                    ArrayOfIoTEntity.class, resp.getEntity().getContent());
            if (resp.getStatusLine().getStatusCode() == 200)
            {
                return response;
            }
        }
        catch (Exception ex)
        {
            log.error("Exception: ", ex);
        }
        return new ArrayOfIoTEntity();
    }

    /**
     * Push the new IoTEntity into the CNET cloud data manager.
     * 
     * @param iotEntity is the IoTEntity to be pushed.
     * @return an IoTEntity with the about reference
     */
    public IoTEntity pushNewIoTEntity(IoTEntity iotEntity)
    {
        try
        {
            HttpPost postIoTEntity = new HttpPost(serviceEndpoint);
            postIoTEntity.setEntity(new ByteArrayEntity(this.xmlMapper.marshal(
                    IoTEntity.class, iotEntity).toByteArray()));
            CloseableHttpResponse resp = PwalHttpClient.getInstance()
                    .executeRequest(postIoTEntity);
            IoTEntity iotEntityResp = this.xmlMapper.unmarshal(IoTEntity.class,
                    resp.getEntity().getContent());
            if (resp.getStatusLine().getStatusCode() == 200)
            {
                return iotEntityResp;
            }
        }
        catch (Exception ex)
        {
            log.error("Exception: ", ex);
        }
        return null;
    }

    /**
     * Pushs new values into the CNET cloud data manager.
     * 
     * @param iotEntity is the IoTEntity containing the IoTObservation
     * @return true if ok, false if not.
     */
    public Boolean pushNewValues(IoTEntity iotEntity)
    {
        try
        {
            HttpPost postIoTEntityValues = new HttpPost(serviceEndpoint);
            postIoTEntityValues.setEntity(new ByteArrayEntity(this.xmlMapper
                    .marshal(IoTEntity.class, iotEntity).toByteArray()));
            CloseableHttpResponse resp;

            resp = PwalHttpClient.getInstance().executeRequest(
                    postIoTEntityValues);

            if (resp.getStatusLine().getStatusCode() == 200)
            {
                return true;
            }
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
}