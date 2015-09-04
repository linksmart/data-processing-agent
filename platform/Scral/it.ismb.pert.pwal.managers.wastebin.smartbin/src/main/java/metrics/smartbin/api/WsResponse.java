/**
 * WsResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package metrics.smartbin.api;

public class WsResponse  extends metrics.smartbin.api.WebPojo  implements java.io.Serializable {
    private java.lang.String errorMessage;

    private int errorNumber;

    private boolean errors;

    private metrics.smartbin.api.PayloadWrapper parameters;

    public WsResponse() {
    }

    public WsResponse(
           java.lang.String errorMessage,
           int errorNumber,
           boolean errors,
           metrics.smartbin.api.PayloadWrapper parameters) {
        this.errorMessage = errorMessage;
        this.errorNumber = errorNumber;
        this.errors = errors;
        this.parameters = parameters;
    }


    /**
     * Gets the errorMessage value for this WsResponse.
     * 
     * @return errorMessage
     */
    public java.lang.String getErrorMessage() {
        return errorMessage;
    }


    /**
     * Sets the errorMessage value for this WsResponse.
     * 
     * @param errorMessage
     */
    public void setErrorMessage(java.lang.String errorMessage) {
        this.errorMessage = errorMessage;
    }


    /**
     * Gets the errorNumber value for this WsResponse.
     * 
     * @return errorNumber
     */
    public int getErrorNumber() {
        return errorNumber;
    }


    /**
     * Sets the errorNumber value for this WsResponse.
     * 
     * @param errorNumber
     */
    public void setErrorNumber(int errorNumber) {
        this.errorNumber = errorNumber;
    }


    /**
     * Gets the errors value for this WsResponse.
     * 
     * @return errors
     */
    public boolean isErrors() {
        return errors;
    }


    /**
     * Sets the errors value for this WsResponse.
     * 
     * @param errors
     */
    public void setErrors(boolean errors) {
        this.errors = errors;
    }


    /**
     * Gets the parameters value for this WsResponse.
     * 
     * @return parameters
     */
    public metrics.smartbin.api.PayloadWrapper getParameters() {
        return parameters;
    }


    /**
     * Sets the parameters value for this WsResponse.
     * 
     * @param parameters
     */
    public void setParameters(metrics.smartbin.api.PayloadWrapper parameters) {
        this.parameters = parameters;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof WsResponse)) return false;
        WsResponse other = (WsResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.errorMessage==null && other.getErrorMessage()==null) || 
             (this.errorMessage!=null &&
              this.errorMessage.equals(other.getErrorMessage()))) &&
            this.errorNumber == other.getErrorNumber() &&
            this.errors == other.isErrors() &&
            ((this.parameters==null && other.getParameters()==null) || 
             (this.parameters!=null &&
              this.parameters.equals(other.getParameters())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getErrorMessage() != null) {
            _hashCode += getErrorMessage().hashCode();
        }
        _hashCode += getErrorNumber();
        _hashCode += (isErrors() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getParameters() != null) {
            _hashCode += getParameters().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(WsResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://api.smartbin.metrics/", "wsResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errorMessage");
        elemField.setXmlName(new javax.xml.namespace.QName("", "errorMessage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errorNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "errorNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errors");
        elemField.setXmlName(new javax.xml.namespace.QName("", "errors"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("parameters");
        elemField.setXmlName(new javax.xml.namespace.QName("", "parameters"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://api.smartbin.metrics/", "payloadWrapper"));
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
