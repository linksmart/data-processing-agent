"use strict";
/*
	Virtualization Layer | WebSocket module
		by Alexandre Alapetite http://alexandre.alapetite.fr
			from Alexandra Institute http://www.alexandra.dk
			for the ALMANAC European project http://www.almanac-project.eu
*/

module.exports = function (almanac) {

	almanac.server.on('connection', function (socket) {
			var remoteAddress = socket.remoteAddress;	//To populate ._peername https://github.com/joyent/node/blob/03e9f84933fe610b04b107cf1f83d17485e8906e/lib/net.js#L563
		});

	almanac.webSocket = require('socket.io')(almanac.server);

	var ioClients = {},
		ioSockets = {};

	almanac.webSocket.on('connection', function (socket) {
			var remoteAddress = socket.request.connection.remoteAddress,	//Sometimes undefined during fast connections
				remotePort = socket.request.connection.remotePort;
			if ((!remoteAddress) && socket.request.connection._peername) {
				remoteAddress = socket.request.connection._peername.address;
			}
			if ((!remotePort) && socket.request.connection._peername) {
				remotePort = socket.request.connection._peername.port;
			}
			ioSockets[socket.id] = remoteAddress + ':' + remotePort;
			ioClients[remoteAddress + ':' + remotePort] = socket.id;
			almanac.webSocket.emit('chat', 'Connected ' + ioSockets[socket.id]);
			socket.emit('chat', 'Welcome ' + ioSockets[socket.id]);
			console.log('Socket.IO: connected ' + ioSockets[socket.id]);

			socket.on('chat', function (msg) {
					msg = ioSockets[socket.id] + '> ' + msg;
					almanac.webSocket.emit('chat', msg);
					console.log('Socket.IO: chat: ' + msg);
				});

			socket.on('disconnect', function () {
					var clientId = ioSockets[socket.id];
					almanac.webSocket.emit('chat', 'Disconnected ' + clientId);
					console.log('Socket.IO: disconnected ' + clientId);
					try {
						var socketId = ioClients[clientId];
						delete ioSockets[socket.id];
						delete ioClients[clientId];
						delete ioSockets[socketId];
					} catch (ex) {
						console.warn('Socket.IO: warning during disconnection: ' + ex);
					}
				});
		});

	almanac.webSocket.forwardHttp = function (req, res, room) {
		var body = '';
		req.addListener('data', function (chunk) {
				body += chunk;
			});
		req.addListener('end', function () {
				try {
					var json = JSON.parse(body);
					almanac.webSocket.emit(room, {	//Forward to Socket.IO clients (WebSocket)
							instance: almanac.config.hosts.virtualizationLayerPublic,
							headers: req.headers,
							url: req.url,
							body: json,
						});
					console.log('Peering POST forwarded to WebSocket on room ' + room);
				} catch (ex) {
					console.warn('Error while forwarding POST to WebSocket: ' + ex);
				}
			});
	};

	console.log('Socket.IO: started');
};
