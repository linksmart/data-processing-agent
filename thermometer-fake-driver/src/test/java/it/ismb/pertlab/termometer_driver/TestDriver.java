package it.ismb.pertlab.termometer_driver;

import it.ismb.pertlab.pwal.termometer_driver.ThermometerFakeDriver;

import org.junit.Test;

public class TestDriver {

	@Test
	public void testThread()
	{
		ThermometerFakeDriver d=new ThermometerFakeDriver();
		d.start();
		
		try {
			Thread.sleep(10000);
			d.stop();
			Thread.sleep(10000);
			d.start();
			Thread.sleep(10000);
					} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
