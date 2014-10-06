"use strict";
/*
	Virtualization Layer | LinkSmart.java NetworkManager client module
		by Alexandre Alapetite http://alexandre.alapetite.fr
			from Alexandra Institute http://www.alexandra.dk
			for the ALMANAC European project http://www.almanac-project.eu
*/

module.exports = function (almanac) {
	almanac.virtualAddress = null;

	function registerInNetworkManager() {
		almanac.request.post({
				url: 'http://' + almanac.config.hosts.networkManager.host + ':' + almanac.config.hosts.networkManager.port + '/NetworkManager',
				json: true,
				body: JSON.stringify({
						'Endpoint': almanac.config.hosts.virtualizationLayer.scheme + '://' + almanac.config.hosts.virtualizationLayer.host + ':' + almanac.config.hosts.virtualizationLayer.port + '/',
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
		almanac.request.get({
				//url: 'http://' + almanac.config.hosts.networkManager.host + ':' + almanac.config.hosts.networkManager.port + '/NetworkManager?description="VirtualizationLayer"',
				url: 'http://' + almanac.config.hosts.networkManager.host + ':' + almanac.config.hosts.networkManager.port + '/GetNetworkManagerStatus?method=getLocalServices',
				json: true,
				timeout: 2000,
			}, function (error, response, body) {
				if (!error && response.statusCode == 200 && body && body.VirtualAddresses) {
					var virtualAddress = '';
					for (var i = 0; i < body.VirtualAddresses.length; i++) {
						var va = body.VirtualAddresses[i];
						if (va.description && (va.description.indexOf(';SID = eu.linksmart.almanac.virtualizationlayer;') > 0)) {
							virtualAddress = va.virtualAddress;	//Found existing local VirtualizationLayer
						}
					}
					if (!virtualAddress) {	//Needs registration
						registerInNetworkManager();
					} else if (!almanac.virtualAddress) {
						almanac.virtualAddress = virtualAddress;
						console.log('VirtualizationLayer: Already registered in NetworkManager at address: ' + almanac.virtualAddress);
					} else if (almanac.virtualAddress != virtualAddress) {
						console.error('VirtualizationLayer: Inconsistent virtual address in NetworkManager: ' + almanac.virtualAddress + ' != ' + virtualAddress);
					}
				} else {
					console.warn('VirtualizationLayer: Cannot contact the NetworkManager! Will try again.');
				}
			});
	}

	refreshInNetworkManager();
	setInterval(refreshInNetworkManager, 120000);

	function proxyNetworkManagerTunnel(req, res) {
		req.pipe(almanac.request.get('http://' + almanac.config.hosts.networkManager.host + ':' + almanac.config.hosts.networkManager.port + '/Tunneling/0/' + req.url,
			function (error, response, body) {
				if (error) {
					almanac.basicHttp.serve500(req, res, 'Error proxying to NetworkManager tunneling!');
				}
			})).pipe(res);
	}

	almanac.routes['tunnel/'] = proxyNetworkManagerTunnel;	//Proxying to NetworkManager tunnel
};
