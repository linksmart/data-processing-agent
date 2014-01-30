package eu.ebbits.pwal.impl.driver.opcxmlda.stub;

import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.axis2.databinding.ADBException;

/**
 * Utility class with methods useful for all the classes
 * <p> 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 * 
 * 
 * @author     ISMB
 * @version    %I%, %G%
 * @since      PWAL 0.2.0
 */ 
public final class GenericFactory {
    
    private GenericFactory() {
    }
    
    /**
     * Retrieves the namespace of the reader
     * 
     * @param reader - reader to use <code>javax.xml.stream.XMLStreamReader</code>
     * @param content - content to parse as <code>String</code>
     * 
     * @return    the namespace URI as a <code>String</code>
     */
    public static String getNamespaceURI(javax.xml.stream.XMLStreamReader reader, String content) {
        String prefix = "";
        String namespaceuri = "";
        if (content.indexOf(':') > 0) {
            // this seems to be a Qname so find the namespace and send
            prefix = content.substring(0, content.indexOf(':'));
            namespaceuri = reader.getNamespaceURI(prefix);
        } 
        return namespaceuri;
    }
    
    
    /**
     * Handle an ItemPath attribute
     * 
     * @param tempAttribItemPath - the ItemPath value read as a <code>String</code>
     * @param object - the object on which to set the ItemPath attribute
     * @param handledAttributes - a <code>Vector</code> containing the list of attributes already handled
     * 
     */
    public static void handleItemPath(String tempAttribItemPath, 
                                        ADBBeanImplementation object, 
                                        List<String> handledAttributes) {
        if (tempAttribItemPath!=null) {
            object.setItemPath(tempAttribItemPath);
        }
        handledAttributes.add("ItemPath");        
    }
    
    
    /**
     * Handle an ReqType attribute
     * 
     * @param tempAttribReqType - the ReqType value read as a <code>String</code>
     * @param object - the object on which to set the ReqType attribute
     * @param handledAttributes - a <code>Vector</code> containing the list of attributes already handled
     * 
     */
    public static void handleReqType(String tempAttribReqType, 
                                        ADBBeanImplementation object, 
                                        List<String> handledAttributes,
                                        javax.xml.stream.XMLStreamReader reader) {
        String prefix ="";
        String namespaceuri ="";
        if (tempAttribReqType!=null) {
            int index = tempAttribReqType.indexOf(':');
            if(index > -1) {
                prefix = tempAttribReqType.substring(0,index);
            } else {
                // i.e this is in default namespace
                prefix = "";
            }
            namespaceuri = reader.getNamespaceURI(prefix);
            object.setReqType(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToQName(tempAttribReqType,namespaceuri));
        }
        handledAttributes.add("ReqType");
    }
    
    /**
     * Handle an ItemName attribute
     * 
     * @param tempAttribItemName - the ItemName value read as a <code>String</code>
     * @param object - the object on which to set the ItemName attribute
     * @param handledAttributes - a <code>Vector</code> containing the list of attributes already handled
     * 
     */    
    public static void handleItemName(String tempAttribItemName, 
            ADBBeanImplementation object, 
            List<String> handledAttributes) {
        if (tempAttribItemName!=null) {
            object.setItemName(tempAttribItemName);
        }
        handledAttributes.add("ItemName");
    }

    
    /**
     * Handle an ClientItemHandle attribute
     * 
     * @param tempAttribClientItemHandle - the ClientItemHandle value read as a <code>String</code>
     * @param object - the object on which to set the ClientItemHandle attribute
     * @param handledAttributes - a <code>Vector</code> containing the list of attributes already handled
     * 
     */        
    public static void handleClientItemHandle(String tempAttribClientItemHandle, 
            ADBBeanImplementation object, 
            List<String> handledAttributes) {
        if (tempAttribClientItemHandle!=null) {
            object.setClientItemHandle(tempAttribClientItemHandle);
        }
        handledAttributes.add("ClientItemHandle");
    }


    /**
     * Handle an Deadband attribute
     * 
     * @param tempAttribDeadband - the Deadband value read as a <code>String</code>
     * @param object - the object on which to set the Deadband attribute
     * @param handledAttributes - a <code>Vector</code> containing the list of attributes already handled
     * 
     */        
    public static void handleDeadband(String tempAttribDeadband, 
            ADBBeanImplementation object, 
            List<String> handledAttributes) {
        if (tempAttribDeadband!=null) {
            object.setDeadband(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToFloat(tempAttribDeadband));
        } else {
            object.setDeadband(java.lang.Float.NaN);
        }
        handledAttributes.add("Deadband");
    }
    
