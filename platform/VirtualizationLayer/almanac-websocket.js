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

	almanac.webSocket = require('socket.io')(almanac.server, {
			path: '/socket.io',
			//transports: ['websocket'],	//Possibility to force the use of WebSocket solely (needs to be done on the client too)
		});

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
			almanac.log.info('VL', 'Socket.IO: connected ' + ioSockets[socket.id]);
			almanac.webSocket.in('info').emit('info', 'Connected ' + ioSockets[socket.id]);

			var rooms = ['alert', 'DM', 'info', 'peering', 'scral'];
			socket.on('subscribe', function(room) {
					if (rooms.indexOf(room) >= 0) {
						almanac.log.verbose('VL', 'Socket.IO: ' + ioSockets[socket.id] + ' joins room ' + room);
						socket.join(room);
						almanac.webSocket.in('info').emit('info', ioSockets[socket.id] + ' joins room ' + room);
						if (room === 'info') {
							socket.emit('info', 'Welcome ' + ioSockets[socket.id]);
						}
					} else {
						almanac.log.info('VL', 'Socket.IO: ' + ioSockets[socket.id] + ' tries to join invalid room!');
					}
				});
			socket.on('unsubscribe', function(room) {
					if (rooms.indexOf('room') >= 0) {
						almanac.log.verbose('VL', 'Socket.IO: ' + ioSockets[socket.id] + ' leaves room ' + room);
						socket.leave(room);
					} else {
						almanac.log.info('VL', 'Socket.IO: ' + ioSockets[socket.id] + ' tries to leave invalid room!');
					}
				});

			socket.on('info', function (msg) {
					msg = ioSockets[socket.id] + '> ' + msg;
					almanac.webSocket.in('info').emit('info', msg);
					almanac.log.verbose('VL', 'Socket.IO: info: ' + msg);
				});

			socket.on('disconnect', function () {
					var clientId = ioSockets[socket.id];
					almanac.webSocket.in('info').emit('info', 'Disconnected ' + clientId);
					almanac.log.info('VL', 'Socket.IO: disconnected ' + clientId);
					try {
						var socketId = ioClients[clientId];
						delete ioSockets[socket.id];
						delete ioClients[clientId];
						delete ioSockets[socketId];
					} catch (ex) {
						almanac.log.warn('VL', 'Socket.IO: warning during disconnection: ' + ex);
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
					almanac.webSocket.in(room).emit(room, {	//Forward to Socket.IO clients (WebSocket)
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
	};

	almanac.log.info('VL', 'Socket.IO: started');
};
