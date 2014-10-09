"use strict";

var config = require('../config.js').config,
	socketio = require('../node_modules/socket.io/node_modules/socket.io-client')(config.hosts.virtualizationLayerPublic.scheme + '://' + config.hosts.virtualizationLayerPublic.host + ':' + config.hosts.virtualizationLayerPublic.port + '/');

socketio.on('connect', function() {
		console.log('Connected');
	});

socketio.on('disconnect', function() {
		console.log('Disconnected');
	});

socketio.on('info', function(msg) {
		console.log('Info: ' + msg);
	});

socketio.on('DM', function(json) {
		console.log('DM: ' + JSON.stringify(json));
	});

socketio.on('mqtt', function(json) {
		console.log('MQTT: ' + JSON.stringify(json));
	});
