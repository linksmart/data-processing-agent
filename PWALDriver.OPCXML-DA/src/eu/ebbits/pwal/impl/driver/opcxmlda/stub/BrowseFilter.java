package eu.ebbits.pwal.impl.driver.opcxmlda.stub;

/**
 * Stub class used as filter for Browse request
 * <p> 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 * 
 * 
 * @author     ISMB
 * @version    %I%, %G%
 * @since      PWAL 0.2.0
 */ 
public class BrowseFilter extends ADBBeanImplementation {

    /**
     * 
     */
    private static final long serialVersionUID = -1447284959718405310L;

    /* QNAME for this class */
    public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
            OPC_NAMESPACE,
            "browseFilter",
            OPC_PREFIX);

    @Override
    protected String getClassName() {
        return this.getClass().getSimpleName();
    }
    

    /**
     * field for BrowseFilter
     */
    private String localBrowseFilter ;

    /* table to store the registered values */
    private static java.util.Map<String,BrowseFilter> table = new java.util.HashMap<String,BrowseFilter>();

    /**
     * Constructor of the BrowseFilter
     * 
     * @param value - value to filter as <code>String</code>
     * @param isRegisterValue - indicates if the filter must be stored in the table of the register values, 
     *                              true yes, false no
     */
    protected BrowseFilter(String value, boolean isRegisterValue) {
        localBrowseFilter = value;
        if (isRegisterValue) {
            table.put(localBrowseFilter, this);
        }
    }

    //=========literal constants================
    
    public static final String ALL = "all";

    public static final String BRANCH = "branch";

    public static final String ITEM = "item";

    //=========filter constants================
    
    public static final BrowseFilter ALL_FILTER = new BrowseFilter(ALL,true);

    public static final BrowseFilter BRANCH_FILTER = new BrowseFilter(BRANCH,true);

    public static final BrowseFilter ITEM_FILTER = new BrowseFilter(ITEM,true);

    
    
    /**
     * Retrieves the vale used to filter
     * 
     * @return   the value used to filter as a <code>String</code>
     */
    public String getValue() { 
        return localBrowseFilter;
    }

    
    //=========Utility methods used to compare BrowseFilter classes========

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
        return localBrowseFilter;
    }

    //=========Utility methods used to compare BrowseFilter classes end ========

    @Override
    protected void writeAttributes(String prefix,String namespace, org.apache.axiom.om.OMFactory factory, org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter) 
            throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
         if (localBrowseFilter==null) {
             throw new org.apache.axis2.databinding.ADBException("Value cannot be null !!");
         }else{
             xmlWriter.writeCharacters(localBrowseFilter);
         }
         xmlWriter.writeEndElement();
     }


     /**
      * databinding method to get an XML representation of this object
      *
      */
     public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
             throws org.apache.axis2.databinding.ADBException{

         //We can safely assume an element has only one type associated with it
         return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(MY_QNAME,
                 new java.lang.Object[]{
                 org.apache.axis2.databinding.utils.reader.ADBXMLStreamReader.ELEMENT_TEXT,
                 org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localBrowseFilter)
         },
         null);
     }



     /**
      *  Factory class that keeps the parse method
      */
     public static class Factory {

         /**
          * Returns the <code>BrowseFilter</code> object registered
          *  
          * @param value - the value registered when the object has been created as a <code>String</code> 
          * 
          * @return   the <code>BrowseFilter</code> associated with the value passed as parameter
          * 
          * @throws java.lang.IllegalArgumentException - if the value passed as parameter is not registered
          */
         public static BrowseFilter fromValue(String value) {
             BrowseFilter enumeration = (BrowseFilter)table.get(value);
             if (enumeration==null) { 
                 throw new java.lang.IllegalArgumentException();
             }
             return enumeration;
         }
         

         /**
          * Returns the <code>BrowseFilter</code> object registered
          *  
          * @param value - the value registered when the object has been created as a <code>String</code> 
          * @param namespace - the namespace to use
          * 
          * @return   the <code>BrowseFilter</code> associated with the value passed as parameter
          * 
          * @throws java.lang.IllegalArgumentException - if the value passed as parameter is not registered
          * 
          */
         public static BrowseFilter fromString(String value,String namespaceURI) {
             try {
                 return fromValue(value);
             } catch (java.lang.Exception e) {
                 throw new java.lang.IllegalArgumentException(e);
             }
         }

         
         /**
          * Returns the <code>BrowseFilter</code> object from an xml content
          *  
          * @param reader - the <code>javax.xml.stream.XMLStreamReader</code> to use
          * @param content - the xml content as <code>String</code>
          * 
          * @return   the <code>BrowseFilter</code> generated parsing the xml
          * 
          * 
          */
         public static BrowseFilter fromString(javax.xml.stream.XMLStreamReader xmlStreamReader,
                 String content) {
             if (content.indexOf(':') > -1) {
                 String prefix = content.substring(0,content.indexOf(':'));
                 String namespaceUri = xmlStreamReader.getNamespaceContext().getNamespaceURI(prefix);
                 return BrowseFilter.Factory.fromString(content,namespaceUri);
             } else {
                 return BrowseFilter.Factory.fromString(content,"");
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
          * @return the <code>BrowseFilter</code> parsed
          * 
          * @throws Exception - if something goes wrong parsing the xml
          *
          */
         public static BrowseFilter parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
             BrowseFilter object = null;
             try {
                 GenericFactory.goToNextStartElement(reader);
                 while(!reader.isEndElement()) {
                     if (reader.isStartElement()  || reader.hasText()) {
                         String content = reader.getElementText();
                         object = BrowseFilter.Factory.fromString(content,GenericFactory.getNamespaceURI(reader,content));
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