    /**
     * Handle a RequestedSamplingRate attribute
     * 
     * @param tempAttribRequestedSamplingRate - the RequestedSamplingRate value read as a <code>String</code>
     * @param object - the object on which to set the RequestedSamplingRate attribute
     * @param handledAttributes - a <code>Vector</code> containing the list of attributes already handled
     */
    public static void handleRequestedSamplingRate(String tempAttribRequestedSamplingRate, 
            ADBBeanImplementation object, 
            List<String> handledAttributes) {
        if (tempAttribRequestedSamplingRate!=null) {
            object.setRequestedSamplingRate(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(tempAttribRequestedSamplingRate));
        } else {
            object.setRequestedSamplingRate(java.lang.Integer.MIN_VALUE);
        }
        handledAttributes.add("RequestedSamplingRate");
    }

    /**
     * 
     * Handle a EnableBuffering attribute
     * 
     * @param tempAttribEnableBuffering - the EnableBuffering value read as a <code>String</code>
     * @param object - the object on which to set the EnableBuffering attribute
     * @param handledAttributes - a <code>Vector</code> containing the list of attributes already handled
     */
    public static void handleEnableBuffering(String tempAttribEnableBuffering, 
            ADBBeanImplementation object, 
            List<String> handledAttributes) {
        if (tempAttribEnableBuffering!=null) {
            object.setEnableBuffering(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(tempAttribEnableBuffering));
        }
        handledAttributes.add("EnableBuffering");
    }
    
    /**
     * Handle a LocaleID attribute
     * 
     * @param tempAttribLocaleID - the LocaleID value read as a <code>String</code>
     * @param object - the object on which to set the LocaleID attribute
     * @param handledAttributes - a <code>Vector</code> containing the list of attributes already handled
     */
    public static void handleLocaleID(String tempAttribLocaleID, 
            ADBBeanImplementation object, 
            List<String> handledAttributes) {
        if (tempAttribLocaleID!=null) {
            object.setLocaleID(tempAttribLocaleID);
        }
        handledAttributes.add("LocaleID");
    }
    
    
    /**
     * Handle an ClientRequestHandle attribute
     * 
     * @param tempAttribClientRequestHandle - the ClientRequestHandle value read as a <code>String</code>
     * @param object - the object on which to set the ClientRequestHandle attribute
     * @param handledAttributes - a <code>Vector</code> containing the list of attributes already handled
     * 
     */            
    public static void handleClientRequestHandle(String tempAttribClientRequestHandle, 
            ADBBeanImplementation object, 
            List<String> handledAttributes) {
        if (tempAttribClientRequestHandle!=null) {
            object.setClientRequestHandle(tempAttribClientRequestHandle);
        }
        handledAttributes.add("ClientRequestHandle");
    }
    

    /**
     * Handle an ContinuationPoint attribute
     * 
     * @param tempAttribContinuationPoint - the ContinuationPoint value read as a <code>String</code>
     * @param object - the object on which to set the ContinuationPoint attribute
     * @param handledAttributes - a <code>Vector</code> containing the list of attributes already handled
     */
    public static void handleContinuationPoint(String tempAttribContinuationPoint, 
            ADBBeanImplementation object, 
            List<String> handledAttributes) {
        if (tempAttribContinuationPoint!=null) {
            object.setContinuationPoint(tempAttribContinuationPoint);
        }
        handledAttributes.add("ContinuationPoint");
    }
    
    /**
     * Handle a ReturnAllProperties attribute
     * 
     * @param tempAttribReturnAllProperties - the ReturnAllProperties value read as a <code>String</code>
     * @param object - the object on which to set the ReturnAllProperties attribute
     * @param handledAttributes - a <code>Vector</code> containing the list of attributes already handled
     * 
     * @throws ADBException - if the parameter tempAttribReturnAllProperties is null
     * 
     */
    public static void handleReturnAllProperties(String tempAttribReturnAllProperties, 
            ADBBeanImplementation object, 
            List<String> handledAttributes) throws ADBException {
        if (tempAttribReturnAllProperties!=null) {
            object.setReturnAllProperties(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(tempAttribReturnAllProperties));
        } else {
            throw new org.apache.axis2.databinding.ADBException("Required attribute ReturnAllProperties is missing");
        }
        handledAttributes.add("ReturnAllProperties");
    }
    

