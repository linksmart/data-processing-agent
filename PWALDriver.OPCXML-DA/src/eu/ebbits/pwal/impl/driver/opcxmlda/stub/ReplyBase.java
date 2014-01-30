package eu.ebbits.pwal.impl.driver.opcxmlda.stub;

/**
 * Stub class used for requests' responses
 * <p> 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 * 
 * 
 * @author     ISMB
 * @version    %I%, %G%
 * @since      PWAL 0.2.0
 */ 
public class ReplyBase extends ADBBeanImplementation {
    /* This type was generated from the piece of schema that had
         name = ReplyBase
         Namespace URI = http://opcfoundation.org/webservices/XMLDA/1.0/
         Namespace Prefix = ns1
     */

    /**
     * 
     */
    private static final long serialVersionUID = -7812452846692239005L;

    /* static name of the class */
    private static final String NAME = ReplyBase.class.getSimpleName();
    
    @Override
    protected String getClassName() {
        return this.getClass().getSimpleName();
    }

    
    //==============literal constants=================
    private static final String RCV_TIME = "RcvTime";
    
    private static final String REPLY_TIME = "ReplyTime";
    
    private static final String REVISED_LOCALE_ID  = "RevisedLocaleID";
    
    private static final String SERVER_STATE = "ServerState";
    
    
    /**
     * field for RcvTime
     * This was an Attribute!
     */
    private java.util.Calendar localRcvTime ;


    /**
     * Returns the receive time of the response
     * 
     * @return    the receive time as <code>java.util.Calendar</code>
     */
    public java.util.Calendar getRcvTime() {
        return localRcvTime;
    }



    /**
     * Sets the receive time of the response
     * 
     * @param param - the receive time to be set as <code>java.util.Calendar</code>
     */
    public void setRcvTime(java.util.Calendar param) {
        this.localRcvTime=param;
    }


    /**
     * field for ReplyTime
     * This was an Attribute!
     */
    private java.util.Calendar localReplyTime ;


    /**
     * Returns the reply time of the response
     * 
     * @return    the reply time as <code>java.util.Calendar</code>
     */
    public java.util.Calendar getReplyTime() {
        return localReplyTime;
    }



    /**
     * Sets the reply time of the response
     * 
     * @param param - the reply time to be set as <code>java.util.Calendar</code>
     * 
     */
    public void setReplyTime(java.util.Calendar param) {
        this.localReplyTime=param;
    }


    /**
     * field for ClientRequestHandle
     * This was an Attribute!
     */
    private String localClientRequestHandle ;


    /**
     * field for RevisedLocaleID
     * This was an Attribute!
     */
    private String localRevisedLocaleID ;


    /**
     * Returns the locale ID revised
     * 
     * @return   the locale ID revised as <code>String</code>
     */
    public String getRevisedLocaleID() {
        return localRevisedLocaleID;
    }



    /**
     * Sets the revised locale ID
     * 
     * @param param - the revised locale ID to be set as <code>String</code>
     * 
     */
    public void setRevisedLocaleID(String param) {
        this.localRevisedLocaleID=param;
    }


    /**
     * field for ServerState
     * This was an Attribute!
     */
    private ServerState localServerState ;


    /**
     * Rerturns the server state
     * 
     * @return   a <code>ServerState</code> object containing the server state
     */
    public ServerState getServerState() {
        return localServerState;
    }



