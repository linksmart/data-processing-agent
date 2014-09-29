"use strict";
/*
	Virtualization Layer | Configuration file
		by Alexandre Alapetite http://alexandre.alapetite.fr
			from Alexandra Institute http://www.alexandra.dk
			for the ALMANAC European project http://www.almanac-project.eu
*/

var IS_LOCAL_IOT_WEEK = false,
	hosts = {
		RecourceCatalogueUrn: 'urn:schemas-upnp-org:IoTdevice:applicationservicemanager:1',
		NetworkManagerUrl: 'http://localhost:8082/NetworkManager',
		VirtualizationLayerLocalUrl: 'http://localhost/',
		'storageCloud': {	//Public Storage Manager cloud
			headers: {
				host: 'energyportal.cnet.se',
			},
			host: 'energyportal.cnet.se',
			port: 80,
			path: '/StorageManagerCloud/REST',
		},
		'storageLocal': {	//Local Storage Manager at IoT-week
			headers: {
				host: '192.168.1.30',
			},
			host: '192.168.1.30',	//Static IP for IoT-week
			port: 80,
			path: '/StorageManagerLocal/REST',
		},
		'virtualPublic': {	//Public Virtualization Layer
			host: 'almanac.alexandra.dk',
			port: 80,
		},
		'virtualLocal': {	//Local Virtualization Layer at IoT-week
			host: '127.0.0.1',
			port: 8080,
		},
		'virtualTunnel': {	//Virtualization Layer for IoT-week through SSH tunnel
			host: 'localhost',
			port: 8082,
		},
		'scralPublic': {	//Public example of SCRAL
			headers: {
				host: '130.192.85.162:8080',
			},
			host: '130.192.85.162',
			port: 8080,
			path: '/connectors.rest-0.2.1',
		},
		'santanderPublic': {	//Public SmartSantander instance cloud
			headers: {
				host: 'data.smartsantander.eu',
			},
			host: 'data.smartsantander.eu',
			port: 80,
			path: '/ISMB',
		},
	};

hosts.masterStorageManager = IS_LOCAL_IOT_WEEK ? hosts.storageLocal : hosts.storageCloud;
hosts.masterVirtualizationLayer = IS_LOCAL_IOT_WEEK ? hosts.virtualLocal : hosts.virtualPublic;
hosts.slaveVirtualizationLayer = null;	//IS_LOCAL_IOT_WEEK ? hosts.virtualPublic : hosts.virtualTunnel;

exports.config = {
	hosts: hosts,
};
