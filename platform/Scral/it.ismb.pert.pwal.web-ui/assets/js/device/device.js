---

---
var map;
var client;
var sem = false;
var charts = {};
var navBarScript;
var numOfValues = 0;
var changeBrightness = 0;

// the re-connection timer
var timer;

// wait for the document to be loaded and ready
$(document).ready(function() {
	Highcharts.setOptions({
		global : {
			useUTC : false
		}
	});
	getDeviceInfo();
});

// get a request parameter given its name
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

// fill the device sub title
function fillSubTitle(device) {
	$("#deviceDescription").text(
			"UUID: " + device.pwalId + " (" + device.id + ")");
}

// extract the device info and arrange it into the corresponding view elements
function getDeviceInfo() {
	var deviceId = getParameterByName('deviceId');
	navBarScript = new NavBarClass();
	navBarScript.onMqttConnecting();
	var uuid = new UUIDGenerator();
	client = new Paho.MQTT.Client("{{ site.mqtt_broker }}",
			Number("{{ site.mqtt_port }}"),
			"{{ site.mqtt_websocket_endpoint }}", "pwalwebgui_local_"
					+ uuid.generateUUID());

	// set callback handlers
	client.onConnectionLost = onConnectionLost;
	client.onMessageArrived = onMessageArrived;
	client.onSuccess = onConnect;

	// connect the client
	client.connect({
		onSuccess : onConnect,
		mqttVersion : 3
	});

	// $("#mqtt_info").text("Connecting to the mqtt broker...");



	// Liquid: replace site.rest_endpoint with the value configured in the
	// site.yml map
	$.get("{{ site.rest_endpoint }}/devices/" + deviceId, function(device) {
		// fill subtitle
		fillSubTitle(device);
		$("#messages").text(JSON.stringify(device,null,'\t'));
		// fill the device data box
		fillDeviceInfoBox(device);

		// fill map view
		// fillMapPosition(device);

		// activate the update countdown
		startCountDown(device.expiresAt);
	});
}

// handle count-down to the next update
function startCountDown(expiration) {
	if (expiration != null) {
		$("#countDown").empty();
		$("#blink").hide();
		$("#countDown").countdown({
			style : {
				// background color
				background : "bg-transparent",
				// foreground color
				foreground : "fg-black",
				// divider color
				divider : "fg-black"
			},
			blink : false,
			stoptimer : new Date(expiration),
			onstop : onCountDownStop
		});
		$("#countDown").show();
	}
}

function onCountDownStop() {
	$("#countDown").hide();
	$("#countDown").countdown('destroy');
	$("#blink").show();
	$("#blink").blink({
		delay : 500
	});
}

