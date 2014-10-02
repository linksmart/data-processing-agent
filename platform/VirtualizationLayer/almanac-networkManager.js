"use strict";
/*
	Virtualization Layer | LinkSmart.java NetworkManager client module
		by Alexandre Alapetite http://alexandre.alapetite.fr
			from Alexandra Institute http://www.alexandra.dk
			for the ALMANAC European project http://www.almanac-project.eu
*/

module.exports = function (almanac) {
	almanac.virtualAddress = null;

	var request = require('request');

	function registerInNetworkManager() {
		request.post({
				url: almanac.config.hosts.networkManagerUrl,
				json: true,
				body: JSON.stringify({
						'Endpoint': 'http://' + almanac.config.hosts.virtualizationLayer.host + ':' + almanac.config.hosts.virtualizationLayer.port + '/',
						'BackboneName': 'eu.linksmart.network.backbone.impl.soap.BackboneSOAPImpl',
						'Attributes': {
							'description': 'VirtualizationLayer',
							'sid': 'eu.linksmart.almanac.virtualizationlayer'
						}
					}),
				timeout: 4000,
			}, function (error, response, body) {
				if (!error && response.statusCode == 200 && body && body.VirtualAddress) {
					almanac.virtualAddress = body.VirtualAddress;
					console.log('VirtualizationLayer: Registered in the NetworkManager with VirtualAddress: ' + almanac.virtualAddress);
				} else {
					console.error('VirtualizationLayer: Cannot register in the NetworkManager! Will try again.');
				}
			});
	}

	function refreshInNetworkManager() {
		request.get({
					//TODO: use http://localhost:8082/GetNetworkManagerStatus?method=getLocalServices instead to get only local services: body.VirtualAddresses[i].description.indexOf('eu.linksmart.almanac.virtualizationlayer;') > 0; body.VirtualAddresses[i].virtualAddress
					url: almanac.config.hosts.networkManagerUrl + '?description="VirtualizationLayer"',
					json: true,
					timeout: 2000,
				}, function (error, response, body) {
				if (!error && response.statusCode == 200 && body) {
					if (body.length == 0) {	//Needs registration
						registerInNetworkManager();
					} else if (almanac.virtualAddress == null) {
						almanac.virtualAddress = body[0].VirtualAddress;
						console.log('VirtualizationLayer: Already registered in NetworkManager at address: ' + almanac.virtualAddress);
					} else if (almanac.virtualAddress != body[0].VirtualAddress) {
						console.error('VirtualizationLayer: Inconsistent virtual address in NetworkManager: ' + almanac.virtualAddress + ' != ' + body[0].VirtualAddress);
					}
				} else {
					console.warn('VirtualizationLayer: Cannot contact the NetworkManager! Will try again.');
				}
			});
	}

	refreshInNetworkManager();
	setInterval(refreshInNetworkManager, 120000);
};
