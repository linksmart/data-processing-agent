/**
 * MetricServices.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package metrics.smartbin.api;

public interface MetricServices extends java.rmi.Remote {
    public metrics.smartbin.api.MetricsWrapper metrics(java.lang.String apiName, java.lang.String apiKey) throws java.rmi.RemoteException;
    public metrics.smartbin.api.MetricsWrapper ping() throws java.rmi.RemoteException;
    public metrics.smartbin.api.MetricsWrapper searchMetrics(java.lang.String apiName, java.lang.String apiKey, java.lang.String sensors) throws java.rmi.RemoteException;
}
