package eu.linksmart.gc.network.backbone.zmq;

import javax.inject.Inject;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;

import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import eu.linksmart.gc.api.network.backbone.Backbone;
import eu.linksmart.it.utils.ITConfiguration;


@RunWith(PaxExam.class)
public class BackboneZMQIT {
	
	private static Logger LOG = Logger.getLogger(BackboneZMQIT.class.getName());
	
	@Inject
    private Backbone backbone;
	
    @Configuration
    public Option[] config() {
        return new Option[] {
        		ITConfiguration.regressionDefaults(),
        		features(ITConfiguration.getTestingFeaturesRepoURL(),"gc-backbone-zmq-it"),  
        };
    }
    
    @Test
    public void testBackbone() {
    	
    	try {
    		
    		LOG.info("starting backbone-zmq test");
            
    		//
    		// this assertion might not work all the time if multiple backbone implementations (Http etc) available in run-time, then OSGi registry
    		// can return arbitrary implementation of Backbone interface 
    		//
    		
    		Assert.assertEquals("eu.linksmart.gc.network.backbone.zmq.BackboneZMQImpl",backbone.getClass().getName());
                		
    		LOG.info("backbone-zmq test successfully completed");
			
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("unable to access backboneimpl service");
		}
    }
    
}