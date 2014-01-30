package eu.ebbits.pwal.tests.dev;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.ebbits.pwal.api.driver.PWALDelegateSubscriber;
import eu.ebbits.pwal.api.driver.PWALVariablesDelegate;
import eu.ebbits.pwal.api.model.PWALControlEvent;
import eu.ebbits.pwal.impl.driver.opcxmlda.OPCDriverImpl;

/**
 * This test case includes all test needed to verify the functionalities of the PLC driver and its functionalities.
 * Note: due to the PLC nature, the current PLC driver implementation mostly relies on the variable delegates.
 *  
 * <p> 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @see eu.ebbits.pwal
 * @since PWAL 0.1.0
 */
public class PLCDriverTest extends TestCase implements PWALDelegateSubscriber {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /*
     * All test in this case assume the use of local_driver
     * */
    OPCDriverImpl local_driver = null;

    /*
     * Each case uses its own instance of local_driver. It is instantiated here.
     * */
    @Before
    public void setUp() throws Exception {
        this.cnt_driverstarted = 0;
//        this.cnt_driverstopped = 0;
//        this.cnt_debug = 0;
//        this.cnt_info = 0;
//        this.cnt_warning = 0;
//        this.cnt_error = 0;
//        this.cnt_critical = 0;
        this.local_driver = new OPCDriverImpl();
    }

    private int cnt_driverstarted;
//    private int cnt_driverstopped;
//    private int cnt_debug;
//    private int cnt_info;

//    private int cnt_warning;
//    private int cnt_error;
//    private int cnt_critical;

    /**
     * Each case uses its own instance of local_driver, this function cleans it
     * up
     * */
    @After
    public void tearDown() throws Exception {
        this.local_driver = null;
        System.gc();
    }

    @Test
    public void testTypesAndConnection() throws Exception {
        Assert.assertNotNull(this.local_driver);
        Assert.assertNotNull(this.local_driver.getEventsDelegate());
        Assert.assertNotNull(this.local_driver.getVariablesDelegate());
        Assert.assertNotNull(this.local_driver.getServicesDelegate());

        Assert.assertEquals("0.2.0", this.local_driver.getDriverVersion());
        Assert.assertEquals("OPCDriver", this.local_driver.getDriverName());

        this.local_driver.subscribe(this);

        Assert.assertEquals(0, this.cnt_driverstarted);
        this.local_driver.start();
        Assert.assertEquals(1, this.cnt_driverstarted);

    }
/*
    @Test
    public void testConnectionWrongAddress() {
        this.local_driver.subscribe(this);
        // this should make the address wrong, thus generating a critical
        this.local_driver.configure("conf_endpoint", "http://wrongaddress:1234");

        Assert.assertEquals(0, this.cnt_driverstarted);
        Assert.assertEquals(0, this.cnt_critical);
        this.local_driver.start();
        Assert.assertEquals(1, this.cnt_driverstarted);
        Assert.assertEquals(2, this.cnt_critical);
    }
*/
    @Test
    // this steps assumes the PLC is running the M12 demo PLC program
    public void testVariableDelegateAvailables() throws Exception {

        this.local_driver.subscribe(this);

        Assert.assertEquals(0, this.cnt_driverstarted);
        this.local_driver.start();
        Assert.assertEquals(1, this.cnt_driverstarted);

        PWALVariablesDelegate var_delegate = this.local_driver.getVariablesDelegate();

        // nobody updated the main variable tables yet...
        Assert.assertEquals(0, var_delegate.getPWALVariablesCollection().size());
        Assert.assertEquals(0, var_delegate.getPWALVariablesCollectionSize());

        var_delegate.updatePWALVariablesCollection();

//        Assert.assertEquals(8, var_delegate.getPWALVariablesCollection().size());
//        Assert.assertEquals(8, var_delegate.getPWALVariablesCollectionSize());

        Assert.assertNull((var_delegate.getPWALVariable("pippo")));
        Assert.assertNull((var_delegate.getPWALVariable("pluto")));

        Assert.assertNotNull((var_delegate.getPWALVariable("Step_Number_Station")));
        Assert.assertNotNull((var_delegate.getPWALVariable("Automatic_Mode_On")));
        Assert.assertNotNull((var_delegate.getPWALVariable("Manual_Mode_On")));
        Assert.assertNotNull((var_delegate.getPWALVariable("No_Fault")));
/*        
        Assert.assertNotNull((var_delegate.getPWALVariable("temperature_in")));
        Assert.assertNotNull((var_delegate.getPWALVariable("temperature_mid")));
        Assert.assertNotNull((var_delegate.getPWALVariable("temperature_out")));
        Assert.assertNotNull((var_delegate.getPWALVariable("robot_powersupply_V")));
        Assert.assertNotNull((var_delegate.getPWALVariable("robot_powersupply_C")));
        Assert.assertNotNull((var_delegate.getPWALVariable("cooling_powersupply_V")));
        Assert.assertNotNull((var_delegate.getPWALVariable("cooling_powersupply_C")));
        Assert.assertNotNull((var_delegate.getPWALVariable("water_flow")));
*/
    }

