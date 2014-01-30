package eu.ebbits.pwal.impl.driver.opcxmlda.stub;

import java.io.IOException;
import java.rmi.RemoteException;

import javax.xml.namespace.QName;

import org.apache.axis2.AxisFault;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import eu.ebbits.pwal.impl.driver.opcxmlda.stub.Browse;
import eu.ebbits.pwal.impl.driver.opcxmlda.stub.BrowseElement;
import eu.ebbits.pwal.impl.driver.opcxmlda.stub.BrowseFilter;
import eu.ebbits.pwal.impl.driver.opcxmlda.stub.BrowseResponse;
import eu.ebbits.pwal.impl.driver.opcxmlda.stub.ItemValue;
import eu.ebbits.pwal.impl.driver.opcxmlda.stub.Read;
import eu.ebbits.pwal.impl.driver.opcxmlda.stub.ReadRequestItem;
import eu.ebbits.pwal.impl.driver.opcxmlda.stub.ReadRequestItemList;
import eu.ebbits.pwal.impl.driver.opcxmlda.stub.ReadResponse;
import eu.ebbits.pwal.impl.driver.opcxmlda.stub.RequestOptions;

/**
 * 
 * Inner class, wrapping Webservices calls to OPC server
 *  
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author     ISMB
 * @version    %I%, %G%
 * @see        eu.ebbits.pwal.impl.driver.PWALDriver
 * @since      PWAL 0.1.0
 */
public class OPCXMLDataAccessClient {
    
    /* the logger */
    private Logger log =  Logger.getLogger(OPCXMLDataAccessClient.class.getName());
    
    /**
     * Returns the type from a qualifier
     * 
     * @param typeQualifier - the qualifier to use as <code>String</code>
     * 
     * @return the type as <code>OPCXMLDataAccessNumericTypes</code>
     */
    public static OPCXMLDataAccessNumericTypes typeFromString(String typeQualifier) {
        return null;
    }

    // TODO consider these lines: they could be moved to a more LinkSmart-like
    // approach; Or add Set/Get + XML file as delegate in the general framework.
    //TODO we might also consider to insert a "tree-like" exploration of variables and PLCs.
    private static final String DEFAULT_ENDPOINT_PREFIX = "http://";
    private static final String DEFAULT_ENDPOINT_HOSTNAME = "projects.ismb.it";
    private static final String DEFAULT_ENDPOINT_PORT = "18085";
    private static final String DEFAULT_ENDPOINT_PATH = "/OPCXMLServer/sopcweb.asmx";
    private static final String DEFAULT_ENDPOINT_POSTFIX = "?WSDL";
    
    private static final int HTTP_OK = 200;
    
    private static final int TIME_CONST = 256;
    
    private static final int PARAMS_SIZE = 3;
    
    private static final int MAX_ELEMENTS_RETURNED = 1000;
    
    private String opcXmlServerEndpoint = DEFAULT_ENDPOINT_PREFIX + DEFAULT_ENDPOINT_HOSTNAME + ":" + DEFAULT_ENDPOINT_PORT + DEFAULT_ENDPOINT_PATH;
    private String opcXmlAccessProtocol = "\\SYM:\\";
    private String opcXmlSymbolPath = "Stazione SIMATIC 400.CPU 416F-2";

    private HttpClient httpcli;
    private int httpclienthandleseqno = 0;

    private OPCXMLDataAccessStub proxyx = null;

    /**
     * Retrieves the server endpoint
     * 
     * @return a <code>String</code> containing the server endpoint    
     *  
     */
    public String getOpcXmlServerEndpoint() {
        return opcXmlServerEndpoint;
    }

    /**
     * Sets the server endpoint
     * 
     * @param opcXmlServerEndpoint the server enpoint to be set
     */
    public void setOpcXmlServerEndpoint(String opcXmlServerEndpoint) {
        this.opcXmlServerEndpoint = opcXmlServerEndpoint;
    }

    /**
     * Retrieves the access protocol used 
     * 
     * @return a <code>String</code> containing the access protocol used
     */
    public String getOpcXmlAccessProtocol() {
        return opcXmlAccessProtocol;
    }

    /**
     * Sets the access protocol to be used
     * 
     * @param opcXmlAccessProtocol the access protocol to be used
     */
    public void setOpcXmlAccessProtocol(String opcXmlAccessProtocol) {
        this.opcXmlAccessProtocol = opcXmlAccessProtocol;
    }

    /**
     * Retrieves the OPC symbolic name
     * 
     *  @return a String containing the OPC symbolic name
     */
    public String getOpcXmlSymbolPath() {
        return this.opcXmlSymbolPath;
    }

