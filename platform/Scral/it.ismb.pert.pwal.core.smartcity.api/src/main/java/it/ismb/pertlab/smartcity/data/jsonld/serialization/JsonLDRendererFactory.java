/*
 * SmartCityAPI - KML to JSON-LD conversion
 * 
 * Copyright (c) 2014 Dario Bonino
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package it.ismb.pertlab.smartcity.data.jsonld.serialization;

import java.util.HashMap;

/**
 * The factory for getting JSON-LD renderers specific for a given api instance.
 * 
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public class JsonLDRendererFactory
{
	private static HashMap<String, JsonLDRenderer<?>> registeredRenderers = new HashMap<>();
	
	public static <T> JsonLDRenderer<T> getJsonLDRenderer(Class<T> entityClass)
	{
		@SuppressWarnings("unchecked")
		JsonLDRenderer<T> theRenderer = (JsonLDRenderer<T>) JsonLDRendererFactory.registeredRenderers.get(entityClass
				.getName());
		
		return theRenderer;
	}
	
	public static <T> void addRenderer(JsonLDRenderer<T> renderer, Class<T> entityClass)
	{
		JsonLDRendererFactory.registeredRenderers.put(entityClass.getName(), renderer);
	}
	
}
