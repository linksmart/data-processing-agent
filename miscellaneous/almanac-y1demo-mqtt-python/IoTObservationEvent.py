import json

class IoTObservationEvent(object):
	
	def __init__(self, j):
		self.__dict__ = json.loads(j)
		
	def __str__(self):
		return "IoTObservationEvent" + json.dumps(self.__dict__, sort_keys=True, indent=2, separators=(',', ': '));