// fill the device information box
function fillDeviceInfoBox(device) {
	var content = "";
	content += '<table class="table striped"><tbody>';
	for (i in device) {
		if ((i != "unit") && (i != "location")) {
			content += '<tr>';
			content += '<td><strong>' + i + ':</strong></td><td id="' + i
					+ '">' + device[i] + '</td>';
			content += '</tr>';
		}
	}
	
	switch (device.type) {
	case "PhilipsHue":
		content += '<tr>';
		content += '<td><strong>Change bulb brightness</strong></td>';
		content += '<td><input type="range" id="slider-brightness" min="1" max="254"></td>';
// content += '<td><div class="slider span8" id="slider-brightness"
// data-role="slider" data-show-hint="true" data-animate="true"></div></td>';
		content += '</tr>';
		
		content += '<tr>';
		content += '<td><strong>Change bulb saturation</strong></td>';
		content += '<td><input type="range" id="slider-saturation" min="1" max="254"></td>';
		content += '</tr>';
		
		content += '<tr>';
		content += '<td><strong>Change bulb hue</strong></td>';
		content += '<td><input type="range" id="slider-hue" min="1" max="65535"></td>';
		content += '</tr>';
		
		content += '<tr>';
		content += '<td><strong>Switch on/off</strong></td>';
		content += '<td><div class="input-control switch" data-role="input-control">';
		content += '<label class="inline-block" style="margin-right: 20px">';
		content += '<input id="switch-onoff" type="checkbox">';
		content += '<span class="check"></span></label></div>'
		content += '</td>';
		content += '</tr>';
		
		content += '<tr>';
		content += '<td><strong>Change color</strong></td>';
// content += '<td>#<input type="text" id="colorPicker"></input>'
		content += '<td><div class="span8" id="colorPicker"></div>'
		content += '</td>';
		content += '</tr>';
		
// $("#device_position").text("Videocamera");
// var camera_content = '';
// camera_content += '<img src="http://130.192.85.32:8090/" />'
// $("#map-canvas").append(camera_content);
		break;
	case "WaterPump":
		content += '<tr>';
		content += '<td><strong>Change water pump speed</strong></td>';
		content += '<td><div class="slider span6" id="slider-pumpspeed" data-role="slider" data-show-hint="true" data-animate="true"></div></td>';
		content += '</tr>';
		break;
	case "Meter":
		content += '<tr>';
		content += '<td><strong>Switch on/off</strong></td>';
		content += '<td><div class="input-control switch" data-role="input-control">';
		content += '<label class="inline-block" style="margin-right: 20px">';
		content += '<input id="switch-onoff" type="checkbox">';
		content += '<span class="check"></span></label></div>'
		content += '</td>';
		content += '</tr>';
		content += '<tr>';
		content += '<td><strong>New data available in: </strong></td>';
		content += '<td id="countDownCol"><div id="countDown" class="countdown"></div>';
		content += '<div class="blink_second" id="blink">Waiting for new data from device<div></td>';
		content += '</tr>';
		break;
	default:
		content += '<tr>';
		content += '<td><strong>New data available in: </strong></td>';
		content += '<td id="countDownCol"><div id="countDown" class="countdown"></div>';
		content += '<div class="blink_second" id="blink">Waiting for new data from device<div></td>';
		content += '</tr>';
		break;
	}
	content += '</tbody></table>';
	content += '</div>';
	$("#deviceInfo").append(content);

	changeBrightness= $("#brightness").text();
	console.log(changeBrightness);
	
	$("#blink").hide();
	
	if($("#slider-brightness") != null)
	{
		$("#slider-brightness").change(function(event){onPhilipsHueSliderBrightnessChange();})
		$("#slider-brightness").attr({"value":parseInt($("#brightness").text())});
	}

	if($("#slider-saturation") != null)
	{
		$("#slider-saturation").change(function(event){onPhilipsHueSliderSaturationChange();})
		$("#slider-saturation").attr({"value":parseInt($("#saturation").text())});
	}
	
	if($("#slider-hue") != null)
	{
		$("#slider-hue").change(function(event){onPhilipsHueSliderHueChange();})
		$("#slider-hue").attr({"value":parseInt($("#hue").text())});
	}
	
	if($("#swtich-onoff") != null)
	{
		$("#switch-onoff").change(function(event){onOnOffSwitchChange();})
		var isOn = $("#isOn").text();
		$("#switch-onoff").prop('checked',eval(isOn));
	}
	
	if($("#slider-pumpspeed") != null)
	{
		$("#slider-pumpspeed").change(function(event){onWaterPumpSliderChange();})
		$("#slider-pumpspeed").attr({"checked":parseInt($("#speed").text())});
	}
	
	if($("#colorPicker") != null)
	{
		$("#colorPicker").colpick({
				flat:true,
				layout:'rgb',
				submit:0,
				onChange: function(hsb, rgb, el, bySetColor)
				{
					onPhilipsHueColorPickChange(el);
				}
		});
		var rgb = $("#rgbcolor").text().split(",");
		$("#colorPicker").colpickSetColor('{r:' + rgb[0] + ', g:' + rgb[1] + ', b:' + rgb[2] + '}' ,true);
	}
}

