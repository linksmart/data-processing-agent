select wasteBin.id from federation1.trn.v2.observation.hash.std:lastevent() as wasteBin where wasteBin.sensor.metadata = "http://almanac-project.eu/ontologies/smartcity.owl#FillLevelSensor" and wasteBin.resultValue > 50
	