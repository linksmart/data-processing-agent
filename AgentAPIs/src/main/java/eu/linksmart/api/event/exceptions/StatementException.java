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
 * The exception represent any kind of exception where the statement given by the user produces an error.
 * As statement can be seen any input given by the user that must be interpreted by the agent.
 * Usually, this exception is translated in a REST 400 error.
 *
 * @see eu.linksmart.api.event.exceptions.TraceableException
 * @author Jose Angel Carvajal Soto
 * @since  1.3.1
 *
 * */
public class StatementException extends TraceableException {

    /**
     * super class constructor with errorProducerId and errorProducerType parameters
     *
     * @param errorProducerType The id is a method to trace back the source of the error to a specific instance (if possible) or part.
     * @param errorProducerId The type indicates what kind of source the id (errorProducerId) points out.
     * @param message the detail message.
     *
     * @see eu.linksmart.api.event.exceptions.TraceableException
     * */
    public StatementException(String errorProducerId, String errorProducerType, String message) {
        super(errorProducerId, errorProducerType, message);
    }
    /**
     * super class constructor with errorProducerId and errorProducerType parameters
     *
     * @param errorProducerType The id is a method to trace back the source of the error to a specific instance (if possible) or part.
     * @param errorProducerId The type indicates what kind of source the id (errorProducerId) points out.
     * @param message the detail message.
     * @param cause the cause.  (A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
     *
     * @see eu.linksmart.api.event.exceptions.TraceableException
     * */
    public StatementException(String errorProducerId, String errorProducerType, String message, Throwable cause) {
        super(errorProducerId, errorProducerType, message, cause);
    }
    /**
     * super class constructor with errorProducerId and errorProducerType parameters
     *
     * @param errorProducerType The id is a method to trace back the source of the error to a specific instance (if possible) or part.
     * @param errorProducerId The type indicates what kind of source the id (errorProducerId) points out.
     * @param cause the cause.  (A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
     * @see eu.linksmart.api.event.exceptions.TraceableException
     * */
    public StatementException(String errorProducerId, String errorProducerType, Throwable cause) {
        super(errorProducerId, errorProducerType, cause);
    }
    /**
     * super class constructor with errorProducerId and errorProducerType parameters
     *
     * @param errorProducerType The id is a method to trace back the source of the error to a specific instance (if possible) or part.
     * @param errorProducerId The type indicates what kind of source the id (errorProducerId) points out.
     * @param message the detail message.
     * @param cause the cause.  (A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
     * @param enableSuppression whether or not suppression is enabled or disabled
     * @param writableStackTrace whether or not the stack trace should be writable
     * @see eu.linksmart.api.event.exceptions.TraceableException
     * */
    public StatementException(String errorProducerId, String errorProducerType, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(errorProducerId, errorProducerType, message, cause, enableSuppression, writableStackTrace);
    }
}
