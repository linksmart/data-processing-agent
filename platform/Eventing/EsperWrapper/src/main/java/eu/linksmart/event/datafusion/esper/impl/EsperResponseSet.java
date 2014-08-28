package eu.linksmart.event.datafusion.esper.impl;

import eu.linksmart.api.event.datafusion.ResponseSet;
import eu.linksmart.api.event.datafusion.ResponseSet.Response;

import java.util.ArrayList;

public class EsperResponseSet extends  ArrayList<Response> implements ResponseSet{

	final private String name;
	//final private ArrayList <EsperResponse> responseSet;
	public EsperResponseSet(String name, ArrayList< EsperResponse> response){
		this.name=name;

		for (EsperResponse r : response)
			add(r);
		
	}
	public EsperResponseSet(String name){
		this.name=name;
		
	}
	
	@Override
	public String getName() {
		return name;
	}

	
	@Override
	public Response get(int i) {
		// TODO Auto-generated method stub
		return get(i);

	}

	@Override
	public Response[] getResponses() {
		
		return (Response[]) toArray();
	}

	@Override
	public int size() {
		
		return size();
	}

}
