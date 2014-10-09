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
		if (headers && headers.ST === almanac.config.hosts.recourceCatalogueUrn &&
			headers.LOCATION && headers.LOCATION !== almanac.recourceCatalogueUrl &&
			(!almanac.recourceCatalogueUrl || almanac.recourceCatalogueUrl.indexOf('http://127.0.0.1') < 0)) {	//Priority to 127.0.0.1 address
			almanac.recourceCatalogueUrl = headers.LOCATION;
			console.log('UPnP: discovered the resource catalogue on ' + almanac.recourceCatalogueUrl);
			almanac.webSocket.emit('info', 'UPnP: discovered the resource catalogue');
		}
	});

	function discoverResourceCatalogues() {
		almanac.ssdpClient.search(almanac.config.hosts.recourceCatalogueUrn);
	}

	discoverResourceCatalogues();
	setInterval(discoverResourceCatalogues, 60000);
};
