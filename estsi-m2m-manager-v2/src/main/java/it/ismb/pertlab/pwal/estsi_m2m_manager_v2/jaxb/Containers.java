//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.07.21 at 11:00:05 AM CEST 
//


package it.ismb.pertlab.pwal.estsi_m2m_manager_v2.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for Containers complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Containers">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://uri.etsi.org/m2m}accessRightID" minOccurs="0"/>
 *         &lt;element ref="{http://uri.etsi.org/m2m}creationTime" minOccurs="0"/>
 *         &lt;element ref="{http://uri.etsi.org/m2m}lastModifiedTime" minOccurs="0"/>
 *         &lt;element ref="{http://uri.etsi.org/m2m}containerCollection" minOccurs="0"/>
 *         &lt;element ref="{http://uri.etsi.org/m2m}containerAnncCollection" minOccurs="0"/>
 *         &lt;element ref="{http://uri.etsi.org/m2m}locationContainerCollection" minOccurs="0"/>
 *         &lt;element ref="{http://uri.etsi.org/m2m}locationContainerAnncCollection" minOccurs="0"/>
 *         &lt;element ref="{http://uri.etsi.org/m2m}subscriptionsReference" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Containers", propOrder = {
    "accessRightID",
    "creationTime",
    "lastModifiedTime",
    "containerCollection",
    "containerAnncCollection",
    "locationContainerCollection",
    "locationContainerAnncCollection",
    "subscriptionsReference"
})
public class Containers {

    @XmlSchemaType(name = "anyURI")
    protected String accessRightID;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar creationTime;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastModifiedTime;
    protected NamedReferenceCollection containerCollection;
    protected NamedReferenceCollection containerAnncCollection;
    protected NamedReferenceCollection locationContainerCollection;
    protected NamedReferenceCollection locationContainerAnncCollection;
    @XmlSchemaType(name = "anyURI")
    protected String subscriptionsReference;

    /**
     * Gets the value of the accessRightID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccessRightID() {
        return accessRightID;
    }

    /**
     * Sets the value of the accessRightID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccessRightID(String value) {
        this.accessRightID = value;
    }

    /**
     * Gets the value of the creationTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreationTime() {
        return creationTime;
    }

    /**
     * Sets the value of the creationTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreationTime(XMLGregorianCalendar value) {
        this.creationTime = value;
    }

    /**
     * Gets the value of the lastModifiedTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLastModifiedTime() {
        return lastModifiedTime;
    }

    /**
     * Sets the value of the lastModifiedTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLastModifiedTime(XMLGregorianCalendar value) {
        this.lastModifiedTime = value;
    }

    /**
     * Gets the value of the containerCollection property.
     * 
     * @return
     *     possible object is
     *     {@link NamedReferenceCollection }
     *     
     */
    public NamedReferenceCollection getContainerCollection() {
        return containerCollection;
    }

    /**
     * Sets the value of the containerCollection property.
     * 
     * @param value
     *     allowed object is
     *     {@link NamedReferenceCollection }
     *     
     */
    public void setContainerCollection(NamedReferenceCollection value) {
        this.containerCollection = value;
    }

    /**
     * Gets the value of the containerAnncCollection property.
     * 
     * @return
     *     possible object is
     *     {@link NamedReferenceCollection }
     *     
     */
    public NamedReferenceCollection getContainerAnncCollection() {
        return containerAnncCollection;
    }

    /**
     * Sets the value of the containerAnncCollection property.
     * 
     * @param value
     *     allowed object is
     *     {@link NamedReferenceCollection }
     *     
     */
    public void setContainerAnncCollection(NamedReferenceCollection value) {
        this.containerAnncCollection = value;
    }

    /**
     * Gets the value of the locationContainerCollection property.
     * 
     * @return
     *     possible object is
     *     {@link NamedReferenceCollection }
     *     
     */
    public NamedReferenceCollection getLocationContainerCollection() {
        return locationContainerCollection;
    }

    /**
     * Sets the value of the locationContainerCollection property.
     * 
     * @param value
     *     allowed object is
     *     {@link NamedReferenceCollection }
     *     
     */
    public void setLocationContainerCollection(NamedReferenceCollection value) {
        this.locationContainerCollection = value;
    }

    /**
     * Gets the value of the locationContainerAnncCollection property.
     * 
     * @return
     *     possible object is
     *     {@link NamedReferenceCollection }
     *     
     */
    public NamedReferenceCollection getLocationContainerAnncCollection() {
        return locationContainerAnncCollection;
    }

    /**
     * Sets the value of the locationContainerAnncCollection property.
     * 
     * @param value
     *     allowed object is
     *     {@link NamedReferenceCollection }
     *     
     */
    public void setLocationContainerAnncCollection(NamedReferenceCollection value) {
        this.locationContainerAnncCollection = value;
    }

    /**
     * Gets the value of the subscriptionsReference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubscriptionsReference() {
        return subscriptionsReference;
    }

    /**
     * Sets the value of the subscriptionsReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubscriptionsReference(String value) {
        this.subscriptionsReference = value;
    }

}
