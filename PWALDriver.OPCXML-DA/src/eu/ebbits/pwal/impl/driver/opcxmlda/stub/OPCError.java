package eu.ebbits.pwal.impl.driver.opcxmlda.stub;


/**
 * Stub class for the OPC errors
 * <p> 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 * 
 * 
 * @author     ISMB
 * @version    %I%, %G%
 * @since      PWAL 0.2.0
 */ 
public class OPCError extends ADBBeanImplementation {
    /* This type was generated from the piece of schema that had
         name = OPCError
         Namespace URI = http://opcfoundation.org/webservices/XMLDA/1.0/
         Namespace Prefix = ns1
     */

    /**
     * 
     */
    private static final long serialVersionUID = 3898653813100856381L;
    
    /* static reference to the name of this class*/
    private static final String NAME = OPCError.class.getSimpleName();
    
    @Override
    protected String getClassName() {
        return this.getClass().getSimpleName();
    }
    
    
    /**
     * field for Text
     */
    private String localText ;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    private boolean localTextTracker = false ;


    /**
     * Returns the text of the OPC error
     * 
     * @return String   the text of the OPC error as <code>String</code>
     */
    public  String getText() {
        return localText;
    }



    /**
     * Sets the text of the OPC error
     * 
     * @param param Text - a <code>String</code> containing the text of the error
     *
     */
    public void setText(String param) {
        if (param != null) {
            //update the setting tracker
            localTextTracker = true;
        } else {
            localTextTracker = false;
        }
        this.localText=param;
    }


    /**
     * field for ID
     * This was an Attribute!
     */
    private javax.xml.namespace.QName localID ;


    /**
     * Retrieves the ID of the OPC error
     * 
     * @return   a <code>javax.xml.namespace.QName</code> containing the ID of the error
     * 
     */
    public  javax.xml.namespace.QName getID() {
        return localID;
    }


    /**
     * Sets the ID of the OPC error
     * 
     * @param param - an <code>javax.xml.namespace.QName</code> containing the ID of the error
     */
    public void setID(javax.xml.namespace.QName param) {
        this.localID=param;
    }


    @Override
    protected void writeAttributes(String prefix,String namespace, org.apache.axiom.om.OMFactory factory, org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter) 
            throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
        writeQNameAttribute("", "ID", localID, xmlWriter);
        writeAttributeWithNamespace(localTextTracker,"Text",localText,xmlWriter);
        xmlWriter.writeEndElement();
    }


    @Override
    public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
            throws org.apache.axis2.databinding.ADBException{

        java.util.ArrayList<Object> elementList = new java.util.ArrayList<Object>();
        java.util.ArrayList<Object> attribList = new java.util.ArrayList<Object>();

        addItem(localTextTracker,localText,"Text",elementList);
        attribList.add(new javax.xml.namespace.QName("","ID"));
        attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localID));
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
         * @return the <code>OPCError</code> parsed
         * 
         * @throws Exception - if something goes wrong parsing the xml
         *
         */
        public static OPCError parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            OPCError object = new OPCError();
            String prefix ="";
            String namespaceuri ="";
            try {
                GenericFactory.goToNextStartElement(reader);
                OPCError result = (OPCError) checkADBBeanType(NAME,reader); 
                if(result!=null) {
                    return result;
                }
                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.List<String> handledAttributes = new java.util.ArrayList<String>();
                // handle attribute "ID"
                String tempAttribID = reader.getAttributeValue(null,"ID");
                if (tempAttribID!=null) {
                    int index = tempAttribID.indexOf(':');
                    if(index > -1) {
                        prefix = tempAttribID.substring(0,index);
                    } else {
                        // i.e this is in default namesace
                        prefix = "";
                    }
                    namespaceuri = reader.getNamespaceURI(prefix);
                    object.setID(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToQName(tempAttribID,namespaceuri));
                }
                handledAttributes.add("ID");
                reader.next();
                GenericFactory.goToNextStartElement(reader);
                if (reader.isStartElement() && new javax.xml.namespace.QName(OPC_NAMESPACE,"Text").equals(reader.getName())) {
                    String content = reader.getElementText();
                    object.setText(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
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