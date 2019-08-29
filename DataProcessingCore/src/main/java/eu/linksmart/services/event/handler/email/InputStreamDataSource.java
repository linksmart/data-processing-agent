package eu.linksmart.services.event.handler.email;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

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
 * Generic data source for email attachment
 *
 * @author Nadir Raimondo
 * @since  1.8.0
 *
 */
public class InputStreamDataSource implements DataSource{

    private final InputStream inputStream;
    private final String contentType, name;
    
    public InputStreamDataSource(InputStream inputStream) {
    	this(inputStream,null,null);
    	
    }

    public InputStreamDataSource(InputStream inputStream, String contentType) {
    	this(inputStream, contentType, null);
    }
    
    public InputStreamDataSource(InputStream inputStream, String contentType, String name) {
        this.inputStream = inputStream;
        this.contentType = contentType!=null?contentType:"*/*";
        this.name = name!=null?name:"InputStreamDataSource";
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return inputStream;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public String getName() {
        return name;
    }

}
