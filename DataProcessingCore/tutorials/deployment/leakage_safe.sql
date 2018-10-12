insert into 
	meeter1Safe 
select istream 
	* 
from 
	observation(id = "1bfccd37dd7f12dc24c84c0c3dcdf14a15bc15294adc9f3abe6c291031050f62").win:length(2) 
where 
	 prev(resultValue) = resultValue 
	 or
	 prev(resultValue) is null

	
	