    /**
     * Sets the server state
     * 
     * @param param - the server state to be set as a <code>ServerState</code> object
     */
    public void setServerState(ServerState param) {
        this.localServerState=param;
    }


    
    @Override
    protected void writeAttributes(String prefix,String namespace, org.apache.axiom.om.OMFactory factory, org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter) 
            throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
        if (localRcvTime != null) {
            writeAttribute("",
                    RCV_TIME,
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localRcvTime), xmlWriter);
        } else {
            throw new org.apache.axis2.databinding.ADBException("required attribute localRcvTime is null");
        }
        if (localReplyTime != null) {
            writeAttribute("",
                    REPLY_TIME,
                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localReplyTime), xmlWriter);
        } else {
            throw new org.apache.axis2.databinding.ADBException("required attribute localReplyTime is null");
        }
        writeAttribute("", "ClientRequestHandle", localClientRequestHandle, xmlWriter);
        writeAttribute("", REVISED_LOCALE_ID, localRevisedLocaleID, xmlWriter);
        if (localServerState != null) {
            writeAttribute("",
                    SERVER_STATE,
                    localServerState.toString(), xmlWriter);
        } else {
            throw new org.apache.axis2.databinding.ADBException("required attribute localServerState is null");
        }
        xmlWriter.writeEndElement();
    }


    @Override
    public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
            throws org.apache.axis2.databinding.ADBException{

        java.util.ArrayList<Object> elementList = new java.util.ArrayList<Object>();
        java.util.ArrayList<Object> attribList = new java.util.ArrayList<Object>();

        attribList.add(new javax.xml.namespace.QName("",RCV_TIME));
        attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localRcvTime));
        attribList.add(new javax.xml.namespace.QName("",REPLY_TIME));
        attribList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localReplyTime));
        attribList.add(new javax.xml.namespace.QName("","ClientRequestHandle"));
        attribList.add(localClientRequestHandle);
        attribList.add(new javax.xml.namespace.QName("",REVISED_LOCALE_ID));
        attribList.add(localRevisedLocaleID);
        attribList.add(new javax.xml.namespace.QName("",SERVER_STATE));
        attribList.add(localServerState.toString());
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
         * @return the <code>ReplyBase</code> parsed
         * 
         * @throws Exception - if something goes wrong parsing the xml
         *
         */
        public static ReplyBase parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            ReplyBase object = new ReplyBase();
            try {
                GenericFactory.goToNextStartElement(reader);
                ReplyBase result = (ReplyBase) checkADBBeanType(NAME,reader); 
                if(result!=null) {
                    return result;
                }
                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.List<String> handledAttributes = new java.util.ArrayList<String>();
                // handle attribute "RcvTime"
                String tempAttribRcvTime = reader.getAttributeValue(null,RCV_TIME);
                if (tempAttribRcvTime!=null) {
                    object.setRcvTime(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToDateTime(tempAttribRcvTime));
                } else {
                    throw new org.apache.axis2.databinding.ADBException("Required attribute RcvTime is missing");
                }
                handledAttributes.add(RCV_TIME);
                // handle attribute "ReplyTime"
                String tempAttribReplyTime = reader.getAttributeValue(null,REPLY_TIME);
                if (tempAttribReplyTime!=null) {
                    object.setReplyTime(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToDateTime(tempAttribReplyTime));
                } else {
                    throw new org.apache.axis2.databinding.ADBException("Required attribute ReplyTime is missing");
                }
                handledAttributes.add(REPLY_TIME);
                // handle attribute "ClientRequestHandle"
                GenericFactory.handleClientRequestHandle(reader.getAttributeValue(null,"ClientRequestHandle"), object,handledAttributes);
                // handle attribute "RevisedLocaleID"
                String tempAttribRevisedLocaleID = reader.getAttributeValue(null,REVISED_LOCALE_ID);
                if (tempAttribRevisedLocaleID!=null) {
                    object.setRevisedLocaleID(
                            org.apache.axis2.databinding.utils.ConverterUtil.convertToString(tempAttribRevisedLocaleID));
                }
                handledAttributes.add(REVISED_LOCALE_ID);
                // handle attribute "ServerState"
                String tempAttribServerState =    reader.getAttributeValue(null,SERVER_STATE);
                if (tempAttribServerState!=null) {
                    object.setServerState(
                            ServerState.Factory.fromString(reader,tempAttribServerState));
                } else {
                    throw new org.apache.axis2.databinding.ADBException("Required attribute ServerState is missing");
                }
                handledAttributes.add(SERVER_STATE);
                reader.next();
            } catch (javax.xml.stream.XMLStreamException e) {
                throw new java.lang.Exception(e);
            }
            return object;
        }

    }//end of factory class
}