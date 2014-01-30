package eu.ebbits.pwal.impl.driver.opcxmlda.stub;

/**
 * Stub class used for the response to a Browse request
 * <p> 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 * 
 * 
 * @author     ISMB
 * @version    %I%, %G%
 * @since      PWAL 0.2.0
 */ 
public class BrowseResponse extends ADBBeanImplementation {

    /**
     * 
     */
    private static final long serialVersionUID = -1543161589753205347L;
    
    
    /* QNAME for this class */
    public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
            OPC_NAMESPACE,
            "BrowseResponse",
            OPC_PREFIX);

    /* static name of the class */
    private static final String NAME = BrowseResponse.class.getSimpleName();
    
    @Override
    protected String getClassName() {
        return this.getClass().getSimpleName();
    }

    
    //===========literal constants===========
    private static final String MORE_ELEMENTS = "MoreElements";
    
    private static final String ELEMENTS = "Elements";
    
    /**
     * field for BrowseResult
     */
    private ReplyBase localBrowseResult ;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    private boolean localBrowseResultTracker = false ;


    /**
     * Retrieves the result of the Browse request
     * 
     * @return   a <code>ReplyBase</code> containing the result of the Browse request
     */
    public  ReplyBase getBrowseResult() {
        return localBrowseResult;
    }



    /**
     * Sets the result of the Browse request
     * 
     * @param param - the result of the Browse request as <code>ReplyBase</code>
     * 
     */
    public void setBrowseResult(ReplyBase param) {
        if (param != null) {
            //update the setting tracker
            localBrowseResultTracker = true;
        } else {
            localBrowseResultTracker = false;
        }
        this.localBrowseResult=param;
    }


    /**
     * field for Elements
     * This was an Array!
     */
    private BrowseElement[] localElements ;

    /*  This tracker boolean wil be used to detect whether the user called the set method
     *   for this attribute. It will be used to determine whether to include this field
     *   in the serialized XML
     */
    private boolean localElementsTracker = false ;


    /**
     * Retrieves the list of elements of the response
     * 
     * @return   the list of <code>BrowseElement[]</code>
     */
    public  BrowseElement[] getElements() {
        return localElements;
    }


    /**
     * 
     * validate the array for Elements
     * 
     * @param param - the list of elements as <code>BrowseElement[]</code>
     *  
     */
    protected void validateElements(BrowseElement[] param) {
    }


    /**
     * Sets the list of elements of the response
     * 
     * @param param - the list of elements to set, as <code>BrowseElement[]</code>
     */
    public void setElements(BrowseElement[] param) {
        BrowseElement[] elementsToUse = param.clone();
        validateElements(elementsToUse);
        if (elementsToUse != null) {
            //update the setting tracker
            localElementsTracker = true;
        } else {
            localElementsTracker = false;
        }
        this.localElements=elementsToUse;
    }



    /**
     * Adds a <code>BrowseElement</code> to the list
     * 
     * @param param - <code>BrowseElement</code> to add
     * 
     */
    public void addElements(BrowseElement param) {
        if (localElements == null) {
            localElements = new BrowseElement[]{};
        }
        //update the setting tracker
        localElementsTracker = true;
        java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(localElements);
        list.add(param);
        this.localElements = (BrowseElement[])list.toArray(new BrowseElement[list.size()]);
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
     * field for ContinuationPoint
     * This was an Attribute!
     */
    private String localContinuationPoint ;

    /**
     * field for MoreElements
     * This was an Attribute!
     */
    private boolean localMoreElements ;


    /**
     * Indicates if the response has more elements
     * 
     * @return  a <code>boolean</code> - true if the response has more elements, false otherwise
     */
    public  boolean getMoreElements() {
        return localMoreElements;
    }


    /**
     * Sets if the response has more elements
     * 
     * @param param - if the response has more elements true, otherwise false  
     * 
     */
    public void setMoreElements(boolean param) {
        this.localMoreElements=param;
    }
    
    
    @Override
    protected void writeAttributes(String prefix,String namespace, org.apache.axiom.om.OMFactory factory, org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter) 
            throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
        writeAttribute("", "ContinuationPoint", localContinuationPoint, xmlWriter);
        writeAttribute("",
                MORE_ELEMENTS,
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localMoreElements), xmlWriter);
        serializeAttribute(localBrowseResultTracker,localBrowseResult,"BrowseResult",factory,xmlWriter);
        serializeAttributes(localElementsTracker,localElements,ELEMENTS,factory,xmlWriter);
        serializeAttributes(localErrorsTracker,localErrors,"Errors",factory,xmlWriter);
        xmlWriter.writeEndElement();
    }


    @Override
    public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
            throws org.apache.axis2.databinding.ADBException{

        java.util.ArrayList<Object> elementList = new java.util.ArrayList<Object>();
        java.util.ArrayList<Object> attribList = new java.util.ArrayList<Object>();

        addItem(localBrowseResultTracker,localBrowseResult,"BrowseResult",elementList);
        addItems(localElementsTracker,localElements,ELEMENTS,elementList);
        addItems(localErrorsTracker,localErrors,"Errors",elementList);
        attribList.add(new javax.xml.namespace.QName("","ContinuationPoint"));
        attribList.add(localContinuationPoint);
        attribList.add(new javax.xml.namespace.QName("",MORE_ELEMENTS));
        attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localMoreElements));
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
         * @return the <code>BrowseResponse</code> parsed
         * 
         * @throws Exception - if something goes wrong parsing the xml
         *
         */
        public static BrowseResponse parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            BrowseResponse object =
                    new BrowseResponse();
            try {
                GenericFactory.goToNextStartElement(reader);
                BrowseResponse result = (BrowseResponse) checkADBBeanType(NAME,reader); 
                if(result!=null) {
                    return result;
                }
                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.List<String> handledAttributes = new java.util.ArrayList<String>();
                // handle attribute "ContinuationPoint"
                GenericFactory.handleContinuationPoint(reader.getAttributeValue(null,"ContinuationPoint"),object,handledAttributes);
                // handle attribute "MoreElements"
                String tempAttribMoreElements = reader.getAttributeValue(null,MORE_ELEMENTS);
                if (tempAttribMoreElements!=null) {
                    object.setMoreElements(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(tempAttribMoreElements));
                } else {
                    throw new org.apache.axis2.databinding.ADBException("Required attribute MoreElements is missing");
                }
                handledAttributes.add(MORE_ELEMENTS);
                reader.next();
                java.util.ArrayList <Object> list2 = new java.util.ArrayList<Object>();
                GenericFactory.goToNextStartElement(reader);
                if (reader.isStartElement() && new javax.xml.namespace.QName(OPC_NAMESPACE,"BrowseResult").equals(reader.getName())) {
                    object.setBrowseResult(ReplyBase.Factory.parse(reader));
                    reader.next();
                }  // End of if for expected property start element
                GenericFactory.goToNextStartElement(reader);
                if (reader.isStartElement() && new javax.xml.namespace.QName(OPC_NAMESPACE,ELEMENTS).equals(reader.getName())) {
                    // Process the array and step past its final element's end.
                    list2.add(BrowseElement.Factory.parse(reader));
                    //loop until we find a start element that is not part of this array
                    boolean loopDone2 = false;
                    while(!loopDone2) {
                        loopDone2 = GenericFactory.findStartElementNotPartOfTheArray(ELEMENTS,reader);
                        if(!loopDone2) {
                            list2.add(BrowseElement.Factory.parse(reader));
                        }
                    }
                    // call the converter utility  to convert and set the array
                    object.setElements((BrowseElement[])
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                    BrowseElement.class,
                                    list2));
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