package it.ismb.pertlab.pwal.wsn.driver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class App 
{
	private static final Logger log=LoggerFactory.getLogger(App.class);
    public static void main( String[] args )
    {
       WSNManager s=new WSNManager();
       s.start();
       log.debug("aaa");
    }
}
