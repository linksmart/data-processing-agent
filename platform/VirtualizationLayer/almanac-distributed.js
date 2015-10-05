"use strict";
/*
	Virtualization Layer | Distributed request module
		by Alexandre Alapetite http://alexandre.alapetite.fr
			from Alexandra Institute http://www.alexandra.dk
			for the ALMANAC European project http://www.almanac-project.eu
*/

module.exports = function (almanac) {

	var peerTimeout = 10 * 60 * 1000;	//In milliseconds

	almanac.distributedInstances = {};

	function servedistributedInstances(req, res) {
		almanac.basicHttp.serveJson(req, res, almanac.distributedInstances);
	}

	almanac.updateInstance = function (info) {
		var instanceNames = Object.keys(almanac.distributedInstances);

		//Delete expired peers
		var expireDate = Date.now() - peerTimeout;
		for (var i = instanceNames.length - 1; i >= 0; i--) {
			var instanceName = instanceNames[i];
			var instance = almanac.distributedInstances[instanceName];
			if (instance.date < expireDate) {
				almanac.log.info('VL', 'Distributed peer expired "' + instanceName + '": ' + JSON.stringify(almanac.distributedInstances[instanceName]));
				delete almanac.distributedInstances[instanceName];
			}
		}

		if (info && info.instanceName) {
			var wasKnown = almanac.distributedInstances[info.instanceName];
			almanac.distributedInstances[info.instanceName] = {
					date: Date.now(),
					info: info,
				};
			if (!wasKnown) {
				almanac.log.info('VL', 'Distributed peer discovered "' + info.instanceName + '": ' + JSON.stringify(almanac.distributedInstances[info.instanceName]));
			}
		}
	};

	var currentResponses = {};

	function endDistributedResponse(reqId) {
		var currentResponse = currentResponses[reqId];
		delete currentResponses[reqId];
		if (currentResponse && currentResponse.res && currentResponse.res.end) {
			currentResponse.res.end("}}\n");
		}
	}

	function processDistributedRequest(req, res) {
		//TODO: Check that it comes from an authorised peer
		if (req.method !== 'GET') {
			almanac.basicHttp.serve405(req, res, 'GET');
		} else if (!almanac.mqttClient || !almanac.mqttClient.broadcastDistributedRequest) {
			almanac.basicHttp.serve503(req, res);
		} else {
			var reqId = Date.now() + '_' + Math.random();
			currentResponses[reqId] = {
					already: {},
					res: res,
				};
			try {
				res.write('{"instances":{');
				almanac.mqttClient.broadcastDistributedRequest({
						reqId: reqId,
						//method: req.method,	//Always GET
						uri: req.url,
					});
				setTimeout(function() {
						almanac.log.verbose('VL', 'processDistributedRequest: timeout without all responses');
						endDistributedResponse(reqId);
					}, 30000);
			} catch (ex) {
				almanac.log.warn('VL', 'Error while processing distributed request: ' + ex);
			}
		}
	}

	almanac.processDistributedResponse = function (json) {
		if (!(json && json.payload && json.payload.reqId && json.info && json.info.instanceName && json.payload)) {
			return;
		}
		try {
			var currentResponse = currentResponses[json.payload.reqId];
			if (currentResponse && currentResponse.res && currentResponse.res.write) {
				//Little check to avoid processing two times the same response (should be a proper mutex)
				if (currentResponse.already[json.info.instanceName]) {
					almanac.log.warn('VL', 'Distributed response already received for: ' + json.info.instanceName);
					return;
				}
				currentResponse.already[json.info.instanceName] = true;

				var alreadyNb = Object.keys(currentResponse.already).length;
				currentResponse.res.write(
					(alreadyNb > 1 ? ',' : '' ) +
					'"' + json.info.instanceName + '":' + JSON.stringify(json.payload));

				if (alreadyNb >= 1 + Object.keys(almanac.distributedInstances).length) {
					almanac.log.verbose('VL', 'processDistributedResponse: received all responses');
					endDistributedResponse(json.payload.reqId);
				}
			} else {
				almanac.log.warn('VL', 'No current response with ID: ' + json.payload.reqId);
			}
		} catch (ex) {
			almanac.log.warn('VL', 'Error while processing distributed response: ' + ex);
		}
	};

	var lastReqId = '';

	almanac.replyToDistributedRequest = function (json) {
		if (!(almanac.mqttClient && almanac.mqttClient.broadcastDistributedResponse && json.payload && json.payload.reqId)) {
			return;
		}
		if (json.payload.reqId === lastReqId) {
			//Do not respond 2 times in a row to the same request
			return;
		}
		almanac.request.get({
				url: almanac.config.hosts.virtualizationLayer.scheme + '://' + almanac.config.hosts.virtualizationLayer.host + ':' + almanac.config.hosts.virtualizationLayer.port + '/' + json.payload.uri,
				json: true,
				timeout: 10000,
			}, function (error, response, body) {
				if (error || response.statusCode != 200 || !(body)) {
					almanac.log.warn('VL', 'Error ' + (response ? response.statusCode : 'undefined') + ' performing local request for distributed query! ' + json.payload.uri);
				}
				lastReqId = json.payload.reqId;
				//TODO: Send the response only to the original requester instead of broadcast
				almanac.mqttClient.broadcastDistributedResponse({
						reqId: json.payload.reqId,
						statusCode: response.statusCode,
						error: error,
						body: body,
					});
			});
	};

	almanac.routes['distributed/'] = processDistributedRequest;
	almanac.routes['distributedInstances'] = servedistributedInstances;
};
