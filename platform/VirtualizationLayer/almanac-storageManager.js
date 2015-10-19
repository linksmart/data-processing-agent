"use strict";
/*
	Virtualization Layer | StorageManager client
		by Alexandre Alapetite http://alexandre.alapetite.fr
			from Alexandra Institute http://www.alexandra.dk
			for the ALMANAC European project http://www.almanac-project.eu
*/

var xmlWriter = require('xml-writer'),
	atomWriter = require('atom-writer');

module.exports = function (almanac) {

	function proxyStorageManager(req, res) {

		req.pipe(almanac.request({
				method: req.method,
				uri: almanac.config.hosts.storageManagerUrl + req.url,
				timeout: 25000,
			}, function (error, response, body) {
				if (error || response.statusCode != 200 || !body) {
					almanac.log.warn('VL', 'Error ' + (response ? response.statusCode : 'undefined') + ' proxying to StorageManager!');
					if (!body) {
						almanac.basicHttp.serve503(req, res);
					}
				} else if (req.method === 'POST') {
					almanac.log.verbose('VL', 'POST request forwarded to StorageManager' + JSON.stringify({response: response}));
				}
			})).pipe(res);
	}

	// function dmToGeojson(json) {	//Conversion to GeoJSON format (JSON convention for geographic data)
		// var geoJson = {
				// 'type': 'FeatureCollection',
				// 'features': [],
			// };
		// if (json) {
			// if (Array.isArray(json.IoTStateObservation) && json.IoTStateObservation.length > 0 &&
				// /[0-9.]+ [0-9.]+/.test(json.IoTStateObservation[0].Value)) {	//Encapsulate IoTStateObservation into an IoTProperty. Ex: /dm-geojson/IoTEntities/40A5F4D2-54AD-4B5B-9996-5AF6DB7046CB/Properties/40A5F4D2-54AD-4B5B-9996-5AF6DB7046CB:location/observations
				// json = {
						// IoTProperty: [{
								// DataType: 'xs:geojson', 	//Assuming correct type if the user asks to transform output to GeoJSON
								// IoTStateObservation: [ json.IoTStateObservation[0] ],	//Take only the first/newest IoTStateObservation
							// }],
					// };
			// }
			// if (Array.isArray(json.IoTProperty)) {	//Encapsulate a single IoTProperty into an IoTEntity. Ex: /dm-geojson/IoTEntities/E3CFE564-EFD8-4839-BB69-89F1939DADE0/properties
				// json = {
						// IoTEntity: [{
								// Properties: json.IoTProperty,
							// }],
					// };
			// }
			// if (Array.isArray(json.IoTEntity)) {	//Ex: /dm-geojson/IoTEntities
				// var ioTEntities = json.IoTEntity,
					// coordinates = [];
				// for (var i = 0; i < ioTEntities.length; i++) {
					// var ioTEntity = ioTEntities[i];
					// if (Array.isArray(ioTEntity.Meta)) {
						// for (var j = 0; j < ioTEntity.Meta.length; j++) {
							// var iotMeta = ioTEntity.Meta[j];
							// if (iotMeta && iotMeta.Value && iotMeta.property === 'geo:point') {
								// coordinates = iotMeta.Value.split(' ', 3);
								// geoJson.features.push({
									// 'type': 'Feature',
									// 'geometry': {
										// 'type': 'Point',
										// 'coordinates': [1 * (coordinates[0] || 0), 1 * (coordinates[1] || 0)],	//GeoJSON: longitude, latitude
									// },
									// 'properties': {
										// 'name': ioTEntity.Name,
										// 'description': ioTEntity.Description,
									// },
								// });
								// break;
							// }
						// }
					// }
					// if (Array.isArray(ioTEntity.Properties)) {
						// for (var k = 0; k < ioTEntity.Properties.length; k++) {
							// var iotProperty = ioTEntity.Properties[k] || {};
							// if (Array.isArray(iotProperty.IoTStateObservation) && iotProperty.IoTStateObservation[0] && iotProperty.IoTStateObservation[0].Value &&
								// iotProperty.DataType && (iotProperty.DataType.toUpperCase().indexOf('GEOJSON') >= 0)) {
								// coordinates = iotProperty.IoTStateObservation[0].Value.split(' ', 3);
								// geoJson.features.push({
									// 'type': 'Feature',
									// 'geometry': {
										// 'type': 'Point',
										// 'coordinates': [1 * (coordinates[0] || 0), 1 * (coordinates[1] || 0)],	//GeoJSON: longitude, latitude
									// },
									// 'properties': {
										// 'name': (ioTEntity.Name || '') + ' | ' + (iotProperty.Name || ''),
										// 'description': (ioTEntity.Description || '') + ' | ' + (iotProperty.Description || ''),
									// },
								// });
							// }
						// }
					// }
				// }
			// }
		// }
		// return geoJson;
	// }

	// function proxyStorageManagerToGeojson(req, res) {
		// var get = almanac.http.request({
				// url: almanac.config.hosts.storageManagerUrl + req.url,
				// method: 'GET',
				// headers: {
					// 'Accept': 'application/json',
				// }
			// }, function(res2) {
				// var body = '';
				// res2.setEncoding('utf8');
				// res2.on('error', function (err) {
					// almanac.basicHttp.serve500(req, res, 'Error getting from StorageManager: ' + err);
				// });
				// res2.on('data', function (chunk) {
					// body += chunk;
				// });
				// res2.on('end', function () {
					// try {
						// var geoJson = dmToGeojson(JSON.parse(body));	//Do the conversion
						// res.writeHead(res2.statusCode, {
								// 'Access-Control-Allow-Origin': '*',
								// 'Access-Control-Allow-Methods': 'GET',
								// 'Content-Type': 'application/json; charset=UTF-8',
								// 'Date': res2.headers.date,
								// 'Server': almanac.basicHttp.serverSignature,
							// });
						// res.end(JSON.stringify(geoJson));	//Send the response back to the client
					// } catch (ex) {
						// almanac.basicHttp.serve500(req, res, 'Error GeoJSON conversion from StorageManager: ' + ex);
					// }
				// });
			// });
		// get.end();
	// }

	function smToAtom(json, res, uri) {	//Conversion to Atom feed format (similar to RSS)
		var xw = new xmlWriter(false, function (string, encoding) {
				res.write(string, encoding);
			}),
			aw = new atomWriter(xw);
		aw.startFeed(uri)
			.writeGenerator('ALMANAC Virtualization Layer @ ' + almanac.config.hosts.instanceName, almanac.version, almanac.config.hosts.virtualizationLayerPublicUrl);

		if (json) {
			if (!json.Observations || !Array.isArray(json.Observations)) {
				json.Observations = [
						json,
					];
			}
			for (var i = 0; i < json.Observations.length; i++) {
				var observation = json.Observations[i];
				aw.startEntry('urn:almanac:vl:atom:sm:' + ((observation.Datastream || {}).ID || '') + ':' + (observation.Time || ''),
						new Date(observation.Time))
					.writeTitle(((observation.Datastream || {}).ID || '') + ' @ ' + (observation.Time || ''))
					.writeContent(observation.ResultValue || '', 'text')
					.endEntry();
			}
		}
		aw.endFeed();
	}

	function smToTxt(json, res, d) {//Conversion to text format (CVS, tab-separated)
		if (json) {
			if (!json.Observations || !Array.isArray(json.Observations)) {
				json.Observations = [
						json,
					];
			}
			//var q = (d === ',' ? '"' : '');	//CSV quotes
			res.write("DatastreamId" + d + "DateTime" + d + "Value\n");
			for (var i = 0; i < json.Observations.length; i++) {
				var observation = json.Observations[i];
				res.write(
					((observation.Datastream || {}).ID || '') + d +
					(observation.Time || '') + d +
					(observation.ResultValue || '') +
					"\n");
			}
		}
	}

	function proxyStorageManagerToFormat(req, res, format) {
		var url = almanac.config.hosts.storageManagerUrl + req.url;
		almanac.request({
				method: req.method,
				url: url,
				timeout: 25000,
			}, function (error, response, body) {
				if (error || response.statusCode != 200 || !body) {
					almanac.log.warn('VL', 'Error ' + (response ? response.statusCode : 'undefined') + ' proxying to StorageManager!');
					if (!body) {
						almanac.basicHttp.serve503(req, res);
					}
				} else {
					try {
						var json = JSON.parse(body),
							responseHeaders = {
								'Access-Control-Allow-Origin': '*',
									'Access-Control-Allow-Methods': 'GET',
									'Date': response.headers.date,
									'Server': almanac.basicHttp.serverSignature,
							};
						switch (format) {
							case 'csv':
								responseHeaders['Content-Disposition'] = 'inline; filename="sm.csv"';
								responseHeaders['Content-Type'] = 'text/csv; charset=UTF-8';
								break;
							case 'txt':
							case 'tsv':
								responseHeaders['Content-Disposition'] = 'inline; filename="sm.txt"';
								responseHeaders['Content-Type'] = 'text/tab-separated-values; charset=UTF-8';
								break;
							case 'rss':
							case 'atom':
								responseHeaders['Content-Disposition'] = 'inline; filename="sm.atom.xml"';
								responseHeaders['Content-Type'] = 'application/atom+xml; charset=UTF-8';
								break;
						}
						res.writeHead(response.statusCode, responseHeaders);
						switch (format) {	//Do the conversion
							case 'csv':
								smToTxt(json, res, ',');
								break;
							case 'txt':
							case 'tsv':
								smToTxt(json, res, "\t");
								break;
							case 'rss':
							case 'atom':
								smToAtom(json, res, url);
								break;
						}
						res.end();
					} catch (ex) {
						almanac.basicHttp.serve500(req, res, 'Error format conversion from StorageManager: ' + ex);
					}
				}
			});
	}

	almanac.routes['sm/'] = proxyStorageManager;	//Proxying to Storage Manager
	almanac.routes['sm-atom/'] = function (req, res) { proxyStorageManagerToFormat(req, res, 'atom'); };	//Conversion of Storage Manager JSON to ATOM (RSS)
	almanac.routes['sm-rss/'] = function (req, res) { proxyStorageManagerToFormat(req, res, 'atom'); };	//Conversion of Storage Manager JSON to ATOM (RSS)
	almanac.routes['sm-txt/'] = function (req, res) { proxyStorageManagerToFormat(req, res, 'tsv'); };	//Conversion of Storage Manager JSON to TXT
	almanac.routes['sm-tsv/'] = function (req, res) { proxyStorageManagerToFormat(req, res, 'tsv'); };	//Conversion of Storage Manager JSON to TXT
	almanac.routes['sm-csv/'] = function (req, res) { proxyStorageManagerToFormat(req, res, 'csv'); };	//Conversion of Storage Manager JSON to TXT
};
