package eu.ebbits.pwal.impl.driver.opcxmlda.stub;

/**
 * Stub class used for the ServerState
 * <p> 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 * 
 * 
 * @author     ISMB
 * @version    %I%, %G%
 * @since      PWAL 0.2.0
 */
public class ServerState extends ADBBeanImplementation {

    /**
     * 
     */
    private static final long serialVersionUID = 4094394591454250760L;

    /* QNAME for this class */
    public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
            OPC_NAMESPACE,
            "serverState",
            OPC_PREFIX);

    
    @Override
    protected String getClassName() {
        return this.getClass().getSimpleName();
    }
    
    
    /**
     * field for ServerState
     */
    private String localServerState ;

    /* table to store the registerd instances of ServerState */
    private static java.util.Map<String,ServerState> table = new java.util.HashMap<String,ServerState>();

    /**
     * Constructor of the ServerState
     * 
     * @param value - value of the server state as <code>String</code>
     * @param isRegisterValue - indicates if the server state must be stored in the table of the register values, 
     *                              true yes, false no
     */
    protected ServerState(String value, boolean isRegisterValue) {
        localServerState = value;
        if (isRegisterValue) {
            table.put(localServerState, this);
        }
    }

    //=========literal constants================
    
    public static final String RUNNING = "running";

    public static final String FAILED = "failed";

    public static final String NO_CONFIG = "noConfig";

    public static final String SUSPENDED = "suspended";

    public static final String TEST = "test";

    public static final String COMM_FAULT = "commFault";

    
    //=========server state constants================
    
    public static final ServerState RUNNING_SERVER_STATE = new ServerState(RUNNING,true);

    public static final ServerState FAILED_SERVER_STATE = new ServerState(FAILED,true);

    public static final ServerState NO_CONFIG_SERVER_STATE = new ServerState(NO_CONFIG,true);

    public static final ServerState SUSPENDED_SERVER_STATE = new ServerState(SUSPENDED,true);

    public static final ServerState TEST_SERVER_STATE = new ServerState(TEST,true);

    public static final ServerState COMM_FAULT_SERVER_STATE = new ServerState(COMM_FAULT,true);


    
    /**
     * Retrieves the server state of this object
     * 
     * @return   the server state as <code>String</code>
     */
    public String getValue() { 
        return localServerState;
    }
    
    //=========Utility methods used to compare ServerState classes========

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
        return localServerState;
    }


    //=========Utility methods used to compare ServerState classes end ========

    
    @Override
    protected void writeAttributes(String prefix,String namespace, org.apache.axiom.om.OMFactory factory, org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter) 
            throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
        if (localServerState==null) {
            throw new org.apache.axis2.databinding.ADBException("Value cannot be null !!");
        }else{
            xmlWriter.writeCharacters(localServerState);
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
                org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localServerState)
        }, null);
    }



     /**
      *  Factory class that keeps the parse method
      */
     public static class Factory {

         
         /**
          * Returns the <code>ServerState</code> object registered
          *  
          * @param value - the value registered when the object has been created as a <code>String</code> 
          * 
          * @return   the <code>ServerState</code> associated with the value passed as parameter
          * 
          * @throws java.lang.IllegalArgumentException - if the value passed as parameter is not registered
          * 
          */
         public static ServerState fromValue(String value) {
             ServerState enumeration = (ServerState) table.get(value);
             if (enumeration==null) {
                 throw new java.lang.IllegalArgumentException();
             }
             return enumeration;
         }
         
         
         /**
          * Returns the <code>ServerState</code> object registered
          *  
          * @param value - the value registered when the object has been created as a <code>String</code> 
          * @param namespace - the namespace to use
          * 
          * @return   the <code>ServerState</code> associated with the value passed as parameter
          * 
          * @throws java.lang.IllegalArgumentException - if the value passed as parameter is not registered
          * 
          */
         public static ServerState fromString(String value,String namespaceURI) {
             try {
                 return fromValue(value);
             } catch (java.lang.Exception e) {
                 throw new java.lang.IllegalArgumentException(e);
             }
         }

         
         /**
          * Returns the <code>ServerState</code> object from an xml content
          *  
          * @param reader - the <code>javax.xml.stream.XMLStreamReader</code> to use
          * @param content - the xml content as <code>String</code>
          * 
          * @return   the <code>ServerState</code> generated parsing the xml
          * 
          * 
          */
         public static ServerState fromString(javax.xml.stream.XMLStreamReader xmlStreamReader,
                 String content) {
             if (content.indexOf(':') > -1) {
                 String prefix = content.substring(0,content.indexOf(':'));
                 String namespaceUri = xmlStreamReader.getNamespaceContext().getNamespaceURI(prefix);
                 return ServerState.Factory.fromString(content,namespaceUri);
             } else {
                 return ServerState.Factory.fromString(content,"");
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
          * @return the <code>ServerState</code> parsed
          * 
          * @throws Exception - if something goes wrong parsing the xml
          *
          */
         public static ServerState parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
             ServerState object = null;
             try {
                 GenericFactory.goToNextStartElement(reader);
                 while(!reader.isEndElement()) {
                     if (reader.isStartElement()  || reader.hasText()) {
                         String content = reader.getElementText();
                         object = ServerState.Factory.fromString(content,GenericFactory.getNamespaceURI(reader,content));
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