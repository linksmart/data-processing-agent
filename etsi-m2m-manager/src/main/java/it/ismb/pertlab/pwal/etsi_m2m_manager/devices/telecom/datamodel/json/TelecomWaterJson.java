package it.ismb.pertlab.pwal.etsi_m2m_manager.devices.telecom.datamodel.json;

import it.ismb.pertlab.pwal.estsi_m2m_manager.devices.telecom.base.TelecomBaseJson;

public class TelecomWaterJson extends TelecomBaseJson {

	private Double ph;
	private Double flow;

	public Double getFlow() {
		return flow;
	}

	public void setFlow(Double flow) {
		this.flow = flow;
	}

	public Double getPh() {
		return ph;
	}

	public void setPh(Double ph) {
		this.ph = ph;
	}
}
