---

---

var client;
var navBarScript;

$(document).ready(function() {
	fillDevicesDataTable();
});

function fillDevicesDataTable() {
	var uuid = new UUIDGenerator();
	client = new Paho.MQTT.Client("{{ site.mqtt_broker }}", Number("{{ site.mqtt_port }}"),
			"{{ site.mqtt_websocket_endpoint }}", "pwalwebgui_local_" + uuid.generateUUID());

	navBarScript = new NavBarClass();
	navBarScript.onMqttConnecting();
	// set callback handlers
	client.onConnectionLost = onConnectionLost;
	client.onMessageArrived = onMessageArrived;
	client.onSuccess = onConnect;
	
	client.connect({onSuccess : onConnect , mqttVersion : 3});
	$('#devices_table')
			.dataTable(
					{
						"bProcessing" : true,
						"responsive" : true,
						"createdRow" : function(row, data, index) {
							$('td', row).eq(0)
									.html(
											"<a href=\"device.html?deviceId="
													+ data[0] + "\">" + data[0]
													+ "</a>")
						},
						"ajax" : "{{ site.rest_endpoint }}/detaileddevices2"
					});
}

function onConnect() {
	console.log("connected");
	navBarScript.onMqttConnect();
	newDeviceTopic = "/impress/metadata/iotentity/#";
	client.subscribe(newDeviceTopic, {
		qos : 0
	});
	deviceRemovedTopic = "/impress/rai/iotentity/removed/#";
	client.subscribe(deviceRemovedTopic, {
		qos : 0
	});
	navBarScript.onMqttSubscribed();
//	$("#mqtt_info")
//			.text("Connected to the mqtt broker. (topic: " + topic + ")");
	clearInterval(timer);
}

// called when the client loses its connection
function onConnectionLost(responseObject) {
	if (responseObject.errorCode !== 0) {
		console.log("onConnectionLost:" + responseObject.errorMessage);
	}
	navBarScript.onMqttConnectionLost();
	// re - Connect
	// connect the client
	timer = setInterval(function() {

		console.log("Attempting re-connection after 5s...");

		console.log("client currently:"+client.isConnected());
		if (client.isConnected()== false) {
			client.connect({onSuccess : onConnect , mqttVersion : 3});
		}
	}, 5000);

}

// called when a message arrives
function onMessageArrived(message) {
	//TODO: add or remove devices from table
}