    /**
     * Set the OPC symbolic name
     * 
     * @param opcXmlSymbolPath the symbolic name to set
     */
    public void setOpcXmlSymbolPath(String opcXmlSymbolPath) {
        this.opcXmlSymbolPath = opcXmlSymbolPath;
    }

    /**
     * Tests the connection with the server
     * 
     * @return <code>true</code> if the connection is ok, <code>false</code> if something goes wrong
     */
    public boolean testConnection() {
        if (this.httpcli == null) {
            this.httpcli = new HttpClient();
        }
        // test url is: http://127.0.0.1:4444/OPCXMLServer/sopcweb.asmx
        GetMethod method = new GetMethod(this.opcXmlServerEndpoint);

        method.getParams().setParameter("Accept-Encoding", "gzip,deflate");
        method.getParams().setParameter("Content-Type", "text/xml;charset=UTF-8");
        method.setRequestHeader("Content-type", "text/xml; charset=ISO-8859-1");
        method.setRequestHeader("SOAPAction", "http://opcfoundation.org/webservices/XMLDA/1.0/Write");
        method.setQueryString("WSDL");
        // method.addParameter("", xmldata);
        // method.setRequestBody(xmldata);
        int a;
        try {
             a = this.httpcli.executeMethod(method);
        } catch (HttpException e) {
            log.error(e.getMessage(),e);
            return false;
        } catch (IOException e) {
            log.error(e.getMessage(),e);
            return false;
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return false;
        } 
        if(a!=HTTP_OK) {
            log.warn("expected HTTP code 200, but was ["+a+"]");
        }
        return true;
    }

    /**
     * Reads the variable
     * 
     * @param variablepath - the path of the variable to read
     * 
     * @return a <code>String</code> containing the variable value
     */
    public synchronized String read(String variablepath) {
        String[] arr = new String[1];
        arr[0] = variablepath;
        return this.read(arr)[0];
    }

    /**
     * Method used to write a value in a variable
     * 
     * @param variablepath - path of the variable
     * @param valued - the value to write (double)
     * @param t - type of the value:
     *         REAL, BOOLEAN, INT, SIMATIC_TIME
     * 
     */
    public synchronized void writeOld(String variablepath, double valued, OPCXMLDataAccessNumericTypes t) {
//        Assert.fail("DOVE BECCO IL TYPE ? - forse lo devo beccare in fase di browse!");

        // FIXME dirty trick: the version using actual SOAP calls is buggy, and
        // its being fixed ... Currently we use a this version forging SOAP
        // requests manually.
        this.manualwrite(variablepath, valued, t);
    }

    /**
     * Reads a set of variables
     * 
     * @param variablepathx a list of paths of the variables
     * 
     * @return a list of the variables' values read
     * 
     */
    public synchronized String[] read(String[] variablepathx) {

        // FAKE testing implementation by Riccardo
        /*
         * tmp_seqno++; return
         * this.tmp_examplevalues[tmp_seqno%(tmp_examplevalues.length)];
         */
        Read read = new Read();
        ReadRequestItemList itemlist = new ReadRequestItemList();
        RequestOptions options = new RequestOptions();

        int handleseqno = 0;
        for (String pathx : variablepathx) {
            ReadRequestItem item = new ReadRequestItem();
            String actualreqitem=this.opcXmlSymbolPath + "." + pathx;
            item.setItemName(actualreqitem);
            //System.out.println("richiedo: ["+actualreqitem+"]");
            item.setClientItemHandle("handle" + handleseqno);
            item.setMaxAge(0);
            itemlist.addItems(item);
            handleseqno++;
        }
        options.setReturnErrorText(true);
        options.setReturnDiagnosticInfo(true);
        options.setReturnItemTime(true);
        options.setReturnItemName(true);
        options.setReturnItemPath(true);
        options.setClientRequestHandle("pippo");
        options.setLocaleID("");
        
        /*
         * ReplyBaseHolder readResult = new ReplyBaseHolder();
         * OPCErrorArrayHolder errors = new OPCErrorArrayHolder();
         */
        read.setItemList(itemlist);
        read.setOptions(options);
        ReadResponse ret = null;

        if (this.proxyx == null) {
            try {
                this.proxyx = new OPCXMLDataAccessStub(this.opcXmlServerEndpoint + DEFAULT_ENDPOINT_POSTFIX);
            } catch (AxisFault e) {
                log.error(e.getStackTrace());
                // LOG.error("AXISFAULT!!!");

            }

            if (this.proxyx == null) {
                return null;
            }
        }

        try {
            ret = this.proxyx.read(read);
        } catch (RemoteException e) {
            log.error(e.getMessage(),e);
        }

        
        ItemValue[] items = ret.getRItemList().getItems();
        String[] doubleret = new String[items.length];
        
        for (int i = 0; i < items.length; i++) {
            log.debug(items[i].getItemName());
            log.debug(items[i].getValue().getText());
            
            log.debug(items[i]);
            
            if(items[i]==null) {                
                log.error("items["+i+"] null on request for ["+variablepathx[i]+"]");
            }
            
            if(items[i].getValue()==null) {
                log.error("Value of items["+i+"] null on request for ["+variablepathx[i]+"]");
            }
            
            //OMElement val = items[i].getValue();
            //System.out.println(val.toString());
            
            //FIXME Maybe I could make some sanitization here before storing into OPCValueTypeTuple... This stuff comes from a webservice after all...
            doubleret[i] = items[i].getValue().getText();
        }
        return doubleret;
    }

