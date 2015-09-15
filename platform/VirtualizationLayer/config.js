"use strict";
/*
	Virtualization Layer | Glogal configuration file
	> Do not edit "config.js" but create instead a "config.local.js" file with only the properties you would like to change;
	> an example is available in "config.local.example.js"
		by Alexandre Alapetite http://alexandre.alapetite.fr
			from Alexandra Institute http://www.alexandra.dk
			for the ALMANAC European project http://www.almanac-project.eu
*/

var hosts = {
		instanceName: '',	//Name of the instance in the federation
		virtualizationLayerPublic: {	//Public IP of this Virtualization Layer, if any
			scheme: 'http',
			host: 'example.net',
			port: 80,
		},
		virtualizationLayer: {
			scheme: 'http',
			host: 'localhost',
			port: 80,
		},
		mqttBrokerUrl: 'mqtt://localhost',
		networkManagerUrl: 'http://localhost:8181',
		recourceCatalogueUrn: 'urn:schemas-upnp-org:IoTdevice:OGCapplicationIoTresourcemanager:1',	//Set to blank to disable UPnP
		recourceCatalogueUrl: '',	//Leave blank for UPnP discovery
		scral: {
			host: 'localhost',
			port: 8080,
			path: '/connectors.rest/',
		},
		storage: {	//Local Storage Manager
			scheme: 'http',
			host: 'localhost',
			port: 8081,
			path: '/StorageManagerLocal/REST/',
		},
		storageCloud: {	//Public Storage Manager cloud
			scheme: 'http',
			host: 'almanac_dmf1.cnet.se',
			port: 80,
			path: '/StorageManagerCloud/REST/',
		},
		santander: {	//Public SmartSantander instance cloud
			host: 'data.smartsantander.eu',
			port: 80,
			path: '/ISMB/',
		},
		virtualizationLayerPeers: [	//Manual peering (sends the local MQTT events to other VirtualizationLayers
			//'http://almanac.alexandra.dk/',	//Alexandra Institute (Ubuntu)
			//'http://p2.alapetite.dk:8080/',	//Alexandra Institute (Raspberry Pi)
			//'http://130.192.86.227:8088/',	//ISMB
		],
	};

hosts.masterStorageManager = hosts.storageCloud;

exports.config = {
	hosts: hosts,

	//{silent, error, warn, http, info, verbose, silly}
	logLevel: 'info',

	//Forward or not the local MQTT events by HTTP to the StorageManager (the one defined in hosts.masterStorageManager)
	mqttToHttpStorageManagerEnabled: false,
};
