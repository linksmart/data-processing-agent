package eu.ebbits.pwal.impl.driver.opcxmlda.stub;



/**
 * Stub class for the Read request
 * <p> 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 * 
 * 
 * @author     ISMB
 * @version    %I%, %G%
 * @since      PWAL 0.2.0
 */ 
public class Read extends ADBBeanImplementation {

    /**
     * 
     */
    private static final long serialVersionUID = -2440336061465796249L;

    /* QNAME for this class */
    public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
            OPC_NAMESPACE,
            "Read",
            OPC_PREFIX);
    

    /* static name of the class */
    private static final String NAME = Read.class.getSimpleName();
    
    
    @Override
    protected String getClassName() {
        return this.getClass().getSimpleName();
    }
    
    
    /**
     * field for Options
     */
    private RequestOptions localOptions ;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    private boolean localOptionsTracker = false;


    /**
     * field for ItemList
     */
    private ReadRequestItemList localItemList;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    private boolean localItemListTracker = false;

    
    /**
     * Return the list of item for this Read request
     * 
     * @return  the items list as <code>ReadRequestItemList</code>
     * 
     */
    public ReadRequestItemList getItemList() {
        return localItemList;
    }

    /**
     * 
     * Set the items list
     * 
     * @param param - items list to set as <code>ReadRequestItemList</code>
     * 
     * 
     */
    public void setItemList(ReadRequestItemList param) {
        setProtectedFieldValueWithTracker("localItemList", "localItemListTracker", param);
    }

    
    @Override
    protected void writeAttributes(String prefix,String namespace, org.apache.axiom.om.OMFactory factory, org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter) 
            throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
        serializeAttribute(localOptionsTracker,localOptions,  "Options", factory, xmlWriter);
        serializeAttribute(localItemListTracker,localItemList, "ItemList", factory, xmlWriter);
        xmlWriter.writeEndElement();
    }



    @Override
    public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
            throws org.apache.axis2.databinding.ADBException{

        java.util.ArrayList<Object> elementList = new java.util.ArrayList<Object>();
        java.util.ArrayList<Object> attribList = new java.util.ArrayList<Object>();

        addItem(localOptionsTracker,localOptions,"Options",elementList);
        addItem(localItemListTracker,localItemList,"ItemList",elementList);
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
          * @return the <code>Read</code> parsed
          * 
          * @throws Exception - if something goes wrong parsing the xml
          *
          */
        public static Read parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            Read object = new Read();
            try {
                GenericFactory.goToNextStartElement(reader);
                Read result = (Read) checkADBBeanType(NAME,reader); 
                if(result!=null) {
                    return result;
                }
                reader.next();
                GenericFactory.readOptionsElement(reader,object);
                if (reader.isStartElement() && new javax.xml.namespace.QName(OPC_NAMESPACE,"ItemList").equals(reader.getName())) {
                    object.setItemList(ReadRequestItemList.Factory.parse(reader));
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