    /**
     * Method used to write a value in a variable
     * 
     * @param variablepath - path of the variable
     * @param valued - the value to write (double)
     * @param type - type of the value:
     *         REAL, BOOLEAN, INT, SIMATIC_TIME
     */
    public synchronized void write(String variablepath, double valued, OPCXMLDataAccessNumericTypes type) {
        // FIXME dirty trick: the version using actual SOAP calls is buggy, and
        // its being fixed ... Currently we use a this version forging SOAP
        // requests manually.
//        Assert.fail("SM"); // this has not been tested with Junit, as it needs interaction with the actual PLC.
        this.manualwrite(variablepath, valued, type);
    }

    /**
     * Method used to write values in a set of variables
     * 
     * @param variablepath - list of paths of the variables to write
     * @param valued - values to be write
     * @param type - types of the values:
     *          REAL, BOOLEAN, INT, SIMATIC_TIME
     */
    public synchronized void write(String variablepath[], double valued[], OPCXMLDataAccessNumericTypes type[]) {
        // FIXME dirty trick: the version using actual SOAP calls is buggy, and
        // its being fixed ... Currently we use a this version forging SOAP
        // requests manually.
//        Assert.fail("SM"); // this has not been tested with Junit, as it needs interaction with the actual PLC.
        for (int i = 0; (i < variablepath.length) && (i < valued.length) && (i < type.length); i++) {
            this.manualwrite(variablepath[i], valued[i], type[i]);
        }
    }

