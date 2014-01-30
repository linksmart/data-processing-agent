package eu.ebbits.pwal.impl.driver.opcxmlda.stub;


/**
 * Stub class used for the response to a request of subscription refresh
 * <p> 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 * 
 * 
 * @author     ISMB
 * @version    %I%, %G%
 * @since      PWAL 0.2.0
 */ 
public class SubscriptionPolledRefreshResponse extends ADBBeanImplementation {

    /**
     * 
     */
    private static final long serialVersionUID = 1467005477143134249L;

    /* QNAME for this class */
    public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
            OPC_NAMESPACE,
            "SubscriptionPolledRefreshResponse",
            OPC_PREFIX);

    /* static name of the class */
    private static final String NAME = SubscriptionPolledRefreshResponse.class.getSimpleName();
    
    
    @Override
    protected String getClassName() {
        return this.getClass().getSimpleName();
    }
    
    
    //===========literal constants===========
    private static final String DATA_BUFFER_OVERFLOW = "DataBufferOverflow";
    
    private static final String INVALID_SERVER_SUB_HANDLES = "InvalidServerSubHandles";
    
    private static final String R_ITEM_LIST =  "RItemList";
    


    
    /**
     * field for SubscriptionPolledRefreshResult
     */
    private ReplyBase localSubscriptionPolledRefreshResult ;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    private boolean localSubscriptionPolledRefreshResultTracker = false ;


    /**
     * Returns the result of the subscription refresh request
     * 
     * @return   the result of the request as <code>ReplyBase</code>
     */
    public ReplyBase getSubscriptionPolledRefreshResult() {
        return localSubscriptionPolledRefreshResult;
    }



    /**
     * Sets the result of the request
     * 
     * @param param - the result of the request to be set as <code>ReplyBase</code>
     */
    public void setSubscriptionPolledRefreshResult(ReplyBase param) {
        if (param != null) {
            //update the setting tracker
            localSubscriptionPolledRefreshResultTracker = true;
        } else {
            localSubscriptionPolledRefreshResultTracker = false;

        }
        this.localSubscriptionPolledRefreshResult=param;
    }


    /**
     * field for InvalidServerSubHandles
     * This was an Array!
     */
    private String[] localInvalidServerSubHandles ;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    private boolean localInvalidServerSubHandlesTracker = false ;


    /**
     * Returns the list of invalid server subscription handles 
     * 
     * @return  the list of invalid server subscription handles as <code>String[]</code>
     * 
     */
    public  String[] getInvalidServerSubHandles() {
        return localInvalidServerSubHandles;
    }



    /**
     * Validates the array for InvalidServerSubHandles
     * 
     * @param param - array to be validated
     */
    protected void validateInvalidServerSubHandles(String[] param) {

    }


    /**
     * Sets the array of invalid server subscription handles
     * 
     * @param param - array of invalid server subscription handles to be set as <code>String[]</code>
     */
    public void setInvalidServerSubHandles(String[] param) {
        String[] invalidServerSubHandlesToUse = param.clone();
        validateInvalidServerSubHandles(invalidServerSubHandlesToUse);
        if (invalidServerSubHandlesToUse != null) {
            //update the setting tracker
            localInvalidServerSubHandlesTracker = true;
        } else {
            localInvalidServerSubHandlesTracker = false;
        }
        this.localInvalidServerSubHandles=invalidServerSubHandlesToUse;
    }



    /**
     * Adds an invalid server subscription handle to the list
     * 
     * @param param - invalid server subscription handle to add as <code>String</code>
     */
    public void addInvalidServerSubHandles(String param) {
        if (localInvalidServerSubHandles == null) {
            localInvalidServerSubHandles = new String[]{};
        }
        //update the setting tracker
        localInvalidServerSubHandlesTracker = true;
        java.util.List<String> list =
                org.apache.axis2.databinding.utils.ConverterUtil.toList(localInvalidServerSubHandles);
        list.add(param);
        this.localInvalidServerSubHandles =
                (String[])list.toArray(
                        new String[list.size()]);

    }


    /**
     * field for RItemList
     * This was an Array!
     */
    private SubscribePolledRefreshReplyItemList[] localRItemList ;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    private boolean localRItemListTracker = false ;


    /**
     * Returns the list of items of the response to a subscription refresh request 
     * 
     * @return   the list of items of the response to a subscription refresh reqeuest as <code>SubscribePolledRefreshReplyItemList[]</code>
     */
    public SubscribePolledRefreshReplyItemList[] getRItemList() {
        return localRItemList;
    }


    /**
     * Validates the array for RItemList
     * 
     * @param param - the array to validate
     */
    protected void validateRItemList(SubscribePolledRefreshReplyItemList[] param) {
    }


    /**
     * Sets the array of items
     * 
     * @param param - the array to be set as <code>SubscribePolledRefreshReplyItemList[]</code>
     */
    public void setRItemList(SubscribePolledRefreshReplyItemList[] param) {
        SubscribePolledRefreshReplyItemList[] rItemIlistToUse = param.clone();
        validateRItemList(rItemIlistToUse);
        setProtectedFieldValueWithTracker("localRItemList", "localRItemListTracker", rItemIlistToUse);
    }



    /**
     * Adds an item to the array
     * 
     * @param param - item to be added as <code>SubscribePolledRefreshReplyItemList</code>
     */
    public void addRItemList(SubscribePolledRefreshReplyItemList param) {
        if (localRItemList == null) {
            localRItemList = new SubscribePolledRefreshReplyItemList[]{};
        }
        //update the setting tracker
        localRItemListTracker = true;
        java.util.List<SubscribePolledRefreshReplyItemList> list =
                org.apache.axis2.databinding.utils.ConverterUtil.toList(localRItemList);
        list.add(param);
        this.localRItemList =
                (SubscribePolledRefreshReplyItemList[])list.toArray(
                        new SubscribePolledRefreshReplyItemList[list.size()]);
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
     * field for DataBufferOverflow
     * This was an Attribute!
     */
    private boolean localDataBufferOverflow ;


    /**
     * Returns the indication if the buffer is overflowed
     * 
     * @return  a <code>boolean</code>, true if the buffer is overflowed, false otherwise
     * 
     */
    public boolean getDataBufferOverflow() {
        return localDataBufferOverflow;
    }



    /**
     * Sets the indication if the buffer is overflowed
     * 
     * @param param -  a <code>boolean</code>, true if the buffer is overflowed, false otherwise
     * 
     */
    public void setDataBufferOverflow(boolean param) {
        this.localDataBufferOverflow=param;
    }


    @Override
    protected void writeAttributes(String prefix,String namespace, org.apache.axiom.om.OMFactory factory, org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter) 
            throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
        writeAttribute("",
                DATA_BUFFER_OVERFLOW,
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localDataBufferOverflow), xmlWriter);
        serializeAttribute(localSubscriptionPolledRefreshResultTracker,localSubscriptionPolledRefreshResult,"SubscriptionPolledRefreshResult",factory,xmlWriter);
        writeAttributesWithNamespace(localInvalidServerSubHandlesTracker,INVALID_SERVER_SUB_HANDLES,localInvalidServerSubHandles,xmlWriter);
        serializeAttributes(localRItemListTracker,localRItemList,R_ITEM_LIST,factory,xmlWriter);
        serializeAttributes(localErrorsTracker,localErrors,"Errors",factory,xmlWriter);
        xmlWriter.writeEndElement();
    }

    
    @Override
    public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
            throws org.apache.axis2.databinding.ADBException{

        java.util.ArrayList<Object> elementList = new java.util.ArrayList<Object>();
        java.util.ArrayList<Object> attribList = new java.util.ArrayList<Object>();

        addItem(localSubscriptionPolledRefreshResultTracker,localSubscriptionPolledRefreshResult,"SubscriptionPolledRefreshResult",elementList);
        addItems(localInvalidServerSubHandlesTracker,localInvalidServerSubHandles,INVALID_SERVER_SUB_HANDLES,elementList);
        addItems(localRItemListTracker,localRItemList,R_ITEM_LIST,elementList);
        addItems(localErrorsTracker,localErrors,"Errors",elementList);
        attribList.add(new javax.xml.namespace.QName("",DATA_BUFFER_OVERFLOW));
        attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localDataBufferOverflow));
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
         *
         * @param reader - the <code>javax.xml.stream.XMLStreamReader</code> to use to parse the xml
         * 
         * @return the <code>SubscriptionPolledRefreshResponse</code> parsed
         * 
         * @throws Exception - if something goes wrong parsing the xml
         *
         */
        public static SubscriptionPolledRefreshResponse parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            SubscriptionPolledRefreshResponse object = new SubscriptionPolledRefreshResponse();
            try {
                GenericFactory.goToNextStartElement(reader);
                SubscriptionPolledRefreshResponse result = (SubscriptionPolledRefreshResponse) checkADBBeanType(NAME,reader); 
                if(result!=null) {
                    return result;
                }
                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.List<String> handledAttributes = new java.util.ArrayList<String>();
                // handle attribute "DataBufferOverflow"
                String tempAttribDataBufferOverflow = reader.getAttributeValue(null,DATA_BUFFER_OVERFLOW);
                if (tempAttribDataBufferOverflow!=null) {
                    object.setDataBufferOverflow(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(tempAttribDataBufferOverflow));
                } else {
                    throw new org.apache.axis2.databinding.ADBException("Required attribute DataBufferOverflow is missing");
                }
                handledAttributes.add(DATA_BUFFER_OVERFLOW);
                reader.next();
                java.util.ArrayList <Object> list2 = new java.util.ArrayList<Object>();
                java.util.ArrayList <Object> list3 = new java.util.ArrayList<Object>();
                GenericFactory.goToNextStartElement(reader);
                if (reader.isStartElement() && new javax.xml.namespace.QName(OPC_NAMESPACE,"SubscriptionPolledRefreshResult").equals(reader.getName())) {
                    object.setSubscriptionPolledRefreshResult(ReplyBase.Factory.parse(reader));
                    reader.next();
                }  // End of if for expected property start element
                GenericFactory.goToNextStartElement(reader);
                if (reader.isStartElement() && new javax.xml.namespace.QName(OPC_NAMESPACE,INVALID_SERVER_SUB_HANDLES).equals(reader.getName())) {
                    // Process the array and step past its final element's end.
                    list2.add(reader.getElementText());
                    //loop until we find a start element that is not part of this array
                    boolean loopDone2 = false;
                    while(!loopDone2) {
                        loopDone2 = GenericFactory.findStartElementNotPartOfTheArray(INVALID_SERVER_SUB_HANDLES,reader);
                        if(!loopDone2) {
                            list2.add(reader.getElementText());
                        }
                    }
                    // call the converter utility  to convert and set the array
                    object.setInvalidServerSubHandles((String[])
                            list2.toArray(new String[list2.size()]));
                }  // End of if for expected property start element
                GenericFactory.goToNextStartElement(reader);
                if (reader.isStartElement() && new javax.xml.namespace.QName(OPC_NAMESPACE,R_ITEM_LIST).equals(reader.getName())) {
                    // Process the array and step past its final element's end.
                    list3.add(SubscribePolledRefreshReplyItemList.Factory.parse(reader));
                    //loop until we find a start element that is not part of this array
                    boolean loopDone3 = false;
                    while(!loopDone3) {
                        loopDone3 = GenericFactory.findStartElementNotPartOfTheArray(R_ITEM_LIST,reader);
                        if(!loopDone3) {
                            list3.add(SubscribePolledRefreshReplyItemList.Factory.parse(reader));
                        }
                    }
                    // call the converter utility  to convert and set the array
                    object.setRItemList((SubscribePolledRefreshReplyItemList[])
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                    SubscribePolledRefreshReplyItemList.class,
                                    list3));
                }  // End of if for expected property start element
                GenericFactory.readErrorsElement(reader,object);
                GenericFactory.checkUnexpectedStartElements(reader);
            } catch (javax.xml.stream.XMLStreamException e) {
                throw new java.lang.Exception(e);
            }
            return object;
        }
    }//end of factory class
}