// callback for philips hue color picker
function onPhilipsHueColorPickChange(rgb)
{
	var json = '{"commandName": "changeRGBColor","params": { "R": ' 
		+ rgb.r + ', "G": '+ rgb.g +', "B": '+rgb.b+'}}';
		var deviceId = getParameterByName('deviceId');
		$.ajax({
			url : "{{ site.rest_endpoint }}/devices/" + deviceId,
			type : "POST",
			data : json,
			contentType : "application/json;",
			dataType : "json"
		});
}

// callback for philips hue brightness slider changed event
function onPhilipsHueSliderBrightnessChange() {
		var value = $("#slider-brightness").val();
		var json = "{\"commandName\": \"changeBrightness\",\"params\": { \"brightness\": "
				+ value + "}}";
// console.log(json);

		var deviceId = getParameterByName('deviceId');
		// console.log(deviceId);
		$.ajax({
			url : "{{ site.rest_endpoint }}/devices/" + deviceId,
			type : "POST",
			data : json,
			contentType : "application/json;",
			dataType : "json"
		});
}

// callback for philips hue saturation slider changed event
function onPhilipsHueSliderSaturationChange() {
		var value = $("#slider-saturation").val();
		var json = "{\"commandName\": \"changeSaturation\",\"params\": { \"saturation\": "
				+ value + "}}";
// console.log(json);

		var deviceId = getParameterByName('deviceId');
		// console.log(deviceId);
		$.ajax({
			url : "{{ site.rest_endpoint }}/devices/" + deviceId,
			type : "POST",
			data : json,
			contentType : "application/json;",
			dataType : "json"
		});
}

// callback for philips hue saturation slider changed event
function onPhilipsHueSliderHueChange() {
		var value = $("#slider-hue").val();
		var json = "{\"commandName\": \"changeHue\",\"params\": { \"hue\": "
				+ value + "}}";
// console.log(json);

		var deviceId = getParameterByName('deviceId');
		// console.log(deviceId);
		$.ajax({
			url : "{{ site.rest_endpoint }}/devices/" + deviceId,
			type : "POST",
			data : json,
			contentType : "application/json;",
			dataType : "json"
		});
}

// callback for water pump slider change event
function onWaterPumpSliderChange(event) {
	var json = "{\"commandName\": \"setSpeed\",\"params\": [" + event + "]}";
	// console.log(json);

	var deviceId = getParameterByName('deviceId');
	// console.log(deviceId);
	$.ajax({
		url : "{{ site.rest_endpoint }}/devices/" + deviceId,
		type : "POST",
		data : json,
		contentType : "application/json;",
		dataType : "json"
	});
}

// callback for philips hue on off switch
function onOnOffSwitchChange()
{
	var value = $("#switch-onoff").prop('checked');
	if(value)
		var json = "{\"commandName\": \"turnOn\"}";
	else
		var json = "{\"commandName\": \"turnOff\"}";
		// console.log(json);

	var deviceId = getParameterByName('deviceId');
	// console.log(deviceId);
	$.ajax({
		url : "{{ site.rest_endpoint }}/devices/" + deviceId,
		type : "POST",
		data : json,
		contentType : "application/json;",
		dataType : "json"
	});
}

// place a marker in the real location of the device, on the map view
function fillMapPosition(data) {

	// build the latitude an longitude location
	var markerLatLng = new google.maps.LatLng(data.location.lat,
			data.location.lon);
	// build the marker
	var marker = new google.maps.Marker({
		position : markerLatLng,
		title : data.id,
	});

	// specify map options
	var mapOptions = {
		zoom : 18,
		center : markerLatLng,
		mapTypeId : google.maps.MapTypeId.SATELLITE,
		disableDefaultUI : true
	};

	// build the map
	map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);

	// add the marker to the map
	marker.setMap(map);
}

// called when the client connects
function onConnect() {
	console.log("connected");
	navBarScript.onMqttConnect();
	var deviceId = getParameterByName('deviceId');
	var topic = "{{ site.mqtt_root_topic }}/observation/iotentity/_"
			+ deviceId.replace(/-/g, "_");

client.subscribe(topic, {
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
			client.connect({
				onSuccess : onConnect,
				mqttVersion : 3
			});
		}
	}, 5000);

}

