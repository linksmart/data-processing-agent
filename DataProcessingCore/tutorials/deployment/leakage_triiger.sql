insert into 
	meeter1Safe
select istream
	meeter
from 
	federation1.trn.v2.observation.T1.std:length(2) as meeter 
where
	prev(meeter).resultValue = meeter.resultValue
	