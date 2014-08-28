package it.ismb.pertlab.pwal.smartsantander.datamodel.json;

public class SmartSantanderSingleNodeJson {
	
	String nodeId;
	String type;
	Double longitude;
	Double latitude;
	
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	
	@Override
	public String toString() {
		return "VehicleSpeedJson [nodeId=" + nodeId + ", type=" + type
				+ ", longitude=" + longitude + ", latitude=" + latitude + "]";
	} 
}
