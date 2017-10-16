package eu.linksmart.sdk.catalog.service;

public class APIDescriptorImpl implements APIDescriptor {
        protected String protocol, url;

        @Override
        public String getProtocol() {
            return protocol;
        }

        @Override
        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        @Override
        public String getUrl() {
            return url;
        }

        @Override
        public void setUrl(String url) {
            this.url = url;
        }
    }