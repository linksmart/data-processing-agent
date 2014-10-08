"use strict";
/*
	Virtualization Layer | LinkSmart.NET ResourceCatalogue client module
		by Alexandre Alapetite http://alexandre.alapetite.fr
			from Alexandra Institute http://www.alexandra.dk
			for the ALMANAC European project http://www.almanac-project.eu
*/

module.exports = function (almanac) {
	almanac.recourceCatalogueUrl = '';	//Defined by UPnP discovery

	function proxyResourceCatalogue(req, res) {

		if (!almanac.recourceCatalogueUrl) {
			almanac.basicHttp.serve503(req, res);
			return;
		}

		var proxy = almanac.http.request(almanac.recourceCatalogueUrl + req.url, function (res2) {
			res2.pipe(res, { end: true });
		});

		req.pipe(proxy, { end: true });
	}

	almanac.routes['ResourceCatalogue/'] = proxyResourceCatalogue;
};
