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
 * The exception represent any kind of exception where the source of the error can be traced back by some Id.
 * The idea is to send back information to the user or developer to fix the error.
 *
 *
 * @author Jose Angel Carvajal Soto
 * @since  1.3.1
 *
 * */
public abstract class TraceableException extends Exception {

    private static final long serialVersionUID = 5614280930770087934L;
    /**
     * The id is a method to trace back the source of the error to a specific instance (if possible) or part.
     *
     *  */
    protected final String errorProducerId;
    /**
     *
     * The type indicates what kind of source the id (errorProducerId) points out.
     *
     *  */
    protected final String  errorProducerType;

    /**
     * super class constructor with errorProducerId and errorProducerType parameters
     *
     * @param errorProducerType The id is a method to trace back the source of the error to a specific instance (if possible) or part.
     * @param errorProducerId The type indicates what kind of source the id (errorProducerId) points out.
     * @param message the detail message.
     *
     * @see java.lang.Exception
     * */
    public TraceableException(String errorProducerId,String errorProducerType,  String message) {
        super(message);
        this.errorProducerId =errorProducerId;
        this.errorProducerType =errorProducerType;
    }
    /**
     * super class constructor with errorProducerId and errorProducerType parameters
     *
     * @param errorProducerType The id is a method to trace back the source of the error to a specific instance (if possible) or part.
     * @param errorProducerId The type indicates what kind of source the id (errorProducerId) points out.
     * @param message the detail message.
     * @param cause the cause.  (A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
     *
     * @see java.lang.Exception
     * */
    public TraceableException(String errorProducerId,String errorProducerType,String message,  Throwable cause) {
        super(message, cause);
        this.errorProducerId =errorProducerId;
        this.errorProducerType =errorProducerType;
    }
    /**
     * super class constructor with errorProducerId and errorProducerType parameters

     * @param errorProducerType The id is a method to trace back the source of the error to a specific instance (if possible) or part.
     * @param errorProducerId The type indicates what kind of source the id (errorProducerId) points out.
     * @param cause the cause.  (A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
     *
     * @see java.lang.Exception
     * */
    public TraceableException( String errorProducerId, String errorProducerType,Throwable cause) {
        super(cause);
        this.errorProducerId =errorProducerId;
        this.errorProducerType =errorProducerType;
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
     *
     * @see java.lang.Exception
     * */
    public TraceableException( String errorProducerId, String errorProducerType,String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorProducerId =errorProducerId;
        this.errorProducerType =errorProducerType;
    }
    /**
     * returns the id of the 'whatever' that produced the exception.
     *
     * @return an id as string
     * */
    public String getErrorProducerId() {
        return errorProducerId;
    }
    /**
     * returns the type of the 'whatever' that produced the exception.
     *
     * @return a type as string
     * */
    public String getErrorProducerType() {
        return errorProducerType;
    }
}
