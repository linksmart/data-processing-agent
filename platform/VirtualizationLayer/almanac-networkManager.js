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
						'Endpoint': almanac.config.hosts.virtualizationLayer.scheme + '://' + almanac.config.hosts.virtualizationLayer.host + ':' + almanac.config.hosts.virtualizationLayer.port + '/',	//The port number must always be mentionned for LinkSmart
						'BackboneName': 'eu.linksmart.network.backbone.impl.soap.BackboneSOAPImpl',
						'Attributes': {
							'description': 'VirtualizationLayer',
							'sid': 'eu.linksmart.almanac.virtualizationlayer',
						}
					}),
				timeout: 4000,
			}, function (error, response, body) {
				if (!error && response.statusCode == 200 && body && body.VirtualAddress) {
					almanac.virtualAddress = body.VirtualAddress;
					almanac.log.info('VL', 'Registered in the NetworkManager with VirtualAddress: ' + almanac.virtualAddress);
				} else {
					almanac.log.warn('VL', 'Cannot register in the NetworkManager! Will try again.');
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
						almanac.log.info('VL', 'Already registered in NetworkManager at address: ' + almanac.virtualAddress);
					} else if (almanac.virtualAddress != virtualAddress) {
						almanac.log.error('VL', 'Inconsistent virtual address in NetworkManager: ' + almanac.virtualAddress + ' != ' + virtualAddress);
					}
				} else {
					almanac.log.warn('VL', 'Cannot contact the NetworkManager! Will try again.');
				}
			});
	}

	refreshInNetworkManager();
	setInterval(refreshInNetworkManager, 120000);

	function proxyNetworkManagerTunnel(req, res) {
		req.pipe(almanac.request({
				method: req.method,
				uri: 'http://' + almanac.config.hosts.networkManager.host + ':' + almanac.config.hosts.networkManager.port + '/Tunneling/0/' + req.url,
				timeout: 20000,
			}, function (error, response, body) {
				if (error || response.statusCode != 200 || !body) {
					almanac.log.warn('VL', 'Error ' + (response ? response.statusCode : 'undefined') + ' proxying to NetworkManager tunneling!');
					if (!body) {
						almanac.basicHttp.serve503(req, res);
					}
				}
			})).pipe(res);
	}

	almanac.routes['tunnel/'] = proxyNetworkManagerTunnel;	//Proxying to NetworkManager tunnel
};
