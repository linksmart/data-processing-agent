package eu.linksmart.gc.api.network.routing;
public class RouteEntry {
		
		private String backboneName;
		
		private String endpoint;

		public String getBackboneName() {
			return backboneName;
		}

		public void setBackboneName(String backboneName) {
			this.backboneName = backboneName;
		}

		public String getEndpoint() {
			return endpoint;
		}

		public void setEndpoint(String endpoint) {
			this.endpoint = endpoint;
		}

		public RouteEntry(String backboneName, String endpoint) {
			super();
			this.backboneName = backboneName;
			this.endpoint = endpoint;
		}

	}