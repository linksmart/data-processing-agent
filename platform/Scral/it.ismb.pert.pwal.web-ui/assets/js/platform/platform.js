---

---
var deviceCount;
var client;
var navBarScript;
var checkBoxes = [];
var timerAccordion = null;

$(document).ready(function() {
	init();
	getAvailableDevicesManagers();
});

function init()
{
	getDeviceCount();
	uuid = new UUIDGenerator();
	client = new Paho.MQTT.Client("{{ site.mqtt_broker }}", Number("{{ site.mqtt_port }}"),
			"{{ site.mqtt_websocket_endpoint }}", "pwalwebgui_local_"+uuid.generateUUID());
	navBarScript = new NavBarClass();
	navBarScript.onMqttConnecting();
	// set callback handlers
	client.onConnectionLost = onConnectionLost;
	client.onMessageArrived = onMessageArrived;
	client.onSuccess = onConnect;

	// connect the client
	client.connect({onSuccess : onConnect , mqttVersion : 3});
}

function getDeviceCount()
{
	$.get("{{ site.rest_endpoint }}/countDevice",function(data)
	{
		deviceCount = data;
		fillCountDevice(deviceCount);
	}).fail(function()
	{
//		deviceCount = 0;
//		fillCountDevice(deviceCount);
		getDeviceCount();
	});
}

function getAvailableDevicesManagers()
{	
	$.get("{{ site.rest_endpoint }}/devicesmanagers", function(dml)
			{
				$.each(dml, function(j, dm)
						{ 
							var listContent = '<tr >';
							listContent += '<td data-toggle="collapse" data-target="#'+dm['id']+'_accordion" class="clickable">';
							listContent += dm['id'];
							listContent += '</td>';
							listContent += '<td data-toggle="collapse" data-target="#'+dm['id']+'_accordion" class="clickable">';
							listContent += dm['networkType'];
							listContent += '</td>';
							listContent += '<td class="right statuscol clickable" id="'+ dm['id'] +'_status" data-toggle="collapse" data-target="#'+dm['id']+'_accordion">';
							listContent += dm['status'];
							listContent += '</td>';
							listContent += '<td class="right">';
							listContent += '<label class="inline-block">';
							listContent += '<div class="input-control switch" data-role="input-control">';
							listContent += '<input id="'+ dm['id'] +'" type="checkbox" checked=""></input>';
							listContent += '<span class="check"></span>';
							listContent += '</label>'
							listContent += '</div>'
							listContent += '</td>';
							listContent += '</tr>';		
							listContent += '<tr>';
							listContent += '<td colspan="6" style="padding:0">';
							listContent += '<div id="'+dm['id']+'_accordion" class="collapse"></div>';
							listContent += '</td>';
							listContent += '</tr>';
							$("#dmtable").append(listContent);
							if(dm['status'] == "STARTED")
								$("#"+dm['id']).prop("checked", true);
							else if(dm['status'] == "STOPPED")
								$("#"+dm['id']).prop("checked", false);
							else
								alert("Unknown DevicesManager status");
							$("#"+dm['id']).attr('disabled', true);
							checkBoxes.push(dm['id']);
							retrieveManagerInfo(dm['id']);
							$("#"+dm['id']).change(function(){statusChanged(dm['id']);});
						});
			}).fail(function()
			{
				alert("{{ site.title }} could be offline");
			});
}

function retrieveManagerInfo(managerId)
{
//	$("#"+managerId+"_accordion").empty();
	$.get("{{ site.rest_endpoint }}/devicesmanagers/"+managerId, function(data)
	{
		if(isEmpty($("#"+managerId+"_accordion")))
		{
				var content="";
				content += '<table class="table hovered" style="margin-bottom:0px"><tbody>';
				for (i in data) {
					content += '<tr class="bg-white">';
					content += '<td><strong>' + i + ':</strong></td><td id="'+ managerId + '_accordion_' + i
							+ '">' + data[i] + '</td>';
					content += '</tr>';
				}
				content += '</tbody></table>';
				$("#"+managerId+"_accordion").append(content);
		}
		else
		{
			for(i in data)
			{
				replaceText(managerId+'_accordion_'+i, data[i]);
			}
		}
	});
}

function isEmpty( el ){
    return !$.trim(el.html())
}

function statusChanged(managerId)
{
	var managerStatus;
	$("#"+managerId).is(':checked') ? managerStatus = "STARTED" : managerStatus = "STOPPED";
	$.ajax({
		type: "POST",
		url: '{{ site.rest_endpoint }}/devicesmanagers/'+managerId,
		data: '{"status":"'+ managerStatus +'"}',
		success: function(data){
					console.log("change devices manager status succeeded. ManagerId: "+ managerId + ", ManagerStatus: "+ managerStatus);
					replaceText(managerId + "_status", managerStatus);
					retrieveManagerInfo(managerId);
				 },
		error: function()
			   {
			      alert("POST status failed...sorry dude :( please try again.");
			   },
	    contentType: "application/json; charset=utf-8"
		});
}

function replaceText(sId, sText) {
	var el;
	console.log(sId + ": " + sText);
	if (document.getElementById && (el = document.getElementById(sId))) {
		while (el.hasChildNodes())
			el.removeChild(el.lastChild);
		el.appendChild(document.createTextNode(sText));
	}
}

function fillCountDevice(count)
{
	$("#countDevice").text(count);
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
	$.each(checkBoxes, function(j, cb)
			{
				$("#"+cb).attr('disabled', false);
			});
//	$("#mqtt_info")
//			.text("Connected to the mqtt broker. (topic: " + topic + ")");
	clearInterval(timer);
}

// called when the client loses its connection
function onConnectionLost(responseObject) {
	if (responseObject.errorCode !== 0) {
		console.log("onConnectionLost:" + responseObject.errorMessage);
	}
	$.each(checkBoxes, function(j, cb)
			{
				$("#"+cb).attr('disabled', true);
			});
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
	
	if(timerAccordion != null)
		clearInterval(timerAccordion);
	if(message.destinationName.match(newDeviceTopic.substring(0,newDeviceTopic.length-1)))
		deviceCount++;
	else if(message.destinationName.match(deviceRemovedTopic.substring(0,deviceRemovedTopic.length-1)))
	{
		if(deviceCount > 0)
			deviceCount--;
	}
	else
		console.log("yo bro! wrong topic");
	
	fillCountDevice(deviceCount);
	
	timerAccordion = setTimeout(function()
	{
		for(managerId in checkBoxes)
		{
			retrieveManagerInfo(checkBoxes[managerId]);
		}
		getDeviceCount();
	}, 3000);
}