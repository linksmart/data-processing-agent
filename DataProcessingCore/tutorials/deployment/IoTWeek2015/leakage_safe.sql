insert into 
	lekage_safe_00a467b9290129a71c6b496813cf52b437d878f25148773494967e2b85a2031b 
select istream 
	* 
from 
	observation(id = "00a467b9290129a71c6b496813cf52b437d878f25148773494967e2b85a2031b").win:length(2) 
where 
	 prev(resultValue) = resultValue 
	 or
	 prev(resultValue) is null

	
	