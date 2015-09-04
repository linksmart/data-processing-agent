---

---
var deviceCount;
var newDeviceTopic;
var deviceRemovedTopic;
var navBarScript;
var uuid;
var timerDevicesCount = null;


$(document).ready(function() {
	init();
});

function getDevicesCount()
{
	$.get("{{ site.rest_endpoint }}/countDevice",function(data)
		{
			deviceCount = data;
			fillCountDevice(deviceCount);
		}).fail(function()
		{
			deviceCount = 0;
			fillCountDevice(deviceCount);
		});
}

function init()
{
	uuid = new UUIDGenerator();
	client = new Paho.MQTT.Client("{{ site.mqtt_broker }}", Number("{{ site.mqtt_port }}"),
			"{{ site.mqtt_websocket_endpoint }}", "pwalwebgui_local_"+uuid.generateUUID());
	navBarScript = new NavBarClass();
	navBarScript.onMqttConnecting();
	// set callback handlers
	client.onConnectionLost = onConnectionLost;
	client.onMessageArrived = onMessageArrived;
	client.onSuccess = onConnect;

	//get the initial device count
	getDevicesCount();

	// connect the client
	client.connect({onSuccess : onConnect , mqttVersion : 3});
}

function fillCountDevice(count)
{
	$("#countDevice").text(count);
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
function onConnectionLost(responseObject) 
{
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
			navBarScript.onMqttConnecting();
			client.connect({onSuccess : onConnect , mqttVersion : 3});
		}
	}, 5000);
}

// called when a message arrives
function onMessageArrived(message) 
{
	if(timerDevicesCount != null)
		clearInterval(timerDevicesCount);
	if(message.destinationName.match(newDeviceTopic.substring(0,newDeviceTopic.length-1)))
		deviceCount++;
	else if(message.destinationName.match(deviceRemovedTopic.substring(0,deviceRemovedTopic.length-1)))
		deviceCount--;
	else
		console.log("yo bro! wrong topic");
	
	fillCountDevice(deviceCount);
	timerDevicesCount = setTimeout(function()
			{
				getDevicesCount();
			}, 1000);
}