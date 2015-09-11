"use strict";
/*
	Virtualization Layer | Example of local configuration file
	> Do not edit "config.js" but create instead a "config.local.js" file with only the properties you would like to change;
	> see "config.js" for the full list of options.
*/

var hosts = {
		instanceName: 'YourPlace',
		virtualizationLayerPublic: {	//Public IP of this Virtualization Layer, if any
			host: 'example.org',
			port: 8088,
		},
		virtualizationLayer: {
			port: 8088,
		},
		//recourceCatalogueUrl: 'http://localhost:44441/',
		scral: {
			host: 'example.com',
			port: 8080,
			path: '/connectors.rest-0.2.0/',
		},
	};

exports.config = {
	hosts: hosts,
	logLevel: 'verbose',
};
