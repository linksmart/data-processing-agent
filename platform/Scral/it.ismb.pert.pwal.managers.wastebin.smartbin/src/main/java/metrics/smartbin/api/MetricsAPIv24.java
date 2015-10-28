/**
 * MetricsAPIv24.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package metrics.smartbin.api;

public interface MetricsAPIv24 extends javax.xml.rpc.Service {
    public java.lang.String getMetricServicesPortAddress();

    public metrics.smartbin.api.MetricServices getMetricServicesPort() throws javax.xml.rpc.ServiceException;

    public metrics.smartbin.api.MetricServices getMetricServicesPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
