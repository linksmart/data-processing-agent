package it.ismb.pertlab.pwal.wsn.driver.api;

import it.ismb.pertlab.pwal.wsn.driver.api.impl.Message;

public class Response extends Message {
	public Response (String srcAddress){
		this.srcAddress = srcAddress;
	}

	@Override
	public MessageType getType() {
		
		return type;
	}
}
