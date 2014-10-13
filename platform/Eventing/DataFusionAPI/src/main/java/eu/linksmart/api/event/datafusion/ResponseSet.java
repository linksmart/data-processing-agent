package eu.linksmart.api.event.datafusion;


/**
 * Response is a interface which identify the functionality to access the resulting data of a SQL-like query generated by a Data Fusion Engine. 
 * <p>The interface represents a aray of "rows" in a SQL-like response, this mean the a complete response of a SQL-like query. <p>
 * The Response is a collections of Response (@see Response)<p>
 * 
 * The motivation of this interface is to allow the wrappers to implement the data structure as they please.<p>
 * 
 * The functionality provided described in the interface allows the access to the data generated by a DataFusionWrappers.
 * 
 * For the implementation of the interface a ArrayList or a List implementation is recommended. 
 * 
 * @author Jos� �ngel Carvajal Soto
 * @version     0.01
 * @since       0.01
 * @see DataFusionWrapper
 * @see Cell
 * @see ResponseSet.Response
 * */
public interface ResponseSet {
	/**
	 * Get the name of response set. This is the name of the query which generated the response. <p>
	 * This name is use later as the topic where the response will be published
	 * 
	 * @return The name of response set as String.
	 * 
	 * */
	String getName();
	/**
	 * Get the selected {@link ResponseSet.Response} by parameter index.
	 * 
	 * @param i is the index of the element to return
	 * @return The selected response.
	 * @throws IndexOutOfBoundsException - if the index is out of range (index < 0 || index >= size())
	 * 
	 * */
	Response get(int i) throws IndexOutOfBoundsException;
	/**
	 * Returns an array of Response containing all of the elements in this ResponseSet in proper sequence (from first to last element).
	 * 
	 * @return an array of Response containing all of the Responses
	 * */
	Response[] getResponses();
	/**
	 * Returns the number of Responses in the ResponseSet
	 * 
	 * @return amount of elements contained in the ResponseSet  
	 * */
	int size();
	/**
	 * Response is a interface which identify the functionality to access the resulting data of a SQL-like query generated by a Data Fusion Engine. 
	 * <p>The interface represents a "row" in a SQL-like response. <p>
	 * The Response is a collections of Cells (@see Cell)<p>
	 * 
	 * The motivation of this interface is to allow the wrappers to implement the data structure as they please.<p>
	 * 
	 * The functionality provided described in the interface allows the access to the data generated by a DataFusionWrappers.
	 * 
	 * For the implementation of the interface a Hashmap or a Map implementation is recommended. 
	 * 
	 * @author Jos� �ngel Carvajal Soto
	 * @version     0.01
	 * @since       0.01
	 * @see DataFusionWrapper
	 * @see ResponseSet.Response.Cell
	 * */
	public interface Response {
		
		/**
		 * Get the names of the each data in the data collection. In the SQL semantics, the column names of the data
		 * 
		 * @return The names of the value as String Array.
		 * 
		 * */
		String[] getColumnsNames();
		/**
		 * Get the inner values of the each data in the data collection. In the SQL semantics, the value contained in the cells.
		 * 
		 * @return The values as Object Array.
		 * 
		 * */
		Object[] getRawRow();
		/**
		 * Get the cells contained in the response. In the SQL semantics, the row with its correspondent column name
		 * 
		 * @return The Cells as Array of @see Cell.
		 * 
		 * @see ResponseSet.Response.Cell
		 * */
		Cell[] getCells();
		/**
		 * Check if a particular column name is contained in the responses. 
		 * 
		 * @param column is name of the column which will be queried.
		 * 
		 * @return <code>true</code> if the name exist in the response, <code>false</code> otherwise.
		 * 
		 * */
		boolean exist(String column);
		/**
		 * Obtain the value of the selected column name.
		 *  
		 * @param columnName of the element to return.
		 * 
		 * @return The value of the selected data by the parameter if exist, <code>null</code> otherwise.
		 * 
		 * */
		Object getValueOf(String columnName);
		/**
		 * Obtain the @see cell of the selected column name.
		 *  
		 * @param columnNo is the index of the element to return.
		 * 
		 * @return The cell of the selected column by the parameter if exist, <code>null</code> otherwise.
		 * 
		 * @see ResponseSet.Response.Cell
		 * */
		Cell getCell(int columnNo);
		/**
		 * Returns the number of cells in the response
		 * 
		 * @return amount of elements contained in the response  
		 * */
		int size();
		/**
		 * Cell is a interface which identify the minimum functionality to access the resulting data of a SQL-like query generated by a Data Fusion Engine. 
		 * The interface represents where a "Column" and "row" cross SQL-like response. <p>
		 * 
		 * The motivation of this interface is to allow the wrappers to implement the data structure as they please.
		 * 
		 * The functionality provided described in the interface allows the access to the data generated by a DataFusionWrappers.
		 * 
		 * @author Jos� �ngel Carvajal Soto
		 * @version     0.01
		 * @since       0.01
		 * @see DataFusionWrapper
		 * 
		 * */
		public interface Cell {
			/**
			 * Get the name of the data. In the SQL semantics the column name
			 * 
			 * @return The name of the value as String.
			 * 
			 * */
			String getName();
			/**
			 * Get the value of the data. In the SQL semantics the value contained in a cell (cross of a row with a column)
			 * 
			 * @return The value as Object.
			 * 
			 * */
			Object getValue();

		}

	}

}