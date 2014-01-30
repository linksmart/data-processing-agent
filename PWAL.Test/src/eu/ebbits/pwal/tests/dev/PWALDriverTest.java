package eu.ebbits.pwal.tests.dev;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.ebbits.pwal.impl.driver.PWALDriverImpl;
import eu.ebbits.pwal.impl.driver.PWALEventsDelegateImpl;
import eu.ebbits.pwal.impl.driver.PWALServicesDelegateImpl;
import eu.ebbits.pwal.impl.driver.PWALVariablesDelegateImpl;
import eu.ebbits.pwal.impl.driver.test.TestDriver;
import eu.ebbits.pwal.impl.driver.test.TestEventsDelegate;
import eu.ebbits.pwal.impl.driver.test.TestServicesDelegate;
import eu.ebbits.pwal.impl.driver.test.TestVariablesDelegate;
/**
 * Generic PWAL test, testing only the very basic structure of a PWAL driver.
 * This test is based on the {@link TestDriver} class.
 *  
 * <p> 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @see eu.ebbits.pwal
 * @since PWAL 0.1.0
 */
public class PWALDriverTest extends TestCase {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /*
     * All test in this case assume the use of local_driver
     * */
    TestDriver local_driver=null;
    
    /**
     * To void time- or order-related issues, each case uses its own instance of local_driver.
     * (According to the JUnit workflow, the setUp class is run before each test in the TestCase)
     * */
    @Before
    public void setUp() throws Exception {
        this.local_driver = new TestDriver();
    }

    
    /**
     * To void time- or order-related issues, each case uses its own instance of local_driver, which is cleaned up in this function.
     * (According to the JUnit workflow, the tearDown class is run after each test in the TestCase)
     * */
    @After
    public void tearDown() throws Exception {
        this.local_driver = null;
        System.gc();
    }

    
    /**
     * This test verifies the basic driver features (name, version and start/stop cycle).
     * @throws Exception 
     * */
    @Test
    public final void testPWALDriverControl() throws Exception {
        
        PWALDriverImpl ctrl_internal = this.local_driver;
        
        //check that the driver actually overrides the default name and version of the driver
        Assert.assertEquals("DummyDriver",ctrl_internal.getDriverName());
        Assert.assertEquals("0.0",ctrl_internal.getDriverVersion());
    }
    
    
    /**
     * This test simply checks the presence of the correct Variables delegate in the driver.
     * */
    @Test
    public final void testVariablesDelegate() {
        PWALVariablesDelegateImpl del = (PWALVariablesDelegateImpl) this.local_driver.getVariablesDelegate();
        Assert.assertEquals(TestVariablesDelegate.class, del.getClass());
        
    }
    
    /**
     * This test simply checks the presence of the correct Services delegate in the driver.
     * */
    @Test
    public final void testServicesDelegate() {
        PWALServicesDelegateImpl del = (PWALServicesDelegateImpl) this.local_driver.getServicesDelegate();
        Assert.assertEquals(TestServicesDelegate.class, del.getClass());

    }
    
    /**
     * This test simply checks the presence of the correct Events delegate in the driver.
     * */
    @Test
    public final void testEventsDelegate() {
        PWALEventsDelegateImpl del = (PWALEventsDelegateImpl) this.local_driver.getEventsDelegate();
        Assert.assertEquals(TestEventsDelegate.class, del.getClass());

    }

}
