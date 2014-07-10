"use strict";
/*
	Virtualization Layer | Main file
		by Alexandre Alapetite http://alexandre.alapetite.fr
			from Alexandra Institute http://www.alexandra.dk
			for the ALMANAC European project http://www.almanac-project.eu
*/

var basic = require('./basic.js').basic,	//Static files, logs
	almanac = require('./almanac.js').almanac,
	http = require('http');

var server = http.createServer(function (req, res) {
	var reqUrl0 = '';	//Original request
	try {
		if (req && req.url) {
			reqUrl0 += req.url;
			var urlSegments = req.url.split('/', 3),
				s1 = '';
			switch (urlSegments.length) {
				case 3:
					s1 = urlSegments[1] + '/';
					break;
				case 2:
					s1 = urlSegments[1];
					break;
			}
switch (s1) {	//Routing
	case 'dm/':	//Proxying to Data Management
		req.url = req.url.substring(s1.length);
		almanac.proxyDataManagement(req, res);
		break;
	case 'dm-geojson/':	//Conversion of Data Management JSON to GeoJSON
		req.url = req.url.substring(s1.length);
		almanac.proxyDataManagementToGeojson(req, res);
		break;
	case 'dm-atom/':	//Conversion of Data Management JSON to ATOM (RSS)
	case 'dm-rss/':
		req.url = req.url.substring(s1.length);
		almanac.proxyDataManagementToAtom(req, res);
		break;
	case 'dm-txt/':	//Conversion of Data Management JSON to TXT
	case 'dm-tsv/':
	case 'dm-csv/':
		req.url = req.url.substring(s1.length);
		almanac.proxyDataManagementToText(req, res, s1 === 'dm-csv/' ? 'csv' : 'tsv');
		break;
	case 'scral/':	//Proxying to SCRAL
		req.url = req.url.substring(s1.length);
		almanac.proxyScral(req, res);
		break;
	case 'santander/':	//Proxying to SmartSantander
		req.url = req.url.substring(s1.length);
		almanac.proxySmartSantander(req, res);
		break;
	case '':	//Serve a welcome page
		almanac.serveHome(req, res);
		break;
	default:	//Serve a static file
		basic.serveStaticFile(req, res);
		break;
}
		} else {
			basic.serve400(req, res);
		}
	} catch (ex) {
		req.url = reqUrl0;
		basic.serve500(req, res, 'Exception: ' + ex);
	}
	try {
		req.url = reqUrl0;
		basic.log(req, res);
	} catch (ex) {
		console.error('Log exception: %s', ex);
	}
});

almanac.ioInit(server);	//Socket.IO

server.listen(almanac.config.hosts.masterVirtualizationLayer.port)

console.log('Node.js server running ALMANAC Virtualization Layer at %j', server.address());
