package eu.ebbits.pwal.tests.dev;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Vector;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import eu.ebbits.pwal.api.PWAL;
import eu.ebbits.pwal.impl.driver.test.TestDriver;
import eu.ebbits.pwal.impl.driver.test.TestEventsDelegate;

/**
 * Event delegate tests, used to verify how driver can expose event generation.
 * 
 * This test is based on the {@link TestDriver} class.
 *  
 * <p> 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @see eu.ebbits.pwal
 * @since PWAL 0.1.0
 */
public class PWALEventsDelegateTest extends TestCase {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /*
     * All test in this case assume the use of local_driver
     * */
    private TestDriver local_driver;
    private TestEventsDelegate local_delegate;
    private int countFlowEvents = 0;
    private int countTemperatureEvents = 0;
    private int countPWALEvents = 0;
    private int countCoolingEvents = 0;
    private PWAL pwal = null;
    /**
     * To void time- or order-related issues, each case uses its own instance of local_driver.
     * (According to the JUnit workflow, the setUp class is run before each test in the TestCase)
     * 
     * @throw Exception - if something goes wrong during the set-up 
     * 
     */
    @Before
    public void setUp() throws Exception {
        this.local_driver = new TestDriver();
        this.local_delegate = (TestEventsDelegate) this.local_driver.getEventsDelegate();
        this.local_delegate.updatePWALEventsCollection();
        boolean isPwalFound = false;
        while(!isPwalFound) {
            try {
                //TODO come si recupera il context?
                // Get the PWAL interface
//                pwal = (PWAL) context
//                    .locateService(PWAL.class.getSimpleName());
                if (pwal != null) {
                    isPwalFound = true;
                } else {
                    Thread.sleep(5000);
                }
            } catch (Exception e) {
                System.out.println("Could not locate PWAL: " + e.getMessage());
            }
        }
    }

    /**
     * To void time- or order-related issues, each case uses its own instance of local_driver, which is cleaned up in this function.
     * (According to the JUnit workflow, the tearDown class is run after each test in the TestCase)
     *
     * @throw Exception - if something goes wrong during the tear down
     *
     */
    @After
    public void tearDown() throws Exception {
        this.local_driver = null;
        this.local_delegate = null;
        System.gc();
    }


    /**
     * This test checks whether events are exposed correctly.
     */
    @Test
    public final void testEventsExposure() {
        // The DummyDriver exposes 3 events, with different properties

        Assert.assertEquals(3, this.local_delegate.getPWALEventsCollectionSize());

        for (Event ev : this.local_delegate.getPWALEventsCollection()) {
            if (ev.getTopic().equals("pwal/llrpreader/readreport")) {
                Assert.assertEquals(ev.getPropertyNames().length, 1);
                Assert.assertEquals(ev.getProperty("report").getClass(), String.class);
            } else if (ev.getTopic().equals("pwal/coolingcircuit/temperature")) {
                Assert.assertEquals(ev.getPropertyNames().length, 2);
                Assert.assertEquals(ev.getProperty("temperature").getClass(), Float.class);
                Assert.assertEquals(ev.getProperty("timestamp").getClass(), Integer.class);
            } else if (ev.getTopic().equals("pwal/coolingcircuit/flowcontrol")) {
                Assert.assertEquals(ev.getPropertyNames().length, 1);
                Assert.assertEquals(ev.getProperty("flowcontrol").getClass(), Float.class);
            } else {
                fail("Unexpected topic name: " + ev.getTopic());
            }
        }
    }

