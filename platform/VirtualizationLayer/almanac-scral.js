"use strict";
/*
	Virtualization Layer | SCRAL client module
		by Alexandre Alapetite http://alexandre.alapetite.fr
			from Alexandra Institute http://www.alexandra.dk
			for the ALMANAC European project http://www.almanac-project.eu
*/

module.exports = function (almanac) {

	function proxyScral(req, res) {
		req.pipe(almanac.request({
				method: req.method,
				uri: 'http://' + almanac.config.hosts.scral.host + ':' + almanac.config.hosts.scral.port + almanac.config.hosts.scral.path + req.url,
				timeout: 15000,
			}, function (error, response, body) {
				if (error || response.statusCode != 200 || !body) {
					almanac.log.warn('VL', 'Error ' + (response ? response.statusCode : 'undefined') + ' proxying to SCRAL!');
					if (!body) {
						almanac.basicHttp.serve503(req, res);
					}
				}
			})).pipe(res);
	}

	almanac.routes['scral/'] = proxyScral;	//Proxying to SCRAL
};
