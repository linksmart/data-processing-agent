"use strict";

var socketio = require('./node_modules/socket.io/node_modules/socket.io-client')('http://p2.alapetite.dk:8080/');

socketio.on('connect', function() {
		console.log('Connected');
	});

socketio.on('disconnect', function() {
		console.log('Disconnected');
	});

socketio.on('chat', function(msg) {
		console.log('Chat: ' + msg);
	});

socketio.on('DM', function(msg) {
		try {
			var json = JSON.parse(msg.body);
			console.log('Valid JSON: ' + JSON.stringify(json) + ' ; posted to: ' + msg.url + ' ; with headers: ' + JSON.stringify(msg.headers));
		} catch (e) {
			console.log('Invalid JSON: ' + msg.body + ' ; posted to: ' + msg.url);
		}
	});
