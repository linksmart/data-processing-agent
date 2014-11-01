select 
	*
from topics
		{/almanac/observation/iotentity/#}.win:time_batch(5 sec).std:unique(about)
		
		