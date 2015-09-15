---

---

//global variables

//the map
var map;

// the google hybrid layer
var ggl_hybrid;

// the google street layer
var ggl_street;

// the open street maps layer
var osm;

// the marker clusterer
var markers;

//the progress bar container
var progress;

//the progress bar inner
var progressBar;

//the type-based layer
var markerTypes = {};

//the color translation map
var colors = {
	carta : "#f0da04",
	organico : "#723309",
	vetrolattine : "#00a4f4",
	nonrecuperabile : "#00ff00",
	abiti : "#c100f4",
	plastica : "#c1eef4",
	vehiclespeed : "#c0a467",
	vehiclecounter : "#d54f73",
	simplefilllevelsensor : "#abde85",
	thermometer : "#23e4a6",
	altro : "#cc0000"
};

// initialization on document ready
$(function() {
	initMap();
	initProgressBar();
	loadDevices(true);
});

// initialize leaflet map
function initMap() {
	map = new L.Map('map-canvas', {
		center : new L.LatLng(47.331881,5.032221),
		zoom : 5
	});
	osm = new L.TileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png');
	ggl_hybrid = new L.Google('HYBRID');
	//ggl_street = new L.Google('ROADMAP');
	map.addLayer(ggl_hybrid);
	/*map.addControl(new L.Control.Layers({
		'Open Street Maps' : osm,
		'Google' : ggl_hybrid,
		'Google roadmap' : ggl_street
	}, {}));*/
}

//initialize the progress bar
function initProgressBar()
{
	progress = document.getElementById('progress');
	progressBar = document.getElementById('progress-bar');
}

//load devices handled by the pwal through the PWAL REST endpoint
function loadDevices(initialLoading) {

	if (initialLoading)
		$('#spinner').show();

	// Load the data asynchronously
	// alert("Loading devices");
	var jqXHR = $.getJSON("{{ site.rest_endpoint }}/detaileddevices", function(
			data) {

		if (initialLoading) {
			$('#spinner').hide();

			markers = L.markerClusterGroup({
				chunkedLoading : true,
				chunkProgress : updateProgressBar,
				disableClusteringAtZoom : 18
			});
		} else {
			markerTypes = {};
			markers.clearLayers();
		}

		console.log('start creating markers: ' + window.performance.now());

		// populate the layers of markers to show on the map
		populateMarkers(data);

		// update the current number of devices
		nDevices = data.length;
		console.log('nDevices: ' + nDevices);

		console.log('start clustering: ' + window.performance.now());

		for (type in markerTypes)
			markers.addLayers(markerTypes[type]);
		map.addLayer(markers);

		// log the marker clustering end
		console.log('end clustering: ' + window.performance.now());

		if (initialLoading) {
			// log the start of the watcher process
			console.log('starting marker watching process...');
			setInterval(function() {
				checkDeviceCount();
			}, 4000);
		}
	});
}

function populateMarkers(data) {
	for (var i = 0; i < data.length; i++) {
		var device = data[i];
		var title = device.id;

		// the device marker latitude and longitude object
		var markerLatLng;

		// fill the device location accounting for missing data
		if (device.location != null)
			markerLatLng = new L.LatLng(device.location.lat,
					device.location.lon);
		else
			markerLatLng = new L.LatLng(device.latitude, device.longitude);

		// handle coloring on markers on the map
		var color = colors.altro;

		// generate the device type
		var deviceType;

		if (device.id != null) {
			// extract the identifier for custom-color
			var binNameParts = device.id.split("_");

			// generate the device type for getting colors
			if (binNameParts[0].toLowerCase() in colors)
				deviceType = binNameParts[0].toLowerCase();
			else if ((device.type != null)
					&& (device.type.toLowerCase() in colors))
				deviceType = device.type.toLowerCase();
		}

		color = eval("colors." + deviceType);

		var circleOptions = {
			radius : 15,
			color : color,
			fillColor : color,
			fillOpacity : 0.5,
			title : device.id
		};

		var marker = new L.CircleMarker(markerLatLng, circleOptions);

		// prepare the popUp
		// prepare the title and the location labels
		var content = '<div>' + '<h4>' + device.id + '</h4>' + '<p>'
				+ '<i class="icon-earth"></i> ' + markerLatLng.lat + ' '
				+ markerLatLng.lng
				+ '</p><p><a class="button small" href="device.html?deviceId='
				+ device.pwalId + '">More...</a></p>';

		// extract the name and value of all available properties of the
		// device
		content += '<table class="table striped"><tbody>';
		for (prop in device) {
			if ((prop != "location") && (prop != "unit")) {
				content += '<tr>';
				content += '<td><strong>' + prop + ':</strong></td><td>'
						+ device[prop] + '</td>';
				content += '</tr>';
			}
		}

		content += '</tbody></table>';
		content += '</div>';

		marker.bindPopup(content, {
			// 'minWidth' : 400,
			'autoPan' : true
		});

		// build a per type list
		if (!(deviceType in markerTypes))
			markerTypes[deviceType] = [];

		markerTypes[deviceType].push(marker);
	}
}

function checkDeviceCount() {
	$.get("{{ site.rest_endpoint }}/countDevice", function(data) {
		console.log('current device count: ' + data);
		if (data != nDevices) {
			loadDevices(false);
		}

	});
}

//update the progress bar
function updateProgressBar(processed, total, elapsed, layersArray) {
	if (elapsed > 1000) {
		// if it takes more than a second to load, display the progress
		// bar:
		progress.style.display = 'block';
		progressBar.style.width = Math.round(processed / total * 100)
				+ '%';
	}

	if (processed === total) {
		// all markers processed - hide the progress bar:
		progress.style.display = 'none';
	}
}
