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

	almanac.mqttClient.on('message', function (topic, message) {
		if (!topic || !message) {
			return;
		}
		almanac.log.silly('VL', 'MQTT: ' + topic + ': ' + message);
		try {
			var json = JSON.parse(message);
			if (almanac.webSocket) {
				almanac.webSocket.forwardMqtt(topic, json);
			}
			if (topic === '/broadcast') {
				almanac.log.verbose('VL', 'MQTT: ' + topic + ': ' + message);
				switch (json.type) {
					case 'HELLO':
					case 'ALIVE':
						if (json.info && !almanac.isMe(json.info)) {
							if (almanac.updateInstance) {
								almanac.updateInstance(json.info);
							}
							if (json.type === 'HELLO') {
								broadcastAlive();
							}
						}
						break;
					case 'CHAT':
						if (almanac.webSocketChat) {
							almanac.webSocketChat.broadcast(json);
						}
						break;
					case 'DISTREQ':
						if (almanac.replyToDistributedRequest) {
							almanac.replyToDistributedRequest(json);
						}
						break;
					case 'DISTRESP':
						if (almanac.processDistributedResponse) {
							almanac.processDistributedResponse(json);
						}
						break;
				}
			} else if (topic.indexOf('/iotentity') > 0) {
				if (almanac.peering) {
					almanac.peering.mqttPeering(topic, json);	//Peering with other VirtualizationLayers
				}
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
					randomId: almanac.randomId,
				},
			}));
	};

	almanac.mqttClient.broadcastDistributedRequest = function (payload) {
		almanac.mqttClient.publish('/broadcast', JSON.stringify({
				date: Date.now(),
				type: 'DISTREQ',
				payload: payload,
				info: {
					instanceName: almanac.config.hosts.instanceName,
					randomId: almanac.randomId,
				},
			}));
	};

	almanac.mqttClient.broadcastDistributedResponse = function (payload) {
		almanac.mqttClient.publish('/broadcast', JSON.stringify({
				date: Date.now(),
				type: 'DISTRESP',
				payload: payload,
				info: {
					instanceName: almanac.config.hosts.instanceName,
					randomId: almanac.randomId,
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
