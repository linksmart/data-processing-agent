---

---

// global variables

// the map
var map;

// the google hybrid layer
var ggl_hybrid;

// the google street layer
var ggl_street;

// the open street maps layer
var osm;

// the marker clusterer
var markers;

// the current amount of devices
var nDevices = 0;

// the progress bar container
var progress;

// the progress bar inner
var progressBar;

// the type-based layer
var markerTypes = {};

// the geocoder object
var geocoder;

// the color translation map
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

var substringMatcher = function(strs) {
	return function findMatches(q, cb) {
		var matches, substrRegex;

		// an array that will be populated with substring matches
		matches = [];

		// regex used to determine if a string contains the substring `q`
		substrRegex = new RegExp(q, 'i');

		// iterate through the pool of strings and for any string that
		// contains the substring `q`, add it to the `matches` array
		$.each(strs, function(i, str) {
			if (substrRegex.test(str)) {
				// the typeahead jQuery plugin expects suggestions to a
				// JavaScript object, refer to typeahead docs for more info
				matches.push({
					value : str
				});
			}
		});

		cb(matches);
	};
};

// initialization on document ready
$(function() {
	// attach the form submission event
	$('#search-form').submit(function(event) {
		// handle submission events
		handleFiltering();
		// prevent default form submission behavior
		event.preventDefault();
		return false;
	});

	// attach the handler for the "near to me" button
	$('#nearToMe').click(function() {
		handleLocation();
	});

	// attach the address picker
	var addressPicker = new AddressPicker();
	var typeAheadWhere = $('#where').typeahead(null, {
		displayKey : 'description',
		source : addressPicker.ttAdapter()
	});

	typeAheadWhere.on('typeahead:selected', function(event, selection) {
		$('#search-form').submit();
	});

	typeAheadWhere.on('typeahead:autocompleted', function(event, selection) {
		$('#search-form').submit();
	})

	// prepare the typeahead types
	var types = [];
	for (i in colors)
		types.push(i);

	var typeAheadWhat = $('#what').typeahead({
		hint : true,
		highlight : true,
		minLength : 1
	}, {
		name : 'types',
		displayKey : 'value',
		source : substringMatcher(types)
	});

	typeAheadWhat.on('typeahead:selected', function(event, selection) {
		$('#search-form').submit();
	});

	typeAheadWhat.on('typeahead:autocompleted', function(event, selection) {
		$('#search-form').submit();
	})

	// initialize the geocoder
	geocoder = new google.maps.Geocoder();

	initMap();
	initProgressBar();
	initLegend();
	// initSearch();
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
	ggl_street = new L.Google('ROADMAP');
	map.addLayer(ggl_hybrid);
	map.addControl(new L.Control.Layers({
		'Open Street Maps' : osm,
		'Google' : ggl_hybrid,
		'Google roadmap' : ggl_street
	}, {}));
	map.on('popupopen', function(e) {
		// Convert latlng to pixels
		var px = map.project(e.popup._latlng);
		// Add pixel height offset to converted pixels (screen origin is top
		// left)
		px.y -= e.popup._container.clientHeight * 2 / 3;
		// pan to center
		map.panTo(map.unproject(px), {
			animate : true
		});
	});
	map.on('baselayerchange', function(e) {
		console.log(e);
		// check the base layer name
		if (e.name != "Google") {
			$("#search-form").children(".fg-white").removeClass("fg-white")
					.addClass("fg-darkBlue");
		} else {
			$("#search-form").children(".fg-darkBlue").removeClass(
					"fg-darkBlue").addClass("fg-white");
		}
		;
	});
}

// initialize the legend
function initLegend() {

	var legend = L.control({
		position : 'bottomright'
	});

	legend.onAdd = function(map) {

		// initialize the legend
		for (type in colors) {
			$("#legend")
					.append(
							"<div class=\"fg-white\"><i style=\"background: "
									+ colors[type]
									+ "; padding: 2px 10px; border-radius: 10px; width: 10px;\"></i> &nbsp;<strong>"
									+ type + "</strong></div><br/>")
		}

		return $("#legend")[0];
	};

	legend.addTo(map);
}

// initialize the search form
function initSearch() {
	var searchForm = L.control({
		position : 'topleft'
	});
	searchForm.onAdd = function(map) {
		return $("#search-control")[0];
	}

	searchForm.addTo(map);
}

// initialize the progress bar
function initProgressBar() {
	progress = document.getElementById('progress');
	progressBar = document.getElementById('progress-bar');
}

// load devices handled by the pwal through the PWAL REST endpoint
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

// update the progress bar
function updateProgressBar(processed, total, elapsed, layersArray) {
	if (elapsed > 1000) {
		// if it takes more than a second to load, display the progress
		// bar:
		progress.style.display = 'block';
		progressBar.style.width = Math.round(processed / total * 100) + '%';
	}

	if (processed === total) {
		// all markers processed - hide the progress bar:
		progress.style.display = 'none';
	}
}

// handle device filtering based on id / type or location
function handleFiltering() {
	// get the text inside the what field
	var what = $('#what').val();

	// clear any marker already present
	markers.clearLayers();

	// search for matches against the what field, if not empty
	if (what.length > 0) {
		// handle the id
		if (what in markerTypes)
			markers.addLayers(markerTypes[what]);
	} else {
		for (type in markerTypes)
			markers.addLayers(markerTypes[type]);
	}

	// get the text inside the where field and filter using the content as a
	// real worl address
	var where = $('#where').val()

	if (where.length > 0) {
		// find the latitude and longitude of the given address
		geocoder.geocode({
			'address' : where
		}, function(results, status) {
			if (status == google.maps.GeocoderStatus.OK) {
				// set the map center to the found location
				var location = results[0].geometry.location;
				map.panTo(new L.LatLng(location.lat(), location.lng()));
				// set the zoom level
				map.setZoom(Math.max(map.getZoom(), 12));
			} else {
				alert('Geocode was not successful for the following reason: '
						+ status);
			}
		});
	} /*
		 * else { map.panTo(new L.LatLng(45.0667, 7.7)); }
		 */

}

// handle self location using HTML5 geo location
function handleLocation() {
	// Try HTML5 geolocation, if available
	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(function(position) {
			var pos = new L.LatLng(position.coords.latitude,
					position.coords.longitude);

			// open an inforwindow for showing the detected
			// position
			/*
			 * var infowindow = new google.maps.InfoWindow( { map : map,
			 * position : pos, content : '<div id="content">You are currently
			 * located here</div>' });
			 */

			// center and zoom
			map.panTo(pos);
			map.setZoom(Math.max(map.getZoom(), 16));

		}, function() {
			// alert the user to explain why location is not
			// working
			alert("No location supported");
		});

	}
}
