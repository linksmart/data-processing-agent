package eu.linksmart.services.event.ceml.models;

public class PyroBackendRequest{
        protected String name="", path="", registerName=name+"-backend-"+PythonBackendManager.requestCount, host="localhost";
        protected int port =0;
        protected boolean nameServer=false;

        public boolean isNameServer() {
            return nameServer;
        }

        public void setNameServer(boolean nameServer) {
            this.nameServer = nameServer;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getRegisterName() {
            return registerName;
        }

        public void setRegisterName(String registerName) {
            this.registerName = registerName;
        }


        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

    }