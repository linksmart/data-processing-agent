/**
 * SearchMetrics.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package metrics.smartbin.api;

public class SearchMetrics  implements java.io.Serializable {
    private java.lang.String apiName;

    private java.lang.String apiKey;

    private java.lang.String sensors;

    public SearchMetrics() {
    }

    public SearchMetrics(
           java.lang.String apiName,
           java.lang.String apiKey,
           java.lang.String sensors) {
           this.apiName = apiName;
           this.apiKey = apiKey;
           this.sensors = sensors;
    }


    /**
     * Gets the apiName value for this SearchMetrics.
     * 
     * @return apiName
     */
    public java.lang.String getApiName() {
        return apiName;
    }


    /**
     * Sets the apiName value for this SearchMetrics.
     * 
     * @param apiName
     */
    public void setApiName(java.lang.String apiName) {
        this.apiName = apiName;
    }


    /**
     * Gets the apiKey value for this SearchMetrics.
     * 
     * @return apiKey
     */
    public java.lang.String getApiKey() {
        return apiKey;
    }


    /**
     * Sets the apiKey value for this SearchMetrics.
     * 
     * @param apiKey
     */
    public void setApiKey(java.lang.String apiKey) {
        this.apiKey = apiKey;
    }


    /**
     * Gets the sensors value for this SearchMetrics.
     * 
     * @return sensors
     */
    public java.lang.String getSensors() {
        return sensors;
    }


    /**
     * Sets the sensors value for this SearchMetrics.
     * 
     * @param sensors
     */
    public void setSensors(java.lang.String sensors) {
        this.sensors = sensors;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SearchMetrics)) return false;
        SearchMetrics other = (SearchMetrics) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.apiName==null && other.getApiName()==null) || 
             (this.apiName!=null &&
              this.apiName.equals(other.getApiName()))) &&
            ((this.apiKey==null && other.getApiKey()==null) || 
             (this.apiKey!=null &&
              this.apiKey.equals(other.getApiKey()))) &&
            ((this.sensors==null && other.getSensors()==null) || 
             (this.sensors!=null &&
              this.sensors.equals(other.getSensors())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getApiName() != null) {
            _hashCode += getApiName().hashCode();
        }
        if (getApiKey() != null) {
            _hashCode += getApiKey().hashCode();
        }
        if (getSensors() != null) {
            _hashCode += getSensors().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SearchMetrics.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://api.smartbin.metrics/", "searchMetrics"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("apiName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "apiName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("apiKey");
        elemField.setXmlName(new javax.xml.namespace.QName("", "apiKey"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sensors");
        elemField.setXmlName(new javax.xml.namespace.QName("", "sensors"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
