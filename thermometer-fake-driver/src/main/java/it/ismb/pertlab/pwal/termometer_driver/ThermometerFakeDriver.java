package it.ismb.pertlab.pwal.termometer_driver;

import it.ismb.pertlab.pwal.api.Driver;
import it.ismb.pertlab.pwal.api.Thermometer;

public class ThermometerFakeDriver extends Driver implements Thermometer {

	private String id;
	private final String type="pwal:Thermometer";

	@Override
	public void run() {
		while(!t.isInterrupted())
		{
			System.out.println("il driver thermometer è attivo");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				t.interrupt();
			}
		}
	}

	public void setId(String id)
	{
		this.id=id;
	}
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public Double getTemperature() {
		return ((25-16)*Math.random())+16;
	}
	
}
