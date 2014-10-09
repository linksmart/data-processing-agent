"use strict";
/*
	Virtualization Layer | Configuration file
		by Alexandre Alapetite http://alexandre.alapetite.fr
			from Alexandra Institute http://www.alexandra.dk
			for the ALMANAC European project http://www.almanac-project.eu
*/

var hosts = {
		virtualizationLayerPublic: {	//Public IP of this Virtualization Layer, if any
			scheme: 'http',
			host: 'almanac.alexandra.dk',
			port: 80,
		},
		virtualizationLayer: {
			scheme: 'http',
			host: 'localhost',
			port: 80,
		},
		recourceCatalogueUrn: 'urn:schemas-upnp-org:IoTdevice:applicationservicemanager:1',
		networkManager: {
			host: 'localhost',
			port: 8082,
		},
		mqttBroker: {
			host: 'localhost',
			port: 1883,
		},
		storageCloud: {	//Public Storage Manager cloud
			scheme: 'http',
			host: 'energyportal.cnet.se',
			port: 80,
			path: '/StorageManagerCloud/REST/',
		},
		storage: {	//Local Storage Manager
			scheme: 'http',
			host: '192.168.1.30',
			port: 80,
			path: '/StorageManagerLocal/REST/',
		},
		scral: {
			host: '130.192.86.227',
			port: 8080,
			path: '/connectors.rest-0.2.0/',
		},
		santander: {	//Public SmartSantander instance cloud
			host: 'data.smartsantander.eu',
			port: 80,
			path: '/ISMB/',
		},
		virtualizationLayerPeers: [	//Temporary manual peering while waiting for deployments using the NetworkManager
			'http://almanac.alexandra.dk/',	//Alexandra Institute (Ubuntu)
			'http://p2.alapetite.dk:8080/',	//Alexandra Institute (Raspberry Pi)
			'http://130.192.86.227:8088/',	//ISMB
		],
	};

hosts.masterStorageManager = hosts.storageCloud;
hosts.slaveVirtualizationLayer = null;

exports.config = {
	hosts: hosts,
};
