package eu.linksmart.event.datafusion.esper.impl;

import java.util.Hashtable;

import eu.linksmart.datafusion.datafusionwrapper.ResponseSet.Response;

public class EsperResponse extends Hashtable<String, Object> implements Response {

	@Override
	public String[] getColumnsNames() {
		
		return (String[]) this.keySet().toArray();
	}

	@Override
	public Object[] getRawRow() {
		
		Object []ret = new Object[this.values().size()];
		Cell[] aux = getCells();
		
		for (int i=0; i< size(); i++)
			ret[i]= aux[i].getValue();
		
		return this.values().toArray();
	}

	@Override
	public Cell[] getCells() {
		
		return (Cell[]) this.values().toArray();
	}

	@Override
	public boolean exist(String column) {
		
		return this.containsKey(column);
	}

	@Override
	public Object getValueOf(String columnName) {
		
		return this.get(columnName);
	}

	@Override
	public Cell getCell(int columnNo) {
		
		return (Cell) this.values().toArray()[columnNo];
	}

	

}
