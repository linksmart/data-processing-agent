"use strict";
/*
	Virtualization Layer | MQTT module
		by Alexandre Alapetite http://alexandre.alapetite.fr
			from Alexandra Institute http://www.alexandra.dk
			for the ALMANAC European project http://www.almanac-project.eu
*/

module.exports = function (almanac) {
	var mqtt = require('mqtt');
	almanac.log.info('VL', 'MQTT: connecting to: ' + almanac.config.hosts.mqttBrokerUrl);
	almanac.mqttClient = mqtt.connect(almanac.config.hosts.mqttBrokerUrl);

	almanac.mqttClient.on('error', function (error) {
			almanac.log.warn('VL', 'MQTT: error: ' + error);
		});

	almanac.mqttClient.on('connect', function () {
			almanac.log.info('VL', 'MQTT: connected to: ' + almanac.config.hosts.mqttBrokerUrl);
			almanac.mqttClient.subscribe('/#');
			almanac.mqttClient.publish('/almanac/0/info', JSON.stringify({
					info: 'VirtualizationLayer MQTT started',
				}));
		});

	almanac.mqttClient.on('message', function (topic, message) {
		try {
			almanac.log.verbose('VL', 'MQTT: ' + topic + ': ' + message);
			if (topic) {
				var json = JSON.parse(message);
				almanac.webSocket.forwardMqtt(topic, json);
				if (topic.indexOf('/almanac/alert') === 0) {
					//almanac.webSocket.in('alert').emit('alert', {	//TODO: Reimplement if needed
					//		instance: almanac.config.hosts.virtualizationLayerPublic,
					//		topic: topic,
					//		body: json,
					//	});
					almanac.peering.mqttPeering(topic, json);	//Peering with other VirtualizationLayers
				} else if (topic.indexOf('/iotentity') > 0) {
					//almanac.webSocket.in('scral').emit('scral', {	//TODO: Reimplement if needed
					//		instance: almanac.config.hosts.virtualizationLayerPublic,
					//		topic: topic,
					//		body: json,
					//	});
					if (almanac.config.mqttToHttpStorageManagerEnabled) {
						almanac.storageManager.postMqttEvent(topic, json);	//Forward to StorageManager
					}
					almanac.peering.mqttPeering(topic, json);	//Peering with other VirtualizationLayers
				} else if (topic.indexOf('/almanac/0/info') === 0) {
					//almanac.webSocket.in('info').emit('info', message);	//TODO: Re-implement if needeed
				}
			}
		} catch (ex) {
			almanac.log.warn('VL', 'MQTT message error: ' + ex);
		}
	});

	function broadcastAlive() {
		almanac.mqttClient.publish('/broadcast', JSON.stringify({
				'type': 'ALIVE',
				'from': context.global.almanac.instance,
				'mqttVirtualAddress': almanac.mqttVirtualAddress,
				'date': Date.now(),
			}));
	}

	setInterval(function () {
			almanac.mqttClient.publish('/broadcast', JSON.stringify({
					info: 'VirtualizationLayer alive',
				}));
		}, 60000);

	/*setInterval(function () {	//TODO: Remove after testing
			almanac.mqttClient.publish('/almanac/observations/iotentity/', '{"About":"dd2f87dd-4f80-455b-a939-e22f7f20a0c1","Properties":[{"IoTStateObservation":[{"Value":"3.36","PhenomenonTime":"2014-10-08T15:42:07.906Z","ResultTime":"2014-10-08T15:42:17.906Z"}],"About":"pipe1:PhMeter:getPh"}]}');
		}, 20000);*/
};
