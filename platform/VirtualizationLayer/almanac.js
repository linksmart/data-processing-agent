"use strict";
/*
	Virtualization Layer | ALMANAC-specific logic
		by Alexandre Alapetite http://alexandre.alapetite.fr
			from Alexandra Institute http://www.alexandra.dk
			for the ALMANAC European project http://www.almanac-project.eu
*/

var almanac = {

	basicHttp: null,	//Static files, logs
	config: null,
	http: null,
	server: null,
	version: '0',
	request: require('request'),

	routes: {	//Routing of requests
	},

	serveHome: function (req, res) {
		var now = new Date();
		res.writeHead(200, {
				'Content-Type': 'text/html; charset=UTF-8',
				'Date': now.toUTCString(),
				'Server': almanac.basicHttp.serverSignature,
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
<a href="./virtualizationLayerInfo">More information about this instance</a>.\n\
It is now ' + now.toISOString() + '.\n\
</pre>\n\
</body>\n\
</html>\n\
 ');
	},

	serveInfo: function (req, res) {
		almanac.basicHttp.serveJson(req, res, {
			version: almanac.version,
			publicAddress: almanac.config.hosts.virtualizationLayer.scheme + '://' + almanac.config.hosts.virtualizationLayerPublic.host + ':' + almanac.config.hosts.virtualizationLayerPublic.port + '/',
			virtualAddress: almanac.virtualAddress,
			networkManager: 'http://' + almanac.config.hosts.networkManager.host + ':' + almanac.config.hosts.networkManager.port + '/',
			storageManager: 'http://' + almanac.config.hosts.masterStorageManager.host + ':' + almanac.config.hosts.masterStorageManager.port + almanac.config.hosts.masterStorageManager.path,
			resourceCatalogue: almanac.recourceCatalogueUrl,
			scral: 'http://' + almanac.config.hosts.scral.host + ':' + almanac.config.hosts.scral.port + almanac.config.hosts.scral.path,
			server: almanac.basicHttp.serverSignature,
			nodejs: process.versions,
		});
	},

	init: function() {
		almanac.routes['virtualizationLayerInfo'] = almanac.serveInfo;	//Requests the public address of this VirtualizationLayer instance and other info

		require('./almanac-peering.js')(almanac);
		require('./almanac-resourceCatalogue.js')(almanac);
		require('./almanac-storageManager.js')(almanac);
		require('./almanac-scral.js')(almanac);
		require('./almanac-santander.js')(almanac);

		setTimeout(function() {
				require('./almanac-websocket.js')(almanac);	//WebSocket (Socket.IO)
				require('./almanac-mqtt.js')(almanac);	//MQTT
				require('./almanac-upnp.js')(almanac);	//UPnP (SSDP)
				require('./almanac-networkManager.js')(almanac);	//Register in the NetworkManager
			}, 2000);
	},

};

exports.almanac = almanac;
