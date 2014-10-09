"use strict";
/*
	Virtualization Layer | SmartSantander client module
		by Alexandre Alapetite http://alexandre.alapetite.fr
			from Alexandra Institute http://www.alexandra.dk
			for the ALMANAC European project http://www.almanac-project.eu
*/

module.exports = function (almanac) {

	function proxySmartSantander(req, res) {
		req.pipe(almanac.request.get('http://' + almanac.config.hosts.santander.host + ':' + almanac.config.hosts.santander.port + almanac.config.hosts.santander.path + req.url,
			function (error, response, body) {
				if (error || response.statusCode != 200 || !body) {
					almanac.log.warn('VL', 'Error ' + (response ? response.statusCode : 'undefined') + ' proxying to SmartSantander!');
					if (!body) {
						almanac.basicHttp.serve500(req, res, 'Error proxying to SmartSantander!');
					}
				}
			})).pipe(res);
	}

	almanac.routes['santander/'] = proxySmartSantander;	//Proxying to SmartSantander
};
