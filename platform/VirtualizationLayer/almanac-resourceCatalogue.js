"use strict";
/*
	Virtualization Layer | LinkSmart.NET ResourceCatalogue client module
		by Alexandre Alapetite http://alexandre.alapetite.fr
			from Alexandra Institute http://www.alexandra.dk
			for the ALMANAC European project http://www.almanac-project.eu
*/

module.exports = function (almanac) {

	function proxyResourceCatalogue(req, res) {

		if (!almanac.config.hosts.recourceCatalogueUrl) {
			almanac.basicHttp.serve503(req, res);
			return;
		}

		var url = almanac.config.hosts.recourceCatalogueUrl + req.url;

		req.pipe(almanac.request({
				method: req.method,
				url: url,
				timeout: 30000,
				encoding: null,
				headers: {
					'Accept': 'application/json',
					'Content-Type': 'application/json',	//For compatibility with Resource Catalogue
				},
			}, function (error, response, body) {
				if (error || response.statusCode != 200 || !body) {
					almanac.log.warn('VL', 'Error ' + (response ? response.statusCode : 0) + ' proxying to Resource Catalogue! ' + url);
					if (!body) {
						almanac.basicHttp.serve503(req, res);
					}
				}
			})).pipe(res, {
					end: true,
				});
	}

	almanac.routes['ResourceCatalogue/'] = proxyResourceCatalogue;
};
