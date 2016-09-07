package eu.linksmart.api.event.exceptions;

/**
 *  Copyright [2013] [Fraunhofer-Gesellschaft]
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
 *
 *
 */
/**
 * The exception represent any kind of traceable exception where the source of the error is known but the reason not.
 * This kind of error are thrown when an unexpected and unpredicted error happens. E.g. if a big portion of the code is
 * enclosed in a big try catch and suddenly an NullPinterException rise.
 * Usually, this exception is translated in a REST 500 error.
 *
 * @see eu.linksmart.api.event.exceptions.TraceableException
 *
 * @author Jose Angel Carvajal Soto
 * @since  1.3.1
 *
 * */
public class UnknownException extends TraceableException{
    /**
     * super class constructor with errorProducerId and errorProducerType parameters
     * @see eu.linksmart.api.event.exceptions.TraceableException
     * */
    public UnknownException(String errorProducerId, String errorProducerType, String message) {
        super(errorProducerId, errorProducerType, message);
    }
    /**
     * super class constructor with errorProducerId and errorProducerType parameters
     * @see eu.linksmart.api.event.exceptions.TraceableException
     * */
    public UnknownException(String errorProducerId, String errorProducerType, String message, Throwable cause) {
        super(errorProducerId, errorProducerType, message, cause);
    }
    /**
     * super class constructor with errorProducerId and errorProducerType parameters
     * @see eu.linksmart.api.event.exceptions.TraceableException
     * */
    public UnknownException(String errorProducerId, String errorProducerType, Throwable cause) {
        super(errorProducerId, errorProducerType, cause);
    }
    /**
     * super class constructor with errorProducerId and errorProducerType parameters
     * @see eu.linksmart.api.event.exceptions.TraceableException
     * */
    public UnknownException(String errorProducerId, String errorProducerType, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(errorProducerId, errorProducerType, message, cause, enableSuppression, writableStackTrace);
    }
}
