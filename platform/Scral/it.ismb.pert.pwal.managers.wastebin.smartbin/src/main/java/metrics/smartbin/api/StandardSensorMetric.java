/**
 * StandardSensorMetric.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package metrics.smartbin.api;

public class StandardSensorMetric  extends metrics.smartbin.api.WebPojo  implements java.io.Serializable {
    private java.lang.String address1;

    private java.lang.String address2;

    private java.lang.String address3;

    private java.lang.String address4;

    private double battery;

    private java.lang.String containerType;

    private java.lang.String community;

    private int communityID;

    private java.lang.String containerName;

    private int days;

    private int distanceToTarget;

    private java.lang.String event;

    private int percent;

    private int id;

    private java.lang.String lastServiced;

    private java.lang.String latitude;

    private java.lang.String longitude;

    private double mobileSignal;

    private java.lang.String timestamp;

    private int sensorID;

    private java.lang.String serial;

    private java.lang.String site;

    private int siteID;

    private double temperature;

    public StandardSensorMetric() {
    }

    public StandardSensorMetric(
           java.lang.String address1,
           java.lang.String address2,
           java.lang.String address3,
           java.lang.String address4,
           double battery,
           java.lang.String containerType,
           java.lang.String community,
           int communityID,
           java.lang.String containerName,
           int days,
           int distanceToTarget,
           java.lang.String event,
           int percent,
           int id,
           java.lang.String lastServiced,
           java.lang.String latitude,
           java.lang.String longitude,
           double mobileSignal,
           java.lang.String timestamp,
           int sensorID,
           java.lang.String serial,
           java.lang.String site,
           int siteID,
           double temperature) {
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
        this.address4 = address4;
        this.battery = battery;
        this.containerType = containerType;
        this.community = community;
        this.communityID = communityID;
        this.containerName = containerName;
        this.days = days;
        this.distanceToTarget = distanceToTarget;
        this.event = event;
        this.percent = percent;
        this.id = id;
        this.lastServiced = lastServiced;
        this.latitude = latitude;
        this.longitude = longitude;
        this.mobileSignal = mobileSignal;
        this.timestamp = timestamp;
        this.sensorID = sensorID;
        this.serial = serial;
        this.site = site;
        this.siteID = siteID;
        this.temperature = temperature;
    }


    /**
     * Gets the address1 value for this StandardSensorMetric.
     * 
     * @return address1
     */
    public java.lang.String getAddress1() {
        return address1;
    }


    /**
     * Sets the address1 value for this StandardSensorMetric.
     * 
     * @param address1
     */
    public void setAddress1(java.lang.String address1) {
        this.address1 = address1;
    }


    /**
     * Gets the address2 value for this StandardSensorMetric.
     * 
     * @return address2
     */
    public java.lang.String getAddress2() {
        return address2;
    }


    /**
     * Sets the address2 value for this StandardSensorMetric.
     * 
     * @param address2
     */
    public void setAddress2(java.lang.String address2) {
        this.address2 = address2;
    }


    /**
     * Gets the address3 value for this StandardSensorMetric.
     * 
     * @return address3
     */
    public java.lang.String getAddress3() {
        return address3;
    }


    /**
     * Sets the address3 value for this StandardSensorMetric.
     * 
     * @param address3
     */
    public void setAddress3(java.lang.String address3) {
        this.address3 = address3;
    }


    /**
     * Gets the address4 value for this StandardSensorMetric.
     * 
     * @return address4
     */
    public java.lang.String getAddress4() {
        return address4;
    }


    /**
     * Sets the address4 value for this StandardSensorMetric.
     * 
     * @param address4
     */
    public void setAddress4(java.lang.String address4) {
        this.address4 = address4;
    }


    /**
     * Gets the battery value for this StandardSensorMetric.
     * 
     * @return battery
     */
    public double getBattery() {
        return battery;
    }


    /**
     * Sets the battery value for this StandardSensorMetric.
     * 
     * @param battery
     */
    public void setBattery(double battery) {
        this.battery = battery;
    }


    /**
     * Gets the containerType value for this StandardSensorMetric.
     * 
     * @return containerType
     */
    public java.lang.String getContainerType() {
        return containerType;
    }


    /**
     * Sets the containerType value for this StandardSensorMetric.
     * 
     * @param containerType
     */
    public void setContainerType(java.lang.String containerType) {
        this.containerType = containerType;
    }


    /**
     * Gets the community value for this StandardSensorMetric.
     * 
     * @return community
     */
    public java.lang.String getCommunity() {
        return community;
    }


    /**
     * Sets the community value for this StandardSensorMetric.
     * 
     * @param community
     */
    public void setCommunity(java.lang.String community) {
        this.community = community;
    }


    /**
     * Gets the communityID value for this StandardSensorMetric.
     * 
     * @return communityID
     */
    public int getCommunityID() {
        return communityID;
    }


    /**
     * Sets the communityID value for this StandardSensorMetric.
     * 
     * @param communityID
     */
    public void setCommunityID(int communityID) {
        this.communityID = communityID;
    }


    /**
     * Gets the containerName value for this StandardSensorMetric.
     * 
     * @return containerName
     */
    public java.lang.String getContainerName() {
        return containerName;
    }


    /**
     * Sets the containerName value for this StandardSensorMetric.
     * 
     * @param containerName
     */
    public void setContainerName(java.lang.String containerName) {
        this.containerName = containerName;
    }


    /**
     * Gets the days value for this StandardSensorMetric.
     * 
     * @return days
     */
    public int getDays() {
        return days;
    }


    /**
     * Sets the days value for this StandardSensorMetric.
     * 
     * @param days
     */
    public void setDays(int days) {
        this.days = days;
    }


    /**
     * Gets the distanceToTarget value for this StandardSensorMetric.
     * 
     * @return distanceToTarget
     */
    public int getDistanceToTarget() {
        return distanceToTarget;
    }


    /**
     * Sets the distanceToTarget value for this StandardSensorMetric.
     * 
     * @param distanceToTarget
     */
    public void setDistanceToTarget(int distanceToTarget) {
        this.distanceToTarget = distanceToTarget;
    }


    /**
     * Gets the event value for this StandardSensorMetric.
     * 
     * @return event
     */
    public java.lang.String getEvent() {
        return event;
    }


    /**
     * Sets the event value for this StandardSensorMetric.
     * 
     * @param event
     */
    public void setEvent(java.lang.String event) {
        this.event = event;
    }


    /**
     * Gets the percent value for this StandardSensorMetric.
     * 
     * @return percent
     */
    public int getPercent() {
        return percent;
    }


    /**
     * Sets the percent value for this StandardSensorMetric.
     * 
     * @param percent
     */
    public void setPercent(int percent) {
        this.percent = percent;
    }


    /**
     * Gets the id value for this StandardSensorMetric.
     * 
     * @return id
     */
    public int getId() {
        return id;
    }


    /**
     * Sets the id value for this StandardSensorMetric.
     * 
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }


    /**
     * Gets the lastServiced value for this StandardSensorMetric.
     * 
     * @return lastServiced
     */
    public java.lang.String getLastServiced() {
        return lastServiced;
    }


    /**
     * Sets the lastServiced value for this StandardSensorMetric.
     * 
     * @param lastServiced
     */
    public void setLastServiced(java.lang.String lastServiced) {
        this.lastServiced = lastServiced;
    }


    /**
     * Gets the latitude value for this StandardSensorMetric.
     * 
     * @return latitude
     */
    public java.lang.String getLatitude() {
        return latitude;
    }


    /**
     * Sets the latitude value for this StandardSensorMetric.
     * 
     * @param latitude
     */
    public void setLatitude(java.lang.String latitude) {
        this.latitude = latitude;
    }


    /**
     * Gets the longitude value for this StandardSensorMetric.
     * 
     * @return longitude
     */
    public java.lang.String getLongitude() {
        return longitude;
    }


    /**
     * Sets the longitude value for this StandardSensorMetric.
     * 
     * @param longitude
     */
    public void setLongitude(java.lang.String longitude) {
        this.longitude = longitude;
    }


    /**
     * Gets the mobileSignal value for this StandardSensorMetric.
     * 
     * @return mobileSignal
     */
    public double getMobileSignal() {
        return mobileSignal;
    }


    /**
     * Sets the mobileSignal value for this StandardSensorMetric.
     * 
     * @param mobileSignal
     */
    public void setMobileSignal(double mobileSignal) {
        this.mobileSignal = mobileSignal;
    }


    /**
     * Gets the timestamp value for this StandardSensorMetric.
     * 
     * @return timestamp
     */
    public java.lang.String getTimestamp() {
        return timestamp;
    }


    /**
     * Sets the timestamp value for this StandardSensorMetric.
     * 
     * @param timestamp
     */
    public void setTimestamp(java.lang.String timestamp) {
        this.timestamp = timestamp;
    }


    /**
     * Gets the sensorID value for this StandardSensorMetric.
     * 
     * @return sensorID
     */
    public int getSensorID() {
        return sensorID;
    }


    /**
     * Sets the sensorID value for this StandardSensorMetric.
     * 
     * @param sensorID
     */
    public void setSensorID(int sensorID) {
        this.sensorID = sensorID;
    }


    /**
     * Gets the serial value for this StandardSensorMetric.
     * 
     * @return serial
     */
    public java.lang.String getSerial() {
        return serial;
    }


    /**
     * Sets the serial value for this StandardSensorMetric.
     * 
     * @param serial
     */
    public void setSerial(java.lang.String serial) {
        this.serial = serial;
    }


    /**
     * Gets the site value for this StandardSensorMetric.
     * 
     * @return site
     */
    public java.lang.String getSite() {
        return site;
    }


    /**
     * Sets the site value for this StandardSensorMetric.
     * 
     * @param site
     */
    public void setSite(java.lang.String site) {
        this.site = site;
    }


    /**
     * Gets the siteID value for this StandardSensorMetric.
     * 
     * @return siteID
     */
    public int getSiteID() {
        return siteID;
    }


    /**
     * Sets the siteID value for this StandardSensorMetric.
     * 
     * @param siteID
     */
    public void setSiteID(int siteID) {
        this.siteID = siteID;
    }


    /**
     * Gets the temperature value for this StandardSensorMetric.
     * 
     * @return temperature
     */
    public double getTemperature() {
        return temperature;
    }


    /**
     * Sets the temperature value for this StandardSensorMetric.
     * 
     * @param temperature
     */
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof StandardSensorMetric)) return false;
        StandardSensorMetric other = (StandardSensorMetric) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.address1==null && other.getAddress1()==null) || 
             (this.address1!=null &&
              this.address1.equals(other.getAddress1()))) &&
            ((this.address2==null && other.getAddress2()==null) || 
             (this.address2!=null &&
              this.address2.equals(other.getAddress2()))) &&
            ((this.address3==null && other.getAddress3()==null) || 
             (this.address3!=null &&
              this.address3.equals(other.getAddress3()))) &&
            ((this.address4==null && other.getAddress4()==null) || 
             (this.address4!=null &&
              this.address4.equals(other.getAddress4()))) &&
            this.battery == other.getBattery() &&
            ((this.containerType==null && other.getContainerType()==null) || 
             (this.containerType!=null &&
              this.containerType.equals(other.getContainerType()))) &&
            ((this.community==null && other.getCommunity()==null) || 
             (this.community!=null &&
              this.community.equals(other.getCommunity()))) &&
            this.communityID == other.getCommunityID() &&
            ((this.containerName==null && other.getContainerName()==null) || 
             (this.containerName!=null &&
              this.containerName.equals(other.getContainerName()))) &&
            this.days == other.getDays() &&
            this.distanceToTarget == other.getDistanceToTarget() &&
            ((this.event==null && other.getEvent()==null) || 
             (this.event!=null &&
              this.event.equals(other.getEvent()))) &&
            this.percent == other.getPercent() &&
            this.id == other.getId() &&
            ((this.lastServiced==null && other.getLastServiced()==null) || 
             (this.lastServiced!=null &&
              this.lastServiced.equals(other.getLastServiced()))) &&
            ((this.latitude==null && other.getLatitude()==null) || 
             (this.latitude!=null &&
              this.latitude.equals(other.getLatitude()))) &&
            ((this.longitude==null && other.getLongitude()==null) || 
             (this.longitude!=null &&
              this.longitude.equals(other.getLongitude()))) &&
            this.mobileSignal == other.getMobileSignal() &&
            ((this.timestamp==null && other.getTimestamp()==null) || 
             (this.timestamp!=null &&
              this.timestamp.equals(other.getTimestamp()))) &&
            this.sensorID == other.getSensorID() &&
            ((this.serial==null && other.getSerial()==null) || 
             (this.serial!=null &&
              this.serial.equals(other.getSerial()))) &&
            ((this.site==null && other.getSite()==null) || 
             (this.site!=null &&
              this.site.equals(other.getSite()))) &&
            this.siteID == other.getSiteID() &&
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
        if (getAddress1() != null) {
            _hashCode += getAddress1().hashCode();
        }
        if (getAddress2() != null) {
            _hashCode += getAddress2().hashCode();
        }
        if (getAddress3() != null) {
            _hashCode += getAddress3().hashCode();
        }
        if (getAddress4() != null) {
            _hashCode += getAddress4().hashCode();
        }
        _hashCode += new Double(getBattery()).hashCode();
        if (getContainerType() != null) {
            _hashCode += getContainerType().hashCode();
        }
        if (getCommunity() != null) {
            _hashCode += getCommunity().hashCode();
        }
        _hashCode += getCommunityID();
        if (getContainerName() != null) {
            _hashCode += getContainerName().hashCode();
        }
        _hashCode += getDays();
        _hashCode += getDistanceToTarget();
        if (getEvent() != null) {
            _hashCode += getEvent().hashCode();
        }
        _hashCode += getPercent();
        _hashCode += getId();
        if (getLastServiced() != null) {
            _hashCode += getLastServiced().hashCode();
        }
        if (getLatitude() != null) {
            _hashCode += getLatitude().hashCode();
        }
        if (getLongitude() != null) {
            _hashCode += getLongitude().hashCode();
        }
        _hashCode += new Double(getMobileSignal()).hashCode();
        if (getTimestamp() != null) {
            _hashCode += getTimestamp().hashCode();
        }
        _hashCode += getSensorID();
        if (getSerial() != null) {
            _hashCode += getSerial().hashCode();
        }
        if (getSite() != null) {
            _hashCode += getSite().hashCode();
        }
        _hashCode += getSiteID();
        _hashCode += new Double(getTemperature()).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(StandardSensorMetric.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://api.smartbin.metrics/", "standardSensorMetric"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("address1");
        elemField.setXmlName(new javax.xml.namespace.QName("", "address1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("address2");
        elemField.setXmlName(new javax.xml.namespace.QName("", "address2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
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
        elemField.setFieldName("battery");
        elemField.setXmlName(new javax.xml.namespace.QName("", "battery"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("containerType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "containerType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("community");
        elemField.setXmlName(new javax.xml.namespace.QName("", "community"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("communityID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "communityID"));
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
        elemField.setFieldName("percent");
        elemField.setXmlName(new javax.xml.namespace.QName("", "percent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
        elemField.setFieldName("latitude");
        elemField.setXmlName(new javax.xml.namespace.QName("", "latitude"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("longitude");
        elemField.setXmlName(new javax.xml.namespace.QName("", "longitude"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mobileSignal");
        elemField.setXmlName(new javax.xml.namespace.QName("", "mobileSignal"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timestamp");
        elemField.setXmlName(new javax.xml.namespace.QName("", "timestamp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
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
        elemField.setFieldName("site");
        elemField.setXmlName(new javax.xml.namespace.QName("", "site"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("siteID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "siteID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
