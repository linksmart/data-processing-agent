"use strict";
/*
	Virtualization Layer | LinkSmart.NET StorageManager client
		by Alexandre Alapetite http://alexandre.alapetite.fr
			from Alexandra Institute http://www.alexandra.dk
			for the ALMANAC European project http://www.almanac-project.eu
*/

var xmlWriter = require('xml-writer'),
	atomWriter = require('atom-writer');

module.exports = function (almanac) {

	almanac.storageManager = {
		postMqttEvent: function (topic, json) {	//Forward an MQTT event over HTTP POST to the StorageManager
			//json.mqttTopic = topic;
			//json.vlInstance: almanac.config.hosts.virtualizationLayerPublic,
			almanac.request.post({
					url: almanac.config.hosts.masterStorageManager.scheme + '://' + almanac.config.hosts.masterStorageManager.host +
						':' + almanac.config.hosts.masterStorageManager.port + almanac.config.hosts.masterStorageManager.path + 'IoTEntities',
					json: true,
					body: json,
					timeout: 9000,
				}, function (error, response, body) {
					if (error || response.statusCode != 200) {
						console.warn('Error ' + (response ? response.statusCode : 'undefined') + ' forwarding MQTT event to StorageManager!');
					} else {
						console.log('MQTT event forwarded to StorageManager ' + JSON.stringify({query: json, response: response}));
					}
				});
		}
	};

	function proxyDataManagement(req, res) {

		if (req.method === 'POST') {
			almanac.webSocket.forwardHttp(req, res, 'DM');	//Forward POST requests to Socket.IO clients (WebSocket)
		}

		req.pipe(almanac.request.get('http://' + almanac.config.hosts.masterStorageManager.host +
			':' + almanac.config.hosts.masterStorageManager.port + almanac.config.hosts.masterStorageManager.path + req.url,
			function (error, response, body) {
				if (error) {
					almanac.basicHttp.serve500(req, res, 'Error proxying to DataManagement!');
				}
			})).pipe(res);
	}

	function dmToGeojson(json) {	//Conversion to GeoJSON format (JSON convention for geographic data)
		var geoJson = {
				'type': 'FeatureCollection',
				'features': [],
			};
		if (json) {
			if (Array.isArray(json.IoTStateObservation)) {	//Encapsulate IoTStateObservation into an IoTProperty. Ex: /dm-geojson/IoTEntities/E3CFE564-EFD8-4839-BB69-89F1939DADE0/properties/E3CFE564-EFD8-4839-BB69-89F1939DADE0:location/observations
				json.IoTProperty = [{
						DataType: 'xs:geojson', 	//Assuming correct type if the user asks to transform output to GeoJSON
						IoTStateObservation: [ json.IoTStateObservation[0] ],	//Take only the first IoTStateObservation
					}];
			}
			if (Array.isArray(json.IoTProperty)) {	//Encapsulate a single IoTProperty into an IoTEntity. Ex: /dm-geojson/IoTEntities/E3CFE564-EFD8-4839-BB69-89F1939DADE0/properties
				json.IoTEntity = [{
						Properties: json.IoTProperty,
					}];
			}
			if (Array.isArray(json.IoTEntity)) {	//Ex: /dm-geojson/IoTEntities
				var ioTEntities = json.IoTEntity,
					coordinates = [];
				for (var i = 0; i < ioTEntities.length; i++) {
					var ioTEntity = ioTEntities[i];
					if (Array.isArray(ioTEntity.Meta) && ioTEntity.Meta[0] && ioTEntity.Meta[0].Value && ioTEntity.Meta[0].property === 'geo:point') {
						coordinates = ioTEntity.Meta[0].Value.split(' ', 3);
						geoJson.features.push({
							'type': 'Feature',
							'geometry': {
								'type': 'Point',
								'coordinates': [1 * (coordinates[1] || 0), 1 * (coordinates[0] || 0)],	//Reversed order for GeoJSON: longitude, latitude
							},
							'properties': {
								'name': ioTEntity.Name,
								'description': ioTEntity.Description,
							},
						});
					}
					if (Array.isArray(ioTEntity.Properties)) {
						for (var j = 0; j < ioTEntity.Properties.length; j++) {
							var iotProperty = ioTEntity.Properties[j] || {};
							if (Array.isArray(iotProperty.IoTStateObservation) && iotProperty.IoTStateObservation[0] && iotProperty.IoTStateObservation[0].Value &&
								iotProperty.DataType && (iotProperty.DataType.toUpperCase().indexOf('GEOJSON') >= 0)) {
								coordinates = iotProperty.IoTStateObservation[0].Value.split(' ', 3);
								geoJson.features.push({
									'type': 'Feature',
									'geometry': {
										'type': 'Point',
										'coordinates': [1 * (coordinates[0] || 0), 1 * (coordinates[1] || 0)],	//GeoJSON: longitude, latitude
									},
									'properties': {
										'name': (ioTEntity.Name || '') + ' | ' + (iotProperty.Name || ''),
										'description': (ioTEntity.Description || '') + ' | ' + (iotProperty.Description || ''),
									},
								});
							}
						}
					}
				}
			}
		}
		return geoJson;
	}

	function proxyDataManagementToGeojson(req, res) {
		var get = almanac.http.request({
				host: almanac.config.hosts.masterStorageManager.host,
				port: almanac.config.hosts.masterStorageManager.port,
				path: almanac.config.hosts.masterStorageManager.path + req.url,
				method: 'GET',
				headers: {
					'Accept': 'application/json',
					'Host': almanac.config.hosts.masterStorageManager.host + ':' + almanac.config.hosts.masterStorageManager.port,
				}
			}, function(res2) {
				var body = '';
				res2.setEncoding('utf8');
				res2.on('error', function (err) {
					almanac.basicHttp.serve500(req, res, 'Error getting from DataManagement: ' + err);
				});
				res2.on('data', function (chunk) {
					body += chunk;
				});
				res2.on('end', function () {
					try {
						var geoJson = dmToGeojson(JSON.parse(body));	//Do the conversion
						res.writeHead(res2.statusCode, {
								'Access-Control-Allow-Origin': '*',
								'Access-Control-Allow-Methods': 'GET',
								'Content-Type': 'application/json; charset=UTF-8',
								'Date': res2.headers.date,
								'Server': almanac.basicHttp.serverSignature,
							});
						res.end(JSON.stringify(geoJson));	//Send the response back to the client
					} catch (ex) {
						almanac.basicHttp.serve500(req, res, 'Error GeoJSON conversion from DataManagement: ' + ex);
					}
				});
			});
		get.end();
	}

	function dmToAtom(json) {	//Conversion to Atom feed format (similar to RSS)
		var xw = new xmlWriter(),
			aw = new atomWriter(xw);
		aw.startFeed('urn:almanac:vl:atom:dm:' + almanac.config.hosts.masterStorageManager.host);

		if (json) {
			if (Array.isArray(json.IoTStateObservation)) {	//Encapsulate IoTStateObservation into an IoTProperty. Ex: /dm-atom/IoTEntities/E3CFE564-EFD8-4839-BB69-89F1939DADE0/properties/E3CFE564-EFD8-4839-BB69-89F1939DADE0:location/observations
				json.IoTProperty = [{
						IoTStateObservation: json.IoTStateObservation,
					}];
			}
			if (Array.isArray(json.IoTProperty)) {	//Encapsulate a single IoTProperty into an IoTEntity. Ex: /dm-atom/IoTEntities/E3CFE564-EFD8-4839-BB69-89F1939DADE0/properties
				json.IoTEntity = [{
						Properties: json.IoTProperty,
					}];
			}
			if (Array.isArray(json.IoTEntity)) {	//Ex: /dm-atom/IoTEntities
				var ioTEntities = json.IoTEntity;
				for (var i = 0; i < ioTEntities.length; i++) {
					var ioTEntity = ioTEntities[i];
					if (Array.isArray(ioTEntity.Properties)) {
						for (var j = 0; j < ioTEntity.Properties.length; j++) {
							var iotProperty = ioTEntity.Properties[j] || {};
							if (Array.isArray(iotProperty.IoTStateObservation) && iotProperty.IoTStateObservation[0] && iotProperty.IoTStateObservation[0].Value) {
								//TODO: Alex works here
							}
						}
					}
				}
			}
		}
		aw.endFeed();
		return xw.toString();
	}

	function proxyDataManagementToAtom(req, res) {
		var get = almanac.http.request({
				host: almanac.config.hosts.masterStorageManager.host,
				port: almanac.config.hosts.masterStorageManager.port,
				path: almanac.config.hosts.masterStorageManager.path + req.url,
				method: 'GET',
				headers: {
					'Accept': 'application/json',
					'Host': almanac.config.hosts.masterStorageManager.host + ':' + almanac.config.hosts.masterStorageManager.port,
				}
			}, function(res2) {
				var body = '';
				res2.setEncoding('utf8');
				res2.on('error', function (err) {
					almanac.basicHttp.serve500(req, res, 'Error getting from DataManagement: ' + err);
				});
				res2.on('data', function (chunk) {
					body += chunk;
				});
				res2.on('end', function () {
					try {
						var atom = dmToAtom(JSON.parse(body));	//Do the conversion
						res.writeHead(res2.statusCode, {
								'Access-Control-Allow-Origin': '*',
								'Access-Control-Allow-Methods': 'GET',
								'Content-Type': 'application/atom+xml; charset=UTF-8',
								'Date': res2.headers.date,
								'Server': almanac.basicHttp.serverSignature,
							});
						res.end(atom);
					} catch (ex) {
						almanac.basicHttp.serve500(req, res, 'Error Atom conversion from DataManagement: ' + ex);
					}
				});
			});
		get.end();
	}

	function dmToTxt(json, res, d) {//Conversion to text format (CVS, tab-separated)
		if (json) {
			if (Array.isArray(json.IoTStateObservation)) {	//Encapsulate IoTStateObservation into an IoTProperty. Ex: /dm-txt/IoTEntities/E3CFE564-EFD8-4839-BB69-89F1939DADE0/properties/E3CFE564-EFD8-4839-BB69-89F1939DADE0:location/observations
				json.IoTProperty = [{
						IoTStateObservation: json.IoTStateObservation,
					}];
			}
			if (Array.isArray(json.IoTProperty)) {	//Encapsulate a single IoTProperty into an IoTEntity. Ex: /dm-txt/IoTEntities/E3CFE564-EFD8-4839-BB69-89F1939DADE0/properties
				json.IoTEntity = [{
						Properties: json.IoTProperty,
					}];
			}
			res.write('EntityName' + d + 'EntityAbout' + d + 'EntityType' + d + 'PropertyName' + d + 'PropertyAbout' + d + 'PropertyType' + d + 'Value' + d + 'PhenomenonTime' + d + 'ResultTime\n');
			if (Array.isArray(json.IoTEntity)) {	//Ex: /dm-txt/IoTEntities
				var q = (d === ',' ? '"' : ''),	//CSV quotes
					ioTEntities = json.IoTEntity;
				for (var i = 0; i < ioTEntities.length; i++) {
					var ioTEntity = ioTEntities[i];
					if (Array.isArray(ioTEntity.Properties)) {
						for (var j = 0; j < ioTEntity.Properties.length; j++) {
							var iotProperty = ioTEntity.Properties[j] || {};
							if (Array.isArray(iotProperty.IoTStateObservation)) {
								for (var k = 0; k < iotProperty.IoTStateObservation.length; k++) {
									var ioTStateObservation = iotProperty.IoTStateObservation[k];
									res.write(q + (ioTEntity.Name || '') + q + d + q + (ioTEntity.About || '') + q + d + q + (ioTEntity.TypeOf || '') + q + d + q +
										(iotProperty.Name || '') + q + d + q + (iotProperty.About || '') + q + d + q + (iotProperty.DataType || '') + q + d + q +
										ioTStateObservation.Value + q + d + q + (ioTStateObservation.PhenomenonTime || '') + q + d + q + (ioTStateObservation.ResultTime || '') + q + "\n");
								}
							}
						}
					}
				}
			}
		}
	}

	function proxyDataManagementToText(req, res, format) {
		var get = almanac.http.request({
				host: almanac.config.hosts.masterStorageManager.host,
				port: almanac.config.hosts.masterStorageManager.port,
				path: almanac.config.hosts.masterStorageManager.path + req.url,
				method: 'GET',
				headers: {
					'Accept': 'application/json',
					'Host': almanac.config.hosts.masterStorageManager.host + ':' + almanac.config.hosts.masterStorageManager.port,
				}
			}, function(res2) {
				var body = '';
				res2.setEncoding('utf8');
				res2.on('error', function (err) {
					almanac.basicHttp.serve500(req, res, 'Error getting from DataManagement: ' + err);
				});
				res2.on('data', function (chunk) {
					body += chunk;
				});
				res2.on('end', function () {
					try {
						res.writeHead(res2.statusCode, {
								'Access-Control-Allow-Origin': '*',
								'Access-Control-Allow-Methods': 'GET',
								'Content-Disposition': 'inline; filename="dm.' + (format === 'csv' ? 'csv' : 'txt') + '"',
								'Content-Type': 'text/' + (format === 'csv' ? 'csv' : 'tab-separated-values') + '; charset=UTF-8',
								'Date': res2.headers.date,
								'Server': almanac.basicHttp.serverSignature,
							});
						dmToTxt(JSON.parse(body), res, (format === 'csv' ? ',' : "\t"));	//Do the conversion
						res.end();
					} catch (ex) {
						almanac.basicHttp.serve500(req, res, 'Error Text conversion from DataManagement: ' + ex);
					}
				});
			});
		get.end();
	}

	almanac.routes['dm/'] = proxyDataManagement;	//Proxying to Data Management
	almanac.routes['dm-geojson/'] = proxyDataManagementToGeojson;	//Conversion of Data Management JSON to GeoJSON
	almanac.routes['dm-atom/'] = proxyDataManagementToAtom;	//Conversion of Data Management JSON to ATOM (RSS)
	almanac.routes['dm-rss/'] = proxyDataManagementToAtom;	//Conversion of Data Management JSON to ATOM (RSS)
	almanac.routes['dm-txt/'] = function (req, res) { proxyDataManagementToText(req, res, 'tsv'); };	//Conversion of Data Management JSON to TXT
	almanac.routes['dm-tsv/'] = function (req, res) { proxyDataManagementToText(req, res, 'tsv'); };	//Conversion of Data Management JSON to TXT
	almanac.routes['dm-csv/'] = function (req, res) { proxyDataManagementToText(req, res, 'csv'); };	//Conversion of Data Management JSON to TXT
};
