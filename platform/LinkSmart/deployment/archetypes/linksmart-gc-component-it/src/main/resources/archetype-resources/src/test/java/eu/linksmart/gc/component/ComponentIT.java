package eu.linksmart.gc.component;

import static org.junit.Assert.*;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;

import org.apache.log4j.Logger;

import eu.linksmart.it.utils.ITConfiguration;

/* 
 * @RunWith(PaxExam.class) hooks Pax Exam into JUnit and does its magic of 
 * setting up a test container with an OSGi framework and bundles under test
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class ComponentIT {

    private static Logger LOG = Logger.getLogger(ComponentIT.class.getName());

    /*
	 * @Inject triggers the injection of OSGi services into the test.
	 * the test method can access the BundleContext of the probe bundle or any service obtained from the OSGi service registry
	 */
    //@Inject
    //private Object exampleService;

    /*
     * @Configuration returns an array of configuration options. 
     * These options define the set of bundles to be provisioned to the OSGi framework in the test container 
     */
    @Configuration
    public Option[] config() {
        return new Option[] {
        		//
        		// this feature will install all LinkSmart bundles including their required dependencies
        		//
        		ITConfiguration.regressionDefaults(),
        		//
        		// this feature will install all LinkSmart bundles including their required dependencies
        		//
        		features(ITConfiguration.getTestingFeaturesRepoURL(),"linksmart-gc"),
        		//
        		// since this integration test is for a given bundle, therefore, that bundle also need to be provisioned inside OSGi container.
        		// any bundle can be installed by following parameter, change those values to fit your service artifact
        		//
        		mavenBundle("${groupId}","${artifactId}","${version}"),                  
        };
    }
    
    @Test
    public void testService() throws Exception {   
        try {
        	assertTrue(true);
        	LOG.info("test successfully completed");
        } catch(Exception e) {
        	fail(e.getMessage());
        }
    }
}