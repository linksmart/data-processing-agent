package eu.ebbits.pwal.impl.driver.opcxmlda.stub;

/**
 * Stub class for OPCQuality
 * <p> 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 * 
 * 
 * @author     ISMB
 * @version    %I%, %G%
 * @since      PWAL 0.2.0
 */ 
public class OPCQuality extends ADBBeanImplementation {
    /* This type was generated from the piece of schema that had
         name = OPCQuality
         Namespace URI = http://opcfoundation.org/webservices/XMLDA/1.0/
         Namespace Prefix = ns1
     */
    
    /* static reference to the name of this class*/
    private static final String NAME = OPCQuality.class.getSimpleName();

    @Override
    protected String getClassName() {
        return this.getClass().getSimpleName();
    }

    
    //===========literal constants===========
    private static final String QUALITY_FIELD = "QualityField";
    
    private static final String LIMIT_FIELD = "LimitField";
    
    private static final String VENDOR_FIELD = "VendorField";
    
    /**
     * field for QualityField
     * This was an Attribute!
     */
    private QualityBits localQualityField ;


    /**
     * Returns the OPC quality
     * 
     * @return   a <code>QualityBits</code> object
     */
    public  QualityBits getQualityField() {
        return localQualityField;
    }



    /**
     * Sets the OPC quality
     * 
     * @param param - OPC quality to be set as <code>QualityBits</code> 
     */
    public void setQualityField(QualityBits param) {
        this.localQualityField=param;
    }


    /**
     * field for LimitField
     * This was an Attribute!
     */
    private LimitBits localLimitField ;


    /**
     * Returns the limit of this object 
     * 
     * @return   limit of this object as <code>LimitBits</code>
     */
    public  LimitBits getLimitField() {
        return localLimitField;
    }



    /**
     * Sets the limit of this object
     * 
     * @param param - limit to set as <code>LimitBits</code>
     */
    public void setLimitField(LimitBits param) {
        this.localLimitField=param;
    }


    /**
     * field for VendorField
     * This was an Attribute!
     */
    private org.apache.axis2.databinding.types.UnsignedByte localVendorField =
            org.apache.axis2.databinding.utils.ConverterUtil.convertToUnsignedByte("0");


    /**
     * Returns the vendor 
     * 
     * @return   the vendor as <code>org.apache.axis2.databinding.types.UnsignedByte</code>
     */
    public org.apache.axis2.databinding.types.UnsignedByte getVendorField() {
        return localVendorField;
    }



    /**
     * Sets the vendor
     * 
     * @param param - vendor to be set as <code>org.apache.axis2.databinding.types.UnsignedByte</code>
     */
    public void setVendorField(org.apache.axis2.databinding.types.UnsignedByte param) {
        this.localVendorField=param;
    }


    @Override
    protected void writeAttributes(String prefix,String namespace, org.apache.axiom.om.OMFactory factory, org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter) 
            throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
        if (localQualityField != null) {
            writeAttribute("",
                    QUALITY_FIELD,
                    localQualityField.toString(), xmlWriter);
        }
        if (localLimitField != null) {
            writeAttribute("",
                    LIMIT_FIELD,
                    localLimitField.toString(), xmlWriter);
        }
        if (localVendorField != null) {
            writeAttribute("",
                    VENDOR_FIELD,
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localVendorField), xmlWriter);
        }
        xmlWriter.writeEndElement();
    }


    @Override
    public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
            throws org.apache.axis2.databinding.ADBException{

        java.util.ArrayList<Object> elementList = new java.util.ArrayList<Object>();
        java.util.ArrayList<Object> attribList = new java.util.ArrayList<Object>();

        attribList.add(new javax.xml.namespace.QName("",QUALITY_FIELD));
        attribList.add(localQualityField.toString());
        attribList.add(new javax.xml.namespace.QName("",LIMIT_FIELD));
        attribList.add(localLimitField.toString());
        attribList.add(new javax.xml.namespace.QName("",VENDOR_FIELD));
        attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localVendorField));
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
         * @return the <code>OPCQuality</code> parsed
         * 
         * @throws Exception - if something goes wrong parsing the xml
         *
         */
        public static OPCQuality parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            OPCQuality object = new OPCQuality();
            try {
                GenericFactory.goToNextStartElement(reader);
                OPCQuality result = (OPCQuality) checkADBBeanType(NAME,reader); 
                if(result!=null) {
                    return result;
                }
                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.List<String> handledAttributes = new java.util.ArrayList<String>();
                // handle attribute "QualityField"
                String tempAttribQualityField = reader.getAttributeValue(null,QUALITY_FIELD);
                if (tempAttribQualityField!=null) {
                    object.setQualityField(
                            QualityBits.Factory.fromString(reader,tempAttribQualityField));
                }
                handledAttributes.add(QUALITY_FIELD);
                // handle attribute "LimitField"
                String tempAttribLimitField = reader.getAttributeValue(null,LIMIT_FIELD);
                if (tempAttribLimitField!=null) {
                    object.setLimitField(
                            LimitBits.Factory.fromString(reader,tempAttribLimitField));
                }
                handledAttributes.add(LIMIT_FIELD);
                // handle attribute "VendorField"
                String tempAttribVendorField = reader.getAttributeValue(null,VENDOR_FIELD);
                if (tempAttribVendorField!=null) {
                    object.setVendorField(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToUnsignedByte(tempAttribVendorField));
                }
                handledAttributes.add(VENDOR_FIELD);
                reader.next();
            } catch (javax.xml.stream.XMLStreamException e) {
                throw new java.lang.Exception(e);
            }
            return object;
        }
    }//end of factory class
}