    /**
     * Based on the events exposed by the previously tested description (i.e. in the testServicesExposure test), it is possible to generate events.
     */
    @Test
    public final void testEvents() {
        Dictionary<String, Object> properties = new Hashtable<String, Object>();
        properties.put("report", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><llrp:RO_ACCESS_REPORT xmlns:llrp=\"http://www.llrp.org/ltk/schema/core/encoding/xml/1.0\" Version=\"1\" MessageID=\"1238254711\">  <llrp:TagReportData>    <llrp:EPC_96>"+
                        "<llrp:EPC>202020202020544f54203132</llrp:EPC>" +
                        "    </llrp:EPC_96>    <llrp:ROSpecID>      <llrp:ROSpecID>1</llrp:ROSpecID>    </llrp:ROSpecID>    <llrp:SpecIndex>      <llrp:SpecIndex>1</llrp:SpecIndex>    </llrp:SpecIndex>    <llrp:InventoryParameterSpecID>      <llrp:InventoryParameterSpecID>9</llrp:InventoryParameterSpecID>    </llrp:InventoryParameterSpecID>    <llrp:AntennaID>      <llrp:AntennaID>1</llrp:AntennaID>    </llrp:AntennaID>    <llrp:PeakRSSI>      <llrp:PeakRSSI>-29</llrp:PeakRSSI>    </llrp:PeakRSSI>    <llrp:ChannelIndex>      <llrp:ChannelIndex>3</llrp:ChannelIndex>    </llrp:ChannelIndex>    <llrp:FirstSeenTimestampUTC>      <llrp:Microseconds>2013-06-25T08:05:21.699819+02:00</llrp:Microseconds>    </llrp:FirstSeenTimestampUTC>    <llrp:LastSeenTimestampUTC>      <llrp:Microseconds>2013-06-25T08:05:21.699819+02:00</llrp:Microseconds>    </llrp:LastSeenTimestampUTC>    <llrp:TagSeenCount>      <llrp:TagCount>1</llrp:TagCount>    </llrp:TagSeenCount>    <llrp:C1G2_PC>      <llrp:PC_Bits>12288</llrp:PC_Bits>    </llrp:C1G2_PC>    <llrp:C1G2_CRC>      <llrp:CRC>22789</llrp:CRC>    </llrp:C1G2_CRC>    <llrp:AccessSpecID>      <llrp:AccessSpecID>0</llrp:AccessSpecID>    </llrp:AccessSpecID>  </llrp:TagReportData></llrp:RO_ACCESS_REPORT>");
        Event reportEvent = new Event("pwal/llrpreader/readreport", properties);
        this.local_delegate.sendEvent(reportEvent);
        
        properties = new Hashtable<String, Object>();
        properties.put("temperature", new Float(15.9));
        properties.put("timestamp", new Integer(12312321));
        Event temperatureEvent = new Event("pwal/coolingcircuit/temperature", properties);
        this.local_delegate.sendEvent(temperatureEvent);
        
        
        properties = new Hashtable<String, Object>();
        properties.put("flowcontrol", new Float(15.9));
        Event flowEvent = new Event("pwal/coolingcircuit/flowcontrol", properties);
        this.local_delegate.sendEvent(flowEvent);
    }

    
    private class ServiceComponent implements EventHandler {        
        public void handleEvent(Event event) {
            if(event != null) {
            }
        }        
    }

    
    /**
     * This test verifies that the PWAL publish/subscribe mechanism works correctly. 
     */
    @Test
    public final void testSubscriptions() {
        ServiceComponent handler = new ServiceComponent();
        
        ServiceRegistration regReport = this.pwal.subscribeTopic("pwal/llrpreader/readreport",null,handler);
        Assert.assertNotNull(regReport);

        ServiceRegistration regTemp = this.pwal.subscribeTopic("pwal/coolingcircuit/temperature",null,handler);
        Assert.assertNotNull(regTemp);

        ServiceRegistration regFlow = this.pwal.subscribeTopic("pwal/coolingcircuit/flowcontrol",null,handler);
        Assert.assertNotNull(regFlow);

        try {
            this.pwal.unsubscribeTopic(regReport);
            this.pwal.unsubscribeTopic(regTemp);
            this.pwal.unsubscribeTopic(regFlow);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private class ReportHandler implements EventHandler {        
        public void handleEvent(Event ev) {
            if(ev != null) {
                Assert.assertEquals(ev.getPropertyNames().length, 1);
                Assert.assertEquals(ev.getProperty("report").getClass(), String.class);                
            }
        }        
    }

    private class TemperatureHandler implements EventHandler {        
        public void handleEvent(Event ev) {
            if(ev != null) {
                Assert.assertEquals(ev.getPropertyNames().length, 2);
                Assert.assertEquals(ev.getProperty("temperature").getClass(), Float.class);
                Assert.assertEquals(ev.getProperty("timestamp").getClass(), Integer.class);
            }
        }        
    }

    private class FlowHandler implements EventHandler {        
        public void handleEvent(Event ev) {
            if(ev != null) {
                Assert.assertEquals(ev.getPropertyNames().length, 1);
                Assert.assertEquals(ev.getProperty("flowcontrol").getClass(), Float.class);
            }
        }
    }
    
    
    /**
     * This test checks the event subscriptions, checking that the events are forwarded to the right handlers 
     */
    @Test
    public final void testEventsSubscribing() {
        ReportHandler reportHandler = new ReportHandler();
        TemperatureHandler temperatureHandler = new TemperatureHandler();
        FlowHandler flowHandler = new FlowHandler();
        
        Vector<ServiceRegistration> regs = new Vector<ServiceRegistration>();
        
        ServiceRegistration regReport = this.pwal.subscribeTopic("pwal/llrpreader/readreport",null,reportHandler);
        Assert.assertNotNull(regReport);
        regs.add(regReport);
        
        ServiceRegistration regTemp = this.pwal.subscribeTopic("pwal/coolingcircuit/temperature",null,temperatureHandler);
        Assert.assertNotNull(regTemp);
        regs.add(regTemp);

        ServiceRegistration regFlow = this.pwal.subscribeTopic("pwal/coolingcircuit/flowcontrol",null,flowHandler);
        Assert.assertNotNull(regFlow);
        regs.add(regFlow);
        
        Dictionary<String, Object> properties = new Hashtable<String, Object>();
        properties.put("report", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><llrp:RO_ACCESS_REPORT xmlns:llrp=\"http://www.llrp.org/ltk/schema/core/encoding/xml/1.0\" Version=\"1\" MessageID=\"1238254711\">  <llrp:TagReportData>    <llrp:EPC_96>"+
                        "<llrp:EPC>202020202020544f54203132</llrp:EPC>" +
                        "    </llrp:EPC_96>    <llrp:ROSpecID>      <llrp:ROSpecID>1</llrp:ROSpecID>    </llrp:ROSpecID>    <llrp:SpecIndex>      <llrp:SpecIndex>1</llrp:SpecIndex>    </llrp:SpecIndex>    <llrp:InventoryParameterSpecID>      <llrp:InventoryParameterSpecID>9</llrp:InventoryParameterSpecID>    </llrp:InventoryParameterSpecID>    <llrp:AntennaID>      <llrp:AntennaID>1</llrp:AntennaID>    </llrp:AntennaID>    <llrp:PeakRSSI>      <llrp:PeakRSSI>-29</llrp:PeakRSSI>    </llrp:PeakRSSI>    <llrp:ChannelIndex>      <llrp:ChannelIndex>3</llrp:ChannelIndex>    </llrp:ChannelIndex>    <llrp:FirstSeenTimestampUTC>      <llrp:Microseconds>2013-06-25T08:05:21.699819+02:00</llrp:Microseconds>    </llrp:FirstSeenTimestampUTC>    <llrp:LastSeenTimestampUTC>      <llrp:Microseconds>2013-06-25T08:05:21.699819+02:00</llrp:Microseconds>    </llrp:LastSeenTimestampUTC>    <llrp:TagSeenCount>      <llrp:TagCount>1</llrp:TagCount>    </llrp:TagSeenCount>    <llrp:C1G2_PC>      <llrp:PC_Bits>12288</llrp:PC_Bits>    </llrp:C1G2_PC>    <llrp:C1G2_CRC>      <llrp:CRC>22789</llrp:CRC>    </llrp:C1G2_CRC>    <llrp:AccessSpecID>      <llrp:AccessSpecID>0</llrp:AccessSpecID>    </llrp:AccessSpecID>  </llrp:TagReportData></llrp:RO_ACCESS_REPORT>");
        Event reportEvent = new Event("pwal/llrpreader/readreport", properties);
        this.local_delegate.sendEvent(reportEvent);
        
        properties = new Hashtable<String, Object>();
        properties.put("temperature", new Float(15.9));
        properties.put("timestamp", new Integer(12312321));
        Event temperatureEvent = new Event("pwal/coolingcircuit/temperature", properties);
        this.local_delegate.sendEvent(temperatureEvent);
        
        
        properties = new Hashtable<String, Object>();
        properties.put("flowcontrol", new Float(15.9));
        Event flowEvent = new Event("pwal/coolingcircuit/flowcontrol", properties);
        this.local_delegate.sendEvent(flowEvent);
        
        try {
            this.pwal.unsubscribeTopics(regs);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    
    
    private class CountReportHandler implements EventHandler {        
        public void handleEvent(Event ev) {
            if(ev != null) {
            }
        }        
    }

    private class CountTemperatureHandler implements EventHandler {        
        public void handleEvent(Event ev) {
            if(ev != null) {
                countTemperatureEvents++;
            }
        }        
    }

    private class CountFlowHandler implements EventHandler {        
        public void handleEvent(Event ev) {
            if(ev != null) {
                countFlowEvents++;
            }
        }
    }
    
    /**
     * This test checks the event subscriptions, checking that the events are forwarded to the right handlers
     * and checks the possibility to filter the events using content-filtering
     *  
     */
    @Test
    public final void testEventsSubscribingWithContentFilter() {
        CountReportHandler reportHandler = new CountReportHandler();
        CountTemperatureHandler temperatureHandler = new CountTemperatureHandler();
        CountFlowHandler flowHandler = new CountFlowHandler();
        
        ServiceRegistration regReport = this.pwal.subscribeTopic("pwal/llrpreader/readreport",null,reportHandler);
        Assert.assertNotNull(regReport);

        ServiceRegistration regTemp = this.pwal.subscribeTopic("pwal/coolingcircuit/temperature","(timestamp=111)",temperatureHandler);
        Assert.assertNotNull(regTemp);

        ServiceRegistration regFlow = this.pwal.subscribeTopic("pwal/coolingcircuit/flowcontrol","(flowcontrol=15.9)",flowHandler);
        Assert.assertNotNull(regFlow);

        
        Dictionary<String, Object> properties = new Hashtable<String, Object>();
        properties.put("report", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><llrp:RO_ACCESS_REPORT xmlns:llrp=\"http://www.llrp.org/ltk/schema/core/encoding/xml/1.0\" Version=\"1\" MessageID=\"1238254711\">  <llrp:TagReportData>    <llrp:EPC_96>"+
                        "<llrp:EPC>202020202020544f54203132</llrp:EPC>" +
                        "    </llrp:EPC_96>    <llrp:ROSpecID>      <llrp:ROSpecID>1</llrp:ROSpecID>    </llrp:ROSpecID>    <llrp:SpecIndex>      <llrp:SpecIndex>1</llrp:SpecIndex>    </llrp:SpecIndex>    <llrp:InventoryParameterSpecID>      <llrp:InventoryParameterSpecID>9</llrp:InventoryParameterSpecID>    </llrp:InventoryParameterSpecID>    <llrp:AntennaID>      <llrp:AntennaID>1</llrp:AntennaID>    </llrp:AntennaID>    <llrp:PeakRSSI>      <llrp:PeakRSSI>-29</llrp:PeakRSSI>    </llrp:PeakRSSI>    <llrp:ChannelIndex>      <llrp:ChannelIndex>3</llrp:ChannelIndex>    </llrp:ChannelIndex>    <llrp:FirstSeenTimestampUTC>      <llrp:Microseconds>2013-06-25T08:05:21.699819+02:00</llrp:Microseconds>    </llrp:FirstSeenTimestampUTC>    <llrp:LastSeenTimestampUTC>      <llrp:Microseconds>2013-06-25T08:05:21.699819+02:00</llrp:Microseconds>    </llrp:LastSeenTimestampUTC>    <llrp:TagSeenCount>      <llrp:TagCount>1</llrp:TagCount>    </llrp:TagSeenCount>    <llrp:C1G2_PC>      <llrp:PC_Bits>12288</llrp:PC_Bits>    </llrp:C1G2_PC>    <llrp:C1G2_CRC>      <llrp:CRC>22789</llrp:CRC>    </llrp:C1G2_CRC>    <llrp:AccessSpecID>      <llrp:AccessSpecID>0</llrp:AccessSpecID>    </llrp:AccessSpecID>  </llrp:TagReportData></llrp:RO_ACCESS_REPORT>");
        Event reportEvent = new Event("pwal/llrpreader/readreport", properties);
        this.local_delegate.sendEvent(reportEvent);
        
        properties = new Hashtable<String, Object>();
        properties.put("temperature", new Float(15.9));
        properties.put("timestamp", new Integer(111));
        Event temperatureEvent = new Event("pwal/coolingcircuit/temperature", properties);
        this.local_delegate.sendEvent(temperatureEvent);
        
        properties = new Hashtable<String, Object>();
        properties.put("temperature", new Float(15.9));
        properties.put("timestamp", new Integer(325));
        temperatureEvent = new Event("pwal/coolingcircuit/temperature", properties);
        this.local_delegate.sendEvent(temperatureEvent);
        
        // Only one event have to be received
        Assert.assertEquals(countTemperatureEvents, 1);
        
        properties = new Hashtable<String, Object>();
        properties.put("flowcontrol", new Float(15.9));
        Event flowEvent = new Event("pwal/coolingcircuit/flowcontrol", properties);
        this.local_delegate.sendEvent(flowEvent);
        
        properties = new Hashtable<String, Object>();
        properties.put("flowcontrol", new Float(18.9));
        flowEvent = new Event("pwal/coolingcircuit/flowcontrol", properties);
        this.local_delegate.sendEvent(flowEvent);
        
        // Only one event have to be received
        Assert.assertEquals(countFlowEvents, 1);        
    }
    
    
    
    private class CountPWALHandler implements EventHandler {        
        public void handleEvent(Event ev) {
            if(ev != null) {
                countPWALEvents++;
            }
        }
    }

    private class CountCoolingHandler implements EventHandler {        
        public void handleEvent(Event ev) {
            if(ev != null) {
                countCoolingEvents++;
            }
        }
    }

    
    /**
     * This test checks the event subscriptions, checking that the events are forwarded to the right handlers
     * and checks the possibility to filter the events using content-filtering
     *  
     */
    @Test
    public final void testEventsSubscribingMultipleTopics() {
        CountPWALHandler pwalHandler = new CountPWALHandler();
        CountCoolingHandler coolingHandler = new CountCoolingHandler();
        
        ServiceRegistration regPWAL = this.pwal.subscribeTopic("pwal/*",null,pwalHandler);
        Assert.assertNotNull(regPWAL);

        ServiceRegistration regTemp = this.pwal.subscribeTopic("pwal/coolingcircuit/*",null,coolingHandler);
        Assert.assertNotNull(regTemp);

        
        Dictionary<String, Object> properties = new Hashtable<String, Object>();
        properties.put("report", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><llrp:RO_ACCESS_REPORT xmlns:llrp=\"http://www.llrp.org/ltk/schema/core/encoding/xml/1.0\" Version=\"1\" MessageID=\"1238254711\">  <llrp:TagReportData>    <llrp:EPC_96>"+
                        "<llrp:EPC>202020202020544f54203132</llrp:EPC>" +
                        "    </llrp:EPC_96>    <llrp:ROSpecID>      <llrp:ROSpecID>1</llrp:ROSpecID>    </llrp:ROSpecID>    <llrp:SpecIndex>      <llrp:SpecIndex>1</llrp:SpecIndex>    </llrp:SpecIndex>    <llrp:InventoryParameterSpecID>      <llrp:InventoryParameterSpecID>9</llrp:InventoryParameterSpecID>    </llrp:InventoryParameterSpecID>    <llrp:AntennaID>      <llrp:AntennaID>1</llrp:AntennaID>    </llrp:AntennaID>    <llrp:PeakRSSI>      <llrp:PeakRSSI>-29</llrp:PeakRSSI>    </llrp:PeakRSSI>    <llrp:ChannelIndex>      <llrp:ChannelIndex>3</llrp:ChannelIndex>    </llrp:ChannelIndex>    <llrp:FirstSeenTimestampUTC>      <llrp:Microseconds>2013-06-25T08:05:21.699819+02:00</llrp:Microseconds>    </llrp:FirstSeenTimestampUTC>    <llrp:LastSeenTimestampUTC>      <llrp:Microseconds>2013-06-25T08:05:21.699819+02:00</llrp:Microseconds>    </llrp:LastSeenTimestampUTC>    <llrp:TagSeenCount>      <llrp:TagCount>1</llrp:TagCount>    </llrp:TagSeenCount>    <llrp:C1G2_PC>      <llrp:PC_Bits>12288</llrp:PC_Bits>    </llrp:C1G2_PC>    <llrp:C1G2_CRC>      <llrp:CRC>22789</llrp:CRC>    </llrp:C1G2_CRC>    <llrp:AccessSpecID>      <llrp:AccessSpecID>0</llrp:AccessSpecID>    </llrp:AccessSpecID>  </llrp:TagReportData></llrp:RO_ACCESS_REPORT>");
        Event reportEvent = new Event("pwal/llrpreader/readreport", properties);
        this.local_delegate.sendEvent(reportEvent);
        
        properties = new Hashtable<String, Object>();
        properties.put("temperature", new Float(15.9));
        properties.put("timestamp", new Integer(111));
        Event temperatureEvent = new Event("pwal/coolingcircuit/temperature", properties);
        this.local_delegate.sendEvent(temperatureEvent);
        
        properties = new Hashtable<String, Object>();
        properties.put("temperature", new Float(15.9));
        properties.put("timestamp", new Integer(325));
        temperatureEvent = new Event("pwal/coolingcircuit/temperature", properties);
        this.local_delegate.sendEvent(temperatureEvent);
                
        properties = new Hashtable<String, Object>();
        properties.put("flowcontrol", new Float(15.9));
        Event flowEvent = new Event("pwal/coolingcircuit/flowcontrol", properties);
        this.local_delegate.sendEvent(flowEvent);
        
        properties = new Hashtable<String, Object>();
        properties.put("flowcontrol", new Float(18.9));
        flowEvent = new Event("pwal/coolingcircuit/flowcontrol", properties);
        this.local_delegate.sendEvent(flowEvent);
        
        Assert.assertEquals(countPWALEvents, 5);
        Assert.assertEquals(countCoolingEvents, 4);
    }
}