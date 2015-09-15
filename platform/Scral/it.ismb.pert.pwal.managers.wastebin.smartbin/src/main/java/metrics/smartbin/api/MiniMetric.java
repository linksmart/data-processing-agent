/**
 * MiniMetric.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package metrics.smartbin.api;

public class MiniMetric  extends metrics.smartbin.api.WebPojo  implements java.io.Serializable {
    private java.lang.String address3;

    private java.lang.String address4;

    private int adjustedPercentage;

    private java.lang.String containerName;

    private int days;

    private int distanceToTarget;

    private java.lang.String event;

    private java.lang.String lastServiced;

    private int rawPercentage;

    private int sensorID;

    private java.lang.String serial;

    private double temperature;

    public MiniMetric() {
    }

    public MiniMetric(
           java.lang.String address3,
           java.lang.String address4,
           int adjustedPercentage,
           java.lang.String containerName,
           int days,
           int distanceToTarget,
           java.lang.String event,
           java.lang.String lastServiced,
           int rawPercentage,
           int sensorID,
           java.lang.String serial,
           double temperature) {
        this.address3 = address3;
        this.address4 = address4;
        this.adjustedPercentage = adjustedPercentage;
        this.containerName = containerName;
        this.days = days;
        this.distanceToTarget = distanceToTarget;
        this.event = event;
        this.lastServiced = lastServiced;
        this.rawPercentage = rawPercentage;
        this.sensorID = sensorID;
        this.serial = serial;
        this.temperature = temperature;
    }


    /**
     * Gets the address3 value for this MiniMetric.
     * 
     * @return address3
     */
    public java.lang.String getAddress3() {
        return address3;
    }


    /**
     * Sets the address3 value for this MiniMetric.
     * 
     * @param address3
     */
    public void setAddress3(java.lang.String address3) {
        this.address3 = address3;
    }


    /**
     * Gets the address4 value for this MiniMetric.
     * 
     * @return address4
     */
    public java.lang.String getAddress4() {
        return address4;
    }


    /**
     * Sets the address4 value for this MiniMetric.
     * 
     * @param address4
     */
    public void setAddress4(java.lang.String address4) {
        this.address4 = address4;
    }


    /**
     * Gets the adjustedPercentage value for this MiniMetric.
     * 
     * @return adjustedPercentage
     */
    public int getAdjustedPercentage() {
        return adjustedPercentage;
    }


    /**
     * Sets the adjustedPercentage value for this MiniMetric.
     * 
     * @param adjustedPercentage
     */
    public void setAdjustedPercentage(int adjustedPercentage) {
        this.adjustedPercentage = adjustedPercentage;
    }


    /**
     * Gets the containerName value for this MiniMetric.
     * 
     * @return containerName
     */
    public java.lang.String getContainerName() {
        return containerName;
    }


    /**
     * Sets the containerName value for this MiniMetric.
     * 
     * @param containerName
     */
    public void setContainerName(java.lang.String containerName) {
        this.containerName = containerName;
    }


    /**
     * Gets the days value for this MiniMetric.
     * 
     * @return days
     */
    public int getDays() {
        return days;
    }


    /**
     * Sets the days value for this MiniMetric.
     * 
     * @param days
     */
    public void setDays(int days) {
        this.days = days;
    }


    /**
     * Gets the distanceToTarget value for this MiniMetric.
     * 
     * @return distanceToTarget
     */
    public int getDistanceToTarget() {
        return distanceToTarget;
    }


    /**
     * Sets the distanceToTarget value for this MiniMetric.
     * 
     * @param distanceToTarget
     */
    public void setDistanceToTarget(int distanceToTarget) {
        this.distanceToTarget = distanceToTarget;
    }


    /**
     * Gets the event value for this MiniMetric.
     * 
     * @return event
     */
    public java.lang.String getEvent() {
        return event;
    }


    /**
     * Sets the event value for this MiniMetric.
     * 
     * @param event
     */
    public void setEvent(java.lang.String event) {
        this.event = event;
    }


    /**
     * Gets the lastServiced value for this MiniMetric.
     * 
     * @return lastServiced
     */
    public java.lang.String getLastServiced() {
        return lastServiced;
    }


    /**
     * Sets the lastServiced value for this MiniMetric.
     * 
     * @param lastServiced
     */
    public void setLastServiced(java.lang.String lastServiced) {
        this.lastServiced = lastServiced;
    }


    /**
     * Gets the rawPercentage value for this MiniMetric.
     * 
     * @return rawPercentage
     */
    public int getRawPercentage() {
        return rawPercentage;
    }


    /**
     * Sets the rawPercentage value for this MiniMetric.
     * 
     * @param rawPercentage
     */
    public void setRawPercentage(int rawPercentage) {
        this.rawPercentage = rawPercentage;
    }


    /**
     * Gets the sensorID value for this MiniMetric.
     * 
     * @return sensorID
     */
    public int getSensorID() {
        return sensorID;
    }


    /**
     * Sets the sensorID value for this MiniMetric.
     * 
     * @param sensorID
     */
    public void setSensorID(int sensorID) {
        this.sensorID = sensorID;
    }


    /**
     * Gets the serial value for this MiniMetric.
     * 
     * @return serial
     */
    public java.lang.String getSerial() {
        return serial;
    }


    /**
     * Sets the serial value for this MiniMetric.
     * 
     * @param serial
     */
    public void setSerial(java.lang.String serial) {
        this.serial = serial;
    }


    /**
     * Gets the temperature value for this MiniMetric.
     * 
     * @return temperature
     */
    public double getTemperature() {
        return temperature;
    }


    /**
     * Sets the temperature value for this MiniMetric.
     * 
     * @param temperature
     */
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MiniMetric)) return false;
        MiniMetric other = (MiniMetric) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.address3==null && other.getAddress3()==null) || 
             (this.address3!=null &&
              this.address3.equals(other.getAddress3()))) &&
            ((this.address4==null && other.getAddress4()==null) || 
             (this.address4!=null &&
              this.address4.equals(other.getAddress4()))) &&
            this.adjustedPercentage == other.getAdjustedPercentage() &&
            ((this.containerName==null && other.getContainerName()==null) || 
             (this.containerName!=null &&
              this.containerName.equals(other.getContainerName()))) &&
            this.days == other.getDays() &&
            this.distanceToTarget == other.getDistanceToTarget() &&
            ((this.event==null && other.getEvent()==null) || 
             (this.event!=null &&
              this.event.equals(other.getEvent()))) &&
            ((this.lastServiced==null && other.getLastServiced()==null) || 
             (this.lastServiced!=null &&
              this.lastServiced.equals(other.getLastServiced()))) &&
            this.rawPercentage == other.getRawPercentage() &&
            this.sensorID == other.getSensorID() &&
            ((this.serial==null && other.getSerial()==null) || 
             (this.serial!=null &&
              this.serial.equals(other.getSerial()))) &&
            this.temperature == other.getTemperature();
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
        if (getAddress3() != null) {
            _hashCode += getAddress3().hashCode();
        }
        if (getAddress4() != null) {
            _hashCode += getAddress4().hashCode();
        }
        _hashCode += getAdjustedPercentage();
        if (getContainerName() != null) {
            _hashCode += getContainerName().hashCode();
        }
        _hashCode += getDays();
        _hashCode += getDistanceToTarget();
        if (getEvent() != null) {
            _hashCode += getEvent().hashCode();
        }
        if (getLastServiced() != null) {
            _hashCode += getLastServiced().hashCode();
        }
        _hashCode += getRawPercentage();
        _hashCode += getSensorID();
        if (getSerial() != null) {
            _hashCode += getSerial().hashCode();
        }
        _hashCode += new Double(getTemperature()).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MiniMetric.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://api.smartbin.metrics/", "miniMetric"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("address3");
        elemField.setXmlName(new javax.xml.namespace.QName("", "address3"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("address4");
        elemField.setXmlName(new javax.xml.namespace.QName("", "address4"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("adjustedPercentage");
        elemField.setXmlName(new javax.xml.namespace.QName("", "adjustedPercentage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("containerName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "containerName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("days");
        elemField.setXmlName(new javax.xml.namespace.QName("", "days"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("distanceToTarget");
        elemField.setXmlName(new javax.xml.namespace.QName("", "distanceToTarget"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("event");
        elemField.setXmlName(new javax.xml.namespace.QName("", "event"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastServiced");
        elemField.setXmlName(new javax.xml.namespace.QName("", "lastServiced"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rawPercentage");
        elemField.setXmlName(new javax.xml.namespace.QName("", "rawPercentage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sensorID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "sensorID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serial");
        elemField.setXmlName(new javax.xml.namespace.QName("", "serial"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("temperature");
        elemField.setXmlName(new javax.xml.namespace.QName("", "temperature"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
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