    @Test
    // this steps assumes the PLC is running the M12 demo PLC program
    public void testVariableDelegateMonitored() throws Exception {

        this.local_driver.subscribe(this);

        Assert.assertEquals(0, this.cnt_driverstarted);
        this.local_driver.start();
        Assert.assertEquals(1, this.cnt_driverstarted);

//        PLCVariablesDelegate var_delegate = (PLCVariablesDelegate) this.local_driver.getVariablesDelegate();
        PWALVariablesDelegate var_delegate = this.local_driver.getVariablesDelegate();

        // nobody updated the main variable tables yet...
        Assert.assertEquals(0, var_delegate.getPWALVariablesCollection().size());
        Assert.assertEquals(0, var_delegate.getPWALVariablesCollectionSize());

        var_delegate.updatePWALVariablesCollection();

//        Assert.assertEquals(8, var_delegate.getPWALVariablesCollection().size());
//        Assert.assertEquals(8, var_delegate.getPWALVariablesCollectionSize());

        Assert.assertEquals(0, var_delegate.getMonitoredPWALVariablesCollectionSize());

        // these are not monitored, read should return null
        Assert.assertNull((var_delegate.read("pippo")));
        Assert.assertNull((var_delegate.read("pluto")));
        
        Assert.assertNull((var_delegate.read("Step_Number_Station")));
        Assert.assertNull((var_delegate.read("Automatic_Mode_On")));
        Assert.assertNull((var_delegate.read("Manual_Mode_On")));
        Assert.assertNull((var_delegate.read("No_Fault")));
/*        
        Assert.assertNull((var_delegate.read("temperature_in")));
        Assert.assertNull((var_delegate.read("temperature_mid")));
        Assert.assertNull((var_delegate.read("temperature_out")));
        Assert.assertNull((var_delegate.read("robot_powersupply_V")));
        Assert.assertNull((var_delegate.read("robot_powersupply_C")));
        Assert.assertNull((var_delegate.read("cooling_powersupply_V")));
        Assert.assertNull((var_delegate.read("cooling_powersupply_C")));
        Assert.assertNull((var_delegate.read("water_flow")));
*/
        // even if these are not monitored, readnow should not return null, but
        // a valid value (if the variable exist)
        Assert.assertNull((var_delegate.readNow("pippo")));
        Assert.assertNull((var_delegate.readNow("pluto")));
        
        Assert.assertNotNull((var_delegate.readNow("Step_Number_Station")));
        Assert.assertNotNull((var_delegate.readNow("Automatic_Mode_On")));
        Assert.assertNotNull((var_delegate.readNow("Manual_Mode_On")));
        Assert.assertNotNull((var_delegate.readNow("No_Fault")));
/*        
        Assert.assertNotNull((var_delegate.readNow(var_delegate.getPWALVariable("temperature_in"))));
        Assert.assertNotNull((var_delegate.readNow(var_delegate.getPWALVariable("temperature_mid"))));
        Assert.assertNotNull((var_delegate.readNow(var_delegate.getPWALVariable("temperature_out"))));
        Assert.assertNotNull((var_delegate.readNow(var_delegate.getPWALVariable("robot_powersupply_V"))));
        Assert.assertNotNull((var_delegate.readNow(var_delegate.getPWALVariable("robot_powersupply_C"))));
        Assert.assertNotNull((var_delegate.readNow(var_delegate.getPWALVariable("cooling_powersupply_V"))));
        Assert.assertNotNull((var_delegate.readNow(var_delegate.getPWALVariable("cooling_powersupply_C"))));
        Assert.assertNotNull((var_delegate.readNow(var_delegate.getPWALVariable("water_flow"))));
*/
        // in the start configuration all variables are zero
//        Assert.assertNotNull(var_delegate.readNow(var_delegate.getPWALVariable("Step_Number_Station")));
        Assert.assertTrue(0.0 <= var_delegate.readNow("Step_Number_Station").toDouble());
        System.out.println("Read Step_Number_Station = " + var_delegate.read("Step_Number_Station"));
        
//        Assert.assertNotNull(var_delegate.readNow(var_delegate.getPWALVariable("Automatic_Mode_On")));
        Assert.assertTrue(0.0 <= var_delegate.readNow("Automatic_Mode_On").toDouble());
        System.out.println("Read Automatic_Mode_On = " + var_delegate.read("Automatic_Mode_On"));
        
//        Assert.assertNotNull(var_delegate.readNow(var_delegate.getPWALVariable("Manual_Mode_On")));
        Assert.assertTrue(0.0 <= var_delegate.readNow("Manual_Mode_On").toDouble());
        System.out.println("Read Manual_Mode_On = " + var_delegate.read("Manual_Mode_On"));
        
//        Assert.assertNotNull(var_delegate.readNow(var_delegate.getPWALVariable("No_Fault")));
        Assert.assertTrue(0.0 <= var_delegate.readNow("No_Fault").toDouble());
        System.out.println("Read No_Fault = " + var_delegate.read("No_Fault"));
/*        
        Assert.assertEquals(0,var_delegate.readNow(var_delegate.getPWALVariable("temperature_in")).toDouble(),0.001);
        Assert.assertEquals(0,var_delegate.readNow(var_delegate.getPWALVariable("temperature_mid")).toDouble(),0.001);
        Assert.assertEquals(0,var_delegate.readNow(var_delegate.getPWALVariable("temperature_out")).toDouble(),0.001);
        Assert.assertEquals(0,var_delegate.readNow(var_delegate.getPWALVariable("robot_powersupply_V")).toDouble(),0.001);
        Assert.assertEquals(0,var_delegate.readNow(var_delegate.getPWALVariable("robot_powersupply_C")).toDouble(),0.001);
        Assert.assertEquals(0,var_delegate.readNow(var_delegate.getPWALVariable("cooling_powersupply_V")).toDouble(),0.001);
        Assert.assertEquals(0,var_delegate.readNow(var_delegate.getPWALVariable("cooling_powersupply_C")).toDouble(),0.001);
        Assert.assertEquals(0,var_delegate.readNow(var_delegate.getPWALVariable("water_flow")).toDouble(),0.001);
*/
    }

    @Override
    public void driverStarted(PWALControlEvent e) {
        this.cnt_driverstarted++;

    }

    @Override
    public void driverStopped(PWALControlEvent e) {
        fail("This Event is not valid in this testcase");

    }

    @Override
    public void driverDebug(PWALControlEvent e) {
        fail("This Event is not valid in this testcase");

    }

    @Override
    public void driverInfo(PWALControlEvent e) {

    }

    @Override
    public void driverWarning(PWALControlEvent e) {
        fail("This Event is not valid in this testcase");

    }

    @Override
    public void driverError(PWALControlEvent e) {
        fail("This Event is not valid in this testcase");

    }

    @Override
    public void driverCriticalError(PWALControlEvent e) {
        // System.out.println(e);
//        this.cnt_critical++;
    }

}
