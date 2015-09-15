package it.ismb.pertlab.pwal.api.utils;

public class FQDNUtils
{
	
	public static String FQDN2Topic(String fqdn , int prefixOffset, int suffixOffset)
	{
		//the final topic
		StringBuffer topic = new StringBuffer();
		
		//trim the FQDN string
		String originalFQDN = fqdn.trim();
		
		//split over points
		String fqdnParts[] = originalFQDN.split("\\.");
		
		//invert the parts
		for(int i=(fqdnParts.length-1-suffixOffset); i>=prefixOffset; i--)
		{
			topic.append("/");
			topic.append(fqdnParts[i]);
		}
		
		//return the topic string
		return topic.toString();
	}
	
}
