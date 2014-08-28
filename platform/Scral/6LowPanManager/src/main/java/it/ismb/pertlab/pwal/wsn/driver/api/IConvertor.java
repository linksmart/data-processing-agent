package it.ismb.pertlab.pwal.wsn.driver.api;

public interface IConvertor {
	IMessage net2host(Object input);
	Object host2net(IMessage input);	
}
