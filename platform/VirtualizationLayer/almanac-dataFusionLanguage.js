"use strict";
/*
	Virtualization Layer | Data Fusion Language client module
		by Dario Bonino <dario.bonino@gmail.com>
			for the ALMANAC European project http://www.almanac-project.eu
*/

module.exports = function (almanac) {

	function proxyDfl(req, res) {
		req.pipe(almanac.request({
				method: req.method,
				uri: almanac.config.hosts.dflUrl + req.url,
				timeout: 15000,
			}, function (error, response, body) {
				if (error || response.statusCode != 200 || !body) {
					almanac.log.warn('VL', 'Error ' + (response ? response.statusCode : 'undefined') + ' proxying to Data Fusion Language API!');
					if (!body) {
						almanac.basicHttp.serve503(req, res);
					}
				}
			})).pipe(res);
	}

	almanac.routes['dfl/'] = proxyDfl;	//Proxying to Data Fusion Manager
};
