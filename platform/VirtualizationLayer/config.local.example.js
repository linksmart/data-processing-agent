"use strict";
/*
	Virtualization Layer | Example of local configuration file
	> Do not edit "config.js" but create instead a "config.local.js" file with only the properties you would like to change;
	> see "config.js" for the full list of options.
*/

var hosts = {
		instanceName: 'YourPlace',	//Name of the instance in the federation
		virtualizationLayerPublicUrl: 'http://example.net/',	//Public URL of this Virtualization Layer, if any
		virtualizationLayer: {
			port: 80,
		},
		mqttBrokerUrl: 'mqtt://localhost/',
		networkManagerUrl: 'http://localhost:8181/',
		recourceCatalogueUrn: 'urn:schemas-upnp-org:IoTdevice:OGCapplicationIoTresourcemanager:1',	//Set to blank to disable UPnP
		recourceCatalogueUrl: '',	//Leave blank for UPnP discovery
		scralUrl: 'http://localhost:8080/connectors.rest/',
		scralUiUrl: 'http://localhost:8080/gui/',
		storageManagerUrl: 'http://cnet006.cloudapp.net/Dmf/SensorThings/',
		dfmUrl: 'http://localhost:8319/',
	};

exports.config = {
	hosts: hosts,

	//{silent, error, warn, http, info, verbose, silly}
	logLevel: 'info',

	//Enable iff you use Mosquitto >= 1.3
	mqttUseOldVersion3: false,
};
