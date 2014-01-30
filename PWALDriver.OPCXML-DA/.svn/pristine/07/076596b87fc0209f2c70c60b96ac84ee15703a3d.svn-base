package eu.ebbits.pwal.impl.driver.opcxmlda.stub;

/**
 * Stub class used for the Interface Version
 * <p> 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 * 
 * 
 * @author     ISMB
 * @version    %I%, %G%
 * @since      PWAL 0.2.0
 */ 
public class InterfaceVersion extends ADBBeanImplementation {

    /**
     * 
     */
    private static final long serialVersionUID = 4927763774318304478L;

    /* QNAME for this class */
    public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
            OPC_NAMESPACE,
            "interfaceVersion",
            OPC_PREFIX);

    @Override
    protected String getClassName() {
        return this.getClass().getSimpleName();
    }
    

    /**
     * field for InterfaceVersion
     */
    private String localInterfaceVersion;

    /* table used to store the registered instances of InterfaceVersion */
    private static java.util.Map<String,InterfaceVersion> table = new java.util.HashMap<String,InterfaceVersion>();

    /**
     * Constructor of the InterfaceVersion
     * 
     * @param value - value for the version as <code>String</code>
     * @param isRegisterValue - indicates if the interface version must be stored in the table of the register values, 
     *                              true yes, false no
     */
    protected InterfaceVersion(String value, boolean isRegisterValue) {
        localInterfaceVersion = value;
        if (isRegisterValue) {
            table.put(localInterfaceVersion, this);
        }
    }

    /* literal constant */
    public static final String XML_DA_VERSION_1_0 = "XML_DA_Version_1_0";

    /* InterfaceVersion constant */
    public static final InterfaceVersion XML_DA_VERSION_1_0_INTERFACE = new InterfaceVersion(XML_DA_VERSION_1_0,true);

    /**
     * Retrievs the local value for InterfaceVersion
     * 
     * @return    the local value for InterfaceVersion as <code>String</code>
     */
    public String getValue() { 
        return localInterfaceVersion;
    }

    
    //=========Utility methods used to compare InterfaceVersion classes========
    
    @Override
    public boolean equals(java.lang.Object obj) {
        return (obj == this);
    }

    @Override
    public int hashCode() { 
        return toString().hashCode();
    }
    
    @Override
    public String toString() {
        return localInterfaceVersion;
    }

    //=========Utility methods used to compare InterfaceVersion classes end ========

         
    @Override
    protected void writeAttributes(String prefix,String namespace, org.apache.axiom.om.OMFactory factory, org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter) 
                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
         if (localInterfaceVersion==null) {
             throw new org.apache.axis2.databinding.ADBException("Value cannot be null !!");
         }else{
             xmlWriter.writeCharacters(localInterfaceVersion);
         }
         xmlWriter.writeEndElement();
     }


    @Override
    public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
            throws org.apache.axis2.databinding.ADBException{

        //We can safely assume an element has only one type associated with it
        return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(MY_QNAME,
                new java.lang.Object[]{
                org.apache.axis2.databinding.utils.reader.ADBXMLStreamReader.ELEMENT_TEXT,
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localInterfaceVersion)
        },
        null);
    }



     /**
      *  Factory class that keeps the parse method
      */
     public static class Factory {

         /**
          * Returns the <code>InterfaceVersion</code> object registered
          *  
          * @param value - the value registered when the object has been created as a <code>String</code> 
          * 
          * @return   the <code>InterfaceVersion</code> associated with the value passed as parameter
          * 
          * @throws java.lang.IllegalArgumentException - if the value passed as parameter is not registered
          */
         public static InterfaceVersion fromValue(String value) {
             InterfaceVersion enumeration = (InterfaceVersion)
                     table.get(value);
             if (enumeration==null) {
                 throw new java.lang.IllegalArgumentException();
             }
             return enumeration;
         }
         
         
         /**
          * Returns the <code>InterfaceVersion</code> object registered
          *  
          * @param value - the value registered when the object has been created as a <code>String</code> 
          * @param namespace - the namespace to use
          * 
          * @return   the <code>InterfaceVersion</code> associated with the value passed as parameter
          * 
          * @throws java.lang.IllegalArgumentException - if the value passed as parameter is not registered
          * 
          */
         public static InterfaceVersion fromString(String value,String namespaceURI) {
             try {
                 return fromValue(value);
             } catch (java.lang.Exception e) {
                 throw new java.lang.IllegalArgumentException(e);
             }
         }

         /**
          * Returns the <code>InterfaceVersion</code> object from an xml content
          *  
          * @param reader - the <code>javax.xml.stream.XMLStreamReader</code> to use
          * @param content - the xml content as <code>String</code>
          * 
          * @return   the <code>InterfaceVersion</code> generated parsing the xml
          * 
          */
         public static InterfaceVersion fromString(javax.xml.stream.XMLStreamReader xmlStreamReader,
                 String content) {
             if (content.indexOf(':') > -1) {
                 String prefix = content.substring(0,content.indexOf(':'));
                 String namespaceUri = xmlStreamReader.getNamespaceContext().getNamespaceURI(prefix);
                 return InterfaceVersion.Factory.fromString(content,namespaceUri);
             } else {
                 return InterfaceVersion.Factory.fromString(content,"");
             }
         }


         /**
          * static method to create the object
          * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
          *             If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
          * Postcondition: If this object is an element, the reader is positioned at its end element
          *             If this object is a complex type, the reader is positioned at the end element of its outer element
          *
          * @param reader - the <code>javax.xml.stream.XMLStreamReader</code> to use to parse the xml
          * 
          * @return the <code>InterfaceVersion</code> parsed
          * 
          * @throws Exception - if something goes wrong parsing the xml
          *
          */
         public static InterfaceVersion parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
             InterfaceVersion object = null;    
             try {
                 GenericFactory.goToNextStartElement(reader);
                 while(!reader.isEndElement()) {
                     if (reader.isStartElement()  || reader.hasText()) {
                         String content = reader.getElementText();
                         object = InterfaceVersion.Factory.fromString(content,GenericFactory.getNamespaceURI(reader,content));
                     } else {
                         reader.next();
                     }  
                 }  // end of while loop
             } catch (javax.xml.stream.XMLStreamException e) {
                 throw new java.lang.Exception(e);
             }
             return object;
         }
     }//end of factory class
}