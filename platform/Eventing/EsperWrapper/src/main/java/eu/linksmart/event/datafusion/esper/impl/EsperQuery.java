package eu.linksmart.event.datafusion.esper.impl;



public class EsperQuery  {
	private final String name;
	private final String topic;
	private final String query;
	
	public EsperQuery(String name,String query, String topic){
	 this.name= name;
	 this.topic = topic;
	 this.query = query;
	}

	public String getQuery() {
		// TODO Auto-generated method stub
		return query;
	}

	
	public String getTopic() {
		// TODO Auto-generated method stub
		return topic;
	}

	
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

}
