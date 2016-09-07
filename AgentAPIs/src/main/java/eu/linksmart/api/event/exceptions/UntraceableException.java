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
 * The exception represent any kind of exception where the source of the error cannot be traced back by any Id.
 * The reason of the error is expected but no ID and type of an instance that produces the error can be given.
 * This Exception is unlikely to happen.
 * Usually, this exception is translated in a REST 500 error.
 *
 * @see java.lang.Exception
 * @author Jose Angel Carvajal Soto
 * @since  1.3.1
 *
 * */
public class UntraceableException extends Exception {
    /**
     * super class constructor with errorProducerId and errorProducerType parameters
     * @see java.lang.Exception
     * */
    public UntraceableException() {
        super();
    }
    /**
     * super class constructor with errorProducerId and errorProducerType parameters
     * @see java.lang.Exception
     * */
    public UntraceableException(String message) {
        super(message);
    }
    /**
     * super class constructor with errorProducerId and errorProducerType parameters
     * @see java.lang.Exception
     * */
    public UntraceableException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * super class constructor with errorProducerId and errorProducerType parameters
     * @see java.lang.Exception
     * */
    public UntraceableException(Throwable cause) {
        super(cause);
    }
    /**
     * super class constructor with errorProducerId and errorProducerType parameters
     * @see java.lang.Exception
     * */
    public UntraceableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
