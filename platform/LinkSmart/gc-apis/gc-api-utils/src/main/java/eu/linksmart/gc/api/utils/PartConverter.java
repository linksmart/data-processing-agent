package eu.linksmart.gc.api.utils;

import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

@Deprecated public class PartConverter {
	@Deprecated public static Part[] fromProperties(Properties attributes) {
			Set<Entry<Object, Object>> entries = attributes.entrySet();
			Part[] newAttributes = new Part[entries.size()];
			int i = 0;
			for (Entry<Object, Object> entry : entries){
				String key = (String)entry.getKey();
				String value = (String)entry.getValue();
				newAttributes[i] = new Part(key,
						value);
				i++;
			}
			return newAttributes;
		}

	@Deprecated public static Properties toProperties(Part[] attributes) {
		Properties result = new Properties();
		for (Part p : attributes) {
			result.put(p.getKey(), p.getValue());
		}
		return result;
	}
}
