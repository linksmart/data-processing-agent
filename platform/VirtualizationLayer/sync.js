"use strict";
/*
	Synchronisation Storage Manager for IoT-week: Public <-> Local
		by Alexandre Alapetite http://alexandre.alapetite.fr
			from Alexandra Institute http://www.alexandra.dk
			for the ALMANAC European project http://www.almanac-project.eu
*/

var config = require('./config.js').config,
	http = require('http');

config.hosts.storageCloud.path += '/IoTEntities';
config.hosts.storageLocal.path += '/IoTEntities';

var rules = [	//Collection of synchronisation rules
	{
		'from': config.hosts.storageCloud,
		'to': config.hosts.storageLocal,
	}, {
		'from': config.hosts.storageLocal,
		'to': config.hosts.storageCloud,
	},
];

console.log('Start synchronising Storage Managers...\n');

function nextPost(queue, rule) {
	var m = Math.random() < 0.5 ? queue.shift() : queue.pop();
	if (m) {
		console.log('\n--------------------------------\n');
		console.log('POST ' + m.About + ' (' + m.Name + ') to http://' + rule.to.host + ':' + rule.to.port + rule.to.path + "\n");
		var post = http.request({
				host: rule.to.host,
				port: rule.to.port,
				path: rule.to.path,
				method: 'POST',
				headers: {
					'Accept': 'application/json',
					'Content-Type': 'application/json',
				}
			}, function(res2) {
				var body2 = '';
				res2.setEncoding('utf8');
				res2.on('data', function (chunk) {
					body2 += chunk;
				});
				res2.on('end', function () {
					console.log(body2);
					nextPost(queue, rule);
				});
			});
		post.write(JSON.stringify(m));
		post.end();
	} else {
		nextRule();
	}
}

function nextRule() {
	var rule = Math.random() < 0.5 ? rules.shift() : rules.pop();
	if (rule) {
		console.log('\n----------------------------------------------------------------\n');
		console.log('GET http://' + rule.from.host + ':' + rule.from.port + rule.from.path);
		var get = http.request({
				host: rule.from.host,
				port: rule.from.port,
				path: rule.from.path,
				method: 'GET',
				headers: {
					'Accept': 'application/json',
				}
			}, function(res) {
				var body = '';
				res.setEncoding('utf8');
				res.on('data', function (chunk) {
					body += chunk;
				});
				res.on('end', function () {
					var json = JSON.parse(body);
					console.log(json);
					console.log('\n');
					nextPost(json.IoTEntity, rule);
				});
			});
		get.end();
	} else {
		console.log('\nDone.\n');
	}
}

nextRule();
