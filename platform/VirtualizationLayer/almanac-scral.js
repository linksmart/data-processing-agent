"use strict";
/*
	Virtualization Layer | SCRAL client module
		by Alexandre Alapetite http://alexandre.alapetite.fr
			from Alexandra Institute http://www.alexandra.dk
			for the ALMANAC European project http://www.almanac-project.eu
*/

module.exports = function (almanac) {

	function proxyScral(req, res) {
		req.url = almanac.config.hosts.scral.path + req.url;
		almanac.httpProxy.web(req, res, {
				headers: {
					'Connection': 'close',
					host: almanac.config.hosts.scral.headers.host,
				},
				forward: null,
				target: {
					host: almanac.config.hosts.scral.host,
					port: almanac.config.hosts.scral.port,
				},
				xfwd: true,	//Include X-Forwarded-For header
			}, function (err) {
				almanac.basicHttp.serve500(req, res, 'Error proxying to SCRAL: ' + err);
			});
	}

	almanac.routes['scral/'] = proxyScral;	//Proxying to SCRAL
};
