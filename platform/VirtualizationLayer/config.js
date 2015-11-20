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
		instanceName: process.env.INSTANCE_NAME || '',	//Name of the instance in the federation
		virtualizationLayer: {
			scheme:     process.env.VL_SCHEME || 'http',
			  host:     process.env.VL_HOST   || 'localhost',
			  port: int(process.env.VL_PORT)  || 80,
		},
		virtualizationLayerPublicUrl: process.env.VL_PUBLIC_URL || '',	//Public URL of this Virtualization Layer, if any
		mqttBrokerUrl: process.env.MQTT_BROKER_URL || 'mqtt://localhost/',
		networkManagerUrl: process.env.NETWORK_MANAGER_URL || 'http://localhost:8181/',
		recourceCatalogueUrn: process.env.RESOURCE_CATALOGUE_URN || 'urn:schemas-upnp-org:IoTdevice:OGCapplicationIoTresourcemanager:1',	//Set to blank to disable UPnP
		recourceCatalogueUrl: process.env.RESOURCE_CATALOGUE_URL || 'http://localhost:44441/',	//Set to blank to use only UPnP discovery
		scralUrl: process.env.SCRAL_URL || 'http://localhost:8080/connectors.rest/',
		scralUiUrl: process.env.SCRAL_UI_URL || 'http://localhost:8080/gui/',
		storageManagerUrl: process.env.STORAGE_MANAGER_UTL || 'http://cnet006.cloudapp.net/Dmf/SensorThings/',
		dfmUrl: process.env.DFM_URL || 'http://localhost:8319/',
		santanderUrl: process.env.SANTANDER_URL || 'http://data.smartsantander.eu/ISMB/',
		virtualizationLayerPeers: [	//Manual peering (sends the local MQTT events to other VirtualizationLayers
			//'http://almanac.alexandra.dk/',	//Alexandra Institute (Ubuntu)
			//'http://p2.alapetite.dk:8080/',	//Alexandra Institute (Raspberry Pi)
			//'http://130.192.86.227:8088/',	//ISMB
		],
	};

exports.config = {
	hosts: hosts,

	//{silent, error, warn, http, info, verbose, silly}
	logLevel: process.env.LOG_LEVEL || 'info',

	//For compatibility with old MQTT brokers, e.g. Mosquitto < 1.3
	mqttUseOldVersion3: bool(process.env.MQTT_USE_OLD_VERSION_3) || true,
};


function bool(str) {
  if (str === void 0) return false;
  return str.toLowerCase() === 'true';
}

function int(str) {
  if (!str) return 0;
  return parseInt(str, 10);
}

function float(str) {
  if (!str) return 0;
  return parseFloat(str, 10);
}
