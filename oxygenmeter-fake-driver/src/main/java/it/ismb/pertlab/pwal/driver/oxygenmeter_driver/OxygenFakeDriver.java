package it.ismb.pertlab.pwal.driver.oxygenmeter_driver;

import it.ismb.pertlab.pwal.api.Driver;
import it.ismb.pertlab.pwal.api.OxyMeter;

public class OxygenFakeDriver extends Driver implements OxyMeter{

	private String id;
	private final String type="pwal:Oxymeter";
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id=id;
	}

	public String getType() {
		return this.type;
	}

	@Override
	public void run() {
		while(!t.isInterrupted())
		{
			System.out.println("il driver oxygen è attivo");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				t.interrupt();
			}
		}
	}

	public Integer getSaturation() {
		return (int) (((100-95)*Math.random())+95);
	}

}
