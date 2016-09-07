package eu.linksmart.api.event.exceptions;

import eu.linksmart.api.event.types.impl.GeneralRequestResponse;

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
 *
 * This exception is an complex exception. This exception is obtained when an internal exception happens
 * and the exception had being assessed and managed by an component, and the components must thrown an exception.
 * E.g. Having component A, B, C where  A-calls->B-->C and C produces an exception. B asses and generates an responds
 * to A. B throws its response in form of a ErrorResponseException to A.
 * Usually, this exception is translated as REST error code as whatever the assessment in the GeneralRequestResponse
 * had being already decided.
 *
 *
 * @see eu.linksmart.api.event.exceptions.TraceableException
 * @see eu.linksmart.api.event.types.impl.GeneralRequestResponse
 *
 * @author Jose Angel Carvajal Soto
 * @since  1.3.1
 *
 * */
public class ErrorResponseException extends TraceableException {
    /**
     * The original assessment made by a component.
     *
     *  */
    final GeneralRequestResponse requestResponse;
    /**
     * super class constructor with errorProducerId and errorProducerType parameters
     * @see eu.linksmart.api.event.exceptions.TraceableException
     * */
    public ErrorResponseException(GeneralRequestResponse requestResponse) {
        super(requestResponse.getProducerID(), requestResponse.getProducerName(), requestResponse.getMessage());
        this.requestResponse = requestResponse;
    }
    /**
     * super class constructor with errorProducerId and errorProducerType parameters
     * @see eu.linksmart.api.event.exceptions.TraceableException
     * */
    public ErrorResponseException(GeneralRequestResponse requestResponse, Throwable cause) {
        super(requestResponse.getProducerID(), requestResponse.getProducerName(), requestResponse.getMessage(), cause);
        this.requestResponse = requestResponse;
    }
    /**
     * super class constructor with errorProducerId and errorProducerType parameters
     * @see eu.linksmart.api.event.exceptions.TraceableException
     * */
    public ErrorResponseException(GeneralRequestResponse requestResponse, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(requestResponse.getProducerID(), requestResponse.getProducerName(), requestResponse.getMessage(), cause, enableSuppression, writableStackTrace);
        this.requestResponse = requestResponse;
    }
    /**
     * returns the original assessment
     *
     * @return original assessment as GeneralRequestResponse
     * */
    public GeneralRequestResponse getRequestResponse() {
        return requestResponse;
    }

}
