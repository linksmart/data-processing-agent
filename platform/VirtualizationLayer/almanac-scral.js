"use strict";
/*
	Virtualization Layer | SCRAL client module
		by Alexandre Alapetite http://alexandre.alapetite.fr
			from Alexandra Institute http://www.alexandra.dk
			for the ALMANAC European project http://www.almanac-project.eu
*/

module.exports = function (almanac) {

	function proxyScral(req, res) {
		req.pipe(almanac.request.get('http://' + almanac.config.hosts.scral.host + ':' + almanac.config.hosts.scral.port + almanac.config.hosts.scral.path + req.url,
			function (error, response, body) {
				if (error) {
					almanac.basicHttp.serve500(req, res, 'Error proxying to SCRAL!');
				}
			})).pipe(res);
	}

	almanac.routes['scral/'] = proxyScral;	//Proxying to SCRAL
};
