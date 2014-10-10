import json

class IoTEntityEvent(object):
	
	def __init__(self, j):
		self.__dict__ = json.loads(j)
	
	def __str__(self):
		return "IoTEntityEvent" + json.dumps(self.__dict__, sort_keys=True, indent=2, separators=(',', ': '));
