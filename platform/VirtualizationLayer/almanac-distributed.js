"use strict";
/*
	Virtualization Layer | Distributed request module
		by Alexandre Alapetite http://alexandre.alapetite.fr
			from Alexandra Institute http://www.alexandra.dk
			for the ALMANAC European project http://www.almanac-project.eu
*/

module.exports = function (almanac) {

	var peerTimeout = 10 * 60 * 1000;	//In milliseconds

	var distributedInstances = {};

	var extend = require('extend');

	function servedistributedInstances(req, res) {
		var me = {};
		me[almanac.config.hosts.instanceName] = {
				self: true,
				date: Date.now(),
				info: almanac.info(),
			};
		almanac.basicHttp.serveJson(req, res, extend(true, {}, distributedInstances, me));
	}

	function deleteExpiredPeers() {
		var expireDate = Date.now() - peerTimeout;
		var instanceNames = Object.keys(distributedInstances);
		for (var i = instanceNames.length - 1; i >= 0; i--) {
			var instanceName = instanceNames[i];
			var instance = distributedInstances[instanceName];
			if (instance.date < expireDate) {
				almanac.log.info('VL', 'Distributed peer expired "' + instanceName + '": ' + JSON.stringify(distributedInstances[instanceName]));
				delete distributedInstances[instanceName];
			}
		}
	}
	setInterval(deleteExpiredPeers, 60000);

	almanac.updateInstance = function (info) {
		if (info && info.instanceName) {
			var wasKnown = distributedInstances[info.instanceName];
			distributedInstances[info.instanceName] = {
					date: Date.now(),
					info: info,
				};
			if (!wasKnown) {
				almanac.log.info('VL', 'Distributed peer discovered "' + info.instanceName + '": ' + JSON.stringify(distributedInstances[info.instanceName]));
			}
		}
	};

	var currentResponses = {};
	var lastReqIds = {};

	function endDistributedResponse(reqId) {
		var currentResponse = currentResponses[reqId];
		delete currentResponses[reqId];
		if (currentResponse && currentResponse.res && currentResponse.res.end) {
			currentResponse.res.end("}}\n");
			setTimeout(function() {
					delete lastReqIds[reqId];
					almanac.log.verbose('VL', 'lastReqIds size: ' + Object.keys(lastReqIds).length);
				}, 60000);
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
						almanac.log.verbose('VL', 'processDistributedRequest: timeout without all ' + (1 + Object.keys(distributedInstances).length) + ' responses');
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
				if (currentResponse.already[json.info.instanceName]) {
					//Check to avoid processing two times the same response
					almanac.log.verbose('VL', 'Distributed response already received for: ' + json.info.instanceName);
					return;
				}
				currentResponse.already[json.info.instanceName] = true;

				var alreadyNb = Object.keys(currentResponse.already).length;
				currentResponse.res.write(
					(alreadyNb > 1 ? ',' : '' ) +
					'"' + json.info.instanceName + '":' + JSON.stringify(json.payload));

				if (alreadyNb >= 1 + Object.keys(distributedInstances).length) {
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

	almanac.replyToDistributedRequest = function (json) {
		if (!(almanac.mqttClient && almanac.mqttClient.broadcastDistributedResponse && json.payload && json.payload.reqId)) {
			return;
		}
		if (lastReqIds[json.payload.reqId]) {
			//Do not respond 2 times to the same request
			almanac.log.verbose('VL', 'Distributed request already answered: ' + json.payload.reqId);
			return;
		}
		lastReqIds[json.payload.reqId] = true;
		almanac.request.get({
				url: almanac.config.hosts.virtualizationLayer.scheme + '://' + almanac.config.hosts.virtualizationLayer.host + ':' + almanac.config.hosts.virtualizationLayer.port + '/' + json.payload.uri,
				json: true,
				timeout: 10000,
			}, function (error, response, body) {
				if (error || response.statusCode != 200 || !(body)) {
					almanac.log.warn('VL', 'Error ' + (response ? response.statusCode : 'undefined') + ' performing local request for distributed query! ' + json.payload.uri);
				}
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
