package eu.linksmart.it.utils;

import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;
import org.ops4j.pax.exam.karaf.options.LogLevelOption.LogLevel;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.Charset;

import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

public class ITConfiguration {
	
	private static String OSGI_CONTAINER_KARAF = "karaf"; 
	private static String OSGI_CONTAINER_SERVICEMIX = "servicemix"; 
	
	private static String KARAF_DISTRO_GROUP_ID = "org.apache.karaf"; 
	private static String KARAF_DISTRO_ARTIFACT_ID = "apache-karaf";  
	private static String KARAF_DISTRO_BINARY_TYPE = "zip";
	private static String KARAF_DISTRO_NAME = "Apache Karaf";
    // the version will be filtered by maven plugin
    private static String LINKSMART_GC_VERSION = "${linksmart.gc.version}";
    private static String KARAF_DISTRO_VERSION = "${karaf.version}";
    private static String CXF_VERSION = "${cxf.version}";
    private static String CXF_DOSGI_VERSION = "${cxf.dosgi.version}";
    
	private static boolean DEBUG = false;
	private static int DEBUG_PORT = 8889;
	
	private static int HTTP_PORT = 8882;
	
	private static final String CORE_FEATURES_REPOSITORY_URL = "mvn:eu.linksmart.gc.deployment/eu.linksmart.gc.deployment.features.core/" + LINKSMART_GC_VERSION + "/xml/features";
	
	private static final String TESTING_FEATURES_REPOSITORY_URL = "mvn:eu.linksmart.gc.deployment/eu.linksmart.gc.deployment.features.it/" + LINKSMART_GC_VERSION + "/xml/features";
	
	private static final String CXF_FEATURES_REPOSITORY_URL = "mvn:org.apache.cxf.karaf/apache-cxf/" + CXF_VERSION + "/xml/features";
	
	private static final String CXF_DOSGI_FEATURES_REPOSITORY_URL = "mvn:org.apache.cxf.dosgi/cxf-dosgi/" + CXF_DOSGI_VERSION + "/xml/features";
	
	public static Option regressionDefaults() {
        return regressionDefaults(OSGI_CONTAINER_KARAF);
    }
	
	public static Option regressionDefaults(String osgiContainerType) {
        return regressionDefaults(osgiContainerType, DEBUG, DEBUG_PORT, null);
    }
	
	public static Option regressionDefaults(boolean debugFlag) {
        return regressionDefaults(OSGI_CONTAINER_KARAF, debugFlag, DEBUG_PORT, null);
    }
	
	public static Option regressionDefaults(int debugPort) {
        return regressionDefaults(OSGI_CONTAINER_KARAF, true, debugPort, null);
    }

    public static Option regressionDefaults(String osgiContainerType, boolean debugFlag, int debugPort, String unpackDir) {
    	
    	switchPlatformEncodingToUTF8();
    	setOSGiContainer(osgiContainerType);
    	DEBUG = debugFlag;
    	DEBUG_PORT = debugPort;
    	
        return composite(

        		/*
        		 * if using apache service-mix distro, then no need to provision Jetty (for httpService), because Jetty Server is started up during bootstrapping of service-mix 
        		 */
        		// Provision and launch a container based on a distribution of Karaf (Apache ServiceMix/Plain Karaf)
        		karafDistributionConfiguration()
        			.frameworkUrl(mvnKarafDist())
        			.karafVersion(KARAF_DISTRO_VERSION)
        			.name(KARAF_DISTRO_NAME)
        			.unpackDirectory(unpackDir == null ? null : new File(unpackDir))
        			.useDeployFolder(false),

                /*
                 * disable remote SSH port for integration tests (speedup)
                 */
                systemProperty("karaf.startRemoteShell").value("false"),
                /*
                 * keeping container sticks around after the test so we can check the contents
                   of the data directory when things go wrong.        
                 */
                //keepRuntimeFolder(),
                /*
                 * don't bother with local console output as it just ends up cluttering the logs
                 */
                configureConsole().ignoreLocalConsole(),
                /*
                 * force the log level to INFO so we have more details during the test. It defaults to WARN.
                 */
                logLevel(LogLevel.INFO),
                
                // set the system property for pax web
                KarafDistributionOption.editConfigurationFilePut("etc/org.ops4j.pax.web.cfg", "org.osgi.service.http.port", Integer.toString(HTTP_PORT)),
                
                // extend feature repositories list with linksmart-features
                KarafDistributionOption.editConfigurationFileExtend("etc/org.apache.karaf.features.cfg", "featuresRepositories", CORE_FEATURES_REPOSITORY_URL),
                
                KarafDistributionOption.editConfigurationFileExtend("etc/org.apache.karaf.features.cfg", "featuresRepositories", CXF_FEATURES_REPOSITORY_URL),
                
                KarafDistributionOption.editConfigurationFileExtend("etc/org.apache.karaf.features.cfg", "featuresRepositories", CXF_DOSGI_FEATURES_REPOSITORY_URL),

                // disable the shutdown port (speedup)
                KarafDistributionOption.editConfigurationFilePut("etc/custom.properties", "karaf.shutdown.port", "-1"),
                
                /*
                 * activates debugging on the Karaf container using the provided port and holds
                 * the vm till you've attached the debugger.
                 */
                when(isDebug()).useOptions(KarafDistributionOption.debugConfiguration(Integer.toString(DEBUG_PORT), true)));
        
    }

    private static MavenArtifactUrlReference mvnKarafDist() {
        return maven().groupId(KARAF_DISTRO_GROUP_ID).artifactId(KARAF_DISTRO_ARTIFACT_ID).type(KARAF_DISTRO_BINARY_TYPE).version(KARAF_DISTRO_VERSION);
    }
    
    public static void enableDebug(boolean flag) {
    	DEBUG = flag;
    }
    
    public static boolean isDebug() {
        return DEBUG;
    }
    
    public static void setContainerHttpPort(int port) {
    	HTTP_PORT = port;
    }
    
    public static int getContainerHttpPort() {
    	return HTTP_PORT;
    }
    
    public static String getCoreFeaturesRepoURL() {
    	return CORE_FEATURES_REPOSITORY_URL;
    }
    
    public static String getTestingFeaturesRepoURL() {
    	return TESTING_FEATURES_REPOSITORY_URL;
    }
    
    private static void setOSGiContainer(String osgiContainerType) {
    	if(osgiContainerType.equals(OSGI_CONTAINER_KARAF)) {
    		KARAF_DISTRO_GROUP_ID = "org.apache.karaf"; 
    		KARAF_DISTRO_ARTIFACT_ID = "apache-karaf";  
    		KARAF_DISTRO_BINARY_TYPE = "zip"; 
    		KARAF_DISTRO_VERSION = "${karaf.version}";
    		KARAF_DISTRO_NAME = "Apache Karaf"; 
    	}
    	if(osgiContainerType.equals(OSGI_CONTAINER_SERVICEMIX)) {
    		KARAF_DISTRO_GROUP_ID = "org.apache.servicemix";  
    		KARAF_DISTRO_ARTIFACT_ID = "apache-servicemix";   
    		KARAF_DISTRO_BINARY_TYPE = "zip"; 
    		KARAF_DISTRO_VERSION = "5.0.0"; 
    		KARAF_DISTRO_NAME = "Apache ServiceMix";  
    	}
    }
    
    private static void switchPlatformEncodingToUTF8() {
        try {
          System.setProperty("file.encoding","UTF-8");
          Field charset = Charset.class.getDeclaredField("defaultCharset");
          charset.setAccessible(true);
          charset.set(null,null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
    
}