    /**
     * Handle a ReturnPropertyValues attribute
     * 
     * 
     * @param tempAttribReturnAllProperties - the ReturnPropertyValues value read as a <code>String</code>
     * @param object - the object on which to set the ReturnPropertyValues attribute
     * @param handledAttributes - a <code>Vector</code> containing the list of attributes already handled
     * 
     * @throws ADBException - if the parameter tempAttribReturnPropertyValues is null
     * 
     */
    public static void handleReturnPropertyValues(String tempAttribReturnPropertyValues, 
            ADBBeanImplementation object, 
            List<String> handledAttributes) throws ADBException {
        if (tempAttribReturnPropertyValues!=null) {
            object.setReturnPropertyValues(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(tempAttribReturnPropertyValues));
        } else {
            throw new org.apache.axis2.databinding.ADBException("Required attribute ReturnPropertyValues is missing");
        }
        handledAttributes.add("ReturnPropertyValues");
    }

    
    /**
     * Handle a ReturnErrorText attribute
     * 
     * 
     * @param tempAttribReturnErrorText - the ReturnErrorText value read as a <code>String</code>
     * @param object - the object on which to set the ReturnErrorText attribute
     * @param handledAttributes - a <code>Vector</code> containing the list of attributes already handled
     * 
     * @throws ADBException - if the parameter tempAttribReturnPropertyValues is null
     * 
     */    
    public static void handleReturnErrorText(String tempAttribReturnErrorText, 
            ADBBeanImplementation object, 
            List<String> handledAttributes) throws ADBException {
        if (tempAttribReturnErrorText!=null) {
            object.setReturnErrorText(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(tempAttribReturnErrorText));
        } else {
            throw new org.apache.axis2.databinding.ADBException("Required attribute ReturnErrorText is missing");
        }
        handledAttributes.add("ReturnErrorText");
    }
    
    
    /**
     * Handle a Name attribute
     * 
     * 
     * @param tempAttribName - the Name value read as a <code>String</code>
     * @param object - the object on which to set the Name attribute
     * @param handledAttributes - a <code>Vector</code> containing the list of attributes already handled
     * 
     * 
     */    
    public static void handleName(String tempAttribName, 
            ADBBeanImplementation object, 
            List<String> handledAttributes) {
        if (tempAttribName!=null) {
            object.setName(tempAttribName);
        }
        handledAttributes.add("Name");
    }

    /**
     * 
     * Handle a ResultID attribute
     * 
     * @param tempAttribResultID - the Name value read as a <code>String</code>
     * @param object - the object on which to set the Name attribute
     * @param handledAttributes - a <code>Vector</code> containing the list of attributes already handled
     * @param reader - the <code>javax.xml.stream.XMLStreamReader</code> used to read the xml content
     * 
     */
    public static void handleResultID(String tempAttribResultID, 
            ADBBeanImplementation object, 
            List<String> handledAttributes,
            javax.xml.stream.XMLStreamReader reader) {
        String prefix ="";
        String namespaceuri ="";
        if (tempAttribResultID!=null) {
            int index = tempAttribResultID.indexOf(':');
            if(index > -1) {
                prefix = tempAttribResultID.substring(0,index);
            } else {
                // i.e this is in default namespace
                prefix = "";
            }
            namespaceuri = reader.getNamespaceURI(prefix);
            object.setResultID(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToQName(tempAttribResultID,namespaceuri));
        }
        handledAttributes.add("ResultID");
    }
    
    
    /**
     * 
     * Handle a MaxAge attribute
     * 
     * @param tempAttribMaxAge - the MaxAge value read as a <code>String</code>
     * @param object - the object on which to set the MaxAge attribute
     * @param handledAttributes - a <code>Vector</code> containing the list of attributes already handled
     * 
     */
    public static void handleMaxAge(String tempAttribMaxAge, 
            ADBBeanImplementation object, 
            List<String> handledAttributes) {
        if (tempAttribMaxAge!=null) {
            object.setMaxAge(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(tempAttribMaxAge));
        } else {
            object.setMaxAge(java.lang.Integer.MIN_VALUE);
        }
        handledAttributes.add("MaxAge");
    }
    
    
    /**
     * 
     * Handle a RevisedSamplingRate attribute
     * 
     * @param tempAttribRevisedSamplingRate - the RevisedSamplingRate value read as a <code>String</code>
     * @param object - the object on which to set the RevisedSamplingRate attribute
     * @param handledAttributes - a <code>Vector</code> containing the list of attributes already handled
     * 
     */
    public static void handleRevisedSamplingRate(String tempAttribRevisedSamplingRate, 
            ADBBeanImplementation object, 
            List<String> handledAttributes) {
        if (tempAttribRevisedSamplingRate!=null) {
            object.setRevisedSamplingRate(
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToInt(tempAttribRevisedSamplingRate));
        } else {
            object.setRevisedSamplingRate(java.lang.Integer.MIN_VALUE);
        }
        handledAttributes.add("RevisedSamplingRate");
    }
    
