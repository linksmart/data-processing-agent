package eu.ebbits.pwal.tests.dev;

import junit.framework.Test;
import junit.framework.TestSuite;
/**
 * All-inclusive test suite, which includes all tests for the PWAL framework.
 *  
 * <p> 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @see        eu.ebbits.pwal
 * @since    PWAL 0.1.0
 */
public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite(AllTests.class.getName());
        //$JUnit-BEGIN$
        suite.addTestSuite(PWALDriverTest.class);
        suite.addTestSuite(PWALServiceDelegateTest.class);
        suite.addTestSuite(PWALEventsDelegateTest.class);
        suite.addTestSuite(PWALVariablesDelegateTest.class);
        suite.addTestSuite(PLCDriverTest.class);
        // TODO Add test for new drivers
        //$JUnit-END$
        return suite;
    }

}
