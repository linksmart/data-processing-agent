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
	almanac.mqttClient = mqtt.connect(almanac.config.hosts.mqttBrokerUrl,
		almanac.config.mqttUseOldVersion3 ? {
			protocolId: 'MQIsdp',
			protocolVersion: 3,
		} : {});

	almanac.mqttClient.on('error', function (error) {
			almanac.log.warn('VL', 'MQTT: error: ' + error);
		});

	almanac.mqttClient.on('connect', function () {
			almanac.mqttClient.subscribe('/#');
			almanac.log.info('VL', 'MQTT: connected to: ' + almanac.config.hosts.mqttBrokerUrl);
			broadcastAlive('HELLO');
		});

	almanac.federationInstances = {};	//TODO: Move to another file

	almanac.mqttClient.on('message', function (topic, message) {
		if (!topic || !message) {
			return;
		}
		almanac.log.silly('VL', 'MQTT: ' + topic + ': ' + message);
		try {
			var json = JSON.parse(message);
			almanac.webSocket.forwardMqtt(topic, json);
			if (topic === '/broadcast') {
				almanac.log.verbose('VL', 'MQTT: ' + topic + ': ' + message);
				if (json.type === 'HELLO' && !almanac.isMe(json.info)) {
					broadcastAlive();
				} /*else if (json.type === 'CHAT')*/ {
					almanac.webSocketChat.broadcast(json);
				}
			} else if (topic.indexOf('/iotentity') > 0) {
				almanac.peering.mqttPeering(topic, json);	//Peering with other VirtualizationLayers
			}
		} catch (ex) {
			almanac.log.warn('VL', 'MQTT message error: ' + ex);
		}
	});

	almanac.mqttClient.broadcastChat = function (payload, clientInfo) {
		almanac.mqttClient.publish('/broadcast', JSON.stringify({
				date: Date.now(),
				type: 'CHAT',
				payload: payload,
				clientInfo: clientInfo,
				info: {
					instanceName: almanac.config.hosts.instanceName,
				},
			}));
	};

	function broadcastAlive(type) {
		almanac.mqttClient.publish('/broadcast', JSON.stringify({
				date: Date.now(),
				type: type || 'ALIVE',
				info: almanac.info(),
			}));
	}
	setInterval(broadcastAlive, 60000);

};
