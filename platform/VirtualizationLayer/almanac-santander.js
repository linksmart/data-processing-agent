"use strict";
/*
	Virtualization Layer | SmartSantander client module
		by Alexandre Alapetite http://alexandre.alapetite.fr
			from Alexandra Institute http://www.alexandra.dk
			for the ALMANAC European project http://www.almanac-project.eu
*/

module.exports = function (almanac) {

	function proxySmartSantander(req, res) {
		req.url = almanac.config.hosts.santander.path + req.url;
		almanac.httpProxy.web(req, res, {
				headers: {
					'Connection': 'close',
					host: almanac.config.hosts.santander.headers.host,
				},
				forward: null,
				target: {
					host: almanac.config.hosts.santander.host,
					port: almanac.config.hosts.santander.port,
				},
				xfwd: true,	//Include X-Forwarded-For header
			}, function (err) {
				almanac.basicHttp.serve500(req, res, 'Error proxying to SmartSantander: ' + err);
			});
	}

	almanac.routes['santander/'] = proxySmartSantander;	//Proxying to SmartSantander
};
