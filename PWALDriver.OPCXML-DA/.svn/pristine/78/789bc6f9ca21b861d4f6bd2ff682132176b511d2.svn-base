package eu.ebbits.pwal.impl.driver.opcxmlda.stub;

/**
 * Stub class used for the response to a subscribe request
 * <p> 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 * 
 * 
 * @author     ISMB
 * @version    %I%, %G%
 * @since      PWAL 0.2.0
 */ 
public class SubscribeResponse extends ADBBeanImplementation {


    /**
     * 
     */
    private static final long serialVersionUID = 3229524019597043690L;

    /* QNAME for this class */
    public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
            OPC_NAMESPACE,
            "SubscribeResponse",
            OPC_PREFIX);

    /* static name of the class */
    private static final String NAME = SubscribeResponse.class.getSimpleName();
    
    @Override
    protected String getClassName() {
        return this.getClass().getSimpleName();
    }

    
    /**
     * field for SubscribeResult
     */
    private ReplyBase localSubscribeResult ;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    private boolean localSubscribeResultTracker = false ;


    /**
     * Returns the result of ther request
     * 
     * @return  result of the request as <code>tReplyBase</code>
     */
    public  ReplyBase getSubscribeResult() {
        return localSubscribeResult;
    }



    /**
     * Sets the result of the request
     * 
     * 
     * @param param - result of the request as <code>SubscribeResult</code>
     */
    public void setSubscribeResult(ReplyBase param) {
        if (param != null) {
            //update the setting tracker
            localSubscribeResultTracker = true;
        } else {
            localSubscribeResultTracker = false;
        }
        this.localSubscribeResult=param;
    }


    /**
     * field for RItemList
     */
    private SubscribeReplyItemList localRItemList ;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    private boolean localRItemListTracker = false ;


    /**
     * Return the list of items of the response
     * 
     * @return the list of items of the response as <code>SubscribeReplyItemList</code>
     */
    public  SubscribeReplyItemList getRItemList() {
        return localRItemList;
    }

    /**
     * Sets the list of items of the response
     * 
     * @param param - the list of items of the response as <code>SubscribeReplyItemList</code>
     */
    public void setRItemList(SubscribeReplyItemList param) {
        setProtectedFieldValueWithTracker("localRItemList", "localRItemListTracker", param);
    }

    
    
    /**
     * field for Errors
     * This was an Array!
     */
    private OPCError[] localErrors ;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    private boolean localErrorsTracker = false ;


    /**
     * field for ServerSubHandle
     * This was an Attribute!
     */
    private String localServerSubHandle ;


    @Override
    protected void writeAttributes(String prefix,String namespace, org.apache.axiom.om.OMFactory factory, org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter) 
            throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
        writeAttribute("", "ServerSubHandle", localServerSubHandle, xmlWriter);
        serializeAttribute(localSubscribeResultTracker,localSubscribeResult,"SubscribeResult",factory,xmlWriter);
        serializeAttribute(localRItemListTracker,localRItemList,"RItemList",factory,xmlWriter);
        serializeAttributes(localErrorsTracker,localErrors,"Errors",factory,xmlWriter);
        xmlWriter.writeEndElement();
    }


    @Override
    public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
            throws org.apache.axis2.databinding.ADBException{

        java.util.ArrayList<Object> elementList = new java.util.ArrayList<Object>();
        java.util.ArrayList<Object> attribList = new java.util.ArrayList<Object>();

        addItem(localSubscribeResultTracker,localSubscribeResult,"SubscribeResult",elementList);
        addItem(localRItemListTracker,localRItemList,"RItemList",elementList);
        addItems(localErrorsTracker,localErrors,"Errors",elementList);
        attribList.add(new javax.xml.namespace.QName("","ServerSubHandle"));
        attribList.add(localServerSubHandle);
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
         * @return the <code>SubscribeResponse</code> parsed
         * 
         * @throws Exception - if something goes wrong parsing the xml
         *
         */
        public static SubscribeResponse parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            SubscribeResponse object = new SubscribeResponse();
            try {
                GenericFactory.goToNextStartElement(reader);
                SubscribeResponse result = (SubscribeResponse) checkADBBeanType(NAME,reader); 
                if(result!=null) {
                    return result;
                }
                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.List<String> handledAttributes = new java.util.ArrayList<String>();
                // handle attribute "ServerSubHandle"
                GenericFactory.handleServerSubHandle(reader.getAttributeValue(null,"ServerSubHandle"),object,handledAttributes);
                reader.next();
                GenericFactory.goToNextStartElement(reader);
                if (reader.isStartElement() && new javax.xml.namespace.QName(OPC_NAMESPACE,"SubscribeResult").equals(reader.getName())) {
                    object.setSubscribeResult(ReplyBase.Factory.parse(reader));
                    reader.next();
                }  // End of if for expected property start element
                GenericFactory.goToNextStartElement(reader);
                if (reader.isStartElement() && new javax.xml.namespace.QName(OPC_NAMESPACE,"RItemList").equals(reader.getName())) {
                    object.setRItemList(SubscribeReplyItemList.Factory.parse(reader));
                    reader.next();
                }  // End of if for expected property start element
                GenericFactory.readErrorsElement(reader,object);
                if (reader.isStartElement()) {
                    // A start element we are not expecting indicates a trailing invalid property
                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getLocalName());
                }
            } catch (javax.xml.stream.XMLStreamException e) {
                throw new java.lang.Exception(e);
            }
            return object;
        }

    }//end of factory class
}