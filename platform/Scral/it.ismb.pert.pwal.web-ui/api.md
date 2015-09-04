---
title: PWAL - Rest APIs
layout: api
---

## Physical World Abstraction Layer - REST APIs ##
-----

The Physical World Abstraction Layer (PWAL) APIs defines a common interface to access, query and actuate devices handled through the PWAL device managers. Devices are made available with features and commands defined independently from the underlying network protocols, and can be accessed and operated either singularly or in batches.

The PWAL REST APis offer the ability to:

* Get the list of currently handled devices;
* Get the current state of all or of single devices;
* Actuate single devices.

The exposed REST resources are reported in the following. To select the desired response type (currently only JSON is supported, except for SCPD descriptions that are provided in `XML`), the `Accept` HTTP header must be used in the request. In the same way, a proper `Content-Type` *must* be always present for `PUT` and `POST` requests.

----

#### Available Resources ####

|Resource| Description|
|:----|:----|
|[/countDevice](#deviceCount) | Represents the amount of devices currently handled by the PWAL|
|[/devices](#devices) | Represents devices interfaced by the PWAL layer and accessible through the rest APIs. |
|[/detaileddevices](#detailedDevices)| Represents devices interfaced by the PWAL layer and accessible through the rest APIs, inclusing all available details.| 
|[devices/{deviceId}](#singleDevice) | Represents a single device |
|[devices/{deviceId}/scpd](#deviceSCPD) | Represents the SCPD description of a single device |
|[/devicesmanagers](#deviceManagers)| Represents device managers exploited by the PWAL to interface physical devices|
|[devicesmanagers/{deviceManagerId}](#singleDeviceManager)| Represents a single device manager|

----

## Tutorial ##
-----

#####1) Show paper bins on a map #####

Let's imagine to have the need of building a visual representation of available paper bins on a [Google Map](https://developers.google.com/maps/documentation/javascript/) view. 

We first need to extract detailed information about available paper bin devices. In the current API version no filtering functionality can be exploited which eases the selection of a specific kind of device, whereas in next releases more advanced selection features will be offered. 

Therefore, the first step to accomplish for selecting paper bins only, is to get a complete and detailed list of available devices.

<pre>
function loadDevices() {
	// alert("Loading devices");
	var jqXHR = $.getJSON(
			"http://almanac-showcase.ismb.it:8000/iot360/scral/detaileddevices", function(
					data) {
				placeDevices(data);
			});
}
</pre>

Secondly, we need to filter-out paper bins and to draw them as markers on the map. To filter paper bins only, a double phase strategy may be adopted: first we only select `WasteBin` devices, and second, among such devices we only select those having an id which starts by `carta`.

<pre>
function filterDevices(data) {
	for(device in data){
		//check the device type
		if(device.type == "WasteBin"){
			// extract the identifier prefix
			var binNameParts = device.id.split("_");
			//check if it is a paper bin
			if (binNameParts[0].toLowerCase() == "carta"){
				//place the device on the map
				placeDeviceOnMap(device);
			}
		}
	}
}
</pre>

Finally, we just need to place the device on a map, e.g., by using a MarkerManager for clustering nearby bins.

<pre>
function placeDevicesOnMap(device, index) {
	var markerLatLng;
	if (device.location != null)
		markerLatLng = new google.maps.LatLng(device.location.lat,
				device.location.lon);
	else
		markerLatLng = new google.maps.LatLng(device.latitude, device.longitude);
	// build the marker
	var marker = new google.maps.Marker({
		position : markerLatLng,
		title : device.id,
		icon : {
			path : google.maps.SymbolPath.CIRCLE,
			scale : 15,
			fillColor : '#FF0000',
			strokeColor : '#FF0000',
			strokeWeight : 2,
			fillOpacity : 0.5
		},
		deviceIndex : index
	});
	// marker.setMap(map);
	markerManager.addMarker(marker);
}
</pre>
The resulting page is pretty similar to the [Map](map.html) section of this site, whose source code can be easily inspected by righ-clicking on the browser window and by selecting "view page source".

----


## Resource detail ##
-----

### <a id="deviceCount"></a> Resource /countDevice###

<span class="label info place-right">version 0.9</span>

*Updated on Fri, 2014-10-17*


Describes the current total amount of devices managed by the PWAL layer

**URL**: /countDevice

|Method | Operation description |
|:------|:----------------------|
|GET| Get the total amount of handled devices, as an integer number|

##### Example Request ######

GET http://almanac-showcase.ismb.it:8000/iot360/scral/countDevice

{% include list-group-header.html title='Example Response' %}
<pre>
10198
</pre>	
{% include list-group-footer.html%}


----

### <a id="devices"></a> Resource /devices###

<span class="label info place-right">version 0.9</span>

*Updated on Fri, 2014-10-17*


Represents devices interfaced by the PWAL layer and accessible through the rest APIs.

**URL**: /devices

|Method | Operation description |
|:------|:----------------------|
|GET| Provides the list of unique IDs (pwalId) associated to handled devices. IDs may be used to retrieved detailed information |

##### Example Request ######

GET http://almanac-showcase.ismb.it:8000/iot360/scral/devices

{% include list-group-header.html title='Example Response' %}
<pre>
{
	"pwalId":[
		"eba6b372-3e05-30c4-997e-6db4a076d612",
		"2099d91e-547f-322e-a554-d85039aeb03a",
		"d7804647-d1c7-3883-9f62-bb81879de80a",
		"885d0991-14b2-3e19-9829-68af4a2f0804",
		"65043876-feef-3d39-9fe1-a93ee11e0e21",
		"82101dbc-c308-3f08-b69a-f4ea24dfeb26", 
		... ,
		"d60ea029-1822-3e52-b1db-ecd4739c5fe2"
		]
}
</pre>	
{% include list-group-footer.html%}

----

### <a id="detailedDevices"></a> Resource /detaileddevices###

<span class="label info place-right">version 0.9</span>

*Updated on Fri, 2014-10-17*


Represents devices interfaced by the PWAL layer and accessible through the rest APIs.

**URL**: /detaileddevices

|Method | Operation description |
|:------|:----------------------|
|GET| Provides the list of handled devices together with their detailed information |

##### Example Request ######

GET http://almanac-showcase.ismb.it:8000/iot360/scral/detaileddevices

{% include list-group-header.html title='Example Response' %}
<pre>
[
	{
		"pwalId":"16a801b9-300e-3c03-a889-0f150f4fd1c7",
		"type":"WasteBin",
		"networkType":"WasteBinSimulator",
		"fillLevel":14,
		"updatedAt":"2014-10-17T08:22:34.036+0000",
		"expiresAt":"2014-10-17T08:25:34.036+0000",
		"latitude":45.09447766,
		"longitude":7.70180677,
		"temperature":"23",
		"unit":{"value":"Celsius","symbol":"C","type":"basicSI"},
		"location":{
			"name":null,
			"lat":45.09447766,
			"lon":7.70180677,
			"ele":null,
			"exposure":null,
			"domain":null,
			"disposition":null
		},
		"id":"NonRecuperabile_79211209"
	},
	{
		"pwalId":"6b7c1ad4-ec0f-3e4e-97b0-cf662ce85e2b",
		"type":"WasteBin",
		"networkType":"WasteBinSimulator",
		"fillLevel":11,"updatedAt":"2014-10-17T08:22:32.469+0000",
		"expiresAt":"2014-10-17T08:25:32.469+0000",
		"latitude":45.06832169,
		"longitude":7.66833902,
		"temperature":"23",
		"unit":{"value":"Celsius","symbol":"C","type":"basicSI"},
		"location":{
			"name":null,
			"lat":45.06832169,
			"lon":7.66833902,
			"ele":null,
			"exposure":null,
			"domain":null,
			"disposition":null},
		"id":"NonRecuperabile_79221648_1"
	},
	...
]
</pre>	
{% include list-group-footer.html%}

----

### <a id="singleDevice"></a> Resource /devices/{deviceId} ###

<span class="label info place-right">version 0.9</span>

*Updated on Fri, 2014-10-17*


Represents a single device interfaced by the PWAL layer and accessible through the rest APIs.

**URL**: /devices/{deviceId}

|Method | Operation description |
|:------|:----------------------|
|GET| Provides the most up-to-date detailed information about the device identified by the given device id, including current measures and/or states|
|POST| Sends a command to the device identified by the given device id|

##### Example Request ######

GET http://almanac-showcase.ismb.it:8000/iot360/scral/devices/eba6b372-3e05-30c4-997e-6db4a076d612

{% include list-group-header.html title='Example Response' %}
<pre>
{
	"pwalId":"eba6b372-3e05-30c4-997e-6db4a076d612",
	"type":"WasteBin",
	"networkType":"WasteBinSimulator",
	"fillLevel":40,
	"updatedAt":"2014-10-17T14:04:53.840+0000",
	"expiresAt":"2014-10-17T14:07:53.840+0000",
	"id":"Organico_79220707",
	"latitude":45.07008799,
	"longitude":7.64206727,
	"temperature": 20,
	"unit":{"value":"Celsius","symbol":"C","type":"basicSI"},
	"location":{
		"name":null,
		"lat":45.07008799,
		"lon":7.64206727,
		"ele":null,
		"exposure":null,
		"domain":null,
		"disposition":null
	}
}
</pre>	
{% include list-group-footer.html%}

POST http://almanac-showcase.ismb.it:8000/iot360/scral/devices/eba6b372-3e05-30c4-997e-6db4a076d612

{% include list-group-header.html title='Example Request body' %}
<pre>
{
	"setFillLevel": [
		"76"
	]
}
</pre>	
{% include list-group-footer.html%}
----

### <a id="deviceSCPD"></a> Resource /devices/{deviceId}/scpd ###

<span class="label info place-right">version 0.9</span>

*Updated on Fri, 2014-10-17*


Represents the UPNP SCPD description of a single device interfaced by the PWAL layer and accessible through the rest APIs.

**URL**: /devices/{deviceId}/scpd

|Method | Operation description |
|:------|:----------------------|
|GET| Provides the most up-to-date detailed information about the device identified by the given device id, including current measures and/or states, using the SCPD XML format defined by UPNP|

##### Example Request ######

GET http://almanac-showcase.ismb.it:8000/iot360/scral/devices/eba6b372-3e05-30c4-997e-6db4a076d612/scpd

{% include list-group-header.html title='Example Response' %}
<pre>
{% capture xml %}
{% include api/scpd.xml %}
{% endcapture %}
{{ xml | escape }}
</pre>	
{% include list-group-footer.html%}

----

### <a id="deviceManagers"></a> Resource /devicesmanagers ###

<span class="label info place-right">version 0.9</span>

*Updated on Fri, 2014-10-17*


Represents device managers exploited by the PWAL to interface physical devices.

**URL**: /devicesmanagers

|Method | Operation description |
|:------|:----------------------|
|GET| Provides the most up-to-date detailed information about the device manager modules currently exploited by the PWAL.|

##### Example Request ######

GET http://almanac-showcase.ismb.it:8000/iot360/scral/devicesmanagers

{% include list-group-header.html title='Example Response' %}
<pre>
[
	{
		"id":"d6b54c66-3f9f-40ae-953b-15d6bc297adc",
		"status":"STARTED",
		"pollingTimeMillis":10000,
		"networkType":"M2M",
		"devicesListNames":["pipe2","bench2","bench1","pipe1"],
		"contentInstancesList":{
			"pipe2":"http://m2mtilab.dtdns.net/etsi/almanac/applications/water/containers/pipe2/contentInstances",
			"bench2":"http://m2mtilab.dtdns.net/etsi/almanac/applications/smartbench/containers/bench2/contentInstances",
			"bench1":"http://m2mtilab.dtdns.net/etsi/almanac/applications/smartbench/containers/bench1/contentInstances",
			"pipe1":"http://m2mtilab.dtdns.net/etsi/almanac/applications/water/containers/pipe1/contentInstances"},
		"activeSubscriptionsSize":18
	},
	{
		"id":"396a41a9-e16c-4c9e-99c5-f62a37ce282f",
		"status":"STARTED",
		"pollingTimeMillis":60000,
		"networkType":"SmartSantander",
		"activeSubscriptionsSize":38
	},
	{
		"id":"ae07b1e0-e7b2-459d-aea0-605719f93a4b",
		"status":"STARTED",
		"pollingTimeMillis":180000,
		"networkType":"WasteBinSimulator",
		"activeSubscriptionsSize":10902
	}
]
</pre>	
{% include list-group-footer.html%}

----

### <a id="deviceManagers"></a> Resource /devicesmanagers/{deviceManagerId} ###

<span class="label info place-right">version 0.9</span>

*Updated on Fri, 2014-10-17*


Represents a single device manager.

**URL**: /devicesmanagers/{deviceManagerId}

|Method | Operation description |
|:------|:----------------------|
|GET| Provides the most up-to-date detailed information about the device manager having the given id.|
|POST| Sets the current state of the device manager having the given id. |

##### Example Requests ######

GET http://almanac-showcase.ismb.it:8000/iot360/scral/devicesmanagers/396a41a9-e16c-4c9e-99c5-f62a37ce282f

{% include list-group-header.html title='Example Response' %}
<pre>
{
	"id":"396a41a9-e16c-4c9e-99c5-f62a37ce282f",
	"status":"STARTED",
	"pollingTimeMillis":60000,
	"networkType":"SmartSantander",
	"activeSubscriptionsSize":38
}
</pre>	
{% include list-group-footer.html%}

POST http://almanac-showcase.ismb.it:8000/iot360/scral/devicesmanagers/396a41a9-e16c-4c9e-99c5-f62a37ce282f

{% include list-group-header.html title='Example Request body' %}
<pre>
{
	"status": "STOPPED"
}
</pre>	
{% include list-group-footer.html%}