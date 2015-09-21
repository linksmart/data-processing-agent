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

	almanac.webSocketChat = new WebSocketServer({
			server: almanac.server,
			path: '/ws/chat',
		});

	almanac.webSocketChat.on('error', function (error) {
			almanac.log.warn('VL', 'WebSocket chat: server error: ' + util.inspect(error));
		});

	almanac.webSocketChat.on('connection', function (ws) {
			var clientId = nextClientId++;

			ws.on('error', function (error) {
					almanac.log.warn('VL', 'WebSocket chat: error with client #' + clientId + ': ' + util.inspect(error));
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
				};

			ws.on('message', function (data, flags) {
					almanac.log.verbose('VL', 'WebSocket chat: message from client #' + clientId + ': ' + data);
					try {
						var client = wsClients[clientId] || {};
						var payload = JSON.parse(data);
						if (payload && almanac.mqttClient) {
							almanac.mqttClient.broadcastChat(payload, {
									remoteAddress: client.remoteAddress,
									remotePort: client.remotePort,
								});
						}
					} catch (ex) {
						almanac.log.warn('VL', 'WebSocket chat: invalid message from client #: ' + clientId + ': ' + ex);
					}
				});

			ws.on('close', function (code, data) {
					almanac.log.http('VL', 'WebSocket chat: close client #' + clientId + ': ' + code + ' / ' + data);
					delete wsClients[clientId];
				});

			almanac.log.http('VL', 'WebSocket chat: connected client #' + clientId + ' ' + remoteAddress + ':' + remotePort);

		});

	almanac.webSocketChat.broadcast = function (data) {
			try {
				almanac.webSocketChat.clients.forEach(function (client) {
					client.send(JSON.stringify(data));
				});
			} catch (ex) {
				almanac.log.warn('VL', 'WebSocket chat: error broadcasting to clients. ' + ex);
			}
		};

	almanac.log.info('VL', 'WebSocket chat: started');
};
