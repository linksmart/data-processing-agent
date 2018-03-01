package eu.linksmart.api.event.types;

public interface BaseEmail {
	public String getHostName();
	public void send() throws Exception;
}
