package it.ismb.pertlab.pwal.wsn.driver.api;

import it.ismb.pertlab.pwal.wsn.driver.api.impl.Message;

public class Request extends Message {
	 
	public Request( byte []  payload, String  dstAddress, int port  ) {
			super.setPayload(payload);
			super.setDstAddress( dstAddress );
		 
					
	}
}