    /**
     * Method used to forcing SOAP requests manually
     * 
     * @param variablepath - path of the variable
     * @param valued - the value to write (double)
     * @param type - type of the value:
     *         REAL, BOOLEAN, INT, SIMATIC_TIME
     */
    @SuppressWarnings("deprecation")
    private void manualwrite(String variablepath, double dvalue, OPCXMLDataAccessNumericTypes type) {
//        Assert.fail("SM"); // this has not been tested with Junit, as it needs interaction with the actual PLC.
        httpclienthandleseqno++;
        String typetag = "xsd:float";
        String svalue = "17";

        switch (type) {
        case REAL:
            typetag = "xsd:float";
            svalue = dvalue + "";
            break;
        case BOOLEAN:
            typetag = "xsd:boolean";
            if (dvalue == 0) {
                svalue = "false";
            } else {
                svalue = "true";
            }
            break;
        case INT:
            typetag = "xsd:short";
            int ivalue = (int) dvalue;
            svalue = ivalue + "";
            break;
        case SIMATIC_TIME:
            ivalue = (int) dvalue;
            ivalue = ivalue * TIME_CONST;
            svalue = ivalue + "";
            typetag = "xsd:unsignedShort";
            break;
        default:
            break;
        }

        String xmldata = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://opcfoundation.org/webservices/XMLDA/1.0/\"  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">"
                + "<soapenv:Header/>"
                + "<soapenv:Body>"
                + "<ns:Write>"
                + "<ns:Options/>"
                + "<ns:ItemList>"
                + "<ns:Items ItemName=\""
                + variablepath
                + "\" ClientItemHandle=\"handle"
                + httpclienthandleseqno
                + "\">"
                + "<ns:Value xsi:type=\""
                + typetag
                + "\">"
                + svalue
                + "</ns:Value>"
                + "</ns:Items>"
                + "</ns:ItemList>"
                + "</ns:Write>"
                + "</soapenv:Body>"
                + "</soapenv:Envelope>";

        if (this.httpcli == null) {
            this.httpcli = new HttpClient();
        }

        // test url is http://127.0.0.1:4444/OPCXMLServer/sopcweb.asmx
        PostMethod method = new PostMethod(this.opcXmlServerEndpoint);

        method.getParams().setParameter("Accept-Encoding", "gzip,deflate");
        method.getParams().setParameter("Content-Type", "text/xml;charset=UTF-8");
        method.setRequestHeader("Content-type", "text/xml; charset=ISO-8859-1");
        method.setRequestHeader("SOAPAction", "http://opcfoundation.org/webservices/XMLDA/1.0/Write");
        method.setQueryString("WSDL");
        // method.addParameter("", xmldata);
        method.setRequestBody(xmldata);

        try {
            this.httpcli.executeMethod(method);
        } catch (HttpException e) {
            log.error(e.getMessage(),e);
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        /*
         * try { BufferedReader br = new BufferedReader(new
         * InputStreamReader(method.getResponseBodyAsStream())); String
         * readLine; while(((readLine = br.readLine()) != null)) {
         * System.err.println(readLine); } } catch (IOException e) { // TODO
         * Auto-generated catch block e.printStackTrace(); }
         */
    }

    /**
     * Browses the server
     * 
     * @return list of the elements retrieved
     * 
     * @throws RemoteException - if something goes wrong browsing the server
     * 
     */
    public String[] browseAll() throws RemoteException {
        Browse browse = new Browse();
        
        //eu.ebbits.pwal.impl.driver.pwaldriver_plc.support_libs.opc.OPCXMLDataAccessStub.ReadRequestItemList itemlist = new eu.ebbits.pwal.impl.driver.pwaldriver_plc.support_libs.opc.OPCXMLDataAccessStub.ReadRequestItemList();
        //eu.ebbits.pwal.impl.driver.pwaldriver_plc.support_libs.opc.OPCXMLDataAccessStub.RequestOptions options = new eu.ebbits.pwal.impl.driver.pwaldriver_plc.support_libs.opc.OPCXMLDataAccessStub.RequestOptions();
        
        BrowseFilter filter = BrowseFilter.Factory.fromValue("all");
        
        QName[] params = new QName[PARAMS_SIZE];
        params[0]=new QName("dataType");
        params[1]=new QName("quality");
        params[2]=new QName("timestamp");
        

/*    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ns=OPC_NAMESPACE>
   <soapenv:Header/>
   <soapenv:Body>
     <ns:Browse LocaleID="" ClientRequestHandle="XYZ" ItemName="\SYM:\Stazione SIMATIC 400\CPU 416F-2\Memory\Sensors" MaxElementsReturned="1000" BrowseFilter="all" ElementNameFilter="" VendorFilter="" ReturnAllProperties="false" ReturnPropertyValues="true">
        <!--Zero or more repetitions:-->
        <ns:PropertyNames>dataType</ns:PropertyNames>
        <ns:PropertyNames>quality</ns:PropertyNames>
        <ns:PropertyNames>timestamp</ns:PropertyNames>
     </ns:Browse>
   </soapenv:Body>
</soapenv:Envelope>*/
        
        browse.setLocaleID("");
        
        browse.setClientRequestHandle("XYZ");
        //TODO this should go in the conf file
        //TODO Actually in future releases we might go beyond, and use this to browse all PLCs connected to this OPC server 
        browse.setItemName(this.opcXmlAccessProtocol + this.opcXmlSymbolPath.replace(".", "\\"));
        browse.setMaxElementsReturned(MAX_ELEMENTS_RETURNED);
        //read.setItemList(itemlist);
        //browse.setOptions(options);
        browse.setBrowseFilter(filter);
        browse.setReturnAllProperties(false);
        browse.setReturnPropertyValues(false);
        
        
        browse.setPropertyNames(params);
        
        BrowseResponse retx = null;

        if (this.proxyx == null) {
            try {
                this.proxyx = new OPCXMLDataAccessStub(this.opcXmlServerEndpoint + DEFAULT_ENDPOINT_POSTFIX);
            } catch (AxisFault e) {
                log.error(e.getStackTrace());
            }

            if (this.proxyx == null) {
                return null;
            }
        }

//        try {
            //ret = this.proxyx.read(read);
            retx = this.proxyx.browse(browse);
            
            BrowseElement[] els = retx.getElements();
            String[] ret=new String[els.length];
            
            int i=0;
            for(BrowseElement el: els) {
                //TODO in the future we could actually also exploit the full itemname ... or other parameters here.
                //System.out.println(el.getItemName());
                //System.out.println(el.getName());
                ret[i++]=el.getName();

            }
            
            return ret;
            
//        } catch (RemoteException e) {
//            LOG.error(e.getMessage(), e);
            //TODO: throw proper error object
//            return null;
//        }
    }
}