"use strict";
/*
	Virtualization Layer | UPnP/SSDP module
		by Alexandre Alapetite http://alexandre.alapetite.fr
			from Alexandra Institute http://www.alexandra.dk
			for the ALMANAC European project http://www.almanac-project.eu
*/

module.exports = function (almanac) {
	var SsdpClient = require('node-ssdp').Client;
	almanac.ssdpClient = new SsdpClient();

	almanac.ssdpClient.on('response', function (headers, statusCode, rinfo) {
		//console.log('SSDP: ' + JSON.stringify(headers) + ' ; ' + JSON.stringify(statusCode) + ' ; ' + JSON.stringify(rinfo));
		if (headers &&
			headers.ST === almanac.config.hosts.recourceCatalogueUrn &&
			headers.LOCATION && (headers.LOCATION.indexOf('http://127.0.0.1') < 0) &&	//127.0.0.1 is not always visible
			headers.LOCATION !== almanac.recourceCatalogueUrl) {
			almanac.recourceCatalogueUrl = headers.LOCATION;
			console.log('UPnP: discovered the resource catalogue on ' + almanac.recourceCatalogueUrl);
			almanac.webSocket.emit('chat', 'UPnP: discovered the resource catalogue');
		};
	});

	function discoverResourceCatalogues() {
		//console.log('SSDP: discovery');
		almanac.ssdpClient.search(almanac.config.hosts.recourceCatalogueUrn);
	}

	discoverResourceCatalogues();
	setInterval(discoverResourceCatalogues, 30000);
};
