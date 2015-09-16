"use strict";
/*
	Virtualization Layer | WebSocket module
		by Alexandre Alapetite http://alexandre.alapetite.fr
			from Alexandra Institute http://www.alexandra.dk
			for the ALMANAC European project http://www.almanac-project.eu
*/

module.exports = function (almanac) {

	var WebSocketServer = require('ws').Server;
	var util = require('util');

	var nextClientId = 0,
		wsClients = {};

	almanac.webSocket = new WebSocketServer({
			server: almanac.server,
			path: '/ws/custom-events',
		});

	almanac.webSocket.on('error', function (error) {
			almanac.log.warn('VL', 'WebSocket custom events: server error: ' + util.inspect(error));
		});

	almanac.webSocket.on('connection', function (ws) {
			var clientId = nextClientId++;

			ws.on('error', function (error) {
					almanac.log.warn('VL', 'WebSocket custom events: error with client #' + clientId + ': ' + util.inspect(error));
				});

			var remoteAddress = ws.upgradeReq.connection.remoteAddress,	//Sometimes undefined during fast connections
				remotePort = ws.upgradeReq.connection.remotePort;
			if ((!remoteAddress) && ws.upgradeReq.connection._peername) {
				remoteAddress = ws.upgradeReq.connection._peername.address;
			}
			if ((!remotePort) && ws.upgradeReq.connection._peername) {
				remotePort = ws.upgradeReq.connection._peername.port;
			}

			wsClients[clientId] = {
					ws: ws,
					remoteAddress: remoteAddress,
					remotePort: remotePort,
					mqttTopics: {},
				};

			ws.on('message', function (data, flags) {
					almanac.log.verbose('VL', 'WebSocket custom events: message from client #' + clientId + ': ' + data);
					try {
						var payload = JSON.parse(data);
						if (payload && payload.topic && payload.topic.charAt(0) == '/') {
							wsClients[clientId].mqttTopics[payload.topic] = true;
							ws.send(JSON.stringify({
									subscriptions: wsClients[clientId].mqttTopics,
								}));
						}
					} catch (ex) {
						almanac.log.warn('VL', 'WebSocket custom events: invalid message from client #: ' + clientId + ': ' + ex);
					}
				});

			ws.on('close', function (code, data) {
					almanac.log.http('VL', 'WebSocket custom events: close client #' + clientId + ': ' + code + ' / ' + data);
					delete wsClients[clientId];
				});

			almanac.log.http('VL', 'WebSocket custom events: connected client #' + clientId + ' ' + remoteAddress + ':' + remotePort);

		});

	almanac.webSocket.broadcast = function (data) {
			almanac.webSocket.clients.forEach(function (client) {
				client.send(data);
			});
		};

	almanac.webSocket.forwardMqtt = function (topic, data) {
			var clientIds = Object.keys(wsClients);
			for (var i = clientIds.length - 1; i >= 0; i--) {
				var client = wsClients[clientIds[i]] || {};
				var subscriptions = Object.keys(client.mqttTopics);
				for (var j = subscriptions.length - 1; j >= 0; j--) {
					if (subscriptions[j] === topic) {
						client.ws.send(JSON.stringify({
								topic: topic,
								payload: data,
							}));
					}
				}
			}
		};

	/*almanac.webSocket.forwardHttp = function (req, res, room) {
		var body = '';
		req.addListener('data', function (chunk) {
				body += chunk;
			});
		req.addListener('end', function () {
				try {
					var json = JSON.parse(body);
					almanac.webSocket.in(room).emit(room, {	//Forward to WebSocket clients
							instance: almanac.config.hosts.virtualizationLayerPublic,
							headers: req.headers,
							url: req.url,
							body: json,
						});
					almanac.log.verbose('VL', 'Peering POST forwarded to WebSocket on room ' + room);
				} catch (ex) {
					almanac.log.warn('VL', 'Error while forwarding POST to WebSocket: ' + ex);
				}
			});
	};*/

	almanac.log.info('VL', 'WebSocket custom events: started');
};
