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
		virtualizationLayer: {
			scheme: 'http',
			host: 'localhost',
			port: 80,
		},
		virtualizationLayerPublicUrl: '',	//Public URL of this Virtualization Layer, if any
		mqttBrokerUrl: 'mqtt://localhost/',
		networkManagerUrl: 'http://localhost:8181/',
		recourceCatalogueUrn: 'urn:schemas-upnp-org:IoTdevice:OGCapplicationIoTresourcemanager:1',	//Set to blank to disable UPnP
		recourceCatalogueUrl: 'http://localhost:44441/',	//Set to blank to use only UPnP discovery
		scralUrl: 'http://localhost:8080/connectors.rest/',
		storageManagerUrl: 'http://cnet006.cloudapp.net/Dmf/SensorThings/',
		santanderUrl: 'http://data.smartsantander.eu/ISMB/',
		virtualizationLayerPeers: [	//Manual peering (sends the local MQTT events to other VirtualizationLayers
			//'http://almanac.alexandra.dk/',	//Alexandra Institute (Ubuntu)
			//'http://p2.alapetite.dk:8080/',	//Alexandra Institute (Raspberry Pi)
			//'http://130.192.86.227:8088/',	//ISMB
		],
	};

exports.config = {
	hosts: hosts,

	//{silent, error, warn, http, info, verbose, silly}
	logLevel: 'info',

	//For compatibility with old MQTT brokers, e.g. Mosquitto < 1.3
	mqttUseOldVersion3: true,
};
