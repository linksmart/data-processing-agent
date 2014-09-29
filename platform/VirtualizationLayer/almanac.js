"use strict";
/*
	Virtualization Layer | ALMANAC-specific logic
		by Alexandre Alapetite http://alexandre.alapetite.fr
			from Alexandra Institute http://www.alexandra.dk
			for the ALMANAC European project http://www.almanac-project.eu
*/

var basic = require('./basic.js').basic,	//Static files, logs
	config = require('./config.js').config,
	http = require('http'),
	httpProxy = require('http-proxy').createProxyServer({}),
	xmlWriter = require('xml-writer'),
	atomWriter = require('atom-writer'),
	request = require('request');

httpProxy.on('error', function (err, req, res) {
		basic.serve500(req, res, 'Error proxying: ' + err);
	});


var almanac = {

	config: config,

	//<Socket.IO>
	_io: null,	//socket.io (Websocket)
	_ioClients: {},
	_ioSockets: {},
	ioInit: function (server) {
		server.on('connection', function (socket) {
				var remoteAddress = socket.remoteAddress;	//To populate ._peername https://github.com/joyent/node/blob/03e9f84933fe610b04b107cf1f83d17485e8906e/lib/net.js#L563
			});
		almanac._io = require('socket.io')(server);
		almanac._io.on('connection', function (socket) {
				var remoteAddress = socket.request.connection.remoteAddress,	//Sometimes undefined during fast connections
					remotePort = socket.request.connection.remotePort;
				if ((!remoteAddress) && socket.request.connection._peername) {
					remoteAddress = socket.request.connection._peername.address;
				}
				if ((!remotePort) && socket.request.connection._peername) {
					remotePort = socket.request.connection._peername.port;
				}
				almanac._ioSockets[socket.id] = remoteAddress + ':' + remotePort;
				almanac._ioClients[remoteAddress + ':' + remotePort] = socket.id;
				almanac._io.emit('chat', 'Connected ' + almanac._ioSockets[socket.id]);
				socket.emit('chat', 'Welcome ' + almanac._ioSockets[socket.id]);
				console.log('Connected ' + almanac._ioSockets[socket.id]);

				socket.on('chat', function (msg) {
						msg = almanac._ioSockets[socket.id] + '> ' + msg;
						almanac._io.emit('chat', msg);
						console.log('Chat ' + msg);
					});

				socket.on('disconnect', function () {
						var clientId = almanac._ioSockets[socket.id];
						almanac._io.emit('chat', 'Disconnected ' + clientId);
						console.log('Disconnected ' + clientId);
						try {
							var socketId = almanac._ioClients[clientId];
							delete almanac._ioSockets[socket.id];
							delete almanac._ioClients[clientId];
							delete almanac._ioSockets[socketId];
						} catch (ex) {
							console.warn('Warning during Socket.IO disconnection: ' + ex);
						}
					});
			});
	},
	//</Socket.IO>

	//<MQTT>
	_mqttClient: null,
	mqttInit: function () {
		var mqtt = require('mqtt');
		almanac._mqttClient = mqtt.createClient(1883, 'localhost');

		almanac._mqttClient.on('message', function (topic, message) {
			almanac._io.emit('chat', 'MQTT: ' + message);
			console.log('MQTT: ' + message);
		});

		almanac._mqttClient.subscribe('chat');
		setTimeout(function () {
				almanac._mqttClient.publish('chat', 'Hello MQTT');
			}, 5000);
	},
	//</MQTT>

	//<SSDP (UPnP)>
	_ssdpClient: null,
	ssdpInit: function () {
		var SsdpClient = require('node-ssdp').Client;
		almanac._ssdpClient = new SsdpClient();

		almanac._ssdpClient.on('response', function (headers, statusCode, rinfo) {
			//console.log('SSDP: ' + JSON.stringify(headers) + ' ; ' + JSON.stringify(statusCode) + ' ; ' + JSON.stringify(rinfo));
			if (headers &&
				headers.ST === config.hosts.RecourceCatalogueUrn &&
				headers.LOCATION && (headers.LOCATION.indexOf('http://127.0.0.1') === 0) &&
				headers.LOCATION !== almanac._recourceCatalogueUrl) {
				almanac._recourceCatalogueUrl = headers.LOCATION;
				console.log('UPnP: discovered the resource catalogue on ' + almanac._recourceCatalogueUrl);
				almanac._io.emit('chat', 'UPnP: discovered the resource catalogue');
			};
		});

		function discoverResourceCatalogues() {
			//console.log('SSDP: discovery');
			almanac._ssdpClient.search(config.hosts.RecourceCatalogueUrn);
		}

		discoverResourceCatalogues();
		setInterval(discoverResourceCatalogues, 30000);
	},
	//</SSDP (UPnP)>

	//<LinkSmart NetworkManager>
	linksmartInit: function () {
		function registerInNetworkManager() {
			request.post({
					url: config.hosts.NetworkManagerUrl,
					json: true,
					body: JSON.stringify({
							'Endpoint': config.hosts.VirtualizationLayerLocalUrl,
							'BackboneName': 'eu.linksmart.network.backbone.impl.soap.BackboneSOAPImpl',
							'Attributes': {
								'description': 'VirtualizationLayer',
								'sid': 'eu.linksmart.almanac.virtualizationlayer'
							}
						}),
					timeout: 4000
				}, function (error, response, body) {
					if (!error && response.statusCode == 200 && body && body.VirtualAddress) {
						console.error('Registered in the NetworkManager with VirtualAddress: ' + body.VirtualAddress);
					} else {
						console.error('Cannot register in the NetworkManager! Will try again.');
						console.error(JSON.stringify({error: error, response: response, body: body}));
					}
				});
		}

		function refreshInNetworkManager() {
			request.get({
						url: config.hosts.NetworkManagerUrl + '?description="VirtualizationLayer"',
						json: true,
						timeout: 2000
					}, function (error, response, body) {
					if (!error && response.statusCode == 200 && body) {
						if (body.length == 0) {	//Needs registration
							registerInNetworkManager();
						} else {
							console.error('Already registered in NetworkManager at address: ' + body[0].VirtualAddress);
						}
					} else {
						console.error('Cannot contact the NetworkManager! Will try again.');
					}
				});
		}

		refreshInNetworkManager();
		setInterval(refreshInNetworkManager, 120000);
	},
	//</LinkSmart NetworkManager>

	serveHome: function (req, res) {
		var now = new Date();
		res.writeHead(200, {
				'Content-Type': 'text/html; charset=UTF-8',
				'Date': now.toUTCString(),
				'Server': basic.serverSignature
			});
		res.end('<!DOCTYPE html>\n\
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-GB" lang="en-GB">\n\
<head>\n\
<meta charset="UTF-8" />\n\
<title>Virtualization Layer | ALMANAC</title>\n\
<meta name="robots" content="noindex,nofollow" />\n\
</head>\n\
<body>\n\
<h1>ALMANAC Virtualization Layer</h1>\n\
<pre>\n\
Hello ' + req.connection.remoteAddress + '!\n\
This is ' + req.connection.localAddress + ' running <a href="http://nodejs.org/" rel="external">Node.js</a>.\n\
I am a Virtualization Layer for the <a href="http://www.almanac-project.eu/" rel="external">ALMANAC European project (Reliable Smart Secure Internet Of Things For Smart Cities)</a>.\n\
I talk mainly to other machines, but there is a <a href="socket.html">WebSocket demo</a> for humans.\n\
It is now ' + now.toISOString() + '.\n\
</pre>\n\
</body>\n\
</html>\n\
');
	},

	_recourceCatalogueUrl: '',
	proxyResourceCatalogue: function (req, res) {
		if (!almanac._recourceCatalogueUrl) {
			basic.serve503(req, res);
			return;
		}
		var proxy = http.request(almanac._recourceCatalogueUrl + req.url, function (res2) {
			res2.pipe(res, { end: true });
		});

		req.pipe(proxy, { end: true });
	},

	proxyDataManagement: function (req, res) {
		var options = {
			headers: {
				'Connection': 'close',
				'X-Almanac-VL': req.connection.localAddress,
			},
			target: JSON.parse(JSON.stringify(config.hosts.masterStorageManager)),
			xfwd: true,
		};
		options.target.path += req.url;

		//Forward POST requests to Socket.IO clients (WebSocket)
		if (req.method === 'POST' && almanac._io) {
			var body = '';
			req.addListener('data', function (chunk) {
					body += chunk;
				});
			req.addListener('end', function () {
					console.log('POST forward to ' + Object.keys(almanac._ioClients).length + ' clients via Socket.IO');
					almanac._io.emit('DM', {
							body: body,
							headers: req.headers,
							url: req.url,
						});
				});
		}

		//Forward POST requests to another Virtualization Layer instance (for IoT-week);
		if (req.method === 'POST' && config.hosts.slaveVirtualizationLayer && (req.headers['x-almanac-vl'] == undefined)) {	//Do not forward more than once to avoid infinite loops
			console.log('Forward to ' + config.hosts.slaveVirtualizationLayer.host + ':' + config.hosts.slaveVirtualizationLayer.port);
			options.forward = JSON.parse(JSON.stringify(config.hosts.slaveVirtualizationLayer));
		} else {
			options.forward = null;
		}

		httpProxy.web(req, res, options, function (err) {
				basic.serve500(req, res, 'Error proxying to DataManagement: ' + err);
			});
	},

	_dmToGeojson : function (json) {	//Conversion to GeoJSON format (JSON convention for geographic data)
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
				var ioTEntities = json.IoTEntity;
				for (var i = 0; i < ioTEntities.length; i++) {
					var ioTEntity = ioTEntities[i];
					if (Array.isArray(ioTEntity.Meta) && ioTEntity.Meta[0] && ioTEntity.Meta[0].Value && ioTEntity.Meta[0].property === 'geo:point') {
						var coordinates = ioTEntity.Meta[0].Value.split(' ', 3);
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
								var coordinates = iotProperty.IoTStateObservation[0].Value.split(' ', 3);
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
	},

	proxyDataManagementToGeojson: function (req, res) {
		var get = http.request({
				host: config.hosts.masterStorageManager.host,
				port: config.hosts.masterStorageManager.port,
				path: config.hosts.masterStorageManager.path + req.url,
				method: 'GET',
				headers: {
					'Accept': 'application/json',
					'Host': config.hosts.masterStorageManager.headers.host,
					'Connection': 'close',
				}
			}, function(res2) {
				var body = '';
				res2.setEncoding('utf8');
				res2.on('error', function (err) {
					basic.serve500(req, res, 'Error getting from DataManagement: ' + err);
				});
				res2.on('data', function (chunk) {
					body += chunk;
				});
				res2.on('end', function () {
					try {
						var geoJson = almanac._dmToGeojson(JSON.parse(body));	//Do the conversion
						res.writeHead(res2.statusCode, {
								'Access-Control-Allow-Origin': '*',
								'Access-Control-Allow-Methods': 'GET',
								'Content-Type': 'application/json; charset=UTF-8',
								'Date': res2.headers.date,
								'Server': basic.serverSignature,
							});
						res.end(JSON.stringify(geoJson));	//Send the response back to the client
					} catch (ex) {
						basic.serve500(req, res, 'Error GeoJSON conversion from DataManagement: ' + ex);
					}
				});
			});
		get.end();
	},

	_dmToAtom : function (json) {	//Conversion to Atom feed format (similar to RSS)
		var xw = new xmlWriter(),
			aw = new atomWriter(xw);
		aw.startFeed('urn:almanac:vl:atom:dm:' + config.hosts.masterStorageManager.host);

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
	},

	proxyDataManagementToAtom: function (req, res) {
		var get = http.request({
				host: config.hosts.masterStorageManager.host,
				port: config.hosts.masterStorageManager.port,
				path: config.hosts.masterStorageManager.path + req.url,
				method: 'GET',
				headers: {
					'Accept': 'application/json',
					'Host': config.hosts.masterStorageManager.headers.host,
					'Connection': 'close',
				}
			}, function(res2) {
				var body = '';
				res2.setEncoding('utf8');
				res2.on('error', function (err) {
					basic.serve500(req, res, 'Error getting from DataManagement: ' + err);
				});
				res2.on('data', function (chunk) {
					body += chunk;
				});
				res2.on('end', function () {
					try {
						var atom = almanac._dmToAtom(JSON.parse(body));	//Do the conversion
						res.writeHead(res2.statusCode, {
								'Access-Control-Allow-Origin': '*',
								'Access-Control-Allow-Methods': 'GET',
								'Content-Type': 'application/atom+xml; charset=UTF-8',
								'Date': res2.headers.date,
								'Server': basic.serverSignature,
							});
						res.end(atom);
					} catch (ex) {
						basic.serve500(req, res, 'Error Atom conversion from DataManagement: ' + ex);
					}
				});
			});
		get.end();
	},

	_dmToTxt : function (json, res, d) {//Conversion to text format (CVS, tab-separated)
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
	},

	proxyDataManagementToText: function (req, res, format) {
		var get = http.request({
				host: config.hosts.masterStorageManager.host,
				port: config.hosts.masterStorageManager.port,
				path: config.hosts.masterStorageManager.path + req.url,
				method: 'GET',
				headers: {
					'Accept': 'application/json',
					'Host': config.hosts.masterStorageManager.headers.host,
					'Connection': 'close',
				}
			}, function(res2) {
				var body = '';
				res2.setEncoding('utf8');
				res2.on('error', function (err) {
					basic.serve500(req, res, 'Error getting from DataManagement: ' + err);
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
								'Server': basic.serverSignature,
							});
						almanac._dmToTxt(JSON.parse(body), res, (format === 'csv' ? ',' : "\t"));	//Do the conversion
						res.end();
					} catch (ex) {
						basic.serve500(req, res, 'Error Text conversion from DataManagement: ' + ex);
					}
				});
			});
		get.end();
	},

	proxyScral: function (req, res) {
		req.url = config.hosts.scralPublic.path + req.url;
		httpProxy.web(req, res, {
				headers: {
					'Connection': 'close',
					host: config.hosts.scralPublic.headers.host,
				},
				forward: null,
				target: {
					host: config.hosts.scralPublic.host,
					port: config.hosts.scralPublic.port,
				},
				xfwd: true,
			}, function (err) {
				basic.serve500(req, res, 'Error proxying to SCRAL: ' + err);
			});
	},

	proxySmartSantander: function (req, res) {
		req.url = config.hosts.santanderPublic.path + req.url;
		httpProxy.web(req, res, {
				headers: {
					'Connection': 'close',
					host: config.hosts.santanderPublic.headers.host,
				},
				forward: null,
				target: {
					host: config.hosts.santanderPublic.host,
					port: config.hosts.santanderPublic.port,
				},
				xfwd: true,	//Include X-Forwarded-For header
			}, function (err) {
				basic.serve500(req, res, 'Error proxying to SmartSantander: ' + err);
			});
	},

};

exports.almanac = almanac;
