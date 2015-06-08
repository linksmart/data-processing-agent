package eu.linksmart.gc.component;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;

import org.osgi.service.component.ComponentContext;

import org.apache.log4j.Logger;

//
// documentation on Maven SCR plugin: 
// http://felix.apache.org/documentation/subprojects/apache-felix-maven-scr-plugin/scr-annotations.html
//

//
// @Component is used for consuming a service that is declared by @Reference
//
@Component(name="${artifactId}", immediate=true)

//
// @Service is used for declaring a service so that SCR would register this object as a service into
// OSGi service registry
// @Service :: if object is implementing only one interface, then plugin automatically adds the interface name
// @Service({ObjectNameA.class, ObjectNameB.class}) :: in case of multiple interfaces are implemented by this class, and you want to include only 
// some of those interfaces in Component XML
//

public class Example {

    private static Logger LOG = Logger.getLogger(Example.class.getName());
    
    @Reference(name="ExampleService",
			cardinality = ReferenceCardinality.OPTIONAL_UNARY,
			policy=ReferencePolicy.DYNAMIC,
			bind="bindService", 
			unbind="unbindService")
    String exampleService;
    
    protected void bindService(String exampleService) {
    	this.exampleService = exampleService;
    }
    
    protected void unbindService(String exampleService) {
    	this.exampleService = null;
    }

    @Activate
    protected void activate(ComponentContext context) {
    	LOG.info("${artifactId} activated");
    }

    @Deactivate
    protected void deactivate(ComponentContext context) {
    	LOG.info("${artifactId} deactivated");
    }
        
}
