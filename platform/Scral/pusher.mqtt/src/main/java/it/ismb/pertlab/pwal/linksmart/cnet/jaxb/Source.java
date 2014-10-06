//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.09.25 at 09:50:43 AM CEST 
//


package it.ismb.pertlab.pwal.linksmart.cnet.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3c.dom.Element;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://linksmart.org/Event/1.0}Project" minOccurs="0"/>
 *         &lt;element ref="{http://linksmart.org/Event/1.0}Location" minOccurs="0"/>
 *         &lt;element ref="{http://linksmart.org/Event/1.0}ObjectID" minOccurs="0"/>
 *         &lt;element ref="{http://linksmart.org/Event/1.0}ProcessContext" minOccurs="0"/>
 *         &lt;any processContents='skip' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="about" use="required" type="{http://www.w3.org/1999/xhtml/datatypes/}SafeCURIEorCURIEorIRI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "project",
    "location",
    "objectID",
    "processContext",
    "any"
})
@XmlRootElement(name = "Source", namespace = "http://linksmart.org/Event/1.0")
public class Source {

    @XmlElement(name = "Project", namespace = "http://linksmart.org/Event/1.0")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String project;
    @XmlElement(name = "Location", namespace = "http://linksmart.org/Event/1.0")
    protected TypedStringType location;
    @XmlElement(name = "ObjectID", namespace = "http://linksmart.org/Event/1.0")
    protected TypedStringType objectID;
    @XmlElement(name = "ProcessContext", namespace = "http://linksmart.org/Event/1.0")
    protected TypedStringType processContext;
    @XmlAnyElement
    protected List<Element> any;
    @XmlAttribute(name = "about", required = true)
    protected String about;

    /**
     * Gets the value of the project property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProject() {
        return project;
    }

    /**
     * Sets the value of the project property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProject(String value) {
        this.project = value;
    }

    /**
     * Gets the value of the location property.
     * 
     * @return
     *     possible object is
     *     {@link TypedStringType }
     *     
     */
    public TypedStringType getLocation() {
        return location;
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypedStringType }
     *     
     */
    public void setLocation(TypedStringType value) {
        this.location = value;
    }

    /**
     * Gets the value of the objectID property.
     * 
     * @return
     *     possible object is
     *     {@link TypedStringType }
     *     
     */
    public TypedStringType getObjectID() {
        return objectID;
    }

    /**
     * Sets the value of the objectID property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypedStringType }
     *     
     */
    public void setObjectID(TypedStringType value) {
        this.objectID = value;
    }

    /**
     * Gets the value of the processContext property.
     * 
     * @return
     *     possible object is
     *     {@link TypedStringType }
     *     
     */
    public TypedStringType getProcessContext() {
        return processContext;
    }

    /**
     * Sets the value of the processContext property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypedStringType }
     *     
     */
    public void setProcessContext(TypedStringType value) {
        this.processContext = value;
    }

    /**
     * Gets the value of the any property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Element }
     * 
     * 
     */
    public List<Element> getAny() {
        if (any == null) {
            any = new ArrayList<Element>();
        }
        return this.any;
    }

    /**
     * Gets the value of the about property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAbout() {
        return about;
    }

    /**
     * Sets the value of the about property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAbout(String value) {
        this.about = value;
    }

}
