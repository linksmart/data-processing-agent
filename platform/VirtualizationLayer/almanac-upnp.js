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
		if (!almanac.recourceCatalogueUrl) {
			almanac.log.info('VL', 'UPnP: discovered ' + JSON.stringify(headers));
		}
		if (headers && headers.ST === almanac.config.hosts.recourceCatalogueUrn &&
			headers.LOCATION && headers.LOCATION !== almanac.recourceCatalogueUrl &&
			(!almanac.recourceCatalogueUrl || almanac.recourceCatalogueUrl.indexOf('http://127.0.0.1') < 0)) {	//Priority to 127.0.0.1 address
			almanac.recourceCatalogueUrl = headers.LOCATION;
			almanac.log.info('VL', 'UPnP: discovered the resource catalogue on ' + almanac.recourceCatalogueUrl);
			//almanac.webSocket.in('info').emit('info', 'UPnP: discovered the resource catalogue');
		}
	});

	function discoverResourceCatalogues() {
		almanac.ssdpClient.search(almanac.config.hosts.recourceCatalogueUrn);
	}

	almanac.ssdpClient.search('ssdp:all');
	setInterval(discoverResourceCatalogues, 60000);
};
