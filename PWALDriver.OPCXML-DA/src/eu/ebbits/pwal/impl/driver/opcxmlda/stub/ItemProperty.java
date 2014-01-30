package eu.ebbits.pwal.impl.driver.opcxmlda.stub;

/**
 * Stub class used for ItemProperty
 * <p> 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 * 
 * 
 * @author     ISMB
 * @version    %I%, %G%
 * @since      PWAL 0.2.0
 */
public class ItemProperty extends ADBBeanImplementation {
    /* This type was generated from the piece of schema that had
         name = ItemProperty
         Namespace URI = http://opcfoundation.org/webservices/XMLDA/1.0/
         Namespace Prefix = ns1
     */

    /**
     * 
     */
    private static final long serialVersionUID = -2532202429877583145L;


    /* static reference to the name of this class*/
    private static final String NAME = ItemProperty.class.getSimpleName();
    
    @Override
    protected String getClassName() {
        return this.getClass().getSimpleName();
    }
    
    
    //===========literal constant===========
    private static final String DESCRIPTION = "Description";
    
    
    /**
     * field for ItemPath
     * This was an Attribute!
     */
    private String localItemPath;
    
    
    /**
     * field for ItemName
     * This was an Attribute!
     */
    private String localItemName;
    
    
    /**
     * field for Value
     */
    private org.apache.axiom.om.OMElement localValue ;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    private boolean localValueTracker = false ;


    /**
     * Auto generated getter method
     * @return org.apache.axiom.om.OMElement
     */
    public  org.apache.axiom.om.OMElement getValue() {
        return localValue;
    }


    /**
     * field for Name
     * This was an Attribute!
     */
    private javax.xml.namespace.QName localName ;



    /**
     * field for Description
     * This was an Attribute!
     */
    private String localDescription ;


    /**
     * Auto generated getter method
     * @return String
     */
    public  String getDescription() {
        return localDescription;
    }



    /**
     * Auto generated setter method
     * @param param Description
     */
    public void setDescription(String param) {
        this.localDescription=param;
    }


    /**
     * field for ResultID
     * This was an Attribute!
     */
    private javax.xml.namespace.QName localResultID ;


    @Override
    protected void writeAttributes(String prefix,String namespace, org.apache.axiom.om.OMFactory factory, org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter) 
            throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
        writeQNameAttribute("", "Name", localName, xmlWriter);
        writeAttribute("", DESCRIPTION, localDescription, xmlWriter);
        writeAttribute("", "ItemPath", localItemPath, xmlWriter);
        writeAttribute("", "ItemName", localItemName, xmlWriter);
        writeQNameAttribute("", "ResultID", localResultID, xmlWriter);
        writeAttributeWithNamespace(localValueTracker,"Value",localValue,xmlWriter);
        xmlWriter.writeEndElement();
    }



    @Override
    public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
            throws org.apache.axis2.databinding.ADBException{

        java.util.ArrayList<Object> elementList = new java.util.ArrayList<Object>();
        java.util.ArrayList<Object> attribList = new java.util.ArrayList<Object>();

        addItem(localValueTracker,localValue,"Value",elementList);
        attribList.add(new javax.xml.namespace.QName("","Name"));
        attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localName));
        attribList.add(new javax.xml.namespace.QName("",DESCRIPTION));
        attribList.add(localDescription);
        attribList.add(new javax.xml.namespace.QName("","ItemPath"));
        attribList.add(localItemPath);
        attribList.add(new javax.xml.namespace.QName("","ItemName"));
        attribList.add(localItemName);
        attribList.add(new javax.xml.namespace.QName("","ResultID"));
        attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localResultID));
        return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(), attribList.toArray());
    }



    /**
     *  Factory class that keeps the parse method
     */
    public static class Factory  {

        /**
         * static method to create the object
         * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
         *             If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
         * Postcondition: If this object is an element, the reader is positioned at its end element
         *             If this object is a complex type, the reader is positioned at the end element of its outer element
         *
         * @param reader - the <code>javax.xml.stream.XMLStreamReader</code> to use to parse the xml
         * 
         * @return the <code>ItemProperty</code> parsed
         * 
         * @throws Exception - if something goes wrong parsing the xml
         *
         */
        public static ItemProperty parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            ItemProperty object = new ItemProperty();
            try {
                GenericFactory.goToNextStartElement(reader);
                ItemProperty result = (ItemProperty) checkADBBeanType(NAME,reader); 
                if(result!=null) {
                    return result;
                }
                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.List<String> handledAttributes = new java.util.ArrayList<String>();
                // handle attribute "Name"
                GenericFactory.handleName(reader.getAttributeValue(null,"Name"),object,handledAttributes);
                // handle attribute "Description"
                String tempAttribDescription = reader.getAttributeValue(null,DESCRIPTION);
                if (tempAttribDescription!=null) {
                    object.setDescription(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(tempAttribDescription));
                }
                handledAttributes.add(DESCRIPTION);
                // handle attribute "ItemPath"
                GenericFactory.handleItemPath(reader.getAttributeValue(null,"ItemPath"),object,handledAttributes);
                // handle attribute "ItemName"
                GenericFactory.handleItemName(reader.getAttributeValue(null,"ItemName"),object,handledAttributes);
                // handle attribute "ResultID"
                GenericFactory.handleResultID(reader.getAttributeValue(null,"ResultID"),object,handledAttributes,reader);
                reader.next();
                GenericFactory.goToNextStartElement(reader);
                if (reader.isStartElement()) {
                    object.setValue(GenericFactory.createOMElement(reader));
                    reader.next();
                }  // End of if for expected property start element
                GenericFactory.checkUnexpectedStartElements(reader);
            } catch (javax.xml.stream.XMLStreamException e) {
                throw new java.lang.Exception(e);
            }
            return object;
        }
    }//end of factory class
}
