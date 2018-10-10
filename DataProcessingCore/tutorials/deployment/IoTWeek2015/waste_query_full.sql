select 
	wasteBin.id as id 
from 
	observation.win:time_batch(1 sec).std:unique(id) as wasteBin 
where 
	(wasteBin.sensor.metadata like '%FillLevelSensor' or wasteBin.sensor.metadata like '%WasteBin') and 
	cast(wasteBin.resultValue, double) > 70.0 

	