"use strict";
/*
	Virtualization Layer | MQTT module
		by Alexandre Alapetite http://alexandre.alapetite.fr
			from Alexandra Institute http://www.alexandra.dk
			for the ALMANAC European project http://www.almanac-project.eu
*/

module.exports = function (almanac) {
	var mqtt = require('mqtt');
	almanac.mqttClient = mqtt.createClient(almanac.config.hosts.mqttBroker.port, almanac.config.hosts.mqttBroker.host);

	almanac.mqttClient.on('message', function (topic, message) {
		try {
			console.log('MQTT: ' + topic + ': ' + message);

			almanac.webSocket.emit('mqtt', {
					instance: almanac.config.hosts.virtualizationLayerPublic,
					topic: topic,
					body: message,
				});

			if (topic && (topic.indexOf('/iotentity') > 0)) {
				var json = JSON.parse(message);
				almanac.peering.mqttPeering(topic, json);	//Peering with other VirtualizationLayers
				almanac.storageManager.postMqttEvent(topic, json);	//Temporary forward to StorageManager, which does not listens to MQTT yet
			}
		} catch (ex) {
			console.error('MQTT message error: ' + ex);
		}
	});

	almanac.mqttClient.subscribe('/almanac/#');

	setTimeout(function () {
			almanac.mqttClient.publish('/almanac/0/chat', 'VirtualizationLayer MQTT started');
		}, 5000);

	setInterval(function () {
			almanac.mqttClient.publish('/almanac/0/chat', 'VirtualizationLayer alive');
		}, 60000);

	/*setInterval(function () {	//TODO: Remove after testing
			almanac.mqttClient.publish('/almanac/observations/iotentity/', '{"About":"A1E3FD26-E9F2-4E19-8E52-602B30C9C25B","Description":"Supercool device, that is better than yours","Meta":[{"Value":"Location","property":"p1"}],"Name":"iPhone Simulator","Prefix":"inertiaontologies:http:\/\/ns.inertia.eu\/ontologies xs:XMLSchema","Properties":[{"About":"A1E3FD26-E9F2-4E19-8E52-602B30C9C25B:location","DataType":"geo:point","Description":"Location of the iPhone, based on GPS\/WIFI\/CELL. Updated based on significant change.","IoTStateObservation":[{"Value":"-122.406417 37.785834","PhenomenonTime":"2014-07-07T11:38:40.859Z","ResultTime":"2014-07-07T11:38:40.859Z"}],"Meta":null,"Name":"Location of phone","Prefix":"inertiaontologies:http:\/\/ns.inertia.eu\/ontologies xs:XMLSchema","TypeOf":["almanac:location"],"UnitOfMeasurement":{"TypeId":"Location","property":""}},{"About":"A1E3FD26-E9F2-4E19-8E52-602B30C9C25B:flow","DataType":"xs:double","Description":"Toilet using water","IoTStateObservation":[{"Value":"0.003","PhenomenonTime":"2014-07-04T11:36:30.354Z","ResultTime":"2014-07-04T11:36:30.354Z"}],"Meta":null,"Name":"Toilet","Prefix":"almanac:http:\/\/ns.almanac.eu\/ontologies xs:XMLSchema","TypeOf":["almanac:flow"],"UnitOfMeasurement":{"TypeId":"m^3\/s","property":""}}],"TypeOf":["iPhone Simulator 7.1"]}');
		}, 20000);*/
};
