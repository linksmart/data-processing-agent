package eu.linksmart.services.event.handler.email;


import java.util.HashMap;
import java.util.Map;

/**
 *  Copyright [2018] [ISMB]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * Generic list of mime types for email attachment
 * @author nadir
 * @since  1.8.0
 */
public class EmailMimeTypes 
{

	private final static Map<String,String> types;
	
	static {
		types = new HashMap<>();
		
		types.put("text/plain",".txt");
		types.put("text/comma-separated-values", ".csv");
		types.put("application/octet-stream", ".bin");
		types.put("application/msword",".doc");
		types.put("image/gif",".gif");
		types.put("image/jpeg",".jpeg");
		types.put("image/png", ".png");
		types.put("image/tiff", ".tiff");
		types.put("text/html",".html");
		types.put("text/plain",".txt");
		types.put("application/x-tar", ".tar");
		types.put("application/x-gtar", ".tar.gz");
		types.put("application/zip", ".zip");
	}

	private EmailMimeTypes() {}

	public static String getExtension(String mime)
	{
		String ext = types.get(mime);
		return ext==null?".bin":ext;
	}
}