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
		almanac.webSocket.emit('mqtt', {
				instance: almanac.config.hosts.virtualizationLayerPublic,
				topic: topic,
				body: message,
			});
		console.log('MQTT: ' + topic + ': ' + message);

		almanac.peering.mqttPeering(topic, message);	//Peering with other VirtualizationLayers

		if (topic && (topic.indexOf('/iotentity/') > 0)) {	//Temporary forward to StorageManager, which does not listens to MQTT yet
			almanac.storageManager.postMqttEvent(topic, message);
		}
	});

	almanac.mqttClient.subscribe('/almanac/#');

	setTimeout(function () {
			almanac.mqttClient.publish('/almanac/0/chat', 'VirtualizationLayer MQTT started');
		}, 5000);

	setInterval(function () {
			almanac.mqttClient.publish('/almanac/0/chat', 'VirtualizationLayer alive');
		}, 60000);
};
