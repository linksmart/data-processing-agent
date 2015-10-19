"use strict";
/*
	Virtualization Layer | Peering module
		by Alexandre Alapetite http://alexandre.alapetite.fr
			from Alexandra Institute http://www.alexandra.dk
			for the ALMANAC European project http://www.almanac-project.eu
*/

module.exports = function (almanac) {

	for (var i = almanac.config.hosts.virtualizationLayerPeers.length - 1; i >= 0; i--) {	//Remove the local instance from the list of peers
		if (almanac.config.hosts.virtualizationLayerPeers[i].indexOf(almanac.config.hosts.virtualizationLayerPublicUrl) >= 0) {
			almanac.config.hosts.virtualizationLayerPeers.splice(i, 1);
		}
	}

	almanac.peering = {
		mqttPeering: function (topic, json) {
			if (!json) {
				json = {};
			}
			json.mqttTopic = topic;
			json.vlInstance = almanac.config.hosts.virtualizationLayerPublicUrl;

			function postToPeer(peer) {
				almanac.request.post({
						url: peer + 'mqttPeering/',
						json: json,
						timeout: 15000,
					}, function (error, response, body) {
						if (error || !response || response.statusCode != 200) {
							almanac.log.warn('VL', 'Error ' + (response ? response.statusCode : 'undefined') + ' while forwarding MQTT to ' + peer);
						} else {
							almanac.log.verbose('VL', 'MQTT forwarded to ' + peer);
						}
					});
			}

			for (var i = 0; i < almanac.config.hosts.virtualizationLayerPeers.length; i++) {	//Forward to VirtualizationLayer peers
				postToPeer(almanac.config.hosts.virtualizationLayerPeers[i]);
			}
		}
	};

	function processMqttPeering(req, res) {
		//TODO: Check that it comes from an authorised peer
		if (req.method !== 'POST') {
			almanac.basicHttp.serve405(req, res, 'POST');
		} else if (!req.headers['content-type'] || req.headers['content-type'].toLowerCase() !== 'application/json') {
			almanac.basicHttp.serve406(req, res);
		} else if (!almanac.webSocket) {
			almanac.basicHttp.serve503(req, res);
		} else {
			var body = '';
			req.addListener('data', function (chunk) {
					body += chunk;
				});
			req.addListener('end', function () {
					try {
						var json = JSON.parse(body);
						//almanac.webSocket.in('peering').emit('peering', json);	//Forward to WebSocket clients	//TODO: Reimplement if needed
						almanac.log.verbose('VL', 'Peering MQTT forwarded to WebSocket');
						almanac.basicHttp.serveJson(req, res, {});
					} catch (ex) {
						almanac.log.warn('VL', 'MQTT peering error: ' + ex);
						almanac.basicHttp.serve500(req, res);
					}
				});
		}
	}

	almanac.routes['mqttPeering/'] = processMqttPeering;
};