// called when a message arrives
function onMessageArrived(message) {

	var jsonPayload = jQuery.parseJSON(message.payloadString);
	$("#messages").empty();
	$("#messages").text(JSON.stringify(jsonPayload,null,'  '));
	console.log(jsonPayload);
	var expiration;

	$.each(jsonPayload.Properties,
			function(i, prop) {
		$
				.each(
						prop.IoTStateObservation,
						function(j, iso) {
							var props = prop.About
							.split(":");
							var propName = props[props.length-1];
							var measuredQuantity = propName
									.substring(3, 4)
									.toLowerCase()
									+ propName.substring(4);
							expiration = iso.ResultTime;
							replaceText("updatedAt",
									iso.PhenomenonTime);
							replaceText("expiresAt",
									iso.ResultTime);
							replaceText(measuredQuantity,
									iso.Value);
			switch ($("#type").text()) {
			case "PhilipsHue":
				$("#slider-brightness").attr({"value":parseInt($("#brightness").text())});
				$("#slider-saturation").attr({"value":parseInt($("#saturation").text())});
				$("#slider-hue").attr({"value":parseInt($("#text").text())});
				break;
			case "WaterPump":
				// TODO
				break;
			case "Meter":
				createCharts(measuredQuantity, iso);	
				if(measuredQuantity == "isOn")
				{
					if($("#isOn").text() == "true")
						$("#switch-onoff").prop('checked');
					else
						$("#switch-onoff").prop('checked', false);
				}
				break;
			default:
				createCharts(measuredQuantity, iso);
				break;
			}
		});
	});


}

function createCharts(measuredQuantity, iso) {
	if ($("#countDown").is(":hidden"))
		startCountDown(iso.ResultTime);

	if(measuredQuantity != "isOn")
	{
		// create the chart div if needed
		if ($("#" + measuredQuantity + "_chart").length == 0) {
			var content = '<div class="panel"><div class="panel-header bg-lightBlue fg-white">'
					+ measuredQuantity
					+ '</div><div id="'
					+ measuredQuantity
					+ '_chart" style="min-width: 310px; height: 400px; margin: 0 auto">Chart for '
					+ measuredQuantity + '</div></div>';
			$("#values").append(content);
	
			// create the chart
			createChart(measuredQuantity + "_chart");
		}
	
		if (numOfValues < 10) {
			charts[measuredQuantity + "_chart"].series[0].addPoint([
					(new Date()).getTime(), Number(iso.Value) ]);
			numOfValues++;
		} else
			charts[measuredQuantity + "_chart"].series[0].addPoint([
					(new Date()).getTime(), Number(iso.Value) ], true, true);
	}
// console.log(prop);
}

function replaceText(sId, sText) {
	var el;
	if (document.getElementById && (el = document.getElementById(sId))) {
		while (el.hasChildNodes())
			el.removeChild(el.lastChild);
		el.appendChild(document.createTextNode(sText));
	}
}

function createChart(divName, title) {
	charts[divName] = new Highcharts.Chart({
		chart : {
			type : 'spline',
			animation : Highcharts.svg, // don't animate in old IE
			marginRight : 10,
			events : {
			/*
			 * load : function() { // set up the updating of the chart each
			 * second var series = this.series[0]; setInterval(function() { var
			 * x = (new Date()).getTime(), // current time y = Math.random();
			 * series.addPoint([ x, y ], true, true); }, 1000); }
			 */
			},
			renderTo : divName
		},
		title : {
			text : title
		},
		xAxis : {
			type : 'datetime',
			tickPixelInterval : 150
		},
		yAxis : {
			title : {
				text : 'Value'
			},
			plotLines : [ {
				value : 0,
				width : 1,
				color : '#808080'
			} ]
		},
		tooltip : {
			formatter : function() {
				return '<b>' + this.series.name + '</b><br/>'
						+ Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x)
						+ '<br/>' + Highcharts.numberFormat(this.y, 2);
			}
		},
		legend : {
			enabled : false
		},
		exporting : {
			enabled : false
		},
		series : [ {
			name : title,
			data : []
		} ]
	});
}
