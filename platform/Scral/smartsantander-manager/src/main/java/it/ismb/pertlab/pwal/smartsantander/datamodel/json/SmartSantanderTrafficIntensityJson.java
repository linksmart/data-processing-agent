package it.ismb.pertlab.pwal.smartsantander.datamodel.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SmartSantanderTrafficIntensityJson {

	String nodeId;
	String type;
	String date;
	Double latitude;
	Double longitude;
	Double occupancy;
	Double count;
	@JsonIgnore
	@JsonProperty("median_speed")
	Double median_speed;
	@JsonIgnore
	@JsonProperty("average_speed")
	Double average_speed;
	
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
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public Double getOccupancy() {
		return occupancy;
	}
	public void setOccupancy(Double occupancy) {
		this.occupancy = occupancy;
	}
	public Double getCount() {
		return count;
	}
	public void setCount(Double count) {
		this.count = count;
	}
	public Double getMedian_speed() {
		return median_speed;
	}
	public void setMedian_speed(Double median_speed) {
		this.median_speed = median_speed;
	}
	public Double getAverage_speed() {
		return average_speed;
	}
	public void setAverage_speed(Double average_speed) {
		this.average_speed = average_speed;
	}
	@Override
	public String toString() {
		return "TrafficIntensityJson [nodeId=" + nodeId + ", type=" + type
				+ ", date=" + date + ", latitude=" + latitude + ", longitude="
				+ longitude + ", occupancy=" + occupancy + ", count=" + count
				+ ", median_speed=" + median_speed + ", average_speed="
				+ average_speed + "]";
	}
}
