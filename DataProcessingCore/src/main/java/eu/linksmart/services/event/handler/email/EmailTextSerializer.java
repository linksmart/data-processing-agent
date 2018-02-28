package eu.linksmart.services.event.handler.email;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.constants.Const;
import eu.linksmart.services.utils.function.Utils;
import eu.linksmart.services.utils.serialization.Serializer;
import eu.linksmart.services.utils.serialization.SerializerMode;

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
 * Serialize an object as a sequence of key = value
 *
 * @author Nadir Raimondo
 * @since  1.8.0
 *
 */
public class EmailTextSerializer  implements Serializer{

    private ObjectMapper parser = new ObjectMapper();
    protected Configurator conf = Configurator.getDefaultConfig();

    public EmailTextSerializer() {

        parser.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        if(conf !=null && conf.containsKeyAnywhere(Const.TIME_EPOCH_CONF_PATH))
            parser.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, conf.getBoolean(Const.TIME_EPOCH_CONF_PATH));
        parser.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
        parser.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        parser.setDateFormat(Utils.getDateFormat());
        parser.setTimeZone(Utils.getTimeZone());
    }
    
	

	
	private String getString(Map props){
		final StringBuilder sb = new StringBuilder("");
		props.forEach((k, v) -> sb.append(k.toString() +" = " + v.toString()+"\n"));
		return sb.toString();
	}


    @Override
    public <T> void addModule(String name, Class<T> tClass, SerializerMode<T> serializerMode) {
        parser.registerModule(new SimpleModule(name, Version.unknownVersion()).addSerializer(tClass, serializerMode));
    }
    @Override
    public <I,C extends I> void addModule(String name, Class<I> tInterface, Class<C> tClass) {
        parser.registerModule(new SimpleModule(name, Version.unknownVersion()).addAbstractTypeMapping(tInterface,tClass));

    }
    @Override
    public void close() {

    }


	@Override
	public <T> byte[] serialize(T object) throws IOException {
		return serializeStr(object).getBytes();
	}

	@Override
	public <T> String toString(T object) throws IOException {
		return serializeStr(object);
	}
	
	private <T> String serializeStr(T object){
		if(object == null)
			return "";
		
		Object[] objects;
		if(object.getClass().isArray()) objects = (Object[]) object;
		else objects = new Object[] {object};
		
		Map props = null;
		StringBuilder sb = new StringBuilder("");
		for(Object o : objects){
			props = parser.convertValue(o, Map.class);
			sb.append(getString(props)+"\n\n");
		}

		return sb.toString();
	}

}
