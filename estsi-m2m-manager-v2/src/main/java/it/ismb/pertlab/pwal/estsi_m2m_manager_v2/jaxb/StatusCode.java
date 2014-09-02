//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.07.30 at 04:50:17 PM CEST 
//


package it.ismb.pertlab.pwal.estsi_m2m_manager_v2.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StatusCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="StatusCode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="STATUS_OK"/>
 *     &lt;enumeration value="STATUS_ACCEPTED"/>
 *     &lt;enumeration value="STATUS_BAD_REQUEST"/>
 *     &lt;enumeration value="STATUS_PERMISSION_DENIED"/>
 *     &lt;enumeration value="STATUS_FORBIDDEN"/>
 *     &lt;enumeration value="STATUS_NOT_FOUND"/>
 *     &lt;enumeration value="STATUS_METHOD_NOT_ALLOWED"/>
 *     &lt;enumeration value="STATUS_NOT_ACCEPTABLE"/>
 *     &lt;enumeration value="STATUS_REQUEST_TIMEOUT"/>
 *     &lt;enumeration value="STATUS_CONFLICT"/>
 *     &lt;enumeration value="STATUS_UNSUPPORTED_MEDIA_TYPE"/>
 *     &lt;enumeration value="STATUS_INTERNAL_SERVER_ERROR"/>
 *     &lt;enumeration value="STATUS_NOT_IMPLEMENTED"/>
 *     &lt;enumeration value="STATUS_BAD_GATEWAY"/>
 *     &lt;enumeration value="STATUS_SERVICE_UNAVAILABLE"/>
 *     &lt;enumeration value="STATUS_GATEWAY_TIMEOUT"/>
 *     &lt;enumeration value="STATUS_DELETED"/>
 *     &lt;enumeration value="STATUS_EXPIRED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "StatusCode")
@XmlEnum
public enum StatusCode {

    STATUS_OK,
    STATUS_ACCEPTED,
    STATUS_BAD_REQUEST,
    STATUS_PERMISSION_DENIED,
    STATUS_FORBIDDEN,
    STATUS_NOT_FOUND,
    STATUS_METHOD_NOT_ALLOWED,
    STATUS_NOT_ACCEPTABLE,
    STATUS_REQUEST_TIMEOUT,
    STATUS_CONFLICT,
    STATUS_UNSUPPORTED_MEDIA_TYPE,
    STATUS_INTERNAL_SERVER_ERROR,
    STATUS_NOT_IMPLEMENTED,
    STATUS_BAD_GATEWAY,
    STATUS_SERVICE_UNAVAILABLE,
    STATUS_GATEWAY_TIMEOUT,
    STATUS_DELETED,
    STATUS_EXPIRED;

    public String value() {
        return name();
    }

    public static StatusCode fromValue(String v) {
        return valueOf(v);
    }

}
