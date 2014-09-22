package it.ismb.pertlab.pwal.smartsantander.restclient;

import it.ismb.pertlab.pwal.api.shared.PWALJsonMapper;
import it.ismb.pertlab.pwal.api.shared.PwalHttpClient;
import it.ismb.pertlab.pwal.smartsantander.datamodel.json.SmartSantanderSingleNodeJson;
import it.ismb.pertlab.pwal.smartsantander.datamodel.json.SmartSantanderTrafficIntensityJson;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;

public class SmartSantanderRestClient
{

    private static Logger log = null;
    private String serviceEndpoint;
    private PwalHttpClient httpClient;
    private List<SmartSantanderTrafficIntensityJson> measure = null;

    public SmartSantanderRestClient(String serviceEndpoint, Logger logger)
    {
        this.serviceEndpoint = serviceEndpoint;
        this.httpClient = new PwalHttpClient();
        log = logger;
    }

    /**
     * retrive the SmartSantader nodes list
     * 
     * @return List of nodes available in SmartSantander network
     */
    public List<SmartSantanderSingleNodeJson> getNodes()
    {
        try
        {
            HttpGet getSmartSantanderNodes = new HttpGet(serviceEndpoint
                    + "GetNodes");

            CloseableHttpResponse resp = this.httpClient
                    .executeRequest(getSmartSantanderNodes);
            if (resp.getStatusLine().getStatusCode() == 200)
            {
                SmartSantanderSingleNodeJson[] nodes = PWALJsonMapper
                        .json2obj(SmartSantanderSingleNodeJson[].class, resp
                                .getEntity().getContent());
                return Arrays.asList(nodes);
            }
            else
            {
                log.warn("Nodes request failed. HTTP status is: {}", resp
                        .getStatusLine().getStatusCode());
            }
        }
        catch (IOException e)
        {
            log.error("getNodes: ", e.getLocalizedMessage());
        }
        return null;
    }

    public SmartSantanderTrafficIntensityJson getLastMeasures(String nodeId)
    {
        if (this.measure == null)
        {
            this.measure = getMeasures();
            log.info(
                    "Searching for measure belonging to the required nodeId: {}",
                    nodeId);
            for (SmartSantanderTrafficIntensityJson traffic : this.measure)
            {
                if (traffic.getNodeId().equals(nodeId))
                    return traffic;
            }
        }
        else
        {
            if (isOutOfDate())
            {
                this.measure = getMeasures();
            }
            log.info(
                    "Searching for measure belonging to the required nodeId: {}",
                    nodeId);
            for (SmartSantanderTrafficIntensityJson traffic : this.measure)
            {
                if (traffic.getNodeId().equals(nodeId))
                    return traffic;
            }
        }
        return null;
    }

    public List<SmartSantanderTrafficIntensityJson> getMeasures()
    {
        HttpGet getSmartSantanderNodes = new HttpGet(serviceEndpoint
                + "GetTrafficIntensityLastValues");

        CloseableHttpResponse resp;
        try
        {
            resp = this.httpClient.executeRequest(
                    getSmartSantanderNodes);
            if (resp.getStatusLine().getStatusCode() == 200)
            {
                SmartSantanderTrafficIntensityJson[] measure = PWALJsonMapper
                        .json2obj(SmartSantanderTrafficIntensityJson[].class,
                                resp.getEntity().getContent());
                return Arrays.asList(measure);
            }
            else
            {
                log.warn("Measures request failed. HTTP status is: {}", resp
                        .getStatusLine().getStatusCode());
            }
        }
        catch (IOException e)
        {
            log.error("getSmartSantanderNodes: ", e.getLocalizedMessage());
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    private Boolean isOutOfDate()
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "MM-dd-yyyy HH:mm:ss");
        try
        {
            Date date = simpleDateFormat.parse(this.measure.get(0).getDate());
            Date now = new Date();
            if ((date.getMinutes() - now.getMinutes()) != 0)
            {
                log.debug("Measure is out of date");
                return true;
            }
            log.debug("Measure is not out of date");
            return false;
        }
        catch (ParseException ex)
        {
            log.error("Parse date exception: ", ex);
        }
        return false;
    }
}
