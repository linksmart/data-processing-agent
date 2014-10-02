"use strict";
/*
	Virtualization Layer | Main file
		by Alexandre Alapetite http://alexandre.alapetite.fr
			from Alexandra Institute http://www.alexandra.dk
			for the ALMANAC European project http://www.almanac-project.eu
*/

var http = require('http'),
	basicHttp = require('./basicHttp.js').basicHttp,	//Static files, logs
	config = require('./config.js').config,
	almanac = require('./almanac.js').almanac;

almanac.version = require('./package.json').version;
basicHttp.serverSignature = 'ALMANAC VirtualizationLayer ' + almanac.version + ' / ' + basicHttp.serverSignature;
almanac.basicHttp = basicHttp;
almanac.config = config;
almanac.http = http;

var server = http.createServer(function (req, res) {
	var reqUrl0 = '';	//Original request URL
	try {
		if (req && req.url) {
			reqUrl0 += req.url;
			var urlSegments = req.url.split('/', 3),
				s1 = '';
			switch (urlSegments.length) {
				case 3:
					s1 = urlSegments[1] + '/';
					break;
				case 2:
					s1 = urlSegments[1];
					break;
			}
			if (almanac.routes[s1]) {
				req.url = req.url.substring(s1.length + 1);
				almanac.routes[s1](req, res);
			} else if (s1 == '') {
				almanac.serveHome(req, res);
			} else {
				basicHttp.serveStaticFile(req, res);
			}
		} else {
			basicHttp.serve400(req, res);
		}
	} catch (ex) {
		req.url = reqUrl0;
		basicHttp.serve500(req, res, 'Exception: ' + ex);
	}
	try {
		req.url = reqUrl0;
		basicHttp.log(req, res);
	} catch (ex) {
		console.error('Node.js: Log exception: %s', ex);
	}
});

server.on('error', function (err) {
	console.error('Node.js: server error: %s. Check that you can use port %d.', err, config.hosts.virtualizationLayer.port);
	process.exit(1);
});

almanac.server = server;

server.listen(config.hosts.virtualizationLayer.port);

almanac.init();

console.log('Node.js: server running ALMANAC Virtualization Layer at %j', server.address());
