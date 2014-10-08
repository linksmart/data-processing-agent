package eu.linksmart.event.datafusion.esper.impl;


public class EsperQuery  {
	private final String name;
	private final String[] topics;
	private final String query;
	
	public EsperQuery(String name,String query, String[] topics){
	 this.name= name;
	 this.topics = topics;
	 this.query = query;
	}

	public String getQuery() {
		// TODO Auto-generated method stub
		return query;
	}

	
	public String[]  getTopic() {
		// TODO Auto-generated method stub
		return topics;
	}

	
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

}
