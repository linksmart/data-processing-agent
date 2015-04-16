package eu.linksmart.gc.network.identity.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

import eu.linksmart.gc.api.utils.Part;
import eu.linksmart.gc.api.utils.PartConverter;

public class AttributeQueryParser {

	private static void removeRedundantBrackets(LinkedList<String> values) {
			
			Iterator<String> iterator = values.iterator();
			
			int i=0;
			
			while(iterator.hasNext() && i<values.size()){
				if(values.get(i).equals("(") || values.get(i).equals(")")){
					values.remove(i);
					if(i>0)
					{
						i--;
					}
				}
				else{
					i++;
				}
			}
			
		}

	/**
	 * Checks attributes
	 * 
	 * TODO Student: check if method cannot be written easier or less complex
	 * XXX This method needs refactoring (in particular for new Attributes type - Part[])
	 * @param attr the attributes
	 * @param query the query
	 * @return true or false depending on the result
	 */
	@Deprecated public static boolean checkAttributes(Properties attr, LinkedList<String> query) {
		LinkedList<String> values = (LinkedList<String>) query.clone();
		boolean result = false;
		
		for (int j = 0; j < values.size(); j++) {
			if ((values.get(j) != "(") && (values.get(j - 1) == "(")) {
				String key = values.get(j);
				String op = values.get(j + 1);
				String value = (values.get(j + 2) != ")") ? values.get(j + 2) : "";
				boolean r = false;
				
				if (attr.containsKey(key)) {
					if (op == "==") {
						if (value.contains("*")) {
							String parts[] = value.split("\\*");
							String v = (String) attr.get(key);
							r = true;
							for (int i = 0; i < parts.length; i++) {
								r = r && v.contains(parts[i]);
							}
						}
						else {
							if (attr.get(key).equals(value)) {
								r = true;
							}
						}
					}
					
					if (op == "!=") {
						if (value.contains("*")) {
							String parts[] = value.split("\\*");
							String v = (String) attr.get(key);
							r = true;
							for (int i = 0; i < parts.length; i++) {
								r = r && (!v.contains(parts[i]));
							}
						}
						
						if (!attr.get(key).equals(value)) {
							r = true;
						}
					}
				}
				
				values.set(j, String.valueOf(r));
				values.remove(j + 1);
				values.remove(j + 1);
				if (value != "") {
					values.remove(j + 1);
				}
				values.remove(j - 1);
			}
		}
		
		while (!values.isEmpty()) {
			if (values.size() == 1) {
				result =  Boolean.parseBoolean(values.poll());
				break;
			}
			else {
				
				removeRedundantBrackets(values);
				
				int j=0; //no need to iterate, better shift values to the left
				while(values.size()>1){
					if ((values.get(j) != "(")) {
						
							if (values.get(j + 1)== ")") {
								boolean r = Boolean.parseBoolean(values.get(j));
								values.set(j, String.valueOf(r));
								values.remove(j + 1);
								values.remove(j - 1);
							}
							else {
								boolean op1 = Boolean.parseBoolean(values.get(j));
								String operand = values.get(j + 1);
								boolean op2 = Boolean.parseBoolean(values.get(j + 2));
								boolean r = false;
								if (operand == "&&") {
									r = op1 & op2;
								}
								else {
									r = op1 | op2;
								}
								
								if(!r)
								{
									return false;
								}
								
								
								
								values.set(j+1, String.valueOf(r));
								
								if(!(values.contains("&&") || values.contains("||"))){
									return r;
								}
								else{
									values.remove(j+1);
									values.remove(j + 1);
								}
							}		
					}
					/* FIXME if (( have been used insted of single ( IndexOutOfBoundsException will be thrown */
					else{
						values.remove(j);
					}
				}
			}
		}
		return result;
	}
	
	public static boolean checkAttributes(Part[] attr, LinkedList<String> query) {
		return checkAttributes(PartConverter.toProperties(attr), query);
	}
	
	/**
	 * Parses a query
	 * 
	 * @param query the unparsed query
	 * @return the parsed query
	 */
	public static LinkedList<String> parseQuery(String query) {
		LinkedList<String> values = new LinkedList<String>();
		char[] chars = query.toCharArray();
		String value = "";
		char previous = ' ';
		for (int i = 0; i < chars.length; i++) {
			switch (chars[i]) {
				case '(':
					values.addLast("(");
					value = "";
					break;
				case '&':
					if (previous == '&') {
						values.addLast("&&");
					}
					else {
						/* Create the first operator and push it. */
						if (value != "") {
							values.addLast(value);
							value = "";
						}
					}
					break;
				case '|':
					if (previous == '|') {
						values.addLast("||");
					}
					else {
						/* Create the first operator and push it. */
						if (value != "") {
							values.addLast(value);
							value = "";
						}
					}
					break;
				case '!':
					if (value != "") {
						values.addLast(value);
						value = "";
					}
					break;
				case '=':
					if (previous == '=') {
						values.addLast("==");
					}
					else if (previous == '!') {
						values.addLast("!=");
					}
					else {
						/* Create the first operator and push it. */
						if (value != "") {
							values.addLast(value);
							value = "";
						}
					}
					break;
				case ')':
					if (value != "") {
						values.addLast(value);
						value = "";
					}
					values.addLast(")");
					break;
				default:
					value = value + chars[i];
					break;
			}
			previous = chars[i];
		}
		return values;
	}
	
}
