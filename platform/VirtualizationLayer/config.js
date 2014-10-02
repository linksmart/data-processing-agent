"use strict";
/*
	Virtualization Layer | Configuration file
		by Alexandre Alapetite http://alexandre.alapetite.fr
			from Alexandra Institute http://www.alexandra.dk
			for the ALMANAC European project http://www.almanac-project.eu
*/

var hosts = {
		virtualizationLayerPublic: {	//Public IP of this Virtualization Layer, if any
			host: 'almanac.alexandra.dk',
			port: 80,
		},
		virtualizationLayer: {
			host: 'localhost',
			port: 80,
		},
		recourceCatalogueUrn: 'urn:schemas-upnp-org:IoTdevice:applicationservicemanager:1',
		networkManagerUrl: 'http://localhost:8082/NetworkManager',
		mqttBroker: {
			host: 'localhost',
			port: 1883,
		},
		storageCloud: {	//Public Storage Manager cloud
			headers: {
				host: 'energyportal.cnet.se',
			},
			host: 'energyportal.cnet.se',
			port: 80,
			path: '/StorageManagerCloud/REST',
		},
		storage: {	//Local Storage Manager
			headers: {
				host: '192.168.1.30',
			},
			host: '192.168.1.30',
			port: 80,
			path: '/StorageManagerLocal/REST',
		},
		scral: {
			headers: {
				host: '130.192.86.227:8080',
			},
			host: '130.192.86.227',
			port: 8080,
			path: '/connectors.rest-0.2.0',
		},
		santander: {	//Public SmartSantander instance cloud
			headers: {
				host: 'data.smartsantander.eu',
			},
			host: 'data.smartsantander.eu',
			port: 80,
			path: '/ISMB',
		},
		virtualizationLayerPeers: [	//Temporary manual peering while waiting for deployments using the NetworkManager
			'http://almanac.alexandra.dk/',
			'http://p2.alapetite.dk:8080/',
		],
	};

hosts.masterStorageManager = hosts.storageCloud;
hosts.slaveVirtualizationLayer = null;

exports.config = {
	hosts: hosts,
};
