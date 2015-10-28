---

---
var map;
var pertCoords = "1202,223,1527,98";
var newDeviceTopic;
var deviceRemovedTopic;
var navBarScript;

$(function() {
	var requestedMap = getParameterByName("map");
	init(requestedMap);
	var uuid = new UUIDGenerator();
	client = new Paho.MQTT.Client("{{ site.mqtt_broker }}",
			Number("{{ site.mqtt_port }}"),
			"{{ site.mqtt_websocket_endpoint }}", "pwalwebgui_local_"
					+ uuid.generateUUID());
	navBarScript = new NavBarClass();
	navBarScript.onMqttConnecting();
	// set callback handlers
	client.onConnectionLost = onConnectionLost;
	client.onMessageArrived = onMessageArrived;
	client.onSuccess = onConnect;
	// connect the client
	client.connect({
		onSuccess : onConnect,
		mqttVersion : 3
	});
});

function getParameterByName(name) {
	name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
	var regexS = "[\\?&]" + name + "=([^&#]*)", regex = new RegExp(regexS), results = regex
			.exec(window.location.href);
	if (results == null) {
		return "";
	} else {
		return decodeURIComponent(results[1].replace(/\+/g, " "));
	}
}

function init(requestedMap) {

	switch (requestedMap) {
	case "boella":
		urlImage = 'assets/img/boella.jpg';
		$("#mapImage").attr('src', urlImage);
		addBoellaLab(pertCoords, "pert");
		$("#mapImage").maphilight();
		break;
	case "pert":
		urlImage = 'assets/img/pert.jpg';
		$("#mapImage").attr('src', urlImage);
		getDevices();
		break;
	default:
		urlImage = 'assets/img/boella.jpg';
		$("#mapImage").attr('src', urlImage);
		addBoellaLab(pertCoords, "pert");
		$("#mapImage").maphilight();
		break;
	}
}

function addBoellaLab(coord, labName) {
	var element = document.createElement("area");
	element.id = "poly";
	element.shape = "rect";
	element.coords = coord;
	element.href = "map3.html?map=" + labName;
	$("#zones").append(element);
}

function getDevices() {
	var jqXHR = $.getJSON("{{ site.rest_endpoint }}/detaileddevices", function(
			data) {
		$.each(data,
				function(index, value) {
					addDeviceToMap(value);
				});
	});
}

function addDeviceToMap(device){
	if (device.location != null) {
		var content = '';
		content += '<div id="'+device.pwalId+'">';
		content += '<a href="device.html?deviceId='
				+ device.pwalId + '">';
		content += '<img id="img-' + device.pwalId
				+ '" class="device" style="top:'
				+ device.location.lat + 'px; left: '
				+ device.location.lon
				+ 'px; position: absolute;">';
		content += '</a>';
		content += '</div>';
		$("#maps").append(content);
		switch (device.type) {
		case "Meter":
			$('#img-' + device.pwalId)
					.attr('src', 'assets/img/plug.png');
			break;
		case "PhilipsHue":
			$('#img-' + device.pwalId)
			.attr('src', 'assets/img/light.png');
			break;
		case "Thermometer":
			$('#img-' + device.pwalId).attr('src',
					'assets/img/temperature.png');
			break;
		case "HumiditySensor":
			$('#img-' + device.pwalId).attr('src',
					'assets/img/humidity.png');
			break;
		default:
			$('#img-' + device.pwalId).attr('src',
					'assets/img/unknown.png');
			break;
		}
	}
}

// called when the client connects
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

		console.log("client currently:" + client.isConnected());
		if (client.isConnected() == false) {
			navBarScript.onMqttConnecting();
			client.connect({
				onSuccess : onConnect,
				mqttVersion : 3
			});
		}
	}, 5000);
}

//called when a message arrives
function onMessageArrived(message) 
{
	var jsonPayload = jQuery.parseJSON(message.payloadString);
	var deviceId = jsonPayload.About.replace(/_/g,'-').substring(1,jsonPayload.About.length);
	if(message.destinationName.match(newDeviceTopic.substring(0,newDeviceTopic.length-1))){
		$.getJSON("{{ site.rest_endpoint }}/devices/"+deviceId,function(data)
				{
					addDeviceToMap(data);
				});
	}
	else if(message.destinationName.match(deviceRemovedTopic.substring(0,deviceRemovedTopic.length-1)))
		$("#"+deviceId).remove();
	else
		console.log("yo bro! wrong topic");
}