    /**
     * Handle a MaxAge attribute
     * 
     * @param tempAttribServerSubHandle - the MaxAge value read as a <code>String</code>
     * @param object - the object on which to set the MaxAge attribute
     * @param handledAttributes - a <code>Vector</code> containing the list of attributes already handled
     * 
     */
    public static void handleServerSubHandle(String tempAttribServerSubHandle, 
            ADBBeanImplementation object, 
            List<String> handledAttributes) {
        if (tempAttribServerSubHandle!=null) {
            object.setServerSubHandle(tempAttribServerSubHandle);
        }
        handledAttributes.add("ServerSubHandle");
    }
    

    /**
     * 
     * Checks if there is a start element unexpected
     * 
     * @param reader - the <code>javax.xml.stream.XMLStreamReader</code> to read
     * 
     * @throws XMLStreamException - if an unexpected start element is found
     * 
     */
    public static void checkUnexpectedStartElements(javax.xml.stream.XMLStreamReader reader) 
            throws XMLStreamException {
        if (reader.isStartElement()) {
            // A start element we are not expecting indicates a trailing invalid property
            throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getLocalName());
        }
    }

    /**
     * Goes to the next start element and then, checks if there is a start element unexpected
     *  
     * @param reader - the <code>javax.xml.stream.XMLStreamReader</code> to read
     * 
     * @throws XMLStreamException - if an unexpected start element is found
     */
    public static void goToAndCheckUnexpectedStartElements(javax.xml.stream.XMLStreamReader reader) 
            throws XMLStreamException {
        goToNextStartElement(reader);
        checkUnexpectedStartElements(reader);
    }
    

    /**
     * Searches for a new start element different from the element passed as parameter
     * 
     * 
     * @param element - the element to compare as <code>String</code>
     * @param reader - the <code>javax.xml.stream.XMLStreamReader</code> used to parse the content
     * 
     * @return    a <code>boolean</code> - true if the element is different from the element passed, false if it is equal
     * 
     * @throws XMLStreamException - if something goes wrong in the parsing
     *  
     */
    public static boolean findStartElementNotPartOfTheArray(String element,
                javax.xml.stream.XMLStreamReader reader) throws XMLStreamException{ 
        // We should be at the end element, but make sure
        while (!reader.isEndElement()) {
            reader.next();
        }
        // Step out of this element
        reader.next();
        // Step to next element event.
        goToNextStartElement(reader);
        if (reader.isEndElement()) {
            //two continuous end elements means we are exiting the xml structure
            return true;
        } else {
            if (new javax.xml.namespace.QName(ADBBeanImplementation.OPC_NAMESPACE,element).equals(reader.getName())) {
                return false;
            }
            return true;
        }
    }
    
    /**
     * Creates an <code>OMElement</code> from a text element
     * 
     * @param reader - the <code>javax.xml.stream.XMLStreamReader</code> used to parse the content
     * 
     * @return   the <code>org.apache.axiom.om.OMElement</code> created
     * 
     * @throws XMLStreamException - if something goes wrong in the parsing
     * 
     */
    public static org.apache.axiom.om.OMElement createOMElement(javax.xml.stream.XMLStreamReader reader) throws XMLStreamException {
        String content = reader.getElementText();

        org.apache.axiom.om.OMFactory fac = org.apache.axiom.om.OMAbstractFactory.getOMFactory();
        org.apache.axiom.om.OMNamespace omNs = fac.createOMNamespace(ADBBeanImplementation.OPC_NAMESPACE, "");
        org.apache.axiom.om.OMElement valueValue = fac.createOMElement("Value", omNs);
        valueValue.addChild(fac.createOMText(valueValue, content));
        return valueValue;
    }
    
    
    /**
     * Steps to the next start element
     * 
     * @param reader - the <code>javax.xml.stream.XMLStreamReader</code> used to parse the content
     * 
     * @throws XMLStreamException - if something goes wrong in the parsing
     * 
     */
    public static void goToNextStartElement(javax.xml.stream.XMLStreamReader reader) throws XMLStreamException {
        while (!reader.isStartElement() && !reader.isEndElement()) {
            reader.next();
        }
    }
    
    
    /**
     * This method is used to read an element of type Errors, using the reader passed as parameter
     * 
     * @param reader - the <code>javax.xml.stream.XMLStreamReader</code> used to parse the content 
     * @param object - the object on which to set the Name attribute 
     * 
     * @throws Exception - if something goes wrong parsing the element
     * 
     */
    public static void readErrorsElement(javax.xml.stream.XMLStreamReader reader, 
                                        ADBBeanImplementation object) throws Exception {
        goToNextStartElement(reader);
        java.util.ArrayList<Object> list = new java.util.ArrayList<Object>();
        if (reader.isStartElement() && new javax.xml.namespace.QName(ADBBeanImplementation.OPC_NAMESPACE,"Errors").equals(reader.getName())) {
            // Process the array and step past its final element's end.
            list.add(OPCError.Factory.parse(reader));

            //loop until we find a start element that is not part of this array
            boolean loopDone = false;
            while(!loopDone) {
                loopDone = GenericFactory.findStartElementNotPartOfTheArray("Errors",reader);
                if(!loopDone) {
                        list.add(OPCError.Factory.parse(reader));
                }
            }
            // call the converter utility  to convert and set the array

            object.setErrors((OPCError[])
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                            OPCError.class,
                            list));
        }  // End of if for expected property start element
        goToNextStartElement(reader);
    }
    
    /**
     * This method is used to read an element of type PropertyNamesElement, using the reader passed as parameter
     * 
     * @param reader - the <code>javax.xml.stream.XMLStreamReader</code> used to parse the content 
     * @param object - the object on which to set the Name attribute 
     * 
     * @throws Exception - if something goes wrong parsing the element
     */
    public static void readPropertyNamesElement(javax.xml.stream.XMLStreamReader reader, 
            ADBBeanImplementation object) throws Exception {
        goToNextStartElement(reader);
        java.util.ArrayList<Object> list = new java.util.ArrayList<Object>();
        if (reader.isStartElement() && new javax.xml.namespace.QName(ADBBeanImplementation.OPC_NAMESPACE,"PropertyNames").equals(reader.getName())) {
            // Process the array and step past its final element's end.
            list.add(reader.getElementText());
            //loop until we find a start element that is not part of this array
            boolean loopDone = false;
            while(!loopDone) {
                loopDone = GenericFactory.findStartElementNotPartOfTheArray("PropertyNames",reader);
                if(!loopDone) {
                    list.add(reader.getElementText());
                }
            }
            // call the converter utility  to convert and set the array
            object.setPropertyNames((javax.xml.namespace.QName[])
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                            javax.xml.namespace.QName.class,list));

        }  // End of if for expected property start element
        goToNextStartElement(reader);
    }


    /**
     * This method is used to read an element of type ItemProperties, using the reader passed as parameter
     * 
     * @param reader - the <code>javax.xml.stream.XMLStreamReader</code> used to parse the content 
     * @param object - the object on which to set the Name attribute 
     * 
     * @throws Exception - if something goes wrong parsing the element
     */
    public static void readItemProperties(javax.xml.stream.XMLStreamReader reader, 
            ADBBeanImplementation object) throws Exception {
        java.util.ArrayList<Object> list = new java.util.ArrayList<Object>();
        goToNextStartElement(reader);
        if (reader.isStartElement() && new javax.xml.namespace.QName(ADBBeanImplementation.OPC_NAMESPACE,"Properties").equals(reader.getName())) {
            // Process the array and step past its final element's end.
            list.add(ItemProperty.Factory.parse(reader));            
        }
        //loop until we find a start element that is not part of this array
        boolean loopDone = false;
        while(!loopDone) {
            loopDone = GenericFactory.findStartElementNotPartOfTheArray("Properties",reader);
            if(!loopDone) {
                list.add(ItemProperty.Factory.parse(reader));
            }                            
        }
        // call the converter utility  to convert and set the array
        object.setProperties((ItemProperty[])
                org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                        ItemProperty.class,
                        list));        
    }
    
    
    /**
     * This method is used to read an element of type Options, using the reader passed as parameter
     * 
     * @param reader - the <code>javax.xml.stream.XMLStreamReader</code> used to parse the content 
     * @param object - the object on which to set the Name attribute 
     * 
     * @throws Exception - if something goes wrong parsing the element
     */
    public static void readOptionsElement(javax.xml.stream.XMLStreamReader reader, 
            ADBBeanImplementation object) throws Exception {
        goToNextStartElement(reader);
        if (reader.isStartElement() && new javax.xml.namespace.QName(ADBBeanImplementation.OPC_NAMESPACE,"Options").equals(reader.getName())) {
            object.setOptions(RequestOptions.Factory.parse(reader));
            reader.next();
        }  // End of if for expected property start element
        goToNextStartElement(reader);
    }
}
