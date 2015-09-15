package metrics.smartbin.api;

public class MetricServicesProxy implements metrics.smartbin.api.MetricServices {
  private String _endpoint = null;
  private metrics.smartbin.api.MetricServices metricServices = null;
  
  public MetricServicesProxy() {
    _initMetricServicesProxy();
  }
  
  public MetricServicesProxy(String endpoint) {
    _endpoint = endpoint;
    _initMetricServicesProxy();
  }
  
  private void _initMetricServicesProxy() {
    try {
      metricServices = (new metrics.smartbin.api.MetricsAPIv24Locator()).getMetricServicesPort();
      if (metricServices != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)metricServices)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)metricServices)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (metricServices != null)
      ((javax.xml.rpc.Stub)metricServices)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public metrics.smartbin.api.MetricServices getMetricServices() {
    if (metricServices == null)
      _initMetricServicesProxy();
    return metricServices;
  }
  
  public metrics.smartbin.api.MetricsWrapper metrics(java.lang.String apiName, java.lang.String apiKey) throws java.rmi.RemoteException{
    if (metricServices == null)
      _initMetricServicesProxy();
    return metricServices.metrics(apiName, apiKey);
  }
  
  public metrics.smartbin.api.MetricsWrapper ping() throws java.rmi.RemoteException{
    if (metricServices == null)
      _initMetricServicesProxy();
    return metricServices.ping();
  }
  
  public metrics.smartbin.api.MetricsWrapper searchMetrics(java.lang.String apiName, java.lang.String apiKey, java.lang.String sensors) throws java.rmi.RemoteException{
    if (metricServices == null)
      _initMetricServicesProxy();
    return metricServices.searchMetrics(apiName, apiKey, sensors);
  }
  
  
}