package eu.linksmart.services.event.ceml.models;

public class PyroBackedResponse{

        protected String uri, statusMessage="OK";
        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public String getStatusMessage() {
            return statusMessage;
        }

        public void setStatusMessage(String statusMessage) {
            this.statusMessage = statusMessage;
        }
    }