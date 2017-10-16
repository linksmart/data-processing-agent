package eu.linksmart.sdk.catalog.service;

public class APIDocImpl implements APIDoc {

        protected String description, url;

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public void setDescription(String description) {
            this.description = description;
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